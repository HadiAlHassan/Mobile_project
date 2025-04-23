package com.example.mobile_project_hza2m;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.widget.*;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.mobile_project_hza2m.databinding.ActivityTuitionServiceUserBinding;

import java.util.HashMap;
import java.util.Map;

public class TuitionServiceUserActivity extends AppCompatActivity {

    private ActivityTuitionServiceUserBinding binding;
    private EditText editTextUniversity, editTextReference, editTextAmount;
    private Button buttonPayIt, btnUploadScreenshot;
    private ImageView imageViewProof;
    private Uri selectedImageUri;

    private final String PAY_URL = Config.BASE_URL + "services/subscribe_service.php";

    private static final Map<String, Integer> serviceIdMap = new HashMap<>();

    static {
        serviceIdMap.put("Ogero Phone Bills", 1);
        serviceIdMap.put("Insurance", 2);
        serviceIdMap.put("Streaming Services", 3);
        serviceIdMap.put("Telecommunication Services", 4);
        serviceIdMap.put("Tuition Fees", 5);
    }

    private int getServiceIdFor(String serviceName) {
        Integer id = serviceIdMap.get(serviceName);
        return (id != null) ? id : -1;
    }

    private int getUserIdFromPrefs() {
        SharedPreferences prefs = getSharedPreferences("AppPrefs", MODE_PRIVATE); // ← fixed
        return prefs.getInt("user_id", -1); // Default to -1 if not found
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

        SharedPreferences prefs = getSharedPreferences("payment_prefs", MODE_PRIVATE);
        String savedUri = prefs.getString("tuition_image_uri", null);
        if (savedUri != null) {
            imageViewProof.setImageURI(Uri.parse(savedUri));
        }

        btnUploadScreenshot.setOnClickListener(v -> selectImage());

        buttonPayIt.setOnClickListener(v -> {
            String university = editTextUniversity.getText().toString().trim();
            String reference = editTextReference.getText().toString().trim();
            String amount = editTextAmount.getText().toString().trim();

            if (university.isEmpty() || amount.isEmpty()) {
                Toast.makeText(this, "University and amount are required", Toast.LENGTH_SHORT).show();
                return;
            }

            if (selectedImageUri != null) {
                prefs.edit().putString("tuition_image_uri", selectedImageUri.toString()).apply();
            }

            int serviceId = getServiceIdFor("Tuition Fees");
            int userId = getUserIdFromPrefs();

            if (serviceId == -1 || userId == -1) {
                Toast.makeText(this, "Service/User ID not found", Toast.LENGTH_SHORT).show();
                return;
            }

            StringRequest request = new StringRequest(Request.Method.POST, PAY_URL,
                    response -> {
                        // 💾 Save the paid amount for deduction in wallet
                        SharedPreferences appPrefs = getSharedPreferences("AppPrefs", MODE_PRIVATE);
                        SharedPreferences.Editor editor = appPrefs.edit();
                        editor.putFloat("last_payment_amount", Float.parseFloat(amount));
                        editor.apply();

                        Toast.makeText(this, "Payment Sent!", Toast.LENGTH_SHORT).show();

                        // ➡️ Go to wallet
                        startActivity(new Intent(TuitionServiceUserActivity.this, MyWalletActivity.class));
                        finish();
                    },
                    error -> Toast.makeText(this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show()
            ) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();
                    params.put("user_id", String.valueOf(userId));
                    params.put("service_id", String.valueOf(serviceId));
                    params.put("reference", reference);
                    params.put("amount", amount);
                    params.put("description", university);
                    return params;
                }
            };

            Volley.newRequestQueue(this).add(request);
        });

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
}
