package com.example.ihortovpinets.myaccounts;


import java.io.Serializable;

/**
 * Created by IhorTovpinets on 25.08.2016.
 */
public class Deal implements Serializable {

    private Account buyer;
    private Account seller;
    private double sum = 0;
    private String date;
    private String note;

    private Deal(Account buyer, Account seller, String note, double sum, String date) {
        this.buyer = buyer;
        this.seller = seller;
        this.sum = sum;
        this.note = note;
        this.date = date;
    }

    public static Deal createDeal(Account buyer, Account seller, String note, double sum, String date) {
        if (buyer.depositIsChanged(-sum) && seller.depositIsChanged(sum))
            return new Deal(buyer, seller, note, sum, date);
        return null;
    }

    public String getDate() {
        return date;
    }

    public Account getBuyer() {
        return buyer;
    }

    public Account getSeller() {
        return seller;
    }

    public double getSum() {
        return sum;
    }

    public String getNote() {
        return note;
    }
}
