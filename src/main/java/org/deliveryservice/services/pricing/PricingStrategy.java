package org.deliveryservice.services.pricing;

import org.deliveryservice.models.Location;

public interface PricingStrategy {
    double calculatePrice(double volume, double weight, Location destination);
}