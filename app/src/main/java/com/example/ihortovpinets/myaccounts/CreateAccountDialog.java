package com.example.ihortovpinets.myaccounts;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ihortovpinets.myaccounts.DTO.AccountDto;
import com.example.ihortovpinets.myaccounts.Entity.Account;
import com.example.ihortovpinets.myaccounts.Service.SyncService;

/**
 * Created by itovp on 21.06.2017.
 */

public class CreateAccountDialog extends DialogFragment {

    public static final int CODE_FOR_CREATING_ACCOUNT = 201;
    public static final String IS_ACC_CREATED = "IS_ACC_CREATED";
    private TextView mName;
    private TextView mDescription;

    private TextView mDeposit;
    private Button mSubmit;
    private DialogInterface.OnDismissListener mOnDismissListener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new AccCreationDialog(getActivity());
    }

    public void setOnDismissListener(DialogInterface.OnDismissListener listener) {
        mOnDismissListener = listener;
    }

    TextWatcher mChecker = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            mName.setError(mName.getText().toString().matches("[a-zA-Z0-9]{1,}") ? null : "Please dont use spetial characters");
            mDescription.setError(mDescription.getText().toString().matches("[a-zA-Z0-9.,! ?]{0,}") ? null : "Please dont use spetial characters");
            mSubmit.setEnabled(!(mDescription.getError() != null || mName.getError() != null || mName.getText().toString().trim().isEmpty() || mDeposit.getText().toString().trim().isEmpty() || !(Double.valueOf(mDeposit.getText().toString()) >= 0)));
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };

    class AccCreationDialog extends Dialog implements View.OnClickListener {
        public AccCreationDialog(@NonNull Context context) {
            super(context);
        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.create_account);
            mSubmit = (Button) findViewById(R.id.create_account_submit);
            findViewById(R.id.create_account_cancel).setOnClickListener(this);
            mSubmit.setOnClickListener(this);
            mName = (TextView) findViewById(R.id.create_account_name);
            mDescription = (TextView) findViewById(R.id.create_account_description);
            mDeposit = (TextView) findViewById(R.id.create_account_money);
            mName.addTextChangedListener(mChecker);
            mDescription.addTextChangedListener(mChecker);
            mDeposit.addTextChangedListener(mChecker);
            super.onCreate(savedInstanceState);
        }

        @Override
        public void dismiss() {
            mOnDismissListener.onDismiss(this);
            super.dismiss();
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.create_account_submit:
                    String accName = mName.getText().toString();
                    DBHelper dbh = new DBHelper(getActivity());
                    if (dbh.isAccountWithNameExists(accName)) {
                        Toast.makeText(getActivity(), "Account with this name already exists", Toast.LENGTH_LONG).show();
                    } else {
                        try {
                            Account acc = new Account(accName, Double.valueOf(mDeposit.getText().toString()), mDescription.getText().toString(), false);
                            dbh.addAccountToDB(acc);
                            new SyncService(getContext()).saveAccount(new AccountDto(acc));
                        } catch (Exception e) {
                            Toast.makeText(getActivity(), "Something went wrong, please, try again later", Toast.LENGTH_SHORT).show();
                        }
                        Toast.makeText(getActivity(), accName + " has been added to your Accounts", Toast.LENGTH_SHORT).show();
                        dismiss();
                    }
                    break;
                case R.id.create_account_cancel:
                    dismiss();
            }
        }
    }
}

