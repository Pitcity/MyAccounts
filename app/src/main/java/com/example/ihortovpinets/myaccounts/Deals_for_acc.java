package com.example.ihortovpinets.myaccounts;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IhorTovpinets on 26.08.2016.
 */

public class Deals_for_acc extends AppCompatActivity {
    List<Deal> filteredDeals = new ArrayList<Deal>();
    ListView dealsListView;
    String name;

    private class DealsListAdapter extends ArrayAdapter<Deal> {
        public DealsListAdapter() {super(Deals_for_acc.this, R.layout.listview_deals, filteredDeals);
        }

        @Override
        public View getView(int position, View view, ViewGroup parent) {
            Deal currentDeal = filteredDeals.get(position);

            if (view == null)
                view = getLayoutInflater().inflate(R.layout.listview_deals, parent, false);

            TextView date = (TextView) view.findViewById(R.id.viewDeals_Date);
            TextView sum = (TextView) view.findViewById(R.id.viewDeals_sum);
            TextView seller = (TextView) view.findViewById(R.id.viewDeals_seller);
            TextView buyer = (TextView) view.findViewById(R.id.viewDeals_buyer);

            date.setText(currentDeal.getDate());
            if (currentDeal.getSeller().getName().equals(name)) {
                sum.setTextColor(Color.GREEN);
                sum.setText("+"+Double.toString(currentDeal.getSum()));
            }
            else {
                sum.setTextColor(Color.RED);
                sum.setText("-"+Double.toString(currentDeal.getSum()));
            }

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
        }}

        private void populateListofDeals () {
            ArrayAdapter<Deal> adapter = new DealsListAdapter();
            dealsListView.setAdapter(adapter);
        }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.deals_for_acc);

        dealsListView = (ListView) findViewById(R.id.listForDeals);

        name = getIntent().getStringExtra("Name");
        TextView txtName = (TextView) findViewById(R.id.txtName2);
        txtName.setText(name);
        ArrayList<Deal> deals =  (ArrayList<Deal>)getIntent().getSerializableExtra("Deals");

        for (Deal d: deals) {
            if(d.getBuyer().getName().equals(name)||d.getSeller().getName().equals(name)) {
                filteredDeals.add(d);
            }
        }

        populateListofDeals();
    }
}
