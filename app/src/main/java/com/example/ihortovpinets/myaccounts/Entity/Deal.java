package com.example.ihortovpinets.myaccounts.Entity;


import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

public class Deal implements Serializable {

    private Account buyer;
    private Account seller;
    private double sum = 0;
    private long date;
    private String note;
    private String dealId;
    private int syncFlag;

    private Deal(Account buyer, Account seller, String note, double sum, long date, int syncFlag) {
        this.buyer = buyer;
        this.seller = seller;
        this.sum = sum;
        this.note = note;
        this.date = date;
        this.dealId = UUID.randomUUID().toString();
        this.syncFlag = syncFlag;
    }

    public static Deal createDeal(Account buyer, Account seller, String note, double sum, long date, int syncFlag) {
        if (buyer.performWithdrawal(sum) && seller.performAccrual(sum)) {
            return new Deal(buyer, seller, note, sum, date, syncFlag);
        }
        return null;
    }

    public String getDate() {
        return new Date(date).toString();
    }

    public long getDateLong() {
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

    public int getSyncFlag() {
        return syncFlag;
    }

    public String getNote() {
        return note;
    }
}
