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
import com.example.mobile_project_hza2m.databinding.ActivityTuitionServiceProviderBinding;

import org.json.JSONObject;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class TuitionServiceProviderActivity extends AppCompatActivity {

    private ActivityTuitionServiceProviderBinding binding;

    EditText editTextUniversityName, editTextUniversityDetails, editTextBankAccount;
    Button buttonSaveTuitionService;
    private Uri selectedLogoUri;
    private ProgressDialog progressDialog;
    ImageView imageViewLogo, imageViewUploadLogo;;
    private final String UPLOAD_URL = Config.BASE_URL + "services/add_service.php";

    private final ActivityResultLauncher<Intent> logoPickerLauncher = registerForActivityResult(
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
        binding = ActivityTuitionServiceProviderBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        imageViewLogo = findViewById(R.id.imageViewLogo);
        imageViewUploadLogo = findViewById(R.id.imageViewUploadLogo);
        editTextUniversityName = findViewById(R.id.editTextUniversityName);
        editTextUniversityDetails = findViewById(R.id.editTextUniversityDetails);
        editTextBankAccount = findViewById(R.id.editTextBankAccount);
        buttonSaveTuitionService = findViewById(R.id.buttonSaveTuitionService);

        imageViewUploadLogo.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            logoPickerLauncher.launch(intent);
        });

        buttonSaveTuitionService.setOnClickListener(v -> uploadTuitionService());
    }

    private void uploadTuitionService() {
        String universityName = editTextUniversityName.getText().toString().trim();
        String details = editTextUniversityDetails.getText().toString().trim();
        String bankAccount = editTextBankAccount.getText().toString().trim();
        String region = "N/A"; // You can adjust or add a region input field later
        String price = "0.00"; // Tuition service might not have upfront price

        if (universityName.isEmpty() || details.isEmpty() || bankAccount.isEmpty() || selectedLogoUri == null) {
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
                            int serviceId = obj.optInt("service_id", -1);
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
                params.put("category", "tuition");
                params.put("title", universityName);
                params.put("details", details);
                params.put("price", price);
                params.put("address", "University Area");
                params.put("region", region);
                params.put("bank_account", bankAccount);
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
