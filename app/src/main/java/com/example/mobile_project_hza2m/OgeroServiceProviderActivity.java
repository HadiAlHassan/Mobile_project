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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class OgeroServiceProviderActivity extends AppCompatActivity {

    private EditText editTextOgeroNumber, editTextBankAccount;
    private ImageView imageViewProviderLogo;
    private Uri selectedLogoUri;
    private ProgressDialog progressDialog;

    private String uploadedFileName = "";
    private final String UPLOAD_URL = Config.BASE_URL + "services/add_service.php";

    private final ActivityResultLauncher<Intent> logoPickerLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    selectedLogoUri = result.getData().getData();
                    imageViewProviderLogo.setImageURI(selectedLogoUri);
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ogero_service_provider);

        imageViewProviderLogo = findViewById(R.id.imageViewProviderLogo);
        ImageView imageViewUpload = findViewById(R.id.imageViewUpload);
        editTextOgeroNumber = findViewById(R.id.edittextHotline); // FIXED: init missing
        editTextBankAccount = findViewById(R.id.editTextBankAccount);
        Button buttonSubmit = findViewById(R.id.buttonSubmitOgero);

        imageViewUpload.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            logoPickerLauncher.launch(intent);
        });

        buttonSubmit.setOnClickListener(v -> uploadOgeroService());
    }

    private void uploadOgeroService() {
        String ogeroNumber = editTextOgeroNumber.getText().toString().trim();
        String bankAccount = editTextBankAccount.getText().toString().trim();

        if (ogeroNumber.isEmpty() || bankAccount.isEmpty() || selectedLogoUri == null) {
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
                            sendOgeroForm(providerId, ogeroNumber, bankAccount, uri.toString());
                        }))
                .addOnFailureListener(e -> {
                    progressDialog.dismiss();
                    Toast.makeText(this, "Upload failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }

    private void sendOgeroForm(int providerId, String ogeroNumber, String bankAccount, String logoUrl) {
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
                params.put("category", "ogero");
                params.put("title", "Ogero Service");
                params.put("details", "Ogero fixed-line landline service");
                params.put("address", ogeroNumber);
                params.put("region", ogeroNumber);
                params.put("bank_account", bankAccount);
                params.put("logo_url", logoUrl); // ✅ Firebase Storage URL
                return params;
            }
        };

        Volley.newRequestQueue(this).add(request);
    }


}
