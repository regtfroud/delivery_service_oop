package tests;

import org.deliveryservice.models.*;
import org.deliveryservice.services.pricing.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Цей клас є самодостатнім "Test Runner".
 * Він не потребує JUnit. Просто запустіть main().
 */
public class DeliveryServiceTestRunner {

    // Лічильники для статистики
    private static int testsRun = 0;
    private static int testsPassed = 0;
    private static int testsFailed = 0;

    public static void main(String[] args) {
        System.out.println("==========================================");
        System.out.println(" ЗАПУСК ТЕСТІВ DELIVERY SERVICE");
        System.out.println("==========================================\n");

        // 1. Тести стратегії ціноутворення
        runTest("Pricing: Розрахунок для відділення", () -> {
            PricingStrategy strategy = new StandardPricingStrategy();
            Location office = new PostOffice(1, "Test Office");
            // Base(10) + Vol(10*2) + Weight(5*0.5) = 10 + 20 + 2.5 = 32.5
            double price = strategy.calculatePrice(10.0, 5.0, office);
            assertEquals(32.5, price, 0.01);
        });

        runTest("Pricing: Розрахунок для поштомату (з націнкою)", () -> {
            PricingStrategy strategy = new StandardPricingStrategy();
            Location locker = new ParcelLocker(2, "Test Locker", 100);
            // Base(10+15) + Vol(10*2) + Weight(5*0.5) = 25 + 20 + 2.5 = 47.5
            double price = strategy.calculatePrice(10.0, 5.0, locker);
            assertEquals(47.5, price, 0.01);
        });

        // 2. Тести Локацій
        runTest("Location: Поштомат відхиляє велику посилку", () -> {
            ParcelLocker locker = new ParcelLocker(1, "Tiny Locker", 50.0);
            Parcel bigParcel = new Parcel.ParcelBuilder().setVolume(51.0).build();

            assertFalse(locker.canAcceptParcel(bigParcel), "Поштомат мав відхилити посилку 51.0 при ліміті 50.0");
        });

        runTest("Location: Поштомат приймає допустиму посилку", () -> {
            ParcelLocker locker = new ParcelLocker(1, "Tiny Locker", 50.0);
            Parcel smallParcel = new Parcel.ParcelBuilder().setVolume(49.0).build();

            assertTrue(locker.canAcceptParcel(smallParcel), "Поштомат мав прийняти посилку");
        });

        // 3. Тести Балансу
        runTest("User: Списання балансу успішне", () -> {
            User user = new User("Rich Guy", 100.0);
            boolean result = user.deductBalance(50.0);
            assertTrue(result, "Операція мала пройти успішно");
            assertEquals(50.0, user.getBalance(), 0.01);
        });

        runTest("User: Списання балансу неможливе (недостатньо коштів)", () -> {
            User user = new User("Poor Guy", 10.0);
            boolean result = user.deductBalance(50.0);
            assertFalse(result, "Операція мала бути відхилена");
            assertEquals(10.0, user.getBalance(), 0.01);
        });

        // 4. Тест Builder
        runTest("Parcel: Builder створює коректний об'єкт", () -> {
            Location loc = new PostOffice(1, "A");
            Parcel p = new Parcel.ParcelBuilder()
                    .setId(777)
                    .setDestination(loc)
                    .setPrice(99.9)
                    .build();

            assertEquals(777, p.getId());
            assertEquals(99.9, p.getPrice(), 0.01);
            assertEquals(ParcelStatus.CREATED, p.getStatus()); // Перевірка дефолтного статусу
        });

        // 5. Тест логіки Кур'єра
        runTest("Courier: Взяти та віддати посилку", () -> {
            Courier courier = new Courier("Flash", 0);
            Parcel p = new Parcel.ParcelBuilder().setId(1).build();

            // Кур'єр бере
            courier.takeParcel(p);
            assertEquals(1, courier.getParcelsOnHand().size());

            // Кур'єр віддає
            courier.dropParcel(p);
            assertEquals(0, courier.getParcelsOnHand().size());
        });

        // Підсумки
        System.out.println("\n==========================================");
        System.out.println(" РЕЗУЛЬТАТИ:");
        System.out.println(" Всього тестів: " + testsRun);
        System.out.println(" Успішно: " + testsPassed);
        System.out.println(" Провалено: " + testsFailed);
        System.out.println("==========================================");

        if (testsFailed > 0) {
            System.exit(1); // Код помилки для CI/CD
        }
    }

    // --- Допоміжні методи (Mini Framework) ---

    private static void runTest(String name, Runnable test) {
        testsRun++;
        System.out.print("TEST [" + name + "] ... ");
        try {
            test.run();
            System.out.println("OK");
            testsPassed++;
        } catch (AssertionError e) {
            System.out.println("FAILED");
            System.out.println("   -> Помилка: " + e.getMessage());
            testsFailed++;
        } catch (Exception e) {
            System.out.println("ERROR (Exception)");
            e.printStackTrace();
            testsFailed++;
        }
    }

    private static void assertEquals(double expected, double actual, double delta) {
        if (Math.abs(expected - actual) > delta) {
            throw new AssertionError("Очікувалось: " + expected + ", отримано: " + actual);
        }
    }

    private static void assertEquals(Object expected, Object actual) {
        if (expected == null && actual == null) return;
        if (expected == null || !expected.equals(actual)) {
            throw new AssertionError("Очікувалось: " + expected + ", отримано: " + actual);
        }
    }

    private static void assertTrue(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }

    private static void assertFalse(boolean condition, String message) {
        if (condition) {
            throw new AssertionError(message);
        }
    }
}