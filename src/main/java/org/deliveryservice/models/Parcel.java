package org.deliveryservice.models;

//  Клас посилки з використанням шаблону Builder
public class Parcel {
    private int id;
    private double weight;
    private double volume;
    private User sender;
    private User recipient;
    private Location origin;
    private Location destination;
    private ParcelStatus status;
    private double price;

    //  Приватний конструктор, доступний тільки Builder
    private Parcel(ParcelBuilder builder) {
        this.id = builder.id;
        this.weight = builder.weight;
        this.volume = builder.volume;
        this.sender = builder.sender;
        this.recipient = builder.recipient;
        this.origin = builder.origin;
        this.destination = builder.destination;
        this.status = ParcelStatus.CREATED;
        this.price = builder.price;
    }

    public int getId() {
        return id;
    }

    public User getRecipient() {
        return recipient;
    }

    public Location getDestination() {
        return destination;
    }

    public ParcelStatus getStatus() {
        return status;
    }

    public double getVolume() {
        return volume;
    }

    public double getPrice() {
        return price;
    }

    public void setStatus(ParcelStatus status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return String.format("Пакунок #%d | До: %s | Статус: %s | Ціна: %.2f",
                id, destination.getAddress(), status, price);
    }

    // Шаблон Builder
    public static class ParcelBuilder {
        private int id;
        private double weight;
        private double volume;
        private User sender;
        private User recipient;
        private Location origin;
        private Location destination;
        private double price;

        public ParcelBuilder setId(int id) {
            this.id = id;
            return this;
        }

        public ParcelBuilder setWeight(double weight) {
            this.weight = weight;
            return this;
        }

        public ParcelBuilder setVolume(double volume) {
            this.volume = volume;
            return this;
        }

        public ParcelBuilder setSender(User sender) {
            this.sender = sender;
            return this;
        }

        public ParcelBuilder setRecipient(User recipient) {
            this.recipient = recipient;
            return this;
        }

        public ParcelBuilder setOrigin(Location origin) {
            this.origin = origin;
            return this;
        }

        public ParcelBuilder setDestination(Location destination) {
            this.destination = destination;
            return this;
        }

        public ParcelBuilder setPrice(double price) {
            this.price = price;
            return this;
        }

        public Parcel build() {
            return new Parcel(this);
        }
    }
}
