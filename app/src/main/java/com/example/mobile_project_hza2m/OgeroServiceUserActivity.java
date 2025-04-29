package com.example.mobile_project_hza2m;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.*;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.mobile_project_hza2m.databinding.ActivityOgeroServiceUserBinding;

import org.json.JSONObject;
import java.util.HashMap;
import java.util.Map;

public class OgeroServiceUserActivity extends AppCompatActivity {

    private ActivityOgeroServiceUserBinding binding;
    private EditText editTextLineNumber, editTextReference, editTextAmount;
    private Button buttonPayNow;

    private static final String PAY_URL = Config.BASE_URL + "services/subscribe_service.php";
    private static final String BALANCE_URL = Config.BASE_URL + "wallet/get_wallet_balance.php?user_id=";
    private static final String OGERO_SERVICE_URL = Config.BASE_URL + "services/get_ogero_service.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityOgeroServiceUserBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);

        editTextLineNumber = findViewById(R.id.editTextLineNumber);
        editTextReference = findViewById(R.id.editTextReference);
        editTextAmount = findViewById(R.id.editTextAmount);
        buttonPayNow = findViewById(R.id.buttonPayNow);

        buttonPayNow.setOnClickListener(v -> {
            String lineNumber = editTextLineNumber.getText().toString().trim();
            String reference = editTextReference.getText().toString().trim();
            String amountStr = editTextAmount.getText().toString().trim();

            if (lineNumber.isEmpty() || amountStr.isEmpty()) {
                showAlert("Please enter your line number and amount.");
                return;
            }

            if (!TextUtils.isDigitsOnly(amountStr.replace(".", ""))) {
                showAlert("Amount must be a valid number.");
                return;
            }

            float amount;
            try {
                amount = Float.parseFloat(amountStr);
            } catch (NumberFormatException e) {
                showAlert("Invalid amount format.");
                return;
            }

            int userId = getUserIdFromPrefs();
            if (userId == -1) {
                showAlert("User ID not found. Please login again.");
                return;
            }

            // Fetch Ogero service_id dynamically before proceeding
            fetchOgeroServiceIdAndPay(userId, reference, lineNumber, amount);
        });
    }

    private void fetchOgeroServiceIdAndPay(int userId, String reference, String lineNumber, float amount) {
        StringRequest getServiceRequest = new StringRequest(Request.Method.GET, OGERO_SERVICE_URL,
                response -> {
                    try {
                        JSONObject json = new JSONObject(response);
                        if (json.getBoolean("success")) {
                            int serviceId = json.getInt("service_id");
                            Log.d("OGERO_SERVICE", "Fetched service_id: " + serviceId);
                            checkBalanceAndPay(userId, serviceId, reference, lineNumber, amount);
                        } else {
                            showAlert("Ogero service not available. Try again later.");
                        }
                    } catch (Exception e) {
                        Log.e("OGERO_SERVICE_ERROR", e.getMessage());
                        showAlert("Error fetching Ogero service.");
                    }
                },
                error -> {
                    Log.e("OGERO_SERVICE_FAIL", error.toString());
                    showAlert("Network error while fetching service.");
                });

        Volley.newRequestQueue(this).add(getServiceRequest);
    }

    private void checkBalanceAndPay(int userId, int serviceId, String reference, String lineNumber, float amount) {
        StringRequest balanceRequest = new StringRequest(Request.Method.GET, BALANCE_URL + userId,
                response -> {
                    Log.d("PAY_PARAMS", "user_id=" + userId + " service_id=" + serviceId + " ref=" + reference + " amt=" + amount);
                    try {
                        JSONObject json = new JSONObject(response);
                        float balance = Float.parseFloat(json.optString("balance", "0.00"));
                        if (!json.getBoolean("success") || balance < amount) {
                            showAlert("Insufficient balance. Wallet: $" + balance);
                        } else {
                            proceedToPayment(userId, serviceId, reference, lineNumber, amount);
                        }
                    } catch (Exception e) {
                        showAlert("Error checking balance.");
                        Log.e("BALANCE_ERROR", e.getMessage());
                    }
                },
                error -> showAlert("Network error while checking balance")
        );

        Volley.newRequestQueue(this).add(balanceRequest);
    }

    private void proceedToPayment(int userId, int serviceId, String reference, String lineNumber, float amount) {
        StringRequest request = new StringRequest(Request.Method.POST, PAY_URL,
                response -> {
                    Log.d("PAYMENT_RESPONSE", response);
                    try {
                        JSONObject json = new JSONObject(response);
                        if (json.getBoolean("success")) {
                            SharedPreferences prefs = getSharedPreferences("wallet_prefs", MODE_PRIVATE);
                            prefs.edit().putFloat("last_payment_amount", amount).apply();

                            Toast.makeText(this, "Payment successful!", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(this, MyWalletActivity.class));
                            finish();
                        } else {
                            showAlert(json.optString("message", "Payment failed"));
                        }
                    } catch (Exception e) {
                        Log.e("PAYMENT_ERROR", "Parse error: " + e.getMessage());
                        showAlert("Server response error.");
                    }
                },
                error -> {
                    Log.e("PAYMENT_REQUEST_FAIL", error.toString());
                    showAlert("Failed to connect to payment service");
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("user_id", String.valueOf(userId));
                params.put("service_id", String.valueOf(serviceId));
                params.put("reference", reference);
                params.put("amount", String.valueOf(amount));
                params.put("description", lineNumber); // logged in wallet_transactions
                return params;
            }
        };

        Volley.newRequestQueue(this).add(request);
    }

    private int getUserIdFromPrefs() {
        SharedPreferences prefs = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        return prefs.getInt("user_id", -1);
    }

    private void showAlert(String message) {
        new AlertDialog.Builder(this)
                .setMessage(message)
                .setPositiveButton("OK", null)
                .show();
    }
}
