package com.example.ihortovpinets.myaccounts.DTO;

import com.example.ihortovpinets.myaccounts.Entity.Deal;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by IhorTovpinets on 19.11.2016.
 */

public class DealDTO extends JSONObject {

	private String seller;
	private String sellerId;
	private String buyerId;
	private String buyer;
	private long date;
	private String note;
	private double sum;
	private String id;

	private Map<String, Object> mapForJson;

	public DealDTO(String seller, String buyer, long date, double sum, String id) {
		this.seller = seller;
		this.buyer = buyer;
		this.date = date;
		this.sum = sum;
		this.id = id;
		initMap();
	}

	public void initMap() {
		mapForJson = new HashMap<String, Object>();
		mapForJson.put("id", id);
		mapForJson.put("buyer", buyer);
		mapForJson.put("buyerId", buyerId);
		mapForJson.put("seller", seller);
		mapForJson.put("sellerId", sellerId);
		mapForJson.put("date", date);
		mapForJson.put("note", note);
		mapForJson.put("sum", sum);
		for (Map.Entry<String, Object> entry: mapForJson.entrySet()) {
			try {
				put(entry.getKey(), entry.getValue());
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}

	public DealDTO(String seller, String buyer, long date, double sum, String note, String id) {
		this.seller = seller;
		this.buyer = buyer;
		this.date = date;
		this.sum = sum;
		this.note = note;
		this.id = id;
		initMap();
	}

	public DealDTO(Deal deal) {
		this.seller = deal.getSeller().getName();
		this.sellerId = deal.getSeller().getAccountId();
		this.buyer = deal.getBuyer().getName();
		this.buyerId = deal.getBuyer().getAccountId();
		this.date = deal.getDateLong();
		this.sum = deal.getSum();
		this.note = deal.getNote();
		this.id = deal.getDealId();
		initMap();
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

	public Long getDateMilis() {
	    return date;
    }

	public double getSum() {
		return sum;
	}

	public String getNote() {
		return note;
	}

	public String getSellerId() {
		return sellerId;
	}
	public String getBuyerIdId() {
		return buyerId;
	}
	public String getId() {
		return id;
	}
}
