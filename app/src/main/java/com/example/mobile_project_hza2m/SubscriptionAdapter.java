package com.example.mobile_project_hza2m;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class SubscriptionAdapter extends RecyclerView.Adapter<SubscriptionAdapter.ViewHolder> {

    private final List<UserSubscription> list;

    public SubscriptionAdapter(List<UserSubscription> list) {
        this.list = list;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView title, status, price, date;

        public ViewHolder(View view) {
            super(view);
            title = view.findViewById(R.id.subTitle);
            status = view.findViewById(R.id.subStatus);
            price = view.findViewById(R.id.subPrice);
            date = view.findViewById(R.id.subDate);
        }
    }

    @Override
    public SubscriptionAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.subscription_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder h, int pos) {
        UserSubscription s = list.get(pos);
        h.title.setText(s.title + " (" + s.category + ")");
        h.status.setText("Status: " + s.status);
        h.price.setText("Price: $" + s.price);
        h.date.setText("Renew: " + s.renewal_date);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}
