package org.deliveryservice.models;

import java.util.ArrayList;
import java.util.List;

public class Courier extends Person {
    // Список посилок, які зараз у кур'єра "в руках"
    private List<Parcel> parcelsOnHand;

    public Courier(String name, double balance) {
        super(name, balance);
        this.parcelsOnHand = new ArrayList<>();
    }

    public List<Parcel> getParcelsOnHand() {
        return parcelsOnHand;
    }

    public void takeParcel(Parcel p) {
        parcelsOnHand.add(p);
    }

    public void dropParcel(Parcel p) {
        parcelsOnHand.remove(p);
    }
}