package com.example.mobile_project_hza2m;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

public class StreamingServiceUserActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private TextView textViewProviderName;
    private ImageView imageViewProviderLogo;

    private List<Service> serviceList;
    private ServiceAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_streaming_service_user);

        recyclerView = findViewById(R.id.recyclerViewPlans);
        textViewProviderName = findViewById(R.id.textViewProviderName);
        imageViewProviderLogo = findViewById(R.id.imageViewProviderLogo);

        serviceList = new ArrayList<>();
        adapter = new ServiceAdapter(this, serviceList);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        String category = getIntent().getStringExtra("category");
        if (category != null) {
            fetchServicesByCategory(category);
            textViewProviderName.setText("Streaming Services - " + category);
        } else {
            textViewProviderName.setText("Available Streaming Services");
        }
    }

    private void fetchServicesByCategory(String category) {
        String url = Config.BASE_URL + "services/get_services_by_category.php?category=" + Uri.encode(category);

        StringRequest request = new StringRequest(Request.Method.GET, url,
                response -> {
                    try {
                        JSONObject json = new JSONObject(response);
                        if (json.getBoolean("success")) {
                            JSONArray services = json.getJSONArray("services");
                            serviceList.clear();

                            for (int i = 0; i < services.length(); i++) {
                                JSONObject obj = services.getJSONObject(i);
                                Service service = new Service(
                                        obj.getInt("service_id"),
                                        obj.getString("service_name"),
                                        obj.getString("logo_url"),
                                        obj.getString("category")
                                );
                                serviceList.add(service);
                            }

                            if (services.length() > 0) {
                                String firstLogoUrl = services.getJSONObject(0).getString("logo_url");
                                loadImageFromUrl(Config.BASE_URL + firstLogoUrl, imageViewProviderLogo);
                            }

                            adapter.notifyDataSetChanged();
                        } else {
                            Toast.makeText(this, "No services found", Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        Toast.makeText(this, "Error parsing services", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(this, "Fetch failed", Toast.LENGTH_SHORT).show());

        Volley.newRequestQueue(this).add(request);
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
                        imageView.setImageResource(R.drawable.khadmatiico)
                );
            }
        });
    }
}
