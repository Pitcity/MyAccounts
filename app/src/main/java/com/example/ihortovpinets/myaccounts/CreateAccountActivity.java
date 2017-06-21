package com.example.ihortovpinets.myaccounts;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by itovp on 21.06.2017.
 */

public class CreateAccountActivity extends AppCompatActivity implements View.OnClickListener {

	public static final int CODE_FOR_CREATING_ACCOUNT = 201;
	public static final String IS_ACC_CREATED = "IS_ACC_CREATED";
	private TextView mName;
	private TextView mDescription;
	private TextView mDeposit;
	private Button mSubmit;

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.create_account);
		mSubmit = (Button) findViewById(R.id.create_account_submit);
		mSubmit.setOnClickListener(this);
		mName = (TextView) findViewById(R.id.create_account_name);
		mDescription = (TextView) findViewById(R.id.create_account_description);
		mDeposit = (TextView) findViewById(R.id.create_account_money);
		mName.addTextChangedListener(mChecker);
		mDescription.addTextChangedListener(mChecker);
		mDeposit.addTextChangedListener(mChecker);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		ActionBar ab = getSupportActionBar();
		ab.setDisplayHomeAsUpEnabled(true);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		onBackPressed();
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.create_account_submit:
				String accName = mName.getText().toString();
				DBHelper dbh = new DBHelper(getApplicationContext());
				if (dbh.checkExistence(accName)) {
					Toast.makeText(getApplicationContext(), "Account with this name already exists", Toast.LENGTH_LONG).show();
				} else {
					try {
						dbh.addAccountToDB(new Account(accName, Double.valueOf(mDeposit.getText().toString()), mDescription.getText().toString(), false));
					} catch (Exception e) {
						Toast.makeText(getApplicationContext(), "Something went wrong, please, try again later", Toast.LENGTH_SHORT).show();
					}
					Toast.makeText(getApplicationContext(), accName + " has been added to your Accounts", Toast.LENGTH_SHORT).show();
					setResult(CODE_FOR_CREATING_ACCOUNT, new Intent().putExtra(IS_ACC_CREATED, true));
					CreateAccountActivity.super.finish();
				}
				break;
		}
	}

	TextWatcher mChecker = new TextWatcher() {
		@Override
		public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
		}

		@Override
		public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
			mSubmit.setEnabled(!(mName.getText().toString().trim().isEmpty() || mDeposit.getText().toString().trim().isEmpty() || !(Double.valueOf(mDeposit.getText().toString()) >= 0)));
		}

		@Override
		public void afterTextChanged(Editable editable) {

		}
	};
}
