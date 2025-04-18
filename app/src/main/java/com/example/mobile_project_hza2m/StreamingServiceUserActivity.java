package com.example.mobile_project_hza2m;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class StreamingServiceUserActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private TextView textViewProviderName;
    private ImageView imageViewProviderLogo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_streaming_service_user);

        recyclerView = findViewById(R.id.recyclerViewPlans);
        textViewProviderName = findViewById(R.id.textViewProviderName);
        imageViewProviderLogo = findViewById(R.id.imageViewProviderLogo);

        // Simulate setting provider info
        textViewProviderName.setText("Hadi's Netflix");

        // Setup RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        ArrayList<SubscriptionPlan> plans = new ArrayList<>();
        plans.add(new SubscriptionPlan("Standard", "$3/month", 2, R.drawable.netflix_standard));
        plans.add(new SubscriptionPlan("Premium", "$5/month", 4, R.drawable.netflix_premium));

        PlanAdapter adapter = new PlanAdapter(this, plans, plan -> {
            Toast.makeText(this, "Subscribed to: " + plan.getTitle(), Toast.LENGTH_SHORT).show();
            // You can open a subscription form here
        });

        recyclerView.setAdapter(adapter);
    }
}
