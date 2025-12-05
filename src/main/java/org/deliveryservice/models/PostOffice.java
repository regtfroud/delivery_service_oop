package org.deliveryservice.models;

public class PostOffice extends Location {
    public PostOffice(int id, String address) {
        super(id, address);
    }

    @Override
    public boolean canAcceptParcel(Parcel p) {
        return true; // Відділення приймає все
    }

    @Override
    public String toString() {
        return "[Відділення] " + super.toString();
    }
}