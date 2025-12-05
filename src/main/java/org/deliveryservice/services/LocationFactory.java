package org.deliveryservice.services;

import org.deliveryservice.models.*;

public class LocationFactory {
    public static Location createLocation(String type, int id, String address, double capacity) {
        if (type.equalsIgnoreCase("locker")) {
            return new ParcelLocker(id, address, capacity);
        } else {
            return new PostOffice(id, address);
        }
    }
}