package com.example.mobile_project_hza2m;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.*;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.mobile_project_hza2m.databinding.ActivityOgeroServiceUserBinding;

import java.util.HashMap;
import java.util.Map;

public class OgeroServiceUserActivity extends AppCompatActivity {

    private ActivityOgeroServiceUserBinding binding;
    private EditText editTextLineNumber, editTextReference, editTextAmount;
    private Button buttonPayNow;

    private final String PAY_URL = Config.BASE_URL + "services/subscribe_service.php";

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
            String amount = editTextAmount.getText().toString().trim();

            if (lineNumber.isEmpty() || amount.isEmpty()) {
                showAlert("Please enter your line number and amount.");
                return;
            }

            if (!TextUtils.isDigitsOnly(amount)) {
                showAlert("Amount must be a valid number.");
                return;
            }

            int userId = getUserIdFromPrefs();
            int serviceId = 1; // Ogero service ID

            if (userId == -1) {
                showAlert("User ID not found. Please login again.");
                return;
            }

            buttonPayNow.setEnabled(false);

            StringRequest request = new StringRequest(Request.Method.POST, PAY_URL,
                    response -> {
                        showAlert("Payment sent successfully!");
                        buttonPayNow.setEnabled(true);
                    },
                    error -> {
                        showAlert("Error: " + error.getMessage());
                        buttonPayNow.setEnabled(true);
                    }) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();
                    params.put("user_id", String.valueOf(userId));
                    params.put("service_id", String.valueOf(serviceId));
                    params.put("reference", reference);
                    params.put("amount", amount);
                    params.put("description", lineNumber);
                    return params;
                }
            };

            Volley.newRequestQueue(this).add(request);
        });
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
