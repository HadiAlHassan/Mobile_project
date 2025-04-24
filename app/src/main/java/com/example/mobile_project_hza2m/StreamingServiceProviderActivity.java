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
import com.example.mobile_project_hza2m.databinding.ActivityStreamingServiceProviderBinding;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class StreamingServiceProviderActivity extends AppCompatActivity {

    EditText editTextCompany, editTextSupport, editTextBankAccount, editTextRegion, editTextAddress;
    ImageView imageViewProviderLogo, imageViewUpload;
    Button btnSubmitStreaming;
    Uri selectedLogoUri;
    String uploadedFileName = "";
    ProgressDialog progressDialog;

    private ActivityStreamingServiceProviderBinding binding;

    private final String UPLOAD_URL = Config.BASE_URL + "services/add_service.php";

    private final ActivityResultLauncher<Intent> logoPickerLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    selectedLogoUri = result.getData().getData();
                    imageViewProviderLogo.setImageURI(selectedLogoUri);
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityStreamingServiceProviderBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Properly reference all views
        editTextCompany = findViewById(R.id.editTextCompany);
        editTextSupport = findViewById(R.id.editTextSupport);
        editTextBankAccount = findViewById(R.id.editTextBankAccount);
        editTextRegion = findViewById(R.id.editTextRegion);
        editTextAddress = findViewById(R.id.editTextAddress);

        imageViewProviderLogo = findViewById(R.id.imageViewProviderLogo);
        imageViewUpload = findViewById(R.id.imageViewUpload);
        btnSubmitStreaming = findViewById(R.id.buttonSubmitStreaming);

        imageViewUpload.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            logoPickerLauncher.launch(intent);
        });

        btnSubmitStreaming.setOnClickListener(v -> uploadStreamingService());
    }

    private void uploadStreamingService() {
        String company = editTextCompany.getText().toString().trim();
        String support = editTextSupport.getText().toString().trim();
        String bank = editTextBankAccount.getText().toString().trim();
        String region = editTextRegion.getText().toString().trim();
        String address = editTextAddress.getText().toString().trim();

        if (company.isEmpty() || support.isEmpty() || bank.isEmpty() || region.isEmpty() || address.isEmpty() || selectedLogoUri == null) {
            Toast.makeText(this, "Please fill all fields and upload a logo", Toast.LENGTH_SHORT).show();
            return;
        }

        SharedPreferences prefs = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        int providerId = prefs.getInt("provider_id", -1);
        if (providerId == -1) {
            Toast.makeText(this, "Provider ID not found", Toast.LENGTH_SHORT).show();
            return;
        }

        progressDialog = ProgressDialog.show(this, "", "Uploading logo...", true);

        // Firebase Storage upload
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();

        String ext = MimeTypeMap.getSingleton().getExtensionFromMimeType(getContentResolver().getType(selectedLogoUri));
        if (ext == null) ext = "jpg";

        uploadedFileName = "logo_" + System.currentTimeMillis() + "." + ext;
        StorageReference logoRef = storageRef.child("provider_logos/" + uploadedFileName);

        logoRef.putFile(selectedLogoUri)
                .addOnSuccessListener(taskSnapshot -> {
                    logoRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        progressDialog.setMessage("Submitting form...");
                        sendStreamingServiceForm(providerId, company, support, bank, region, address, uri.toString());
                    });
                })
                .addOnFailureListener(e -> {
                    progressDialog.dismiss();
                    Toast.makeText(this, "Upload failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }

    private void sendStreamingServiceForm(int providerId, String company, String support, String bank, String region, String address, String logoUrl) {
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
                        Toast.makeText(this, "Response error", Toast.LENGTH_SHORT).show();
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
                params.put("category", "streaming ");
                params.put("title", company);
                params.put("details", support);
                params.put("address", address);
                params.put("region", region);
                params.put("bank_account", bank);
                params.put("logo_url", logoUrl); // ✅ Now uses full Firebase URL
                return params;
            }
        };

        Volley.newRequestQueue(this).add(request);
    }


}
