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
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.InputStream;
import java.net.URL;
import java.util.List;
import java.util.concurrent.Executors;

public class ServiceAdapter extends RecyclerView.Adapter<ServiceAdapter.ServiceViewHolder> {

    private final Context context;
    private final List<Service> serviceList;

    public ServiceAdapter(Context context, List<Service> serviceList) {
        this.context = context;
        this.serviceList = serviceList;
    }

    @NonNull
    @Override
    public ServiceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_service_icon, parent, false);
        return new ServiceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ServiceViewHolder holder, int position) {
        Service service = serviceList.get(position);
        holder.serviceName.setText(service.getServiceName());

        // Load image without Glide
        loadImageFromUrl(Config.BASE_URL + service.getLogoUrl(), holder.serviceIcon);

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, ServiceItemsActivity.class);
            intent.putExtra("service_id", service.getServiceId());
            intent.putExtra("service_name", service.getServiceName());
            context.startActivity(intent);
        });

    }

    @Override
    public int getItemCount() {
        return serviceList.size();
    }

    private void loadImageFromUrl(String imageUrl, ImageView imageView) {
        Executors.newSingleThreadExecutor().execute(() -> {
            try {
                InputStream input = new URL(imageUrl).openStream();
                Bitmap bitmap = BitmapFactory.decodeStream(input);
                new Handler(Looper.getMainLooper()).post(() -> imageView.setImageBitmap(bitmap));
            } catch (Exception e) {
                e.printStackTrace();
                new Handler(Looper.getMainLooper()).post(() ->
                        imageView.setImageResource(R.drawable.khadmatiico) // fallback icon
                );
            }
        });
    }

    static class ServiceViewHolder extends RecyclerView.ViewHolder {
        ImageView serviceIcon;
        TextView serviceName;

        public ServiceViewHolder(@NonNull View itemView) {
            super(itemView);
            serviceIcon = itemView.findViewById(R.id.imageServiceIcon);
            serviceName = itemView.findViewById(R.id.textServiceName);
        }
    }
}
