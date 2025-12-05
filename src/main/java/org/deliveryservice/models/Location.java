package org.deliveryservice.models;

import java.util.ArrayList;
import java.util.List;

public abstract class Location {
    protected int id;
    protected String address;
    //  Посилки, що знаходяться в цьому місці
    protected List<Parcel> parcelsHere;

    public Location(int id, String address) {
        this.id = id;
        this.address = address;
        this.parcelsHere = new ArrayList<>();
    }

    public int getId() { return id; }
    public String getAddress() { return address; }
    public List<Parcel> getParcels() { return parcelsHere; }

    public void addParcel(Parcel p) {
        parcelsHere.add(p);
    }

    public void removeParcel(Parcel p) {
        parcelsHere.remove(p);
    }

    //  Чи можна відправити в дане відділення/поштомат (поштомати мають обмеження)
    public abstract boolean canAcceptParcel(Parcel p);

    @Override
    public String toString() {
        return "ID: " + id + ", Адреса: " + address;
    }
}