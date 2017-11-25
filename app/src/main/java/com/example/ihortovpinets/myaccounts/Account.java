package com.example.ihortovpinets.myaccounts;

import java.io.Serializable;
import java.util.UUID;

/**
 * Created by IhorTovpinets on 25.08.2016.
 */

public class Account implements Serializable {

    private String accountId;
    private String name;
    private double deposit;
    private String description;
    private boolean isOuter; // TODO: 13.11.2017 all deletion with the status

    /**
     * 1 - edited
     * 0 - deleted
     * 2 - existing
     **/
    private int status;

    public Account(String name, boolean flag) {
        this.name = name;
        this.deposit = 0;
        this.isOuter = flag;
        this.description = "";
        this.accountId = UUID.randomUUID().toString();
    }

    public Account(String name, double deposit, String description, boolean flag) {
        this.name = name;
        this.deposit = deposit;
        this.isOuter = flag;
        this.description = description;
        this.accountId = UUID.randomUUID().toString();
    }

    public String getName() {
        return name;
    }

    public boolean isOuter() {
        return isOuter;
    }

    public String getAccountId() {
        return accountId;
    }

    public double getDeposit() {
        return deposit;
    }

    public String getDescription() {
        return description;
    }

    public boolean depositIsChanged(double ammount) {
        if (!isOuter) {
            if (-ammount > this.deposit)
                return false;
            else {
                this.deposit = this.deposit + ammount;
                return true;
            }
        } else return true;
    }

    public boolean equals(Object o) {
        Account account = (Account) o;
        return this.accountId.equals(account.accountId);
    }
}
