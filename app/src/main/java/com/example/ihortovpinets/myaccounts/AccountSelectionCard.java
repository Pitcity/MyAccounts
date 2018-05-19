package com.example.ihortovpinets.myaccounts;

import android.content.Context;
import android.database.DataSetObserver;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import com.example.ihortovpinets.myaccounts.Entity.Account;

import java.util.List;

/**
 * Created by itovp on 01.06.2017.
 */

public class AccountSelectionCard extends RelativeLayout {

	private final List<Account> mAccounts;
	private CardHolder mCardHolder;
	LayoutInflater inflater;

	public AccountSelectionCard(Context context, View cardView, List<Account> accounts, int resIdForShortLabel) {
		super(context);
		inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.account_choosing_card, this);
		mAccounts = accounts;
	}

	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		mCardHolder = new CardHolder((Spinner) this.findViewById(R.id.card_account_spinner),
				(EditText) this.findViewById(R.id.card_another_account_name),
				(TextView) this.findViewById(R.id.card_account_short_label));

		initialize();
	}

	private void initialize() {

		mCardHolder.accountsSpinner.setAdapter(getSpinnerAdapter(getContext()));
		mCardHolder.shortLabel.setText(getResources().getString(R.string.short_mark_for_buyer));
		//spinnerAdapter.addAll((Collection<? extends String>) mAccounts.stream().map(a->a.getName())); //// TODO: 08.06.2017 Replace with java8 stream

	}

	public SpinnerAdapter getSpinnerAdapter(final Context context) {

		final SpinnerAdapter spinnerAdapter = new SpinnerAdapter() {

			@Override
			public View getDropDownView(int position, View convertView, ViewGroup parent) {
				return LayoutInflater.from(context).inflate(android.R.layout.simple_spinner_dropdown_item, null);
			}

			@Override
			public void registerDataSetObserver(DataSetObserver observer) {

			}

			@Override
			public void unregisterDataSetObserver(DataSetObserver observer) {

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
//				//convertView = LayoutInflater.from(context).inflate(R.layout.dropdond_card_item, null);
//				TextView accountName = (TextView) convertView.findViewById(R.id.dropdown_acc_name);
//				TextView accountDeposit = (TextView) convertView.findViewById(R.id.dropdown_acc_deposit);
//				Account currentAcc = (Account) getItem(position);
//				accountName.setText(currentAcc.getName());
//				accountDeposit.setText(currentAcc.isOuter ? getResources().getString(R.string.card_undefined_value) : Double.toString(currentAcc.getDeposit()));
				return convertView;
			}

			@Override
			public int getItemViewType(int position) {
				return 0;
			}

			@Override
			public int getViewTypeCount() {
				return 0;
			}

			@Override
			public boolean isEmpty() {
				return false;
			}
		};
		return spinnerAdapter;
	}

	private class CardHolder {

		private Spinner accountsSpinner;
		private EditText nameForAnother;
		private TextView shortLabel;

		CardHolder(Spinner accountsSpinner, EditText nameForAnother, TextView shortLabel) {
			this.accountsSpinner = accountsSpinner;
			this.nameForAnother = nameForAnother;
			nameForAnother.setVisibility(GONE);
			this.shortLabel = shortLabel;
		}
	}
}
