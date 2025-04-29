package com.example.mobile_project_hza2m;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.mobile_project_hza2m.databinding.ActivityMyWalletBinding;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MyWalletActivity extends AppCompatActivity {

    private ActivityMyWalletBinding binding;
    private TextView balanceText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMyWalletBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        balanceText = findViewById(R.id.balanceText);
        Button btnAddMoney = findViewById(R.id.btnAddMoney);
        btnAddMoney.setOnClickListener(v -> startActivity(new Intent(this, MyBillingActivity.class)));
    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences appPrefs = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        SharedPreferences walletPrefs = getSharedPreferences("wallet_prefs", MODE_PRIVATE);
        int userId = appPrefs.getInt("user_id", -1);
        if (userId > 0) {
           fetchWalletBalance(userId, walletPrefs);
           fetchTransactions(userId);
            fetchSubscriptions(userId);
        }
    }

    private void fetchWalletBalance(int userId, SharedPreferences walletPrefs) {
        String url = Config.BASE_URL + "wallet/get_wallet_balance.php?user_id=" + userId;

        StringRequest request = new StringRequest(Request.Method.GET, url,
                response -> {
                    try {
                        JSONObject json = new JSONObject(response);
                        float balance = Float.parseFloat(json.optString("balance", "0.00"));

                        float lastPayment = walletPrefs.getFloat("last_payment_amount", 0f);
                        if (lastPayment > 0f) {
                            balance -= lastPayment;
                            walletPrefs.edit().remove("last_payment_amount").apply();
                        }

                        balanceText.setText(String.format("Balance: $%.2f", balance));
                    } catch (Exception e) {
                        balanceText.setText("Balance: $0.00");
                        Log.e("MyWallet", "Parse Error", e);
                    }
                },
                error -> balanceText.setText("Balance: $0.00")
        );

        Volley.newRequestQueue(this).add(request);
    }

    private void fetchTransactions(int userId) {
        String url = Config.BASE_URL + "wallet/get_wallet_transactions.php?user_id=" + userId;

        StringRequest request = new StringRequest(Request.Method.GET, url,
                response -> {
                    try {
                        JSONObject json = new JSONObject(response);
                        if (!json.optBoolean("success", false)) {
                            Log.e("MyWallet", "Transaction fetch unsuccessful: " + json.optString("message"));
                            return;
                        }

                        JSONArray array = json.getJSONArray("transactions");
                        List<WalletTransaction> list = new ArrayList<>();
                        for (int i = 0; i < array.length(); i++) {
                            JSONObject obj = array.getJSONObject(i);
                            WalletTransaction txn = new WalletTransaction();

                            // ✅ Safely handle nullable transaction_id
                            txn.transaction_id = obj.isNull("transaction_id") ? -1 : obj.getInt("transaction_id");

                            txn.type = obj.getString("type");
                            txn.amount = obj.getString("amount");
                            txn.description = obj.optString("description", "");

                            // ✅ Fallback for missing service_name
                            if (obj.has("service_name") && !obj.isNull("service_name")) {
                                txn.service_name = obj.getString("service_name");
                            } else {
                                txn.service_name = txn.type.equals("deposit") ? "Wallet Top-up" : "Service Payment";
                            }

                            txn.created_at = obj.getString("created_at");
                            list.add(txn);

                            Log.d("MyWallet2222", "Transaction: " + txn.service_name);
                        }

                        RecyclerView rv = findViewById(R.id.transactionRecycler);
                        rv.setLayoutManager(new LinearLayoutManager(this));
                        rv.setAdapter(new WalletTransactionAdapter(list));

                    } catch (Exception e) {
                        Log.e("MyWallet", "Transaction parse error", e);
                        Toast.makeText(this, "Error loading transactions", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    Log.e("MyWallet", "Transaction fetch failed", error);
                    Toast.makeText(this, "Network error loading transactions", Toast.LENGTH_SHORT).show();
                });

        Volley.newRequestQueue(this).add(request);
    }




    private void fetchSubscriptions(int userId) {
        String url = Config.BASE_URL + "services/get_user_subscriptions.php?user_id=" + userId;

        StringRequest request = new StringRequest(Request.Method.GET, url,
                response -> {
                    try {
                        JSONObject json = new JSONObject(response);
                        if (!json.getBoolean("success")) {
                            Log.e("MyWallet", "Subscription fetch failed: " + json.optString("message"));
                            showEmptyMessage("No subscriptions found.");
                            return;
                        }

                        JSONArray array = json.getJSONArray("subscriptions");
                        if (array.length() == 0) {
                            showEmptyMessage("You have no active subscriptions.");
                            return;
                        }

                        List<UserSubscription> list = new ArrayList<>();
                        for (int i = 0; i < array.length(); i++) {
                            JSONObject obj = array.getJSONObject(i);
                            UserSubscription s = new UserSubscription();
                            s.service_id = obj.optInt("service_id", -1);
                            s.title = obj.optString("title", "Unknown");
                            s.category = obj.optString("category", "General");
                            s.price = obj.optString("price", "0.00");
                            s.status = obj.optString("status", "unknown");
                            s.subscribed_at = obj.optString("subscribed_at", "");
                            s.renewal_date = obj.optString("renewal_date", "");
                            list.add(s);
                        }

                        RecyclerView rv = findViewById(R.id.subscriptionRecycler);
                        rv.setLayoutManager(new LinearLayoutManager(this));
                        rv.setAdapter(new SubscriptionAdapter(list));
                    } catch (Exception e) {
                        Log.e("MyWallet", "Subscription parse error", e);
                        showEmptyMessage("Failed to load subscriptions.");
                    }
                },
                error -> {
                    Log.e("MyWallet", "Subscription fetch failed", error);
                    showEmptyMessage("Could not connect to server.");
                });

        Volley.newRequestQueue(this).add(request);
    }

    private void showEmptyMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_mywallet) return true;
        if (id == R.id.action_myprofile) {
            startActivity(new Intent(this, MyProfileActivity.class));
            return true;
        }

        if (id == R.id.action_homepage) {
            startActivity(new Intent(this, HomeActivity.class));
            return true;
        }

        if (id == R.id.action_settings) {
            startActivity(new Intent(this, Settings.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
