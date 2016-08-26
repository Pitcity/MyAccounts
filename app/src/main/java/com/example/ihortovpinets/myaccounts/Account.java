package com.example.ihortovpinets.myaccounts;

/**
 * Created by IhorTovpinets on 25.08.2016.
 */
public class Account {
    private String name;
    private double deposit;
    private String description;

    public Account(String name, double deposit, String description) {
        this.name = name;
        this.deposit = deposit;
        this.description = description;
    } //add to DB

    public String getName() {
        return name;
    }

    public double getDeposit() {
        return deposit;
    }

    public String getDescription() {
        return description;
    }

    public boolean changeDeposit (int ammount){
        if (-ammount>this.deposit) return false;
        else {
            this.deposit = this.deposit + ammount;
            return true;
        }

    }
}
