package com.example.ihortovpinets.myaccounts;

import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.TouchDelegate;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    ArrayList<Account> myAccounts = new ArrayList<Account>();
    ArrayList<Deal> deals = new ArrayList<Deal>();
    ListView accsListView, dealsListView;
    EditText accName, accMoney, accDescr;
    DBHelper dbh;
    PieChart moneyPerAcc_chart;
    BarChart chart_perAcc;
    Spinner accForChart_spn;

    public void addDeal(Deal d) {
        if (d != null) deals.add(d);
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
        dbh = new DBHelper(getApplicationContext());
        moneyPerAcc_chart = (PieChart) findViewById(R.id.barchar);
        chart_perAcc = (BarChart) findViewById(R.id.barchart_perAcc);
        accForChart_spn = (Spinner) findViewById(R.id.accForChart_spn);

        TabHost tabHost = (TabHost) findViewById(R.id.tabHost); //// TODO: 13.05.2017 fragment, viewPager 
        tabHost.setup();

        TabHost.TabSpec tabSpec = tabHost.newTabSpec("creator");//// TODO: 13.05.2017 string into constants 
        tabSpec.setContent(R.id.tabCreator);
        tabSpec.setIndicator("Creator");
        tabHost.addTab(tabSpec);
        tabSpec = tabHost.newTabSpec("storeAccs");
        tabSpec.setContent(R.id.tabStoreList);
        tabSpec.setIndicator("Accounts");
        tabHost.addTab(tabSpec);
        tabSpec = tabHost.newTabSpec("AddDeal");
        tabSpec.setContent(R.id.tabDealler);
        tabSpec.setIndicator("Stats");
        tabHost.addTab(tabSpec);
        final Button btnCrAcc = (Button) findViewById(R.id.btnCreateAccount);
        btnCrAcc.post(new Runnable() {
            @Override
            public void run() {
                Rect r = new Rect();
                btnCrAcc.getHitRect(r);
                r.bottom += 50;
                r.left -= 200;
                r.top = 0;
                ((View) btnCrAcc.getParent().getParent()).setTouchDelegate(new TouchDelegate(r, btnCrAcc));
            }
        });

        btnCrAcc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (myAccounts.contains(new Account(accName.getText().toString(), false)))
                    Toast.makeText(getApplicationContext(), "Account with this name already exists", Toast.LENGTH_LONG).show();
                else {
                    addAccount(accName.getText().toString(), Double.valueOf(accMoney.getText().toString()), accDescr.getText().toString(), false);
                    try {
                        dbh.addAccountToDB(new Account(accName.getText().toString(), Double.valueOf(accMoney.getText().toString()), accDescr.getText().toString(), false));
                    } catch (Exception e) {
                    }
                    populateList();
                    populateListofDeals();
                    Toast.makeText(getApplicationContext(), accName.getText().toString() + " has been added to your Accounts", Toast.LENGTH_SHORT).show();
                    updatePieChart(moneyPerAcc_chart);
                    updateSpinner(accForChart_spn);
                }
            }

        });
        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                btnCrAcc.setEnabled(!accName.getText().toString().trim().isEmpty() && !accMoney.getText().toString().trim().isEmpty());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        };
        accName.addTextChangedListener(textWatcher);
        accMoney.addTextChangedListener(textWatcher);

        accForChart_spn.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String statisticForAcc = accForChart_spn.getSelectedItem().toString();
                updateBarChart(dbh.getDealsByName("seller", statisticForAcc), dbh.getDealsByName("buyer", statisticForAcc));
                Toast.makeText(getApplicationContext(), "Are you sure you wanna see that? :D click here if so :P", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        Button btnAddDeal = (Button) findViewById(R.id.btnAddDeal);
        btnAddDeal.setAlpha(0.7f);
        btnAddDeal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, CreateDealActivity.class);
                intent.putExtra("Accounts", myAccounts);
                startActivityForResult(intent, 221);
            }
        });
        myAccounts = dbh.getAccListFromDB();
        deals = dbh.getDealListFromDB();
        updatePieChart(moneyPerAcc_chart);
        updateSpinner(accForChart_spn);
        populateList();
        populateListofDeals();
    }

    private void updateBarChart(ArrayList<DealDTO> dealsByNameAsSeller, ArrayList<DealDTO> dealsByNameAsBuyer) {
        ArrayList<BarEntry> entries = new ArrayList<>();
        ArrayList<String> labels = new ArrayList<>();
        int i = 0;
        for (DealDTO dd : dealsByNameAsBuyer) {
            entries.add(new BarEntry((float) (dd.getSum()), i));
            labels.add(dd.getDate());
            i++;
        }
        BarDataSet dataset = new BarDataSet(entries, "Витрати");
        dataset.setColors(ColorTemplate.COLORFUL_COLORS);
        BarData data = new BarData(labels, dataset);
        this.chart_perAcc.setData(data);
        this.chart_perAcc.getBarData();
    }

    private void updatePieChart(PieChart pc) {
        ArrayList<Entry> entries = new ArrayList<>();
        ArrayList<String> labels = new ArrayList<>();
        int i = 0;
        for (Account account : myAccounts) {
            entries.add(new Entry((float) account.getDeposit(), i));
            i++;
            labels.add(account.getName());
        }

        PieDataSet dataset = new PieDataSet(entries, "Accounts");
        dataset.setColors(ColorTemplate.COLORFUL_COLORS);
        PieData data = new PieData(labels, dataset);
        pc.setData(data);
    }

    private void updateSpinner(Spinner sp) {
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, android.R.id.text1);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp.setAdapter(spinnerAdapter);
        for (Account a : myAccounts) {
            spinnerAdapter.add(a.getName());
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 221 && data != null) {
            Account buyer = null, seller = null;
            Deal deal1 = null;
            if (data.getSerializableExtra("NewDeal") != null)
                deal1 = (Deal) data.getSerializableExtra("NewDeal");
            try {
                if (!myAccounts.contains(deal1.getBuyer())) {
                    buyer = new Account(deal1.getBuyer().getName(), true);
                }
                if (!myAccounts.contains(deal1.getSeller())) {
                    seller = new Account(deal1.getSeller().getName(), true);
                }
                for (Account a : myAccounts) {
                    if (a.getName().equals(deal1.getBuyer().getName()))
                        buyer = a;
                    if (a.getName().equals(deal1.getSeller().getName()))
                        seller = a;
                }
                Deal newDeal1 = Deal.createDeal(buyer, seller, deal1.getNote(), deal1.getSum(), deal1.getDate());
                if (newDeal1 == null) {
                    Toast.makeText(getApplicationContext(), "Impossible transaction (not enough money)", Toast.LENGTH_LONG).show();
                    return;
                }
                dbh.updateAcc(buyer);
                dbh.updateAcc(seller);
                addDeal(newDeal1);
                dbh.addDealToDB(newDeal1);
            } catch (NullPointerException np) {
                Toast.makeText(getApplicationContext(), "Something went wrong while creating new deal", Toast.LENGTH_LONG).show();
                return;
            }
        }
        if (requestCode == 222 && data != null) {
            String name = data.getStringExtra("isAccDeleted");
            Toast.makeText(getApplicationContext(), name + " was successfully deleted", Toast.LENGTH_LONG).show();
        }
        deals = dbh.getDealListFromDB();
        myAccounts = dbh.getAccListFromDB();
        updatePieChart(moneyPerAcc_chart);
        updateSpinner(accForChart_spn);
        populateListofDeals();
        populateList();
    }

    private void populateList() {
        ArrayAdapter<Account> adapter = new AccountsListAdapter();
        accsListView.setAdapter(adapter);
    }

    private void populateListofDeals() {
        ArrayAdapter<Deal> adapter = new DealsListAdapter();
        dealsListView.setAdapter(adapter);
    }

    private void addAccount(String name, double money, String descr, boolean flag) {
        myAccounts.add(new Account(name, money, descr, flag));
    }

    private class AccountsListAdapter extends ArrayAdapter<Account> {
        public AccountsListAdapter() {
            super(MainActivity.this, R.layout.listview_item, myAccounts);
        }

        @Override
        public View getView(int position, View view, ViewGroup parent) {
            if (view == null)
                view = getLayoutInflater().inflate(R.layout.listview_item, parent, false);
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
                            startActivityForResult(intent, 222);
                        }
                    }
            );
            return view;
        }
    }

    class DealsListAdapter extends ArrayAdapter<Deal> {
        public DealsListAdapter() {
            super(MainActivity.this, R.layout.listview_deals, deals);
        }

        @Override
        public View getView(int position, View view, ViewGroup parent) {
            if (view == null)
                view = getLayoutInflater().inflate(R.layout.listview_deals, parent, false);
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
            return view;
        }
    }
}
