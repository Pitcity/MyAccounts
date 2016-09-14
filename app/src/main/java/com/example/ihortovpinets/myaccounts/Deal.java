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

    public Deal(Account buyer, Account seller, String[] products, double[] price, int[] ammount) {
        this.buyer = buyer;
        this.seller = seller;
        this.products = products;
        this.price = price;
        this.ammount = ammount;
        this.sum = countSumDeal(price,ammount);

       // Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
        date = df.format(Calendar.getInstance().getTime());
    }

    public Deal(Account buyer, Account seller) {
        this.buyer = buyer;
        this.seller = seller;
        this.sum = 0;
        // Calendar c = Calendar.getInstance();
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
}
