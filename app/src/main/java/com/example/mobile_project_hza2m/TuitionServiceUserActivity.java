// TuitionServiceUserActivity.java
package com.example.mobile_project_hza2m;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.*;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.mobile_project_hza2m.databinding.ActivityTuitionServiceUserBinding;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.Map;

public class TuitionServiceUserActivity extends AppCompatActivity {

    private ActivityTuitionServiceUserBinding binding;
    private EditText editTextUniversity, editTextReference, editTextAmount;
    private Button buttonPayIt, btnUploadScreenshot;
    private ImageView imageViewProof;
    private Uri selectedImageUri;

    private final String PAY_URL = Config.BASE_URL + "services/subscribe_service.php";
    private final String BALANCE_URL = Config.BASE_URL + "wallet/get_wallet_balance.php?user_id=";

    private static final Map<String, Integer> serviceIdMap = new HashMap<>();

    static {
        serviceIdMap.put("ogero", 1);
        serviceIdMap.put("insurance", 2);
        serviceIdMap.put("streaming", 3);
        serviceIdMap.put("telecommunication", 4);
        serviceIdMap.put("tuition", 5);
    }

    private int getServiceIdFor(String serviceName) {
        Integer id = serviceIdMap.get(serviceName.toLowerCase());
        return (id != null) ? id : -1;
    }

    private int getUserIdFromPrefs() {
        SharedPreferences prefs = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        return prefs.getInt("user_id", -1);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTuitionServiceUserBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);

        editTextUniversity = findViewById(R.id.editTextUniversity);
        editTextReference = findViewById(R.id.editTextReference);
        editTextAmount = findViewById(R.id.editTextAmount);
        imageViewProof = findViewById(R.id.imageViewProof);
        btnUploadScreenshot = findViewById(R.id.btnUploadScreenshot);
        buttonPayIt = findViewById(R.id.buttonPayIt);



        btnUploadScreenshot.setOnClickListener(v -> selectImage());

        buttonPayIt.setOnClickListener(v -> {
            String university = editTextUniversity.getText().toString().trim();
            String reference = editTextReference.getText().toString().trim();
            String amountStr = editTextAmount.getText().toString().trim();

            if (university.isEmpty() || amountStr.isEmpty()) {
                showAlert("Please enter university name and amount.");
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

            if (selectedImageUri == null) {
                showAlert("Please upload a payment screenshot.");
                return;
            }

            int serviceId = getServiceIdFor("tuition");
            int userId = getUserIdFromPrefs();

            if (serviceId == -1 || userId == -1) {
                showAlert("User or service ID not found.");
                return;
            }

            checkBalanceAndPay(userId, serviceId, reference, university, amount);
        });
    }

    private void checkBalanceAndPay(int userId, int serviceId, String reference, String university, float amount) {
        StringRequest balanceRequest = new StringRequest(Request.Method.GET, BALANCE_URL + userId,
                response -> {
                    try {
                        JSONObject json = new JSONObject(response);
                        float balance = Float.parseFloat(json.optString("balance", "0.00"));
                        if (!json.getBoolean("success") || balance < amount) {
                            showAlert("Insufficient balance. Wallet: $" + balance);
                        } else {
                            proceedToPayment(userId, serviceId, reference, university, amount);
                        }
                    } catch (Exception e) {
                        showAlert("Error checking balance.");
                        Log.e("BALANCE_ERR", e.getMessage());
                    }
                },
                error -> showAlert("Network error while checking balance")
        );

        Volley.newRequestQueue(this).add(balanceRequest);
    }

    private void proceedToPayment(int userId, int serviceId, String reference, String university, float amount) {
        StringRequest request = new StringRequest(Request.Method.POST, PAY_URL,
                response -> {
                    try {
                        JSONObject json = new JSONObject(response);
                        if (json.getBoolean("success")) {
                            SharedPreferences walletPrefs = getSharedPreferences("WalletPrefs", MODE_PRIVATE);
                            walletPrefs.edit()
                                    .putFloat("last_payment_amount", amount)
                                    .putLong("last_payment_time", System.currentTimeMillis())
                                    .apply();

                            SharedPreferences prefs = getSharedPreferences("AppPrefs", MODE_PRIVATE);
                            prefs.edit().remove("tuition_image_uri").apply();

                            startActivity(new Intent(this, MyWalletActivity.class));
                            finish();
                        } else {
                            showAlert("Payment failed: " + json.optString("message", "Unknown error"));
                        }
                    } catch (Exception e) {
                        showAlert("Response error: " + e.getMessage());
                    }
                }

                ,
                error -> showAlert("Error: " + error.getMessage())
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("user_id", String.valueOf(userId));
                params.put("service_id", String.valueOf(serviceId));
                params.put("reference", reference);
                params.put("amount", String.valueOf(amount));
                params.put("description", university);
                return params;
            }
        };

        Volley.newRequestQueue(this).add(request);
    }

    private void selectImage() {
        imagePickerLauncher.launch("image/*");
    }

    private final ActivityResultLauncher<String> imagePickerLauncher = registerForActivityResult(
            new ActivityResultContracts.GetContent(),
            uri -> {
                if (uri != null) {
                    selectedImageUri = uri;
                    imageViewProof.setImageURI(uri);
                }
            }
    );

    private void showAlert(String message) {
        new AlertDialog.Builder(this)
                .setMessage(message)
                .setPositiveButton("OK", null)
                .show();
    }
}
