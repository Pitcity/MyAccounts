package com.example.ihortovpinets.myaccounts;

/**
 * Created by IhorTovpinets on 19.11.2016.
 */

public class DealDTO {
    String seller;
    String buyer;
    String date;
    double sum;

    DealDTO(String seller, String buyer, String date, double sum) {
        this.seller = seller;
        this.buyer = buyer;
        this.date = date;
        this.sum = sum;
    }

    public String getSeller() {
        return seller;
    }

    public String getBuyer() {
        return buyer;
    }

    public String getDate() {
        return date;
    }

    public double getSum() {
        return sum;
    }
}
