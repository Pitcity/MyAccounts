package com.example.ihortovpinets.myaccounts;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

/**
 * Created by IhorTovpinets on 15.09.2016.
 */
public class CreateDealActivity extends AppCompatActivity{

    DatePicker mDatePicker;
    ArrayList<Account> accounts;
    Spinner spinnerSeller, spinnerBuyer;
    Button dealAdding_btm;
    EditText additionSeller, additionBuyer, dealSum,dealDescr;

    private Account getAccFromSpin(Spinner sp, EditText et) throws IOException {
        Account acc = null;
        if (sp.getSelectedItem().equals("Another"))
            if (et.getText().toString().equals("")) {
                Toast.makeText(getApplicationContext(), "Enter name of seller under \"Another\"", Toast.LENGTH_LONG).show();
                throw new IOException();
            } else
                acc = new Account(et.getText().toString(), true);
        else
            for (Account a : accounts)
                if (a.getName().equals(sp.getSelectedItem().toString()))
                    acc = a;
        return acc;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_deal);

        mDatePicker = (DatePicker) findViewById(R.id.datePicker);
        mDatePicker.setCalendarViewShown(false);
        spinnerSeller = (Spinner) findViewById(R.id.dealSeller);
        spinnerBuyer= (Spinner) findViewById(R.id.dealBuyer);
        dealAdding_btm = (Button) findViewById(R.id.dealAdding_btn);
        additionBuyer = (EditText) findViewById(R.id.dealAdditionBuyer);
        additionSeller = (EditText) findViewById(R.id.dealAdditionSeller);
        dealSum = (EditText)  findViewById(R.id.dealSum);
        dealDescr = (EditText) findViewById(R.id.dealDescr);
        dealAdding_btm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Account buyer;
                Account seller;
                if (spinnerBuyer.getSelectedItem().toString().equals(spinnerSeller.getSelectedItem().toString()))
                    Toast.makeText(getApplicationContext(),"One acc cant be as buyer and seller in ne deal, change it pls", Toast.LENGTH_LONG).show();
                else {
                    try {
                        seller = getAccFromSpin(spinnerSeller, additionSeller);
                        buyer = getAccFromSpin(spinnerBuyer, additionBuyer);
                    } catch (Exception e) {
                        return;
                    }

                    double sum;
                    String note;
                    try {
                        sum = Double.valueOf(dealSum.getText().toString());
                        note = dealDescr.getText().toString();
                    } catch (Exception e) {
                        Toast.makeText(getApplicationContext(), "Enter correct value as ammount of the deal", Toast.LENGTH_LONG).show();
                        return;
                    }
                    SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
                    String date = df.format(mDatePicker.getCalendarView().getDate());

                    Deal newDeal = Deal.createDeal(buyer, seller, note, sum, date);
                    if (newDeal == null) {
                        Toast.makeText(getApplicationContext(), "Impossible transaction (not enough money)", Toast.LENGTH_LONG).show();
                        return;
                    }
                    Intent resultIntent = new Intent();
                    resultIntent.putExtra("NewDeal", newDeal);
                    setResult(221, resultIntent);
                    finish();
                }
            }
        });

        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, android.R.id.text1);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        accounts = (ArrayList<Account>)getIntent().getSerializableExtra("Accounts");
        spinnerSeller.setAdapter(spinnerAdapter);
        spinnerBuyer.setAdapter(spinnerAdapter);
        for (Account a: accounts) {
            spinnerAdapter.add(a.getName());
        }
        spinnerAdapter.add("Another");

    }
}
