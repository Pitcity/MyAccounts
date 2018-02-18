package com.example.ihortovpinets.myaccounts;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;

/**
 * Created by itovp on 21.06.2017.
 */

public class EditAccountDialog extends DialogFragment {
    private DialogInterface.OnDismissListener mOnDismissListener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new AccCreationDialog(getActivity());
    }

    public void setOnDismissListener(DialogInterface.OnDismissListener listener) {
        mOnDismissListener = listener;
    }

    class AccCreationDialog extends Dialog implements View.OnClickListener {
        public AccCreationDialog(@NonNull Context context) {
            super(context);
        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
           // setContentView(R.layout.edit_account);
            super.onCreate(savedInstanceState);
        }

        @Override
        public void dismiss() {
            mOnDismissListener.onDismiss(this);
            super.dismiss();
        }

        @Override
        public void onClick(View v) {

        }
    }
}

