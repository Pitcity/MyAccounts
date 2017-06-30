package com.example.ihortovpinets.myaccounts;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
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

import static com.example.ihortovpinets.myaccounts.DealsForAccountActivity.ACCOUNT_ID;
import static com.example.ihortovpinets.myaccounts.DealsForAccountActivity.NEED_TO_UPDATE;

/**
 * Created by itovp on 22.05.2017.
 */

public class AccountsFragment extends Fragment implements AdapterView.OnItemClickListener {

	public static final int ACCOUNTS_FRAGMENT_ID = R.id.accounts_frg_id;
	private ListView mListView;
	private AccountsListAdapter mAdapter;

	public class UpdateReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getBooleanExtra(NEED_TO_UPDATE, false)) {
				mAdapter.notifyDataSetChanged();
			}
		}
	}

	@Override
	public void onResume() {
		if (mAdapter != null) {
			mAdapter.notifyDataSetChanged();
		}
		super.onResume();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		LocalBroadcastManager.getInstance(getActivity().getApplicationContext()).registerReceiver(new UpdateReceiver(), new IntentFilter("data-loaded"));
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
			if ((requestCode == CreateDealActivity.CODE_FOR_CREATING_DEAL && data.getBooleanExtra(CreateDealActivity.DEAL_CREATED, false))
					|| (resultCode == DealsForAccountActivity.DEALS_FORR_ACC_ACTIVITY_CODE && data.getBooleanExtra(DealsForAccountActivity.NEED_TO_UPDATE, false))
					|| (resultCode == CreateAccountActivity.CODE_FOR_CREATING_ACCOUNT && data.getBooleanExtra(CreateAccountActivity.IS_ACC_CREATED, false))) {
				mAdapter.notifyDataSetChanged();
			}
			/*switch (requestCode) {
				case CreateDealActivity.CODE_FOR_CREATING_DEAL:
					if (data.getBooleanExtra(CreateDealActivity.DEAL_CREATED, false)) {
						mAdapter.notifyDataSetChanged();
					}
					break;
				case DealsForAccountActivity.DEALS_FORR_ACC_ACTIVITY_CODE:
					if (data.getBooleanExtra(DealsForAccountActivity.NEED_TO_UPDATE, false)) {
						mAdapter.notifyDataSetChanged();
					}
					break;
				case CreateAccountActivity.CODE_FOR_CREATING_ACCOUNT:
					if (data.getBooleanExtra(CreateAccountActivity.IS_ACC_CREATED, false)) {
						mAdapter.notifyDataSetChanged();
					}
					break;
			}*/
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.create_new_account:
				startActivityForResult(new Intent(getContext(), CreateAccountActivity.class), CreateAccountActivity.CODE_FOR_CREATING_ACCOUNT);
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
		mListView.setOnItemClickListener(this);
		mAdapter = new AccountsListAdapter(new DBHelper(getActivity()).getAccListFromDB());
		mListView.setAdapter(mAdapter);

		return view;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		Intent intent = new Intent(getContext(), DealsForAccountActivity.class);
		intent.putExtra(ACCOUNT_ID, mAdapter.getItem(position).getName());
		startActivityForResult(intent, DealsForAccountActivity.DEALS_FORR_ACC_ACTIVITY_CODE);
	}

	private class AccountsListAdapter extends ArrayAdapter<Account> {

		private ArrayList<Account> myAccounts;

		AccountsListAdapter(ArrayList<Account> myAccounts) {
			super(getActivity(), R.layout.account_item_row, myAccounts);
			this.myAccounts = myAccounts;
		}

		@Override
		public int getCount() {
			return myAccounts.size();
		}

		@Nullable
		@Override
		public Account getItem(int position) {
			return myAccounts.get(position);
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
