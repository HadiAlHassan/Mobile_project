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
import com.example.mobile_project_hza2m.databinding.ActivityTelecomServiceProviderBinding;

import org.json.JSONObject;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class InsuranceServiceProviderActivity extends AppCompatActivity {

    EditText editTextCompany, editTextDescription, editTextBankAccount, editTextRegion, editTextPrice;
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

        findViewById(R.id.imageViewUploadLogo).setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            imagePickerLauncher.launch(intent);
        });

        buttonSubmit.setOnClickListener(v -> uploadService());

        binding.fabaddserviceitem.setOnClickListener(view -> {
            Intent i = new Intent(InsuranceServiceProviderActivity.this, InsertServiceItemActivity.class);
            startActivity(i);
        });
    }

    private void uploadService() {
        String company = editTextCompany.getText().toString().trim();
        String description = editTextDescription.getText().toString().trim();
        String bank = editTextBankAccount.getText().toString().trim();
        String region = editTextRegion.getText().toString().trim();;

        if (company.isEmpty() || description.isEmpty() || bank.isEmpty() || region.isEmpty() || selectedLogoUri == null) {
            Toast.makeText(this, "All fields and logo are required", Toast.LENGTH_SHORT).show();
            return;
        }

        SharedPreferences prefs = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        int providerId = prefs.getInt("provider_id", -1);
        if (providerId == -1) {
            Toast.makeText(this, "Invalid provider session", Toast.LENGTH_SHORT).show();
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

                            // Save service_id in SharedPreferences
                            SharedPreferences.Editor editor = getSharedPreferences("AppPrefs", MODE_PRIVATE).edit();
                            editor.putInt("service_id", serviceId);
                            editor.apply();

                            Toast.makeText(this, obj.getString("message"), Toast.LENGTH_SHORT).show();
                            // Optional: startActivity(new Intent(this, AddServiceItemActivity.class));
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
            public Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("provider_id", String.valueOf(providerId));
                params.put("category", "telecom");
                params.put("title", company);
                params.put("details", description);
                params.put("address", region);
                params.put("region", region);
                params.put("bank_account", bank);
                return params;
            }

            @Override
            public Map<String, DataPart> getByteData() {
                Map<String, DataPart> params = new HashMap<>();
                try {
                    InputStream iStream = getContentResolver().openInputStream(selectedLogoUri);
                    byte[] logoData = new byte[iStream.available()];
                    iStream.read(logoData);
                    String ext = MimeTypeMap.getSingleton().getExtensionFromMimeType(getContentResolver().getType(selectedLogoUri));
                    params.put("logo", new DataPart("logo_" + System.currentTimeMillis() + "." + ext, logoData, "image/" + ext));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return params;
            }
        };

        Volley.newRequestQueue(this).add(request);
    }
}
