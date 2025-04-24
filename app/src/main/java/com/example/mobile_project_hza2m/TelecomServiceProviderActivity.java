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
import com.example.mobile_project_hza2m.databinding.ActivityTelecomServiceProviderBinding;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.json.JSONObject;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class TelecomServiceProviderActivity extends AppCompatActivity {

    EditText editTextCompany, editTextDescription, editTextBankAccount, editTextRegion;
    EditText editTextCategory, editTextAddress;
    ImageView imageViewLogo;
    Uri selectedLogoUri;
    Button buttonSubmit;
    ProgressDialog progressDialog;

    private ActivityTelecomServiceProviderBinding binding;
    private final String UPLOAD_URL = Config.BASE_URL + "services/add_service.php";

    private final ActivityResultLauncher<Intent> imagePickerLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    selectedLogoUri = result.getData().getData();
                    imageViewLogo.setImageURI(selectedLogoUri);
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTelecomServiceProviderBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        editTextCompany = findViewById(R.id.editTextCompany);
        editTextDescription = findViewById(R.id.editTextDescription);
        editTextBankAccount = findViewById(R.id.editTextBankAccount);
        editTextRegion = findViewById(R.id.editTextRegion);
        imageViewLogo = findViewById(R.id.imageViewLogo);
        buttonSubmit = findViewById(R.id.buttonSubmitTelecom);

         editTextCategory = findViewById(R.id.editTextCategory);
         editTextAddress = findViewById(R.id.editTextAddress);



        findViewById(R.id.imageViewUploadLogo).setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            imagePickerLauncher.launch(intent);
        });

        buttonSubmit.setOnClickListener(v -> uploadService());

        binding.fabaddserviceitem.setOnClickListener(view -> {
            Intent i = new Intent(TelecomServiceProviderActivity.this, InsertServiceItemActivity.class);
            startActivity(i);
        });
    }

    private void uploadService() {
        String company = editTextCompany.getText().toString().trim();
        String description = editTextDescription.getText().toString().trim();
        String bank = editTextBankAccount.getText().toString().trim();
        String region = editTextRegion.getText().toString().trim();
        String category = editTextCategory.getText().toString().trim();
        String address = editTextAddress.getText().toString().trim();

        if (company.isEmpty() || description.isEmpty() || bank.isEmpty() || region.isEmpty() || category.isEmpty() || address.isEmpty() || selectedLogoUri == null) {
            Toast.makeText(this, "Please fill all fields and upload a logo", Toast.LENGTH_SHORT).show();
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

        String filename = "logo_" + System.currentTimeMillis() + "." + ext;
        StorageReference logoRef = storageRef.child("provider_logos/" + filename);

        logoRef.putFile(selectedLogoUri)
                .addOnSuccessListener(taskSnapshot ->
                        logoRef.getDownloadUrl().addOnSuccessListener(uri -> {
                            progressDialog.setMessage("Submitting form...");
                            sendTelecomForm(providerId, company, description, bank, region, category, address, uri.toString());
                        }))
                .addOnFailureListener(e -> {
                    progressDialog.dismiss();
                    Toast.makeText(this, "Upload failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }

    private void sendTelecomForm(int providerId, String company, String description, String bank, String region,
                                 String category, String address, String logoUrl) {
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
                        Toast.makeText(this, "Failed to parse server response", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    progressDialog.dismiss();
                    Toast.makeText(this, "Submission failed: " + error.getMessage(), Toast.LENGTH_LONG).show();
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("provider_id", String.valueOf(providerId));
                params.put("category", category);
                params.put("title", company);
                params.put("details", description);
                params.put("address", address);
                params.put("region", region);
                params.put("bank_account", bank);
                params.put("logo_url", logoUrl); // ✅ Firebase URL
                return params;
            }
        };

        Volley.newRequestQueue(this).add(request);
    }


}
