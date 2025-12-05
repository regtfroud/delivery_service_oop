package org.deliveryservice;

import org.deliveryservice.models.*;
import org.deliveryservice.services.*;
import org.deliveryservice.services.pricing.*;
import java.util.Scanner;
import java.util.List;

public class Main {
    private static final DatabaseContext db = DatabaseContext.getInstance();
    private static final Scanner scanner = new Scanner(System.in);
    private static final PricingStrategy pricingStrategy = new StandardPricingStrategy();

    public static void main(String[] args) {
        System.out.println("=== Система Служби Доставки ===");

        while (true) {
            System.out.println("\n1. Вхід як Користувач");
            System.out.println("2. Вхід як Кур'єр");
            System.out.println("0. Вихід");
            System.out.print("Ваш вибір: ");

            String choice = scanner.nextLine();

            if (choice.equals("0")) break;

            if (choice.equals("1")) handleUserFlow();
            else if (choice.equals("2")) handleCourierFlow();
            else System.out.println("Невірний вибір.");
        }
    }

    //  Логіка Користувача
    private static void handleUserFlow() {
        System.out.print("Введіть ваше ім'я: ");
        String name = scanner.nextLine();
        User user = db.findUser(name);

        if (user == null) {
            System.out.println("Користувача не знайдено.");
            return;
        }

        System.out.println("Вітаємо, " + user.getName() + ". Баланс: " + user.getBalance());

        while (true) {
            System.out.println("1. Відправити посилку");
            System.out.println("2. Отримати посилку");
            System.out.println("0. Назад");
            String cmd = scanner.nextLine();

            if (cmd.equals("0")) break;
            if (cmd.equals("1")) createParcel(user);
            if (cmd.equals("2")) receiveParcel(user);
        }
    }

    private static void createParcel(User sender) {
        //  Вибір відділення відправки
        Location origin = selectLocation("Оберіть відділення відправки:");
        if (origin == null) return;

        //  Дані отримувача
        System.out.print("Ім'я отримувача: ");
        String rName = scanner.nextLine();
        User recipient = db.findUser(rName);
        if (recipient == null) {
            System.out.println("Отримувача не знайдено в базі (створіть його спочатку).");
            return;
        }

        //  Параметри пакунка
        System.out.print("Вага (кг): ");
        double weight = Double.parseDouble(scanner.nextLine());
        System.out.print("Об'єм (умовні од.): ");
        double volume = Double.parseDouble(scanner.nextLine());

        //  Пункт призначення
        Location dest = selectLocation("Оберіть пункт призначення:");
        if (dest == null) return;

        //  Валідація поштомату (створюється тимчасовий об'єкт для перевірки)
        if (!dest.canAcceptParcel(new Parcel.ParcelBuilder().setVolume(volume).build())) {
            System.out.println("Помилка: Пакунок занадто великий для цього поштомату.");
            return;
        }

        //  Розрахунок ціни
        double price = pricingStrategy.calculatePrice(volume, weight, dest);
        System.out.println("Розрахована ціна доставки: " + price);

        if (sender.getBalance() < price) {
            System.out.println("Недостатньо коштів на балансі.");
            return;
        }

        System.out.println("Підтвердити оплату та відправку? (y/n)");
        if (scanner.nextLine().equalsIgnoreCase("y")) {
            sender.deductBalance(price);

            //  Використання шаблону Builder
            Parcel parcel = new Parcel.ParcelBuilder()
                    .setId(db.getNextParcelId())
                    .setSender(sender)
                    .setRecipient(recipient)
                    .setOrigin(origin)
                    .setDestination(dest)
                    .setWeight(weight)
                    .setVolume(volume)
                    .setPrice(price)
                    .build();

            origin.addParcel(parcel);
            System.out.println("Пакунок успішно створено!");
        }
    }

    private static void receiveParcel(User user) {
        Location loc = selectLocation("Де ви хочете забрати пакунок?");
        if (loc == null) return;

        //  Пошук посилок для користувача
        List<Parcel> myParcels = loc.getParcels().stream()
                .filter(p -> p.getRecipient().equals(user) && p.getStatus() == ParcelStatus.DELIVERED)
                .toList();

        if (myParcels.isEmpty()) {
            System.out.println("У цьому відділенні немає посилок для вас.");
            return;
        }

        System.out.println("Ваші посилки:");
        for (Parcel p : myParcels) {
            System.out.println(p);
        }

        System.out.println("Забрати всі? (y/n)");
        if (scanner.nextLine().equalsIgnoreCase("y")) {
            for (Parcel p : myParcels) {
                p.setStatus(ParcelStatus.RECEIVED);
                loc.removeParcel(p);
            }
            System.out.println("Ви забрали посилки.");
        }
    }

    //  Логіка Кур'єра
    private static void handleCourierFlow() {
        System.out.print("Ім'я кур'єра: ");
        String name = scanner.nextLine();
        Courier courier = db.findCourier(name);
        if (courier == null) {
            System.out.println("Кур'єра не знайдено.");
            return;
        }

        while (true) {
            System.out.println("Кур'єр: " + courier.getName());
            System.out.println("Посилок на руках: " + courier.getParcelsOnHand().size());
            System.out.println("1. Прибути у відділення/поштомат (Забрати/Віддати)");
            System.out.println("0. Вихід");

            String cmd = scanner.nextLine();
            if (cmd.equals("0")) break;

            if (cmd.equals("1")) {
                Location loc = selectLocation("Куди прямуємо?");
                if (loc == null) continue;

                simulateTravel();

                courierArrivedAtLocation(courier, loc);
            }
        }
    }

    private static void courierArrivedAtLocation(Courier courier, Location location) {
        System.out.println("\n--- Ви прибули у " + location.toString() + " ---");

        //  Віддати посилки
        List<Parcel> toDeliver = courier.getParcelsOnHand().stream()
                .filter(p -> p.getDestination().getId() == location.getId())
                .toList();

        if (!toDeliver.isEmpty()) {
            System.out.println("Автоматичне вивантаження " + toDeliver.size() + " посилок...");
            for (Parcel p : toDeliver) {
                courier.dropParcel(p);
                location.addParcel(p);
                p.setStatus(ParcelStatus.DELIVERED);
                System.out.println("Доставлено: Пакунок #" + p.getId());
            }
        }

        //  Забрати посилки
        if (location instanceof ParcelLocker) {
            System.out.println("Це поштомат. Забір посилок неможливий.");
        } else {
            List<Parcel> readyForPickup = location.getParcels().stream()
                    .filter(p -> p.getStatus() == ParcelStatus.CREATED)
                    .toList();

            if (!readyForPickup.isEmpty()) {
                System.out.println("Доступні для забору:");
                for (Parcel p : readyForPickup) {
                    System.out.println(p);
                }
                System.out.println("Забрати всі? (y/n)");
                if (scanner.nextLine().equalsIgnoreCase("y")) {
                    for (Parcel p : readyForPickup) {
                        location.removeParcel(p);
                        courier.takeParcel(p);
                        p.setStatus(ParcelStatus.PICKED_UP);
                    }
                    System.out.println("Посилки завантажено.");
                }
            } else {
                System.out.println("Немає нових посилок для забору.");
            }
        }
    }

    //  Заглушка подорожі
    private static void simulateTravel() {
        System.out.print("Подорож...");
        try {
            for(int i=0; i<5; i++) {
                System.out.print(".");
                Thread.sleep(1000);
            }
            System.out.println(" Прибули!");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private static Location selectLocation(String prompt) {
        System.out.println(prompt);
        List<Location> locs = db.getLocations();
        for (int i = 0; i < locs.size(); i++) {
            System.out.println((i + 1) + ". " + locs.get(i));
        }
        System.out.print("Вибір: ");
        try {
            int idx = Integer.parseInt(scanner.nextLine()) - 1;
            if (idx >= 0 && idx < locs.size()) return locs.get(idx);
        } catch (NumberFormatException e) {
        }
        System.out.println("Невірний вибір.");
        return null;
    }
}