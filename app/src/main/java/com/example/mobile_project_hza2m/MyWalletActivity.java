package com.example.mobile_project_hza2m;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;

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

        btnAddMoney.setOnClickListener(v -> {
            Intent intent = new Intent(MyWalletActivity.this, MyBillingActivity.class);
            startActivity(intent);  // No need for startActivityForResult
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences prefs = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        int userId = prefs.getInt("user_id", -1);
        if (userId > 0) {
            fetchWalletBalance(userId);
            fetchTransactions(userId);
            fetchSubscriptions(userId);
        }
    }

    private void fetchWalletBalance(int userId) {
        String url = Config.BASE_URL+"wallet/get_wallet_balance.php?user_id=" + userId;
        StringRequest request = new StringRequest(Request.Method.GET, url,
                response -> {
                    try {
                        JSONObject json = new JSONObject(response);
                        if (json.getBoolean("success")) {
                            double balance = json.getDouble("balance");
                            balanceText.setText(String.format("Balance: $%.2f", balance));
                        } else {
                            balanceText.setText("Balance: $0.00");
                        }
                    } catch (Exception e) {
                        Log.e("MyWallet", "Balance parse error", e);
                        balanceText.setText("Balance: $0.00");
                    }
                },
                error -> {
                    Log.e("MyWallet", "Balance fetch failed", error);
                    balanceText.setText("Balance: $0.00");
                });

        Volley.newRequestQueue(this).add(request);
    }

    private void fetchTransactions(int userId) {
        String url = Config.BASE_URL+"wallet/get_wallet_transactions.php?user_id=" + userId;
        StringRequest request = new StringRequest(Request.Method.GET, url,
                response -> {
                    try {
                        JSONObject json = new JSONObject(response);
                        if (json.getBoolean("success")) {
                            JSONArray array = json.getJSONArray("transactions");
                            List<WalletTransaction> list = new ArrayList<>();
                            for (int i = 0; i < array.length(); i++) {
                                JSONObject obj = array.getJSONObject(i);
                                WalletTransaction txn = new WalletTransaction();
                                txn.transaction_id = obj.getInt("transaction_id");
                                txn.type = obj.getString("type");
                                txn.amount = obj.getString("amount");
                                txn.description = obj.optString("description", "");
                                txn.service_name = obj.optString("service_name", "");
                                txn.created_at = obj.getString("created_at");
                                list.add(txn);
                            }

                            RecyclerView rv = findViewById(R.id.transactionRecycler);
                            rv.setLayoutManager(new LinearLayoutManager(this));
                            rv.setAdapter(new WalletTransactionAdapter(list));
                        }
                    } catch (Exception e) {
                        Log.e("MyWallet", "Transaction parse error", e);
                    }
                },
                error -> Log.e("MyWallet", "Transaction fetch failed", error)
        );

        Volley.newRequestQueue(this).add(request);
    }

    private void fetchSubscriptions(int userId) {
        String url = Config.BASE_URL+"services/get_user_subscriptions.php?user_id=" + userId;
        StringRequest request = new StringRequest(Request.Method.GET, url,
                response -> {
                    try {
                        JSONObject json = new JSONObject(response);
                        if (json.getBoolean("success")) {
                            JSONArray array = json.getJSONArray("subscriptions");
                            List<UserSubscription> list = new ArrayList<>();
                            for (int i = 0; i < array.length(); i++) {
                                JSONObject obj = array.getJSONObject(i);
                                UserSubscription s = new UserSubscription();
                                s.service_id = obj.getInt("service_id");
                                s.title = obj.getString("title");
                                s.category = obj.getString("category");
                                s.price = obj.getString("price");
                                s.status = obj.getString("status");
                                s.subscribed_at = obj.getString("subscribed_at");
                                s.renewal_date = obj.getString("renewal_date");
                                list.add(s);
                            }

                            RecyclerView rv = findViewById(R.id.subscriptionRecycler);
                            rv.setLayoutManager(new LinearLayoutManager(this));
                            rv.setAdapter(new SubscriptionAdapter(list));
                        }
                    } catch (Exception e) {
                        Log.e("MyWallet", "Subscription parse error", e);
                    }
                },
                error -> Log.e("MyWallet", "Subscription fetch failed", error)
        );

        Volley.newRequestQueue(this).add(request);
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
        return super.onOptionsItemSelected(item);
    }
}
