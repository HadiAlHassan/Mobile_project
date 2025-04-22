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

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class AddServiceItemActivity extends AppCompatActivity {

    private EditText editServiceName, editServicePrice, editServiceDescription;
    private ImageView imagePreview;
    private Uri selectedImageUri;
    private ProgressDialog progressDialog;

    private final String UPLOAD_URL = Config.BASE_URL + "services/add_service_item.php";

    private final ActivityResultLauncher<Intent> imagePickerLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    selectedImageUri = result.getData().getData();
                    imagePreview.setImageURI(selectedImageUri);
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_service_item);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        editServiceName = findViewById(R.id.editServiceName);
        editServicePrice = findViewById(R.id.editServicePrice);
        editServiceDescription = findViewById(R.id.editServiceDescription);
        imagePreview = findViewById(R.id.imagePreview);
        Button btnUpload = findViewById(R.id.btnUploadIcon);
        Button btnSubmit = findViewById(R.id.btnSubmitService);

        btnUpload.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            imagePickerLauncher.launch(intent);
        });

        btnSubmit.setOnClickListener(v -> uploadServiceItem());
    }

    private void uploadServiceItem() {
        String name = editServiceName.getText().toString().trim();
        String price = editServicePrice.getText().toString().trim();
        String description = editServiceDescription.getText().toString().trim();

        if (name.isEmpty() || price.isEmpty() || selectedImageUri == null) {
            Toast.makeText(this, "Please fill all required fields and upload an image", Toast.LENGTH_SHORT).show();
            return;
        }

        SharedPreferences prefs = getSharedPreferences("AppPrefs", MODE_PRIVATE); // named preferences
        int serviceId = prefs.getInt("service_id", -1); // MUST be service_id, not provider_id

        if (serviceId == -1) {
            Toast.makeText(this, "Service ID not found in AppPrefs", Toast.LENGTH_SHORT).show();
            return;
        }

        progressDialog = ProgressDialog.show(this, "", "Uploading...", true);

        VolleyMultipartRequest multipartRequest = new VolleyMultipartRequest(Request.Method.POST, UPLOAD_URL,
                response -> {
                    progressDialog.dismiss();
                    Toast.makeText(this, "Service item added successfully", Toast.LENGTH_SHORT).show();
                    finish();
                },
                error -> {
                    progressDialog.dismiss();
                    Toast.makeText(this, "Upload failed: " + error.getMessage(), Toast.LENGTH_LONG).show();
                }) {

            @Override
            public Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("service_id", String.valueOf(serviceId));
                params.put("service_name", name);
                params.put("description", description);
                params.put("price", price);
                return params;
            }

            @Override
            public Map<String, DataPart> getByteData() {
                Map<String, DataPart> imageParams = new HashMap<>();
                try {
                    InputStream iStream = getContentResolver().openInputStream(selectedImageUri);
                    byte[] inputData = new byte[iStream.available()];
                    iStream.read(inputData);

                    String mime = getContentResolver().getType(selectedImageUri);
                    String extension = MimeTypeMap.getSingleton().getExtensionFromMimeType(mime);

                    imageParams.put("service_icon", new DataPart("svc_" + System.currentTimeMillis() + "." + extension, inputData));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return imageParams;
            }
        };

        Volley.newRequestQueue(this).add(multipartRequest);
    }
}
