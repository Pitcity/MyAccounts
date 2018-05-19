package com.example.ihortovpinets.myaccounts;

import android.content.Context;
import android.content.Intent;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ihortovpinets.myaccounts.Entity.Account;
import com.example.ihortovpinets.myaccounts.Entity.Deal;

import java.util.ArrayList;

import static android.view.View.GONE;

/**
 * Created by IhorTovpinets on 15.09.2016.
 */
public class CreateDealActivity extends AppCompatActivity {

	public static final String DEAL_CREATED = "DEAL_CREATED";
	public static final int CODE_FOR_CREATING_DEAL = 200;
	public static final String ACCOUNT_ID = "acc_id";

	ArrayList<Account> mAccounts;
	String mName;
	Spinner mSpinnerSeller, mSpinnerBuyer;
	EditText mExternalSeller, mExternalBuyer, mDealSum, mDealDescr;
	private TextView mBuyerDeposit, mSellerDeposit;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mName = getIntent().getStringExtra(ACCOUNT_ID);
		setContentView(R.layout.activity_create_deal);
		mSpinnerSeller = (Spinner) findViewById(R.id.dealSeller);
		mSpinnerBuyer = (Spinner) findViewById(R.id.dealBuyer);
		mExternalBuyer = (EditText) findViewById(R.id.dealAdditionBuyer);
		mExternalSeller = (EditText) findViewById(R.id.dealAdditionSeller);
		mDealSum = (EditText) findViewById(R.id.dealSum);
		mDealDescr = (EditText) findViewById(R.id.dealDescr);
		mBuyerDeposit = (TextView) findViewById(R.id.dropdown_buyer_acc_deposit);
		mSellerDeposit = (TextView) findViewById(R.id.dropdown_seller_acc_deposit);
		SpinnerAdapter spinnerAdapter = getSpinnerAdapter(getApplicationContext());
		mAccounts = new DBHelper(this).getAccListFromDB();
		mSpinnerSeller.setAdapter(spinnerAdapter);
		mSpinnerBuyer.setAdapter(spinnerAdapter);
		if (!TextUtils.isEmpty(mName)) {
			setSelectionOnSpinners();
		}
	}

	@Override
	public View onCreateView(String name, Context context, AttributeSet attrs) {
		return super.onCreateView(name, context, attrs);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuItem mi = menu.add(0, R.id.save, 0, getResources().getString(R.string.create_deal_save));
		mi.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
		//mi.setIcon(R.drawable._ic_btn_save_xml);
		ActionBar actionBar = getSupportActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);

		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.save:
				if (validateDataForDeal()) {
					Toast.makeText(getApplicationContext(), getResources().getString(R.string.deal_created), Toast.LENGTH_LONG).show();
					setResult(CODE_FOR_CREATING_DEAL, new Intent().putExtra(DEAL_CREATED, true));
					finish();
				}
				break;
			case android.R.id.home:
				NavUtils.navigateUpFromSameTask(this);
		}
		return super.onOptionsItemSelected(item);
	}

	private Account getAccFromSpin(Spinner sp, EditText et) {
		return sp.getSelectedItemPosition() == mAccounts.size()
				? TextUtils.isEmpty(et.getText())
				? null
				: new Account(et.getText().toString(), true)
				: (Account) sp.getAdapter().getItem(sp.getSelectedItemPosition());
	}

	public void setSelectionOnSpinners() {
		int indexForSelection = -1;
		for (Account acc : mAccounts) {
			indexForSelection++;
			if (acc.getName().equals(mName)) {
				break;
			}
		}
		if (indexForSelection >= 0 && indexForSelection < mAccounts.size()) {
			mSpinnerBuyer.setSelection(indexForSelection);
			mSpinnerSeller.setSelection(indexForSelection);
		}
	}

	private boolean validateDataForDeal() {
		Account buyer, seller;
		if (mSpinnerBuyer.getSelectedItemPosition() == mSpinnerSeller.getSelectedItemPosition()) {
			Toast.makeText(getApplicationContext(), getResources().getString(R.string.impossible_transaction_different_accounts), Toast.LENGTH_LONG).show();
			return false;
		} else {
			seller = getAccFromSpin(mSpinnerSeller, mExternalSeller);
			buyer = getAccFromSpin(mSpinnerBuyer, mExternalBuyer);
			if (seller == null || buyer == null) {
				Toast.makeText(getApplicationContext(), getResources().getString(R.string.impossible_transaction_external_name), Toast.LENGTH_LONG).show();
				return false;
			}

			if (TextUtils.isEmpty(mDealSum.getText())) {
				Toast.makeText(getApplicationContext(), getResources().getString(R.string.impossible_transaction_positive_money), Toast.LENGTH_LONG).show();
				return false;
			}
			double sum = Double.valueOf(mDealSum.getText().toString());
			String note = mDealDescr.getText().toString();

			Deal newDeal = Deal.createDeal(buyer, seller, note, sum, new java.util.Date().getTime()); //// TODO: 30.05.2017 rewrite method
			if (newDeal == null) {
				Toast.makeText(getApplicationContext(), getResources().getString(R.string.impossible_transaction_not_enough_money), Toast.LENGTH_LONG).show();
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

	private void checkAnotherAcc() {
		mExternalBuyer.setVisibility(mSpinnerBuyer.getSelectedItemPosition() == mAccounts.size() ? View.VISIBLE : GONE);
		mExternalSeller.setVisibility(mSpinnerSeller.getSelectedItemPosition() == mAccounts.size() ? View.VISIBLE : GONE);
		mSellerDeposit.setText(mSpinnerSeller.getSelectedItemPosition() == mAccounts.size() ? getResources().getString(R.string.card_undefined_value) : Double.valueOf(((Account) mSpinnerSeller.getAdapter().getItem(mSpinnerSeller.getSelectedItemPosition())).getDeposit()).toString());
		mBuyerDeposit.setText(mSpinnerBuyer.getSelectedItemPosition() == mAccounts.size() ? getResources().getString(R.string.card_undefined_value) : Double.valueOf(((Account) mSpinnerBuyer.getAdapter().getItem(mSpinnerBuyer.getSelectedItemPosition())).getDeposit()).toString());
	}

	public SpinnerAdapter getSpinnerAdapter(final Context context) {

		final SpinnerAdapter spinnerAdapter = new SpinnerAdapter() {

			@Override
			public View getDropDownView(int position, View convertView, ViewGroup parent) {
				convertView = LayoutInflater.from(context).inflate(R.layout.dropdond_card_item, null);
				TextView accountName = (TextView) convertView.findViewById(R.id.dropdown_acc_name);
				TextView accountDeposit = (TextView) convertView.findViewById(R.id.dropdown_acc_deposit);
				Account currentAcc = (Account) getItem(position);
				accountName.setText(currentAcc.getName());
				accountDeposit.setText(currentAcc.isOuter() ? getResources().getString(R.string.card_undefined_value) : Double.toString(currentAcc.getDeposit()));
				return convertView;
			}

			@Override
			public void registerDataSetObserver(DataSetObserver observer) {
			}

			@Override
			public void unregisterDataSetObserver(DataSetObserver observer) {
				checkAnotherAcc();
			}

			@Override
			public int getCount() {
				return mAccounts.size() + 1;
			}

			@Override
			public Object getItem(int position) {
				if (position > mAccounts.size() - 1) {
					return new Account(getResources().getString(R.string.create_deal_external_acc), true);
				} else
					return mAccounts.get(position);
			}

			@Override
			public long getItemId(int position) {
				return 0;
			}

			@Override
			public boolean hasStableIds() {
				return false;
			}

			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				convertView = LayoutInflater.from(context).inflate(R.layout.dropdond_card_item, null);
				TextView accountName = (TextView) convertView.findViewById(R.id.dropdown_acc_name);
				TextView accountDeposit = (TextView) convertView.findViewById(R.id.dropdown_acc_deposit);
				convertView.findViewById(R.id.separate_line).setVisibility(GONE);
				Account currentAcc = (Account) getItem(position);
				accountName.setText(currentAcc.getName());
				accountDeposit.setVisibility(GONE);
				//accountDeposit.setText(currentAcc.isOuter ? getResources().getString(R.string.card_undefined_value) : Double.toString(currentAcc.getDeposit()));
				return convertView;
			}

			@Override
			public int getItemViewType(int position) {
				return 1;
			}

			@Override
			public int getViewTypeCount() {
				return 1;
			}

			@Override
			public boolean isEmpty() {
				return false;
			}
		};
		return spinnerAdapter;
	}

}
