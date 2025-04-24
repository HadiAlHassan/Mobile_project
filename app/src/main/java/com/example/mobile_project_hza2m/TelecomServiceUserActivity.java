package com.example.mobile_project_hza2m;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.mobile_project_hza2m.databinding.ActivityTelecomServiceUserBinding;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class TelecomServiceUserActivity extends AppCompatActivity {

    private ActivityTelecomServiceUserBinding binding;
    private RecyclerView recyclerView;
    private TelecomCardAdapter adapter;
    private ArrayList<TelecomCard> cardList;
    private int serviceId;

    private final String PAY_URL = Config.BASE_URL + "services/subscribe_service.php";
    private final String BALANCE_URL = Config.BASE_URL + "wallet/get_wallet_balance.php?user_id=";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTelecomServiceUserBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        serviceId = getIntent().getIntExtra("service_id", -1);
        String serviceName = getIntent().getStringExtra("service_name");

        if (serviceId == -1) {
            Toast.makeText(this, "Missing service ID", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(serviceName != null ? serviceName : "Telecom Plans");
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        recyclerView = findViewById(R.id.recyclerViewTelecomCards);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        cardList = new ArrayList<>();
        adapter = new TelecomCardAdapter(this, cardList, plan -> {
            int userId = getSharedPreferences("AppPrefs", MODE_PRIVATE).getInt("user_id", -1);
            if (userId == -1) {
                Toast.makeText(this, "User ID not found", Toast.LENGTH_SHORT).show();
                return;
            }
            checkBalanceAndSubscribe(userId, serviceId, plan);
        });
        recyclerView.setAdapter(adapter);

        fetchPlansFromServiceItems(serviceId);
    }

    private void fetchPlansFromServiceItems(int serviceId) {
        String url = Config.BASE_URL + "services/get_service_items.php?service_id=" + serviceId;

        StringRequest request = new StringRequest(Request.Method.GET, url,
                response -> {
                    try {
                        JSONObject json = new JSONObject(response);
                        if (json.getBoolean("success")) {
                            JSONArray items = json.getJSONArray("items");
                            cardList.clear();

                            for (int i = 0; i < items.length(); i++) {
                                JSONObject obj = items.getJSONObject(i);
                                cardList.add(new TelecomCard(
                                        obj.getInt("item_id"),
                                        serviceId,
                                        obj.getString("item_name"),
                                        obj.getString("item_description"),
                                        obj.getString("item_price"),
                                        R.drawable.khadamatlogo
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

    private void checkBalanceAndSubscribe(int userId, int serviceId, TelecomCard plan) {
        String url = BALANCE_URL + userId;

        StringRequest balanceRequest = new StringRequest(Request.Method.GET, url,
                response -> {
                    try {
                        JSONObject json = new JSONObject(response);
                        boolean success = json.optBoolean("success", false);
                        float balance = Float.parseFloat(json.optString("balance", "0.00"));
                        float price = Float.parseFloat(plan.getPrice());

                        if (!success) {
                            Toast.makeText(this, "Failed to retrieve wallet balance.", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        if (balance <= 0 || balance < price) {
                            Toast.makeText(this, "Insufficient balance: $" + balance + " < $" + price, Toast.LENGTH_LONG).show();
                            return;
                        }

                        proceedToPayment(userId, serviceId, plan.getTitle(), plan.getDescription(), price);
                    } catch (Exception e) {
                        Toast.makeText(this, "Balance check error", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(this, "Network error while checking balance", Toast.LENGTH_SHORT).show()
        );

        Volley.newRequestQueue(this).add(balanceRequest);
    }

    private void proceedToPayment(int userId, int serviceId, String reference, String description, float amount) {
        StringRequest request = new StringRequest(Request.Method.POST, PAY_URL,
                response -> {
                    SharedPreferences walletPrefs = getSharedPreferences("wallet_prefs", MODE_PRIVATE);
                    walletPrefs.edit().putFloat("last_payment_amount", amount).apply();

                    Toast.makeText(this, "Subscribed successfully!", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(this, MyWalletActivity.class));
                    finish();
                },
                error -> Toast.makeText(this, "Payment failed", Toast.LENGTH_SHORT).show()
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
