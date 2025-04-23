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
import com.android.volley.toolbox.Volley;
import com.example.mobile_project_hza2m.databinding.ActivityStreamingServiceProviderBinding;

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

        progressDialog = ProgressDialog.show(this, "", "Submitting...", true);

        VolleyMultipartRequest request = new VolleyMultipartRequest(Request.Method.POST, UPLOAD_URL,
                response -> {
                    progressDialog.dismiss();
                    try {
                        String json = new String(response.data);
                        JSONObject obj = new JSONObject(json);
                        if (obj.getBoolean("success")) {
                            int serviceId = obj.getInt("service_id");
                            SharedPreferences.Editor editor = prefs.edit();
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
                    Toast.makeText(this, "Upload failed: " + error.getMessage(), Toast.LENGTH_LONG).show();
                }) {

            @Override
            public Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("provider_id", String.valueOf(providerId));
                params.put("category", "streaming");
                params.put("title", company);
                params.put("details", support);
                params.put("address", address);
                params.put("region", region);
                params.put("bank_account", bank);
                params.put("logo_url", "uploads/" + uploadedFileName);
                return params;
            }

            @Override
            public Map<String, DataPart> getByteData() {
                Map<String, DataPart> params = new HashMap<>();
                try {
                    InputStream iStream = getContentResolver().openInputStream(selectedLogoUri);
                    ByteArrayOutputStream buffer = new ByteArrayOutputStream();
                    byte[] data = new byte[1024];
                    int bytesRead;
                    while ((bytesRead = iStream.read(data, 0, data.length)) != -1) {
                        buffer.write(data, 0, bytesRead);
                    }

                    String ext = MimeTypeMap.getSingleton().getExtensionFromMimeType(
                            getContentResolver().getType(selectedLogoUri));
                    uploadedFileName = "logo_" + System.currentTimeMillis() + "." + ext;
                    params.put("logo", new DataPart(uploadedFileName, buffer.toByteArray(), "image/" + ext));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return params;
            }
        };

        Volley.newRequestQueue(this).add(request);
    }
}
