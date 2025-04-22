package com.example.mobile_project_hza2m;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class MyServiceAdapter extends RecyclerView.Adapter<MyServiceAdapter.ViewHolder> {

    public interface OnDeleteClickListener {
        void onDelete(Services service, int position);
    }

    private List<Services> serviceList;
    private List<Services> originalList;
    private final OnDeleteClickListener deleteClickListener;
    private final Context context;

    public MyServiceAdapter(List<Services> serviceList, OnDeleteClickListener listener, Context context) {
        this.serviceList = serviceList;
        this.originalList = new ArrayList<>(serviceList);
        this.deleteClickListener = listener;
        this.context = context;
    }

    public void setOriginalList(List<Services> newList) {
        this.originalList = new ArrayList<>(newList);
        this.serviceList = new ArrayList<>(newList);
    }

    public void filter(String query) {
        query = query.toLowerCase();
        serviceList.clear();
        if (query.isEmpty()) {
            serviceList.addAll(originalList);
        } else {
            for (Services service : originalList) {
                if (service.getName().toLowerCase().contains(query)) {
                    serviceList.add(service);
                }
            }
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_my_service, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Services service = serviceList.get(position);
        holder.serviceName.setText(service.getName());
        holder.serviceIcon.setImageResource(service.getIconResId());

        holder.deleteBtn.setOnClickListener(v -> {
            if (deleteClickListener != null) {
                deleteClickListener.onDelete(service, position);
            }
        });

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, ServiceItemsActivity.class);
            intent.putExtra("service_id", service.getId());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return serviceList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView serviceName, servicePrice;
        ImageView serviceIcon;
        ImageButton deleteBtn;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            serviceName = itemView.findViewById(R.id.textServiceName);
            serviceIcon = itemView.findViewById(R.id.imageServiceIcon);
            deleteBtn = itemView.findViewById(R.id.buttonDeleteService);
        }
    }
}
