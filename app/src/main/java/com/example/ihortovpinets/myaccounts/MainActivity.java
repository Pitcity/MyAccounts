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

    ArrayList<Account> accounts = new ArrayList<Account>();
    ArrayList<Deal> deals = new ArrayList<Deal>();
    ListView accsListView, dealsListView;
    EditText accName, accMoney, accDescr;

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        accName = (EditText) findViewById(R.id.txtName);
        accMoney = (EditText) findViewById(R.id.txtMoney);
        accDescr = (EditText) findViewById(R.id.txtDescr);
        accsListView = (ListView) findViewById(R.id.listView);

        dealsListView = (ListView) findViewById(R.id.listView_seeDeals);

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

        tabSpec = tabHost.newTabSpec("AddDeal");
        tabSpec.setContent(R.id.tabDealler);
        tabSpec.setIndicator("AddDeal");
        tabHost.addTab(tabSpec);


        deals.add(new Deal(new Account("Ihor",2000,"lalal"),new Account("Ihor2",2000,"lalal"),new String("deal1")));
        deals.add(new Deal(new Account("Ihor3",2000,"lalal"),new Account("Ihor23",2000,"lalal"),new String("deal1")));
        deals.add(new Deal(new Account("Ihor4",2000,"lalal"),new Account("Ihor24",2000,"lalal"),new String("deal1")));


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
                    populateListofDeals();
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



        Button btnAddDeal = (Button) findViewById(R.id.btnAddDeal); //adding new Deal (doesnt work yet)
        btnAddDeal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, CreateDealActivity.class);
                intent.putExtra("Accounts", accounts);

                startActivity(intent);
            }

        });


    }
    private void populateList () {
        ArrayAdapter<Account> adapter = new AccountsListAdapter();
        accsListView.setAdapter(adapter);
    }

    private void populateListofDeals () {
        ArrayAdapter<Deal> adapter = new DealsListAdapter();
        dealsListView.setAdapter(adapter);
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

            final TextView name = (TextView) view.findViewById(R.id.listView_name);
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
                            intent.putExtra("Name", name.getText().toString());
                            intent.putExtra("Deals", deals);
                            startActivity(intent);
                        }
                    }
            );
            return view;
        }

    }


    class DealsListAdapter extends ArrayAdapter<Deal> {
        public DealsListAdapter() {super(MainActivity.this, R.layout.listview_deals, deals);
        }

        @Override
        public View getView(int position, View view, ViewGroup parent) {
            if (view ==null)
                view = getLayoutInflater().inflate(R.layout.listview_deals,parent,false);

            Deal currentDeal = deals.get(position);

            TextView date = (TextView) view.findViewById(R.id.viewDeals_Date);
            TextView sum = (TextView) view.findViewById(R.id.viewDeals_sum);
            TextView seller = (TextView) view.findViewById(R.id.viewDeals_seller);
            TextView buyer = (TextView) view.findViewById(R.id.viewDeals_buyer);

            date.setText(currentDeal.getDate());
            sum.setText(Double.toString(currentDeal.getSum()));
            seller.setText(currentDeal.getSeller().getName());
            buyer.setText(currentDeal.getBuyer().getName());
            /*view.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(MainActivity.this, Deals_for_acc.class);
                            intent.putExtra("Name", name.getText().toString());
                            startActivity(intent);
                        }
                    }
            );*/
            return view;
        }

    }

}
