package com.example.ihortovpinets.myaccounts;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.ihortovpinets.myaccounts.DTO.DealDTO;
import com.example.ihortovpinets.myaccounts.Service.SyncService;

import java.util.ArrayList;

/**
 * Created by IhorTovpinets on 26.08.2016.
 */

public class DealsForAccountActivity extends AppCompatActivity { // TODO: 21.06.2017 waiting for refactoring

	ArrayList<DealDTO> filteredDeals = new ArrayList<>();
	ListView dealsListView;
	String name;
	public static final String ACCOUNT_ID = "ACCOUNT_ID";
	public static final String NEED_TO_UPDATE = "NEED_TO_UPDATE";
	public static final int DEALS_FORR_ACC_ACTIVITY_CODE = 202;
	private DealsListAdapter mAdapter;

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuItem mi = menu.add(0, R.id.delete_acc_btn, 0, R.string.delete_acc_label);
		//mi.setIcon(R.drawable._ic_btn_delete_normal);
		mi.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
		mi = menu.add(0, R.id.add_new_deal, 0, getResources().getString(R.string.add_new_deal));
		mi.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
		//mi.setIcon(R.drawable.add_deal);

		ActionBar actionBar = getSupportActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);

		return super.onCreateOptionsMenu(menu);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (data != null) {
			if (requestCode == CreateDealActivity.CODE_FOR_CREATING_DEAL && data.getBooleanExtra(CreateDealActivity.DEAL_CREATED, false)) {
				mAdapter.notifyDataSetChanged();
				Intent intent = new Intent();
				intent.putExtra(NEED_TO_UPDATE, true);
				LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.delete_acc_btn:
				DBHelper dbh = new DBHelper(getApplicationContext());
				dbh.deleteAccFromBD(name);
				setResult(DEALS_FORR_ACC_ACTIVITY_CODE, new Intent().putExtra(NEED_TO_UPDATE, true));
				new SyncService(getApplicationContext()).deleteAcc(dbh.getAccIdByName(name).getAccountId());
				finish();
				break;
			case R.id.add_new_deal:
				Intent intent = new Intent(getApplicationContext(), CreateDealActivity.class);
				intent.putExtra(ACCOUNT_ID, name);
				startActivityForResult(intent, CreateDealActivity.CODE_FOR_CREATING_DEAL);
				break;
			default:
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
		if (TextUtils.isEmpty(name)) {
			name = "";
		}
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
		public void notifyDataSetChanged() {
			filteredDeals = new DBHelper(getContext()).getDealsByName(name);
			super.notifyDataSetChanged();
		}

		@Override
		public int getCount() {
			return filteredDeals.size();
		}

		@Nullable
		@Override
		public DealDTO getItem(int position) {
			return filteredDeals.get(position);
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
/*
	void prefs() {
		SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sharedPref.edit();
		editor.putInt(getString(R.string.saved_high_score), newHighScore);
		editor.commit();

		SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
		int defaultValue = getResources().getInteger(R.string.saved_high_score_default);
		long highScore = sharedPref.getInt(getString(R.string.saved_high_score), defaultValue);

	}

	void file() {
		String filename = "myfile";
		String string = "Hello world!";
		FileOutputStream outputStream;

		try {
			outputStream = openFileOutput(filename, Context.MODE_PRIVATE);
			outputStream.write(string.getBytes());
			outputStream.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		public File getTempFile(Context context, String url) {
			File file;
			File file = new File(context.getFilesDir(), filename);
			try {
				String fileName = Uri.parse(url).getLastPathSegment();
				file = File.createTempFile(fileName, null, context.getCacheDir());
			} catch (IOException e) {
				// Error while creating file
			}
			return file;
		}
	}*/
}
