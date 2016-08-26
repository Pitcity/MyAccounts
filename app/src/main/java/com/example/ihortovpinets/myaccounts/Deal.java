package com.example.ihortovpinets.myaccounts;

/**
 * Created by IhorTovpinets on 25.08.2016.
 */
public class Deal {
    //ArrOfProducts
    private Account buyer;
    private Account seller;
    private String products[];
    private double price[];
    private int ammount[];
    private double sum=0;

    public Deal(Account buyer, Account seller, String[] products, double[] price, int[] ammount) {
        this.buyer = buyer;
        this.seller = seller;
        this.products = products;
        this.price = price;
        this.ammount = ammount;
        this.sum = countSumDeal(price,ammount);
    }

    private double countSumDeal(double[] price, int[] ammount) {
        double sum = 0;
        for (int i=0;i<price.length;i++) {
            sum +=price[i]*ammount[i];
        }
        return sum;
    }

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
