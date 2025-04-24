package com.example.mobile_project_hza2m;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

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

        // ✅ Load Firebase-hosted image using Glide
        Glide.with(context)
                .load(service.getLogoUrl())
                .placeholder(R.drawable.khadmatiico)
                .error(R.drawable.khadmatiico)
                .into(holder.serviceIcon);

        holder.itemView.setOnClickListener(v -> {
            Intent intent;
            int categoryId = service.getCategoryId();
            switch (categoryId) {
                case 1: intent = new Intent(context, OgeroServiceUserActivity.class); break;
                case 2: intent = new Intent(context, InsuranceServiceUserActivity.class); break;
                case 3: intent = new Intent(context, StreamingServiceUserActivity.class); break;
                case 4: intent = new Intent(context, TelecomServiceUserActivity.class); break;
                case 5: intent = new Intent(context, TuitionServiceUserActivity.class); break;
                default:
                    Toast.makeText(context, "No activity found for category ID: " + categoryId, Toast.LENGTH_SHORT).show();
                    return;
            }

            intent.putExtra("service_id", service.getServiceId());
            intent.putExtra("service_name", service.getServiceName());
            context.startActivity(intent);
        });
    }


    @Override
    public int getItemCount() {
        return serviceList.size();
    }


    static class ServiceViewHolder extends RecyclerView.ViewHolder {
        ImageView serviceIcon;
        TextView serviceName;

        public ServiceViewHolder(@NonNull View itemView) {
            super(itemView);
            serviceIcon = itemView.findViewById(R.id.companyIcon);
            serviceName = itemView.findViewById(R.id.companyName);

        }
    }
}
