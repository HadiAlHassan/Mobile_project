package com.example.mobile_project_hza2m;

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
import com.example.mobile_project_hza2m.databinding.ActivityInsuranceServiceUserBinding;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class InsuranceServiceUserActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private InsurancePlanAdapter adapter;
    private ArrayList<InsurancePlan> plans;
    private ActivityInsuranceServiceUserBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityInsuranceServiceUserBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        int serviceId = getIntent().getIntExtra("service_id", -1);
        String serviceName = getIntent().getStringExtra("service_name"); // optional for UI
        if (serviceId != -1) {
            fetchPlansFromServiceItems(serviceId);
        } else {
            Toast.makeText(this, "Missing service ID", Toast.LENGTH_SHORT).show();
        }

        // Toolbar setup
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(serviceName != null ? serviceName : "Insurance Plans");
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        recyclerView = findViewById(R.id.recyclerViewInsurancePlans);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        plans = new ArrayList<>();
        adapter = new InsurancePlanAdapter(this, plans, plan ->
                Toast.makeText(this, "Requested: " + plan.getTitle(), Toast.LENGTH_SHORT).show());

        recyclerView.setAdapter(adapter);
    }

    private void fetchPlansFromServiceItems(int serviceId) {
        String url = Config.BASE_URL + "services/get_service_items.php?service_id=" + serviceId;

        StringRequest request = new StringRequest(Request.Method.GET, url,
                response -> {
                    try {
                        JSONObject json = new JSONObject(response);
                        if (json.getBoolean("success")) {
                            JSONArray items = json.getJSONArray("items");
                            plans.clear();

                            for (int i = 0; i < items.length(); i++) {
                                JSONObject obj = items.getJSONObject(i);
                                plans.add(new InsurancePlan(
                                        obj.getString("item_name"),
                                        obj.getString("item_description"),
                                        obj.getString("item_price"),
                                        R.drawable.khadamatlogo // use a real image if available
                                ));
                            }

                            adapter.notifyDataSetChanged();
                        } else {
                            Toast.makeText(this, "No plans found", Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        Toast.makeText(this, "Parse error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(this, "Error fetching plans", Toast.LENGTH_SHORT).show()
        );

        Volley.newRequestQueue(this).add(request);
    }
}
