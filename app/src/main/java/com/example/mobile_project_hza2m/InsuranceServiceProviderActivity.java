package com.example.mobile_project_hza2m;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.webkit.MimeTypeMap;
import android.widget.*;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.mobile_project_hza2m.databinding.ActivityInsuranceServiceProviderBinding;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class InsuranceServiceProviderActivity extends AppCompatActivity {

    EditText editTextCompany, editTextCoverage, editTextBankAccount, editTextRegion, editTextAddress;
    ImageView imageViewLogo;
    Uri selectedLogoUri;
    Button buttonSubmit;
    ProgressDialog progressDialog;

    private String uploadedFileName = "";
    private ActivityInsuranceServiceProviderBinding binding;

    private final String UPLOAD_URL = Config.BASE_URL + "services/add_service.php";

    private final ActivityResultLauncher<Intent> imagePickerLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    selectedLogoUri = result.getData().getData();
                    imageViewLogo.setImageURI(selectedLogoUri);
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityInsuranceServiceProviderBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Match layout IDs
        editTextCompany = findViewById(R.id.editTextCompany);
        editTextCoverage = findViewById(R.id.editTextCoverage);
        editTextBankAccount = findViewById(R.id.editTextBankAccount);
        editTextRegion = findViewById(R.id.editTextRegion);
        editTextAddress = findViewById(R.id.editTextAddress);
        imageViewLogo = findViewById(R.id.imageViewProviderLogo);
        buttonSubmit = findViewById(R.id.buttonSubmitInsurance);

        findViewById(R.id.imageViewUpload).setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            imagePickerLauncher.launch(intent);
        });

        buttonSubmit.setOnClickListener(v -> uploadService());

        binding.fabaddserviceitem.setOnClickListener(view -> {
            Intent i = new Intent(this, InsertServiceItemActivity.class);
            startActivity(i);
        });
    }

    private void uploadService() {
        String company = editTextCompany.getText().toString().trim();
        String description = editTextCoverage.getText().toString().trim();
        String bank = editTextBankAccount.getText().toString().trim();
        String region = editTextRegion.getText().toString().trim();
        String address = editTextAddress.getText().toString().trim();

        if (company.isEmpty() || description.isEmpty() || bank.isEmpty() ||
                region.isEmpty() || address.isEmpty() || selectedLogoUri == null) {
            Toast.makeText(this, "All fields and logo are required", Toast.LENGTH_SHORT).show();
            return;
        }

        SharedPreferences prefs = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        int providerId = prefs.getInt("provider_id", -1);
        if (providerId == -1) {
            Toast.makeText(this, "Invalid provider session", Toast.LENGTH_SHORT).show();
            return;
        }

        progressDialog = ProgressDialog.show(this, "", "Uploading logo...", true);

        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();

        String ext = MimeTypeMap.getSingleton().getExtensionFromMimeType(getContentResolver().getType(selectedLogoUri));
        if (ext == null) ext = "jpg";

        uploadedFileName = "logo_" + System.currentTimeMillis() + "." + ext;
        StorageReference logoRef = storageRef.child("provider_logos/" + uploadedFileName);

        logoRef.putFile(selectedLogoUri)
                .addOnSuccessListener(taskSnapshot ->
                        logoRef.getDownloadUrl().addOnSuccessListener(uri -> {
                            progressDialog.setMessage("Submitting form...");
                            sendInsuranceForm(providerId, company, description, bank, region, address, uri.toString());
                        }))
                .addOnFailureListener(e -> {
                    progressDialog.dismiss();
                    Toast.makeText(this, "Upload failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }

    private void sendInsuranceForm(int providerId, String company, String description, String bank, String region,
                                   String address, String logoUrl) {
        StringRequest request = new StringRequest(Request.Method.POST, UPLOAD_URL,
                response -> {
                    progressDialog.dismiss();
                    try {
                        JSONObject obj = new JSONObject(response);
                        if (obj.getBoolean("success")) {
                            int serviceId = obj.getInt("service_id");
                            SharedPreferences.Editor editor = getSharedPreferences("AppPrefs", MODE_PRIVATE).edit();
                            editor.putInt("service_id", serviceId);
                            editor.apply();
                            Toast.makeText(this, obj.getString("message"), Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            Toast.makeText(this, obj.getString("message"), Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Error parsing response", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    progressDialog.dismiss();
                    Toast.makeText(this, "Upload failed: " + error.getMessage(), Toast.LENGTH_LONG).show();
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("provider_id", String.valueOf(providerId));
                params.put("category", "insurance");
                params.put("title", company);
                params.put("details", description);
                params.put("address", address);
                params.put("region", region);
                params.put("bank_account", bank);
                params.put("logo_url", logoUrl); // ✅ Firebase logo URL
                return params;
            }
        };

        Volley.newRequestQueue(this).add(request);
    }

}
