package com.example.mobile_project_hza2m;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.mobile_project_hza2m.databinding.ActivityStreamingServiceUserBinding;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class StreamingServiceUserActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private StreamingPlanAdapter adapter;
    private ArrayList<StreamingPlan> plans;
    private int serviceId;
    private ActivityStreamingServiceUserBinding binding;

    private final String BALANCE_URL = Config.BASE_URL + "wallet/get_wallet_balance.php?user_id=";
    private final String PAY_URL = Config.BASE_URL + "services/subscribe_service.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityStreamingServiceUserBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.toolbar.setTitle("Streaming Plans");

        recyclerView = findViewById(R.id.recyclerViewPlans);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));

        serviceId = getIntent().getIntExtra("service_id", -1);
        if (serviceId == -1) {
            Toast.makeText(this, "Missing service ID", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        plans = new ArrayList<>();
        adapter = new StreamingPlanAdapter(this, plans, plan -> {
            int userId = getSharedPreferences("AppPrefs", MODE_PRIVATE).getInt("user_id", -1);
            if (userId == -1) {
                Toast.makeText(this, "User ID not found", Toast.LENGTH_SHORT).show();
                return;
            }
            checkBalanceAndSubscribe(userId, serviceId, plan);
        });

        recyclerView.setAdapter(adapter);
        fetchStreamingPlans(serviceId);
    }

    private void fetchStreamingPlans(int serviceId) {
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

                                int itemId = obj.getInt("item_id");
                                String name = obj.getString("item_name");
                                String desc = obj.getString("item_description");
                                String price = obj.getString("item_price");
                                String imageUrl = obj.optString("item_image", ""); // Firebase URL

                                plans.add(new StreamingPlan(
                                        itemId,
                                        serviceId,
                                        name,
                                        desc,
                                        price,
                                        imageUrl
                                ));
                            }

                            adapter.notifyDataSetChanged();
                        } else {
                            Toast.makeText(this, "No streaming plans found", Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        Toast.makeText(this, "Parse error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(this, "Network error: " + error.getMessage(), Toast.LENGTH_SHORT).show()
        );

        Volley.newRequestQueue(this).add(request);
    }

    private void checkBalanceAndSubscribe(int userId, int serviceId, StreamingPlan plan) {
        StringRequest request = new StringRequest(Request.Method.GET, BALANCE_URL + userId,
                response -> {
                    try {
                        JSONObject json = new JSONObject(response);
                        float balance = Float.parseFloat(json.optString("balance", "0.00"));
                        float price = Float.parseFloat(plan.getPrice());

                        if (!json.getBoolean("success")) {
                            Toast.makeText(this, "Failed to check balance", Toast.LENGTH_SHORT).show();
                        } else if (balance < price) {
                            Toast.makeText(this, "Insufficient balance. Balance: $" + balance, Toast.LENGTH_SHORT).show();
                        } else {
                            subscribe(userId, serviceId, plan.getName(), plan.getDescription(), price);
                        }
                    } catch (Exception e) {
                        Toast.makeText(this, "Balance check error", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {Toast.makeText(this, "Network error", Toast.LENGTH_SHORT).show();
        Log.e("NetworkError", "Network error (Streaming Service User): " + error.getMessage());}
        );

        Volley.newRequestQueue(this).add(request);
    }

    private void subscribe(int userId, int serviceId, String reference, String description, float amount) {
        StringRequest request = new StringRequest(Request.Method.POST, PAY_URL,
                response -> {
                    SharedPreferences walletPrefs = getSharedPreferences("wallet_prefs", MODE_PRIVATE);
                    walletPrefs.edit().putFloat("last_payment_amount", amount).apply();

                    Toast.makeText(this, "Subscribed successfully!", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(this, MyWalletActivity.class));
                    finish();
                },
                error -> Toast.makeText(this, "Subscription failed", Toast.LENGTH_SHORT).show()
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("user_id", String.valueOf(userId));
                params.put("service_id", String.valueOf(serviceId));
                params.put("reference", reference);
                params.put("description", description);
                params.put("amount", String.valueOf(amount));
                return params;
            }
        };

        Volley.newRequestQueue(this).add(request);
    }
}
