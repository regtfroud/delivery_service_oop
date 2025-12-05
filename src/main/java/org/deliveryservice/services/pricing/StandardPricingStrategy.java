package org.deliveryservice.services.pricing;

import org.deliveryservice.models.Location;
import org.deliveryservice.models.ParcelLocker;

public class StandardPricingStrategy implements PricingStrategy {
    @Override
    public double calculatePrice(double volume, double weight, Location destination) {
        double baseRate = 10.0;
        double volumeRate = volume * 2.0;

        if (destination instanceof ParcelLocker) {
            baseRate += 15.0;
        }

        return baseRate + volumeRate + (weight * 0.5);
    }
}