package com.example.ihortovpinets.myaccounts.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.ihortovpinets.myaccounts.DBHelper;
import com.example.ihortovpinets.myaccounts.Entity.Account;
import com.example.ihortovpinets.myaccounts.R;

import java.util.ArrayList;

import static android.support.v7.widget.RecyclerView.NO_POSITION;

public class AccountListAdapter extends RecyclerView.Adapter<AccountListAdapter.AccountViewHolder> {

    private ArrayList<Account> myAccounts;
    private Context mContext;
    private boolean isUpdateRequired;
    private OnItemClickListener mItemClickListener;
    private OnItemLongClickListener mItemLongClickListener;

    public AccountListAdapter(Context context) {
        super();
        mContext = context;
        updateData();
    }

    public AccountListAdapter(Context context, ArrayList<Account> accs) {
        super();
        mContext = context;
        myAccounts = accs;
        isUpdateRequired = false;
    }

    @Override
    public AccountViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.account_item_row, parent, false);
        AccountViewHolder holder = new AccountViewHolder(v, mItemClickListener, mItemLongClickListener);
        return holder;
    }

    @Override
    public void onBindViewHolder(AccountViewHolder holder, int position) {
        if (isUpdateRequired) {
            updateData();
        }
        Account acc = myAccounts.get(position);
        holder.mDescription.setText(acc.getDescription());
        holder.mMoney.setText(Double.toString(acc.getDeposit()));
        holder.mName.setText(acc.getName());

    }

    @Override
    public int getItemCount() {
        return myAccounts.size();
    }

    public Account getItem(int position) {
        return myAccounts.get(position);
    }

    private void updateData() {
        myAccounts = new DBHelper(mContext).getAccListFromDB();
        isUpdateRequired = false;
    }

    public void update() {
        updateData();
        notifyDataSetChanged();
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mItemClickListener = listener;
    }

    public void setOnItemLongClickListener(OnItemLongClickListener listener) {
        mItemLongClickListener = listener;
    }

    public void setAccounts(ArrayList<Account> accounts) {
        myAccounts = accounts;
        notifyDataSetChanged();
    }

    public void setShoulBeUpdated() {
        isUpdateRequired = true;
    }

    static class AccountViewHolder extends RecyclerView.ViewHolder {

        TextView mName;
        TextView mMoney;
        TextView mDescription;

        public AccountViewHolder(View itemView, final OnItemClickListener listenerClick, final OnItemLongClickListener listenerLongClick) {
            super(itemView);
            mName = (TextView) itemView.findViewById(R.id.account_item_name);
            mMoney = (TextView) itemView.findViewById(R.id.account_item_money);
            mDescription = (TextView) itemView.findViewById(R.id.account_item_desc);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != NO_POSITION) {
                        listenerClick.onItemClick(position);
                    }
                }
            });
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    int position = getAdapterPosition();
                    if (position != NO_POSITION) {
                        listenerLongClick.onItemLongClick(position);
                        return true;
                    }
                    return false;
                }
            });
        }
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public interface OnItemLongClickListener {
        void onItemLongClick(int position);
    }
}