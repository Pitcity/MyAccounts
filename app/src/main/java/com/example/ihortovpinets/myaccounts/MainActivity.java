package com.example.ihortovpinets.myaccounts;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    List<Account> accounts = new ArrayList<Account>();
    ListView accsListView;
    EditText accName, accMoney, accDescr;

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        accName = (EditText) findViewById(R.id.txtName);
        accMoney = (EditText) findViewById(R.id.txtMoney);
        accDescr = (EditText) findViewById(R.id.txtDescr);
        accsListView = (ListView) findViewById(R.id.listView);

        TabHost tabHost = (TabHost) findViewById(R.id.tabHost);
        tabHost.setup();

        TabHost.TabSpec tabSpec = tabHost.newTabSpec("creator");
        tabSpec.setContent(R.id.tabCreator);
        tabSpec.setIndicator("Creator");
        tabHost.addTab(tabSpec);

        tabSpec = tabHost.newTabSpec("storeAccs");
        tabSpec.setContent(R.id.tabStoreList);
        tabSpec.setIndicator("Accounts");
        tabHost.addTab(tabSpec);


        final Button btnCrAcc = (Button) findViewById(R.id.btnCreateAccount);
        btnCrAcc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                boolean isExist = false;
                for (Account o : accounts) {
                    if (o!=null && o.getName().equals(accName.getText().toString())) isExist=true;
                }
                if (isExist) {
                    Toast.makeText(getApplicationContext(),"Account with this name already exists", Toast.LENGTH_LONG).show();
                }
                else {
                    addAccount(accName.getText().toString(), Double.valueOf(accMoney.getText().toString()), accDescr.getText().toString());
                    populateList();
                    Toast.makeText(getApplicationContext(), accName.getText().toString() + " has been added to your Accounts", Toast.LENGTH_SHORT).show();
                }
            }

        });
        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                btnCrAcc.setEnabled(!accName.getText().toString().trim().isEmpty()&&!accMoney.getText().toString().trim().isEmpty());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        };

        accName.addTextChangedListener(textWatcher);
        accMoney.addTextChangedListener(textWatcher);
    }
    private void populateList () {
        ArrayAdapter<Account> adapter = new AccountsListAdapter();
        accsListView.setAdapter(adapter);
    }

    private void addAccount(String name, double money, String descr) {
        accounts.add(new Account(name,money,descr));
    }

    private class AccountsListAdapter extends ArrayAdapter<Account> {
        public AccountsListAdapter() {
            super(MainActivity.this, R.layout.listview_item, accounts);
        }

        @Override
        public View getView(int position, View view, ViewGroup parent) {
            if (view ==null)
                view = getLayoutInflater().inflate(R.layout.listview_item,parent,false);

            Account currentAccount = accounts.get(position);

            TextView name = (TextView) view.findViewById(R.id.listView_name);
            TextView money = (TextView) view.findViewById(R.id.listView_money);
            TextView descr = (TextView) view.findViewById(R.id.listView_descr);
            name.setText(currentAccount.getName());
            money.setText(Double.toString(currentAccount.getDeposit()));
            descr.setText(currentAccount.getDescription());
            view.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(MainActivity.this, Deals_for_acc.class);
                            startActivity(intent);
                        }
                    }
            );
            return view;
        }

    }
}
