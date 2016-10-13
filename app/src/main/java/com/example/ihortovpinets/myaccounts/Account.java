package com.example.ihortovpinets.myaccounts;

import java.io.Serializable;

/**
 * Created by IhorTovpinets on 25.08.2016.
 */
public class Account implements Serializable { //make inh and 2 types of acc, inner and outer, now just a flag
    private String name;
    private double deposit;
    private String description;
    public int flag; //0 - inner ; 1 - outer

    private Account(String name)
    {
        this.name = name;
        this.deposit = 0;
        flag =0;
        this.description = "";
    }

    public Account(String name, int flag)
    {
        this.name = name;
        this.deposit = 0;
        this.flag = flag;
        this.description = "";
    }
    public Account(String name, double deposit, String description, int flag) {
        this.name = name;
        this.deposit = deposit;
        this.flag=flag;
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

    public boolean depositIsChanged (double ammount){
        if (this.flag == 0) {
            if (-ammount > this.deposit) return false;
            else {
                this.deposit = this.deposit + ammount;
                return true;
            }
        }
        else return true;
    }
}
