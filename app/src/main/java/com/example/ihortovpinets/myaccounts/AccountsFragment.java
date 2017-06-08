package com.example.ihortovpinets.myaccounts;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by itovp on 22.05.2017.
 */

public class AccountsFragment extends Fragment implements AdapterView.OnItemClickListener {

	public static final int ACCOUNTS_FRAGMENT_ID = R.id.accounts_frg_id;
	private ListView mListView;
	private AccountsListAdapter mAdapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.main_menu, menu);
		super.onCreateOptionsMenu(menu, inflater);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (data != null) {
			switch (requestCode) {
				case CreateDealActivity.CODE_FOR_CREATING_DEAL:
					if (data.getBooleanExtra(CreateDealActivity.DEAL_CREATED, false)) {
						mAdapter.notifyDataSetChanged();
					}
					break;
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.create_new_account:

				break;

			case R.id.add_new_deal:
				startActivityForResult(new Intent(getContext(), CreateDealActivity.class), CreateDealActivity.CODE_FOR_CREATING_DEAL);
				break;
		}
		return super.onOptionsItemSelected(item);
	}

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		View view = inflater.inflate(R.layout.main_page_accounts_tab, null);
		setHasOptionsMenu(true);
		mListView = (ListView) view.findViewById(R.id.list_of_accounts);
		mAdapter = new AccountsListAdapter(new DBHelper(getActivity()).getAccListFromDB());
		mListView.setAdapter(mAdapter);
		mListView.setOnItemClickListener(this);

		return view;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		//Intent intent = new Intent(getContext(), DealsFragment.class); // TODO: 24.05.2017 deals frg
		//intent.putExtra("Name", view.getTag(R.id.account_name_key).toString());
	}

	private class AccountsListAdapter extends ArrayAdapter<Account> {

		private ArrayList<Account> myAccounts;

		AccountsListAdapter(ArrayList<Account> myAccounts) {
			super(getActivity(), R.layout.account_item_row, myAccounts);
			this.myAccounts = myAccounts;
		}

		@Override
		public View getView(int position, View view, ViewGroup parent) {
			ViewHolder vh;
			if (view == null) {
				view = LayoutInflater.from(getContext()).inflate(R.layout.account_item_row, null);
				vh = new ViewHolder(view);
				view.setTag(vh);
			} else {
				vh = (ViewHolder) view.getTag();
			}

			Account currentAccount = myAccounts.get(position);

			vh.mName.setText(currentAccount.getName());
			vh.mMoney.setText(Double.toString(currentAccount.getDeposit()));
			vh.mDescription.setText(currentAccount.getDescription());
			view.setTag(R.id.account_name_key, currentAccount.getName());

			return view;
		}

		@Override
		public void notifyDataSetChanged() {
			myAccounts = new DBHelper(getActivity()).getAccListFromDB();
			super.notifyDataSetChanged();
		}
	}

	private class ViewHolder {

		TextView mName;
		TextView mMoney;
		TextView mDescription;

		ViewHolder(View view) {
			mName = (TextView) view.findViewById(R.id.account_item_name);
			mMoney = (TextView) view.findViewById(R.id.account_item_money);
			mDescription = (TextView) view.findViewById(R.id.account_item_desc);
		}
	}
}
