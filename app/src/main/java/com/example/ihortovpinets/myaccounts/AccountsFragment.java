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
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.squareup.okhttp.Callback;
import com.squareup.okhttp.HttpUrl;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okio.BufferedSink;

import static com.example.ihortovpinets.myaccounts.DealsForAccountActivity.NEED_TO_UPDATE;

/**
 * Created by itovp on 22.05.2017.
 */

public class AccountsFragment extends Fragment implements AdapterView.OnItemClickListener, DialogInterface.OnDismissListener, AdapterView.OnItemLongClickListener {

    public static final int ACCOUNTS_FRAGMENT_ID = R.id.accounts_frg_id;
    private static final java.lang.String CREATE_ACC_DIALOG = "CREATE_ACC_DIALOG";
    private static final java.lang.String EDITING_ACC_DIALOG = "EDITING_ACC_DIALOG";
    private static final java.lang.String ACCOUNT_ID = "ACCOUNT_ID";
    private ListView mListView;
    private AccountsListAdapter mAdapter;

    @Override
    public void onDismiss(DialogInterface dialog) {
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        getActivity().getActionBar().setHomeButtonEnabled(true);
        //getActivity().getActionBar().menu(R.menu.edit_account);
        // getActivity().getMenuInflater().inflate();
        //EditAccountDialog ead = new EditAccountDialog();
        //ead.setOnDismissListener(this);
        //Bundle bdl = new Bundle();
        // bdl.putString(ACCOUNT_ID, mAdapter.getItem(position).getAccountId());
        // ead.setArguments(bdl);
        // ead.show(getActivity().getFragmentManager(), EDITING_ACC_DIALOG);
        return true;
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
        Request request = new Request.Builder()
                // .url("http://192.168.0.101/getAccList")
                .url(((EditText) view.findViewById(R.id.adress)).getText() + "/getAccList").method("POST", new RequestBody() {
                    @Override
                    public MediaType contentType() {
                        return MediaType.parse("application/json");
                    }

                    @Override
                    public void writeTo(BufferedSink sink) throws IOException {

                    }
                })
                .build();

        HttpUrl.Builder urlBuilder = HttpUrl.parse("https://api.github.help").newBuilder();
        urlBuilder.addQueryParameter("v", "1.0");
        urlBuilder.addQueryParameter("user", "vogella");
        String url = urlBuilder.build().toString();

        Request request2 = new Request.Builder()
                .url(url)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                //setFailure(e);
                e.printStackTrace();
                System.out.println("ressult :  fail" + request.toString());
            }

            @Override
            public void onResponse(Response response) throws IOException {
                if (!response.isSuccessful()) {
                    throw new IOException("Unexpected code " + response);
                } else {
                    //setSuccess(response);
                    String str = response.body().string();
                    ArrayList<Account> myAccounts = new ArrayList<Account>();
                    try {
                        JSONArray jArray = new JSONArray(str);
                        for (int i = 0; i < jArray.length(); i++) {
                            JSONObject obj = jArray.optJSONObject(i);
                            Account acc = new Account(obj.getString("id"), obj.getString("name"), obj.getDouble("deposit"), obj.getString("description"), obj.getBoolean("isOuter"));
                            myAccounts.add(acc);

                            System.out.println("ressult : succ" + acc.toString());
                        }
                        mAdapter = new AccountsListAdapter(myAccounts);

                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mListView.setAdapter(mAdapter);
                                mListView.invalidate();
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
        mListView = (ListView) view.findViewById(R.id.list_of_accounts);
        mListView.setOnItemClickListener(this);
        mAdapter = new AccountsListAdapter(new DBHelper(getActivity()).getAccListFromDB());
        mListView.setAdapter(mAdapter);
        mListView.setOnItemLongClickListener(this);
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
