package org.deliveryservice.models;

public class ParcelLocker extends Location {
    private double maxVolume; //   Обмеження поштомату

    public ParcelLocker(int id, String address, double maxVolume) {
        super(id, address);
        this.maxVolume = maxVolume;
    }

    @Override
    public boolean canAcceptParcel(Parcel p) {
        //  Перевірка габаритів
        return p.getVolume() <= maxVolume;
    }

    @Override
    public String toString() {
        return "[Поштомат] " + super.toString() + " (Max Vol: " + maxVolume + ")";
    }
}