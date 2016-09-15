package com.example.ihortovpinets.myaccounts;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.Spinner;

import java.util.ArrayList;

/**
 * Created by IhorTovpinets on 15.09.2016.
 */
public class CreateDealActivity extends AppCompatActivity{

    DatePicker mDatePicker;
    ArrayList<Account> accounts;
    Spinner spinnerSeller, spinnerBuyer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_deal);

        mDatePicker = (DatePicker) findViewById(R.id.datePicker);
        mDatePicker.setCalendarViewShown(false);
        spinnerSeller = (Spinner) findViewById(R.id.dealSeller);
        spinnerBuyer= (Spinner) findViewById(R.id.dealBuyer);


        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, android.R.id.text1);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        accounts = (ArrayList<Account>)getIntent().getSerializableExtra("Accounts");
        spinnerSeller.setAdapter(spinnerAdapter);
        spinnerBuyer.setAdapter(spinnerAdapter);
        for (Account a: accounts) {
            spinnerAdapter.add(a.getName());
        }
       // mDatePicker.ye


    }
}
