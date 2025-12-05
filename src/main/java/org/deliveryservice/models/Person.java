package org.deliveryservice.models;

public abstract class Person {
    protected String name;
    protected double balance;

    public Person(String name, double balance) {
        this.name = name;
        this.balance = balance;
    }

    public String getName() { return name; }
    public double getBalance() { return balance; }

    public boolean deductBalance(double amount) {
        if (balance >= amount) {
            balance -= amount;
            return true;
        }
        return false;
    }

    public void addBalance(double amount) {
        this.balance += amount;
    }
}