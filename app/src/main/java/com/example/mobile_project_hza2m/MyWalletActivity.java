package com.example.mobile_project_hza2m;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
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

    private static final int BILLING_REQUEST_CODE = 101;

    private Button btnAddMoney;
    private TextView balanceText;
    private ActivityMyWalletBinding  binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMyWalletBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        btnAddMoney = findViewById(R.id.btnAddMoney);
        balanceText = findViewById(R.id.balanceText);

        SharedPreferences prefs = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        int userId = prefs.getInt("user_id", -1);

        btnAddMoney.setOnClickListener(v -> {
            Intent i = new Intent(MyWalletActivity.this, MyBillingActivity.class);
            startActivityForResult(i, BILLING_REQUEST_CODE); // 👈 Use intent
        });

        if (userId > 0) {
            fetchWalletBalance(userId);
            fetchTransactions(userId);
            fetchSubscriptions(userId);
        } else {
            balanceText.setText("Unable to fetch wallet (no user ID)");
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) return true;
        if (id == R.id.action_mywallet) startActivity(new Intent(this, MyWalletActivity.class));
        if (id == R.id.action_myprofile) startActivity(new Intent(this, MyProfileActivity.class));
        return super.onOptionsItemSelected(item);
    }

    private void fetchWalletBalance(int userId) {
        String url = "http://192.168.0.101/Mobile_submodule_backend/PHP/wallet/get_wallet_balance.php?user_id=" + userId;
        StringRequest request = new StringRequest(Request.Method.GET, url,
                response -> {
                    try {
                        JSONObject json = new JSONObject(response);
                        if (json.getBoolean("success")) {
                            double balanceValue = json.getDouble("balance");
                            balanceText.setText(String.format("Balance: $%.2f", balanceValue));
                        } else {
                            balanceText.setText("Balance: $0.00");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        balanceText.setText("Balance: $0.00");
                    }
                },
                error -> {
                    error.printStackTrace();
                    balanceText.setText("Balance: $0.00");
                });

        Volley.newRequestQueue(this).add(request);
    }

    private void fetchTransactions(int userId) {
        String url = "http://192.168.0.101/Mobile_submodule_backend/PHP/wallet/get_wallet_transactions.php?user_id=" + userId;
        StringRequest request = new StringRequest(Request.Method.GET, url,
                response -> {
                    try {
                        JSONObject json = new JSONObject(response);
                        if (json.getBoolean("success")) {
                            JSONArray arr = json.getJSONArray("transactions");
                            List<WalletTransaction> list = new ArrayList<>();
                            for (int i = 0; i < arr.length(); i++) {
                                JSONObject obj = arr.getJSONObject(i);
                                WalletTransaction txn = new WalletTransaction();
                                txn.transaction_id = obj.getInt("transaction_id");
                                txn.type = obj.getString("type");
                                txn.amount = obj.getString("amount");
                                txn.description = obj.optString("description", "");
                                txn.service_name = obj.optString("service_name", "");
                                txn.created_at = obj.getString("created_at");
                                list.add(txn);
                            }
                            WalletTransactionAdapter adapter = new WalletTransactionAdapter(list);
                            RecyclerView rv = findViewById(R.id.transactionRecycler);
                            rv.setLayoutManager(new LinearLayoutManager(this));
                            rv.setAdapter(adapter);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                },
                error -> error.printStackTrace());

        Volley.newRequestQueue(this).add(request);
    }

    private void fetchSubscriptions(int userId) {
        String url = "http://192.168.0.101/Mobile_submodule_backend/PHP/services/get_user_subscriptions.php?user_id=" + userId;
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
                        e.printStackTrace();
                    }
                },
                error -> error.printStackTrace());

        Volley.newRequestQueue(this).add(request);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == BILLING_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            SharedPreferences prefs = getSharedPreferences("AppPrefs", MODE_PRIVATE);
            int userId = prefs.getInt("user_id", -1);
            if (userId > 0) {
                fetchWalletBalance(userId);
                fetchTransactions(userId);
                fetchSubscriptions(userId);
            }
        }
    }

}
