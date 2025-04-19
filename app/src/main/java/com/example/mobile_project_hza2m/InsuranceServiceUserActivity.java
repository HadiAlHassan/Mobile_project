package com.example.mobile_project_hza2m;

import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;

public class InsuranceServiceUserActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private InsurancePlanAdapter adapter;
    private ArrayList<InsurancePlan> plans;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insurance_service_user);

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
}
