package com.example.mobile_project_hza2m;

import android.content.Intent;
import android.os.Bundle;
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

import java.util.ArrayList;
import java.util.List;

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

        String category = getIntent().getStringExtra("category");
        if (category != null) {
            fetchServicesByCategory(category);
        }

        textViewProviderName.setText("Available Streaming Services");

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        serviceList = new ArrayList<>();
    }

    private void fetchServicesByCategory(String category) {
        String url = Config.BASE_URL + "services/get_services_by_category.php?category=" + category;

        StringRequest request = new StringRequest(Request.Method.GET, url,
                response -> {
                    try {
                        JSONObject json = new JSONObject(response);
                        if (json.getBoolean("success")) {
                            JSONArray services = json.getJSONArray("services");
                            for (int i = 0; i < services.length(); i++) {
                                JSONObject obj = services.getJSONObject(i);
                                serviceList.add(new Service(
                                        obj.getInt("service_id"),
                                        obj.getString("service_name"),
                                        obj.getString("logo_url"),
                                        obj.getString("category")
                                ));
                            }

                            adapter = new ServiceAdapter(this, serviceList);
                            recyclerView.setAdapter(adapter);

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
}
