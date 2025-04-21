package com.example.mobile_project_hza2m;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.mobile_project_hza2m.databinding.ActivityAddServiceItemBinding;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class AddServiceItemActivity extends AppCompatActivity {

    private ActivityAddServiceItemBinding binding;
    private EditText editServiceName, editServicePrice;
    private ImageView imagePreview;
    private Button btnUpload, btnSubmit;

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
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddServiceItemBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        editServiceName = findViewById(R.id.editServiceName);
        editServicePrice = findViewById(R.id.editServicePrice);
        imagePreview = findViewById(R.id.imagePreview);
        btnUpload = findViewById(R.id.btnUploadIcon);
        btnSubmit = findViewById(R.id.btnSubmitService);

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

        if (name.isEmpty() || price.isEmpty() || selectedImageUri == null) {
            Toast.makeText(this, "Please fill all fields and upload an image", Toast.LENGTH_SHORT).show();
            return;
        }

        SharedPreferences prefs = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        int providerId = prefs.getInt("provider_id", -1);
        if (providerId == -1) {
            Toast.makeText(this, "Provider ID not found", Toast.LENGTH_SHORT).show();
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
                Map<String, String> map = new HashMap<>();
                map.put("provider_id", String.valueOf(providerId));
                map.put("service_name", name);
                map.put("price", price);
                return map;
            }

            @Override
            public Map<String, DataPart> getByteData() {
                Map<String, DataPart> params = new HashMap<>();
                try {
                    InputStream iStream = getContentResolver().openInputStream(selectedImageUri);
                    byte[] inputData = new byte[iStream.available()];
                    iStream.read(inputData);
                    String extension = MimeTypeMap.getSingleton().getExtensionFromMimeType(getContentResolver().getType(selectedImageUri));
                    params.put("image", new DataPart("service_" + System.currentTimeMillis() + "." + extension, inputData));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return params;
            }
        };

        Volley.newRequestQueue(this).add(multipartRequest);
    }
}