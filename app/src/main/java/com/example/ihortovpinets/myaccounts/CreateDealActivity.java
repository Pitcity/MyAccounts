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
                Account buyer=null;
                Account seller=null;

                if (spinnerBuyer.getSelectedItem().toString().equals(spinnerSeller.getSelectedItem().toString())) {
                    Toast.makeText(getApplicationContext(),"One acc cant be as buyer and seller in ne deal, change it pls", Toast.LENGTH_LONG).show();
                    return;
                }
                if(spinnerSeller.getSelectedItem().equals("Another")) {
                    if (additionSeller.getText().toString().equals("")) {
                        Toast.makeText(getApplicationContext(),"Enter name of seller under \"Another\"", Toast.LENGTH_LONG).show();
                        return;
                    } else
                         seller = new Account(additionSeller.getText().toString(),1);
                } else {
                    for (Account a: accounts) {
                        if(a.getName().equals(spinnerSeller.getSelectedItem().toString())) {
                            seller = a;
                        }
                    }
                }
                if(spinnerBuyer.getSelectedItem().equals("Another")) {
                    if (additionBuyer.getText().toString().equals("")) {
                        Toast.makeText(getApplicationContext(),"Enter name of buyer under \"Another\"", Toast.LENGTH_LONG).show();
                        return;
                    }
                    else
                        buyer = new Account(additionBuyer.getText().toString(),1);
                } else {
                    for (Account a: accounts) {
                        if(a.getName().equals(spinnerBuyer.getSelectedItem().toString())) {
                            buyer = a;
                        }
                    }
                }
                double sum =0;
                String note="";
                try {
                    sum = Double.valueOf(dealSum.getText().toString());
                    note = dealDescr.getText().toString();
                } catch (Exception e) {
                        Toast.makeText(getApplicationContext(),"Enter correct value as ammount of the deal", Toast.LENGTH_LONG).show();
                        return;
                }
                SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
                String date = df.format(mDatePicker.getCalendarView().getDate());

                Deal newDeal = Deal.createDeal(buyer,seller,note,sum, date);
                if (newDeal==null) {
                    Toast.makeText(getApplicationContext(),"Impossible transaction (not enough money)", Toast.LENGTH_LONG).show();
                    return;
                }
                Intent resultIntent = new Intent();

                resultIntent.putExtra("NewDeal",newDeal);

                /*resultIntent.putExtra("buyer",buyer.getName());
                resultIntent.putExtra("seller",seller.getName());
                resultIntent.putExtra("note",note);
                resultIntent.putExtra("sum",sum);
                resultIntent.putExtra("date",date);
*/
                setResult(221, resultIntent);
                finish();
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
       // mDatePicker.ye


    }
}
