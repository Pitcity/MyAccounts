package com.example.ihortovpinets.myaccounts;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by IhorTovpinets on 26.08.2016.
 */

public class DealsForAccountActivity extends AppCompatActivity { // TODO: 21.06.2017 waiting for refactoring

	ArrayList<DealDTO> filteredDeals = new ArrayList<>();
	ListView dealsListView;
	String name;
	public static final String ACCOUNT_ID = "ACCOUNT_ID";
	public static final String IS_ACC_DELETED = "IS_ACC_DELETED";
	public static final int DEALS_FORR_ACC_ACTIVITY_CODE = 202;
	private DealsListAdapter mAdapter;

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setTitle(name);
		MenuItem mi = menu.add(0, R.id.delete_acc_btn, 0, R.string.delete_acc_label);
		mi.setIcon(R.drawable._ic_btn_delete_normal);
		mi.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.delete_acc_btn) {
			DBHelper dbh = new DBHelper(getApplicationContext());
			dbh.deleteAccFromBD(name);
			Intent resultIntent = new Intent();
			resultIntent.putExtra(IS_ACC_DELETED, name);
			setResult(DEALS_FORR_ACC_ACTIVITY_CODE, resultIntent);
			DealsForAccountActivity.super.finish();
		} else {
			onBackPressed();
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.deals_for_acc);
		dealsListView = (ListView) findViewById(R.id.listForDeals);
		name = getIntent().getStringExtra(ACCOUNT_ID);
		filteredDeals = new DBHelper(this).getDealsByName(name);
		populateListofDeals();
	}

	private void populateListofDeals() {
		mAdapter = new DealsListAdapter();
		dealsListView.setAdapter(mAdapter);
	}

	private class DealsListAdapter extends ArrayAdapter<DealDTO> {

		DealsListAdapter() {
			super(DealsForAccountActivity.this, R.layout.deal_item, filteredDeals);
		}

		@Override
		public View getView(int position, View view, ViewGroup parent) {
			DealDTO currentDeal = filteredDeals.get(position);
			if (view == null) {
				view = getLayoutInflater().inflate(R.layout.deal_item, parent, false);
			}
			TextView date = (TextView) view.findViewById(R.id.viewDeals_Date);
			TextView sum = (TextView) view.findViewById(R.id.viewDeals_sum);
			TextView seller = (TextView) view.findViewById(R.id.viewDeals_seller);
			TextView buyer = (TextView) view.findViewById(R.id.viewDeals_buyer);
			TextView descr = (TextView) view.findViewById(R.id.viewDeals_descr);
			date.setText(currentDeal.getDate());
			if (currentDeal.getSeller().equals(name)) {
				sum.setTextColor(Color.GREEN);
				sum.setText(new StringBuilder().append('+').append(currentDeal.getSum()));
			} else {
				sum.setTextColor(Color.RED);
				sum.setText(new StringBuilder().append('-').append(currentDeal.getSum()));
			}
			seller.setText(currentDeal.getSeller());
			buyer.setText(currentDeal.getBuyer());
			descr.setText(currentDeal.getNote());
			return view;
		}
	}
}
