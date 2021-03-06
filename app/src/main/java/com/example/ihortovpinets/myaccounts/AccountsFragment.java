package com.example.ihortovpinets.myaccounts;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.example.ihortovpinets.myaccounts.Adapters.AccountListAdapter;
import com.example.ihortovpinets.myaccounts.Service.SyncService;

import static com.example.ihortovpinets.myaccounts.DealsForAccountActivity.NEED_TO_UPDATE;

/**
 * Created by itovp on 22.05.2017.
 */

public class AccountsFragment extends Fragment implements DialogInterface.OnDismissListener, AdapterView.OnItemLongClickListener, AccountListAdapter.OnItemClickListener, AccountListAdapter.OnItemLongClickListener, SyncService.RequestListener {

    public static final int ACCOUNTS_FRAGMENT_ID = R.id.accounts_frg_id;
    private static final java.lang.String CREATE_ACC_DIALOG = "CREATE_ACC_DIALOG";
    private static final java.lang.String EDITING_ACC_DIALOG = "EDITING_ACC_DIALOG";
    private static final java.lang.String ACCOUNT_ID = "ACCOUNT_ID";
    private RecyclerView mAccountsList;
    private AccountListAdapter mAdapter;
    RecyclerView.LayoutManager mLayoutManager;

    @Override
    public void onDismiss(DialogInterface dialog) {
        mAdapter.update();
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        getActivity().getActionBar().setHomeButtonEnabled(true);
        return true;
    }

    @Override
    public void onItemClick(int position) {
        Intent intent = new Intent(getContext(), DealsForAccountActivity.class);
        intent.putExtra(ACCOUNT_ID, mAdapter.getItem(position).getName());
        startActivityForResult(intent, DealsForAccountActivity.DEALS_FORR_ACC_ACTIVITY_CODE);
    }

    @Override
    public void onItemLongClick(int position) {
        //removeAccount
    }

    @Override
    public void onResponse() {
        getActivity().runOnUiThread(()-> mAdapter.update());
    }

    @Override
    public void onFailure() {

    }

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
            mAdapter.update();
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
                    || (resultCode == CreateAccountDialog.CODE_FOR_CREATING_ACCOUNT && data.getBooleanExtra(CreateAccountDialog.IS_ACC_CREATED, false))) {
                mAdapter.notifyDataSetChanged();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.create_new_account:
                CreateAccountDialog cad = new CreateAccountDialog();
                cad.show(getActivity().getFragmentManager(), CREATE_ACC_DIALOG);
                cad.setOnDismissListener(this);
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
        final View view = inflater.inflate(R.layout.main_page_accounts_tab, null);
        setHasOptionsMenu(true);
        (view.findViewById(R.id.butn)).setOnClickListener(v -> {
			new DBHelper(getContext()).clearDb();
			new SyncService(getActivity()).requestAccounts(view, this);
		});
        mLayoutManager = new LinearLayoutManager(getContext());
        mAccountsList = (RecyclerView) view.findViewById(R.id.list_of_accounts);
        mAdapter = new AccountListAdapter(getContext());
        mAdapter.setOnItemClickListener(this);
        mAdapter.setOnItemLongClickListener(this);
        mAccountsList.setLayoutManager(mLayoutManager);
        mAccountsList.setAdapter(mAdapter);
        return view;
    }

}
