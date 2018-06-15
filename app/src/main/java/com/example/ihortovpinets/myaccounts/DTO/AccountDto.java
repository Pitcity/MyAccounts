package com.example.ihortovpinets.myaccounts.DTO;

import com.example.ihortovpinets.myaccounts.Entity.Account;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by itovp on 11.06.2018.
 */

public class AccountDto extends JSONObject {

	private String accountId;
	private String name;
	private double deposit;
	private String description;
	private boolean isOuter;

	private Map<String, Object> mapForJson;

	public void initMap() {
		mapForJson = new HashMap<String, Object>();
		mapForJson.put("id", accountId);
		mapForJson.put("name", name);
		mapForJson.put("deposit", deposit);
		mapForJson.put("description", description);
		mapForJson.put("isOuter", isOuter);
		for (Map.Entry<String, Object> entry: mapForJson.entrySet()) {
			try {
				put(entry.getKey(), entry.getValue());
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}

	public AccountDto(Account acc) {
		accountId = acc.getAccountId();
		name = acc.getName();
		deposit = acc.getDeposit();
		description = acc.getDescription();
		isOuter = acc.isOuter();
		initMap();
	}
}
