package com.example.mobile_project_hza2m;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class TelecomServiceUserActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private List<Service> serviceList;
    private ServiceAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_telecom_service_user);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Telecom Services");
        setSupportActionBar(toolbar);

        // Enable back arrow
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        recyclerView = findViewById(R.id.recyclerViewTelecomCards);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        serviceList = new ArrayList<>();

        String companyName = getIntent().getStringExtra("company_name");
        String category = getIntent().getStringExtra("category");

        if (companyName != null) {
            fetchServicesForCompany(companyName);
        } else {
            Toast.makeText(this, "Missing company name", Toast.LENGTH_SHORT).show();
        }
    }

    private void fetchServicesForCompany(String companyName) {
        String url = Config.BASE_URL + "services/get_services_by_company.php?company_name=" + Uri.encode(companyName);

        StringRequest request = new StringRequest(Request.Method.GET, url,
                response -> {
                    try {
                        JSONObject json = new JSONObject(response);
                        if (json.getBoolean("success")) {
                            JSONArray serviceArray = json.getJSONArray("services");
                            for (int i = 0; i < serviceArray.length(); i++) {
                                JSONObject obj = serviceArray.getJSONObject(i);
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
                        Toast.makeText(this, "Parse error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(this, "Error loading services", Toast.LENGTH_SHORT).show()
        );

        Volley.newRequestQueue(this).add(request);
    }
}
