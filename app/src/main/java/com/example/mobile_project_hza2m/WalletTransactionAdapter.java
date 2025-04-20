package com.example.mobile_project_hza2m;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class WalletTransactionAdapter extends RecyclerView.Adapter<WalletTransactionAdapter.ViewHolder> {

    private final List<WalletTransaction> transactionList;

    public WalletTransactionAdapter(List<WalletTransaction> list) {
        this.transactionList = list;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView type, amount, desc, date;

        public ViewHolder(View itemView) {
            super(itemView);
            type = itemView.findViewById(R.id.txnType);
            amount = itemView.findViewById(R.id.txnAmount);
            desc = itemView.findViewById(R.id.txnDesc);
            date = itemView.findViewById(R.id.txnDate);
        }
    }

    @Override
    public WalletTransactionAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.transaction_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        WalletTransaction txn = transactionList.get(position);
        holder.type.setText(txn.type);
        holder.amount.setText("$" + txn.amount);
        holder.desc.setText(txn.description != null ? txn.description : (txn.service_name != null ? txn.service_name : ""));
        holder.date.setText(txn.created_at);
    }

    @Override
    public int getItemCount() {
        return transactionList.size();
    }
}
