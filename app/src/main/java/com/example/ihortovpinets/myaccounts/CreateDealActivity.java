package com.example.ihortovpinets.myaccounts;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

/**
 * Created by IhorTovpinets on 15.09.2016.
 */
public class CreateDealActivity extends AppCompatActivity {

	public static final String DEAL_CREATED = "DEAL_CREATED";
	public static final int CODE_FOR_CREATING_DEAL = 221;

	DatePicker mDatePicker;
	ArrayList<Account> accounts;
	Spinner spinnerSeller, spinnerBuyer;
	Button dealAdding_btm;
	EditText additionSeller, additionBuyer, dealSum, dealDescr;

	private Account getAccFromSpin(Spinner sp, EditText et) throws IOException {
		Account acc = null;
		if (sp.getSelectedItem().equals("Another"))
			if (et.getText().toString().equals("")) {
				Toast.makeText(getApplicationContext(), "Enter name of seller under \"Another\"", Toast.LENGTH_LONG).show();
				throw new IOException();
			} else
				acc = new Account(et.getText().toString(), true);
		else
			for (Account a : accounts)
				if (a.getName().equals(sp.getSelectedItem().toString()))
					acc = a;
		return acc;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.save:
				if (validateDataForDeal()) {
					Intent resultIntent = new Intent();
					resultIntent.putExtra(DEAL_CREATED, true);
					setResult(CODE_FOR_CREATING_DEAL, resultIntent);
					finish();
				}
				break;
		}
		return super.onOptionsItemSelected(item);
	}

	private boolean validateDataForDeal() {//// TODO: 30.05.2017 rewrite this method. No logic with exceptions, etc..
		Account buyer, seller;
		if (spinnerBuyer.getSelectedItem().toString().equals(spinnerSeller.getSelectedItem().toString())) {
			Toast.makeText(getApplicationContext(), "One acc cant be as buyer and seller in ne deal, change it pls", Toast.LENGTH_LONG).show();
			return false;
		} else {
			try {
				seller = getAccFromSpin(spinnerSeller, additionSeller);
				buyer = getAccFromSpin(spinnerBuyer, additionBuyer);
			} catch (Exception e) {
				return false;
			}
			double sum;
			String note;
			try {
				sum = Double.valueOf(dealSum.getText().toString());
				note = dealDescr.getText().toString();
			} catch (Exception e) {
				Toast.makeText(getApplicationContext(), "Enter correct value as ammount of the deal", Toast.LENGTH_LONG).show();
				return false;
			}
			SimpleDateFormat df = new SimpleDateFormat("dd.MM.yyyy");
			String date = df.format(mDatePicker.getCalendarView().getDate());

			Deal newDeal = Deal.createDeal(buyer, seller, note, sum, date); //// TODO: 30.05.2017 rewrite method
			if (newDeal == null) {
				Toast.makeText(getApplicationContext(), "Impossible transaction (not enough money)", Toast.LENGTH_LONG).show();
				return false;
			} else {
				DBHelper db = new DBHelper(getApplicationContext());
				db.updateAcc(newDeal.getBuyer());
				db.updateAcc(newDeal.getSeller());
				db.addDealToDB(newDeal);
			}
			return true;
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuItem mi = menu.add(0, R.id.save, 0, getResources().getString(R.string.save));
		mi.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
		mi.setIcon(R.drawable._ic_btn_save_xml);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_create_deal);

		mDatePicker = (DatePicker) findViewById(R.id.datePicker);
		mDatePicker.setCalendarViewShown(false);
		mDatePicker.setMaxDate(new java.util.Date().getTime());
		mDatePicker.setAlpha(0.6f);
		spinnerSeller = (Spinner) findViewById(R.id.dealSeller);
		spinnerBuyer = (Spinner) findViewById(R.id.dealBuyer);
		dealAdding_btm = (Button) findViewById(R.id.dealAdding_btn);
		additionBuyer = (EditText) findViewById(R.id.dealAdditionBuyer);
		additionSeller = (EditText) findViewById(R.id.dealAdditionSeller);
		dealSum = (EditText) findViewById(R.id.dealSum);
		dealDescr = (EditText) findViewById(R.id.dealDescr);

		ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, android.R.id.text1);
		spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		accounts = new DBHelper(this).getAccListFromDB();
		spinnerSeller.setAdapter(spinnerAdapter);
		spinnerBuyer.setAdapter(spinnerAdapter);
		for (Account a : accounts) {
			spinnerAdapter.add(a.getName());
		}
		spinnerAdapter.add("Another");
	}
}
