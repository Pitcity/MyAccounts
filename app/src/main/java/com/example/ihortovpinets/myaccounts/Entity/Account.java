package com.example.ihortovpinets.myaccounts.Entity;

import android.support.annotation.NonNull;

import java.io.Serializable;
import java.util.UUID;

public class Account implements Serializable {

    private String accountId;
    private String name;
    private double deposit;
    private String description;
    private boolean isOuter; // TODO: 13.11.2017 all deletion with the status

    /**
     * 1 - edited
     * 0 - is ok
     * 2 - new
     * 9 - deleted
     **/
    private int syncFlag;

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

    public Account(String accountId, String name, double deposit, String description, boolean flag, int syncFlag) {
        this.name = name;
        this.deposit = deposit;
        this.isOuter = flag;
        this.description = description;
        this.accountId = accountId;
        this.syncFlag = syncFlag;
    }

    public Account(String accountId, String name, double deposit, String description, boolean flag) {
        this.name = name;
        this.deposit = deposit;
        this.isOuter = flag;
        this.description = description;
        this.accountId = accountId;
        this.syncFlag = 0;
    }

    public String getName() {
        return name;
    }

    public boolean isOuter() {
        return isOuter;
    }

    @NonNull
    public String getAccountId() {
        return accountId;
    }

    public double getDeposit() {
        return deposit;
    }

    public String getDescription() {
        return description;
    }

    public boolean performAccrual(double amount) {
        if (!isOuter) {
            this.deposit = this.deposit + amount;
        }
        return true;
    }

    public boolean performWithdrawal(double amount) {
        boolean operationResult;
        if (!isOuter) {
            if (this.deposit - amount < 0) {
                operationResult = false;
            } else {
                this.deposit = this.deposit - amount;
                operationResult = true;
            }
        } else {
            operationResult = true;
        }
        return operationResult;
    }

    public boolean equals(Object o) {
        if (o instanceof Account && ((Account) o).syncFlag != 9 && this.syncFlag != 9) {
            Account account = (Account) o;
            return this.accountId.equals(account.accountId);
        } else
            return false;
    }
}
