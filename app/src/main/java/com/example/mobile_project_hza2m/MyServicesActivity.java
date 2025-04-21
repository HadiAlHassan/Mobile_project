package com.example.mobile_project_hza2m;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class MyServicesActivity extends AppCompatActivity {
    TextView tvServiceType;
    private RecyclerView recyclerView;
    private MyServiceAdapter adapter;
    private List<Service> serviceList;
    private FloatingActionButton fabAddService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_services);

        recyclerView = findViewById(R.id.recyclerViewMyServices);
        fabAddService = findViewById(R.id.fabAddMyService);
        tvServiceType = findViewById(R.id.tvServiceType);

        // 🔐 Get category info from SharedPreferences
        SharedPreferences prefs = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        int categoryId = prefs.getInt("category_id", -1);

        String categoryLabel = getCategoryLabel(categoryId);
        tvServiceType.setText("Service Category: " + categoryLabel);

        // 🧾 Dummy list (replace with real service data)
        serviceList = new ArrayList<>();
        serviceList.add(new Service("Tuition for LAU", "$200", R.drawable.tuitionfees));
        serviceList.add(new Service("Ogero Bill Payment", "$30", R.drawable.ogero));

        adapter = new MyServiceAdapter(serviceList, position -> {
            serviceList.remove(position);
            adapter.notifyItemRemoved(position);
            Toast.makeText(this, "Service deleted", Toast.LENGTH_SHORT).show();
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        // 🟦 FAB click — navigate based on category ID
        fabAddService.setOnClickListener(v -> {
            switch (categoryId) {
                case 1: // Ogero
                    startActivity(new Intent(this, OgeroServiceProviderActivity.class));

                    break;
                case 2: // Insurance
                    startActivity(new Intent(this, InsuranceServiceProviderActivity.class));
                    break;
                case 3: // Streaming
                    startActivity(new Intent(this, StreamingServiceProviderActivity.class));
                    break;
                case 4: // Telecom
                    startActivity(new Intent(this, TelecomServiceProviderActivity.class));
                    break;
                case 5: // Tuition
                    startActivity(new Intent(this, TuitionServiceProviderActivity.class));
                    break;
                default:
                    startActivity(new Intent(this, MyServicesActivity.class));
                    break;
            }
        });
    }

    private String getCategoryLabel(int categoryId) {
        switch (categoryId) {
            case 1: return "Ogero";
            case 2: return "Insurance";
            case 3: return "Streaming";
            case 4: return "Telecom";
            case 5: return "Tuition";
            default: return "General";
        }
    }
}
