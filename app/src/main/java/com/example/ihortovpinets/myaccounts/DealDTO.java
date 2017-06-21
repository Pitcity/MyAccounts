package com.example.ihortovpinets.myaccounts;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by IhorTovpinets on 19.11.2016.
 */

public class DealDTO {

	private String seller;
	private String buyer;
	private long date;
	private String note;
	private double sum;

	DealDTO(String seller, String buyer, long date, double sum) {
		this.seller = seller;
		this.buyer = buyer;
		this.date = date;
		this.sum = sum;
	}

	DealDTO(String seller, String buyer, long date, double sum, String note) {
		this.seller = seller;
		this.buyer = buyer;
		this.date = date;
		this.sum = sum;
		this.note = note;
	}

	public String getSeller() {
		return seller;
	}

	public String getBuyer() {
		return buyer;
	}

	public String getDate() {
		return SimpleDateFormat.getDateInstance().format(new Date(date));
	}

	public double getSum() {
		return sum;
	}

	public String getNote() {
		return note;
	}
}
