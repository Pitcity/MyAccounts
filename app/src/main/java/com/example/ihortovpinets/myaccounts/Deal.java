package com.example.ihortovpinets.myaccounts;


import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by IhorTovpinets on 25.08.2016.
 */
public class Deal implements Serializable {
    //ArrOfProducts
    private Account buyer;
    private Account seller;
    private String products[];
    private double price[];
    private int ammount[];
    private double sum=0;
    private String date;
    private String note;

    public Deal(Account buyer, Account seller, String[] products, double[] price, int[] ammount, String note, String date) {
        this.buyer = buyer;
        this.seller = seller;
        this.products = products;
        this.price = price;
        this.ammount = ammount;
        this.sum = countSumDeal(price,ammount);
        this.note = note;
        this.date = date;
    }

    public Deal(Account buyer, Account seller, String[] products, double[] price, int[] ammount, String note) {
        this.buyer = buyer;
        this.seller = seller;
        this.products = products;
        this.price = price;
        this.ammount = ammount;
        this.sum = countSumDeal(price,ammount);
        this.note = note;

       // Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
        date = df.format(Calendar.getInstance().getTime());
    }

    public Deal(Account buyer, Account seller, String note) {
        this.buyer = buyer;
        this.seller = seller;
        this.sum = 0;
        this.note = note;
        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
        date = df.format(Calendar.getInstance().getTime());
    }

    private double countSumDeal(double[] price, int[] ammount) {
        double sum = 0;
        for (int i=0;i<price.length;i++) {
            sum +=price[i]*ammount[i];
        }
        return sum;
    }

    public String getDate() {return date;}

    public Account getBuyer() {
        return buyer;
    }

    public Account getSeller() {
        return seller;
    }

    public String[] getProducts() {
        return products;
    }

    public double[] getPrice() {
        return price;
    }

    public int[] getAmmount() {
        return ammount;
    }

    public double getSum() {
        return sum;
    }

    public String getNote() {
        return note;
    }
}
