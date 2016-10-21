package com.example.ihortovpinets.myaccounts;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    ArrayList<Account> myAccounts = new ArrayList<Account>();
    //ArrayList<Account> allAccounts = new ArrayList<Account>();
    ArrayList<Deal> deals = new ArrayList<Deal>();
    ListView accsListView, dealsListView;
    EditText accName, accMoney, accDescr;

    public void addDeal(Deal d) {
        if (d!=null) deals.add(d);
    }

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

        final Button btnCrAcc = (Button) findViewById(R.id.btnCreateAccount);
        btnCrAcc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                boolean isExist = false;
                for (Account o : myAccounts)
                    if (o!=null && o.getName().equals(accName.getText().toString()))
                        isExist=true;
                if (isExist)
                    Toast.makeText(getApplicationContext(),"Account with this name already exists", Toast.LENGTH_LONG).show();
                else {
                    addAccount(accName.getText().toString(), Double.valueOf(accMoney.getText().toString()), accDescr.getText().toString(),false);
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

        Button btnAddDeal = (Button) findViewById(R.id.btnAddDeal);
        btnAddDeal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, CreateDealActivity.class);
                intent.putExtra("Accounts", myAccounts);// скопіювалось:? походу так
                startActivityForResult(intent, 221);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==221&&data!=null) {
            Account buyer = null, seller = null;
            Deal deal1 = null;
            if (data.getSerializableExtra("NewDeal")!=null)
                deal1 =(Deal)data.getSerializableExtra("NewDeal");
            try {
                if(!myAccounts.contains(deal1.getBuyer()))
                    buyer = new Account(deal1.getBuyer().getName(),true);
                if(!myAccounts.contains(deal1.getSeller()))
                    seller = new Account(deal1.getSeller().getName(),true);
                for (Account a : myAccounts) {
                    if (a.getName().equals(deal1.getBuyer().getName()))
                        buyer = a;
                    if (a.getName().equals(deal1.getSeller().getName()))
                        seller = a;
                }
                Deal newDeal1 = Deal.createDeal(buyer, seller, deal1.getNote(), deal1.getSum(), deal1.getDate());
                if (newDeal1 == null) { //if deal wasn't created, then returned null
                    Toast.makeText(getApplicationContext(), "Impossible transaction (not enough money)", Toast.LENGTH_LONG).show();
                    return;
                }
                addDeal(newDeal1);
            } catch (NullPointerException np) {
                Toast.makeText(getApplicationContext(), "Something went wrong while creating new deal", Toast.LENGTH_LONG).show();
                return;
            }
            populateList();
            populateListofDeals();
        }
    }

    private void populateList () {
        ArrayAdapter<Account> adapter = new AccountsListAdapter();
        accsListView.setAdapter(adapter);
    }

    private void populateListofDeals () {
        ArrayAdapter<Deal> adapter = new DealsListAdapter();
        dealsListView.setAdapter(adapter);
    }

    private void addAccount(String name, double money, String descr,boolean flag) {
        myAccounts.add(new Account(name,money,descr,flag));
    }

    private class AccountsListAdapter extends ArrayAdapter<Account> {
        public AccountsListAdapter() {
            super(MainActivity.this, R.layout.listview_item, myAccounts);
        }

        @Override
        public View getView(int position, View view, ViewGroup parent) {
            if (view ==null)
                view = getLayoutInflater().inflate(R.layout.listview_item,parent,false);

            Account currentAccount = myAccounts.get(position);

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
            TextView descr = (TextView) view.findViewById(R.id.viewDeals_descr);

            date.setText(currentDeal.getDate());
            sum.setText(Double.toString(currentDeal.getSum()));
            seller.setText(currentDeal.getSeller().getName());
            buyer.setText(currentDeal.getBuyer().getName());
            descr.setText(currentDeal.getNote());
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
