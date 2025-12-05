package org.deliveryservice.services;

import org.deliveryservice.models.*;
import java.util.ArrayList;
import java.util.List;

// Шаблон Singleton
public class DatabaseContext {
    private static DatabaseContext instance;

    private List<User> users;
    private List<Courier> couriers;
    private List<Location> locations;
    private int parcelIdCounter = 1; // Перейменовано лічильник

    private DatabaseContext() {
        users = new ArrayList<>();
        couriers = new ArrayList<>();
        locations = new ArrayList<>();
        seedData();
    }

    public static DatabaseContext getInstance() {
        if (instance == null) {
            instance = new DatabaseContext();
        }
        return instance;
    }

    private void seedData() {
        users.add(new User("Адам", 1000.0));
        users.add(new User("Євлампій", 500.0));

        couriers.add(new Courier("Микола", 0.0));

        locations.add(LocationFactory.createLocation("office", 1, "Київ, відділення #1", 0));
        locations.add(LocationFactory.createLocation("locker", 2, "Львів, поштомат #1", 50.0));
        locations.add(LocationFactory.createLocation("office", 3, "Фастів, відділення #1", 0));
    }

    public List<User> getUsers() { return users; }
    public List<Courier> getCouriers() { return couriers; }
    public List<Location> getLocations() { return locations; }

    public int getNextParcelId() { return parcelIdCounter++; }

    public User findUser(String name) {
        return users.stream().filter(u -> u.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
    }

    public Courier findCourier(String name) {
        return couriers.stream().filter(c -> c.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
    }
}