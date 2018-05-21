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
import android.widget.Button;
import android.widget.EditText;

import com.example.ihortovpinets.myaccounts.Entity.Account;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.BufferedSink;

import static com.example.ihortovpinets.myaccounts.DealsForAccountActivity.NEED_TO_UPDATE;

/**
 * Created by itovp on 22.05.2017.
 */

public class AccountsFragment extends Fragment implements DialogInterface.OnDismissListener, AdapterView.OnItemLongClickListener, AccountListAdapter.OnItemClickListener, AccountListAdapter.OnItemLongClickListener {

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

    private void sendRequest(View view) {
        OkHttpClient client = new OkHttpClient();
        DealsService.address = ((EditText) view.findViewById(R.id.adress)).getText().toString();
        final Request request = new Request.Builder()
                .url(DealsService.address + "/getAccList").method("POST", new RequestBody() {
                    @Override
                    public MediaType contentType() {
                        return MediaType.parse("application/json");
                    }

                    @Override
                    public void writeTo(BufferedSink sink) throws IOException {

                    }
                })
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                System.out.println("ressult :  fail" + request.toString() + "\n");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    throw new IOException("Unexpected code " + response);
                } else {
                    String str = response.body().string();
                    final ArrayList<Account> myAccounts = new ArrayList<Account>();
                    try {
                        JSONArray jArray = new JSONArray(str);
                        for (int i = 0; i < jArray.length(); i++) {
                            JSONObject obj = jArray.optJSONObject(i);
                            Account acc = new Account(obj.getString("id"), obj.getString("name"), obj.getDouble("deposit"), obj.getString("description"), obj.getBoolean("isOuter"));
                            myAccounts.add(acc);

                            System.out.println("ressult : succ" + acc.toString());
                        }

                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                new DBHelper(getActivity()).updateAccountsWithServer(myAccounts);
                                mAdapter.setAccounts(myAccounts);
                            }
                        });
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        final View view = inflater.inflate(R.layout.main_page_accounts_tab, null);
        setHasOptionsMenu(true);
        ((Button) view.findViewById(R.id.butn)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendRequest(view);
            }
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
