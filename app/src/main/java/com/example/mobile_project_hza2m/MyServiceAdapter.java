package com.example.mobile_project_hza2m;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class MyServiceAdapter extends RecyclerView.Adapter<MyServiceAdapter.ViewHolder> {

    public interface OnDeleteClickListener {
        void onDelete(int position);
    }

    private List<Service> serviceList;
    private OnDeleteClickListener deleteClickListener;

    public MyServiceAdapter(List<Service> serviceList, OnDeleteClickListener listener) {
        this.serviceList = serviceList;
        this.deleteClickListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_my_service, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Service service = serviceList.get(position);
        holder.serviceName.setText(service.getName());
        holder.servicePrice.setText(service.getPrice());
        holder.serviceIcon.setImageResource(service.getIconResId());

        holder.deleteBtn.setOnClickListener(v -> deleteClickListener.onDelete(position));
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
            servicePrice = itemView.findViewById(R.id.textServicePrice);
            serviceIcon = itemView.findViewById(R.id.imageServiceIcon);
            deleteBtn = itemView.findViewById(R.id.buttonDeleteService);
        }
    }
}
