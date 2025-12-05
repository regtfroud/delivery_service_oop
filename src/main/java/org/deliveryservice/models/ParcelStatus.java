package org.deliveryservice.models;

// Стан посилки
public enum ParcelStatus {
    CREATED,        // Створена, у відділенні відправника
    PICKED_UP,      // У кур'єра
    DELIVERED,      // Доставлена у відділення отримувача
    RECEIVED        // Отримана клієнтом
}