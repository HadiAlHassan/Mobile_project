package com.example.mobile_project_hza2m;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

public class MyServiceAdapter extends RecyclerView.Adapter<MyServiceAdapter.ViewHolder> {

    public interface OnDeleteClickListener {
        void onDelete(Service service, int position);
    }

    private List<Service> serviceList;
    private List<Service> originalList;
    private final OnDeleteClickListener deleteClickListener;
    private final Context context;

    public MyServiceAdapter(List<Service> serviceList, OnDeleteClickListener listener, Context context) {
        this.serviceList = serviceList;
        this.originalList = new ArrayList<>(serviceList);
        this.deleteClickListener = listener;
        this.context = context;
    }

    public void setOriginalList(List<Service> newList) {
        this.originalList = new ArrayList<>(newList);
        this.serviceList = new ArrayList<>(newList);
    }

    public void filter(String query) {
        query = query.toLowerCase();
        serviceList.clear();
        if (query.isEmpty()) {
            serviceList.addAll(originalList);
        } else {
            for (Service service : originalList) {
                if (service.getServiceName().toLowerCase().contains(query)) {
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
        Service service = serviceList.get(position);
        holder.serviceName.setText(service.getServiceName());

        // ✅ Load logo from URL
        loadImageFromUrl(Config.BASE_URL + service.getLogoUrl(), holder.serviceIcon);

        holder.deleteBtn.setOnClickListener(v -> {
            if (deleteClickListener != null) {
                deleteClickListener.onDelete(service, position);
            }
        });

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, ServiceItemsActivity.class);
            intent.putExtra("service_id", service.getServiceId());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return serviceList.size();
    }

    private void loadImageFromUrl(String url, ImageView imageView) {
        Executors.newSingleThreadExecutor().execute(() -> {
            try {
                InputStream input = new URL(url).openStream();
                Bitmap bitmap = BitmapFactory.decodeStream(input);
                new Handler(Looper.getMainLooper()).post(() -> imageView.setImageBitmap(bitmap));
            } catch (Exception e) {
                e.printStackTrace();
                new Handler(Looper.getMainLooper()).post(() ->
                        imageView.setImageResource(R.drawable.khadmatiico)
                );
            }
        });
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView serviceName;
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
