package com.example.mobile_project_hza2m;

import android.content.Intent;
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

        serviceList = new ArrayList<>();
        // Sample data — replace with data from your DB/API
        serviceList.add(new Service("Tuition for LAU", "$200", R.drawable.tuitionfees));
        serviceList.add(new Service("Ogero Bill Payment", "$30", R.drawable.ogero));

        adapter = new MyServiceAdapter(serviceList, position -> {
            serviceList.remove(position);
            adapter.notifyItemRemoved(position);
            Toast.makeText(this, "Service deleted", Toast.LENGTH_SHORT).show();
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        fabAddService.setOnClickListener(v -> {
            // Launch add service activity
            //based on text view of services we direct
            startActivity(new Intent(this, AddServiceItemActivity.class));
        });
    }
}
