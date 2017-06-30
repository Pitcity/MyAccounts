package com.example.ihortovpinets.myaccounts;

import java.io.Serializable;

/**
 * Created by IhorTovpinets on 25.08.2016.
 */

public class Account implements Serializable {
	private int accId;
	private String name;
	private double deposit;
	private String description;
	public boolean isOuter;

	public Account(String name, boolean flag) {
		this.name = name;
		this.deposit = 0;
		this.isOuter = flag;
		this.description = "";
	}

	public Account(String name, double deposit, String description, boolean flag, int accId) {
		this.name = name;
		this.deposit = deposit;
		this.isOuter = flag;
		this.description = description;
		this.accId = accId;
	}

	public Account(String name, double deposit, String description, boolean flag) {
		this.name = name;
		this.deposit = deposit;
		this.isOuter = flag;
		this.description = description;
	}

	public String getName() {
		return name;
	}

	public double getDeposit() {
		return deposit;
	}

	public String getDescription() {
		return description;
	}

	public boolean depositIsChanged(double ammount) {
		if (!isOuter) {
			if (-ammount > this.deposit)
				return false;
			else {
				this.deposit = this.deposit + ammount;
				return true;
			}
		} else return true;
	}

	@Override
	public boolean equals(Object o) {
		Account account = (Account) o;
		return this.getName().equals(account.getName()) || account.accId == this.accId;
	}
}
