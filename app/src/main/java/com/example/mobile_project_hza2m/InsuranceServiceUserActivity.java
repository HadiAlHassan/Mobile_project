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

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class InsuranceServiceUserActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private InsurancePlanAdapter adapter;
    private ArrayList<InsurancePlan> plans;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insurance_service_user);

        String companyName = getIntent().getStringExtra("company_name");
        String category = getIntent().getStringExtra("category"); // optional

        if (companyName != null) {
            fetchServicesForCompany(companyName);
        }

        // Toolbar setup
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Insurance Services");
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        recyclerView = findViewById(R.id.recyclerViewInsurancePlans);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Dummy data
        plans = new ArrayList<>();
        plans.add(new InsurancePlan("Basic Plan", "Covers medical visits & hospitalization", "$25 / month", R.drawable.ic_launcher_foreground));
        plans.add(new InsurancePlan("Family Plan", "Covers 4 members with dental & optical", "$70 / month", R.drawable.ic_launcher_foreground));
        plans.add(new InsurancePlan("Accident Coverage", "Emergency and accident incidents only", "$12 / month", R.drawable.ic_launcher_foreground));

        adapter = new InsurancePlanAdapter(this, plans, plan ->
                Toast.makeText(this, "Requested: " + plan.getTitle(), Toast.LENGTH_SHORT).show());

        recyclerView.setAdapter(adapter);
    }

    private void fetchServicesForCompany(String companyName) {
        String url = Config.BASE_URL + "services/get_services.php?company_name=" + Uri.encode(companyName);

        StringRequest request = new StringRequest(Request.Method.GET, url,
                response -> {
                    try {
                        JSONObject json = new JSONObject(response);
                        if (json.getBoolean("success")) {
                            JSONArray services = json.getJSONArray("services");
                            plans = new ArrayList<>();

                            for (int i = 0; i < services.length(); i++) {
                                JSONObject obj = services.getJSONObject(i);
                                plans.add(new InsurancePlan(
                                        obj.getString("service_name"),
                                        "Insurance plan from " + obj.getString("service_name"), // fallback
                                        "$10 / month",  // or parse real price if returned
                                        R.drawable.khadamatlogo // fallback icon or parse image_url
                                ));
                            }

                            adapter = new InsurancePlanAdapter(this, plans, plan ->
                                    Toast.makeText(this, "Requested: " + plan.getTitle(), Toast.LENGTH_SHORT).show());
                            recyclerView.setAdapter(adapter);
                        } else {
                            Toast.makeText(this, "No insurance services found", Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        Toast.makeText(this, "Parse error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(this, "Error fetching insurance services", Toast.LENGTH_SHORT).show()
        );

        Volley.newRequestQueue(this).add(request);
    }

}
