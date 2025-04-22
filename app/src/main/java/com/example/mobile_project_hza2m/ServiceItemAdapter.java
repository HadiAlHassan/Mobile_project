package com.example.mobile_project_hza2m;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ServiceItemAdapter extends RecyclerView.Adapter<ServiceItemAdapter.ViewHolder> {

    private final List<ServiceItem> itemList;

    public ServiceItemAdapter(List<ServiceItem> itemList) {
        this.itemList = itemList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_service_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ServiceItem item = itemList.get(position);
        holder.name.setText(item.getItemName());
        holder.description.setText(item.getItemDescription());
        holder.price.setText("$" + item.getItemPrice());
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView name, description, price;

        public ViewHolder(View view) {
            super(view);
            name = view.findViewById(R.id.textItemName);
            description = view.findViewById(R.id.textItemDescription);
            price = view.findViewById(R.id.textItemPrice);
        }
    }
}
