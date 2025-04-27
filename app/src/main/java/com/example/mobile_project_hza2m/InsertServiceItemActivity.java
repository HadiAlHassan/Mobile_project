package com.example.mobile_project_hza2m;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.snackbar.Snackbar;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.mobile_project_hza2m.databinding.ActivityInsertServiceItemBinding;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class InsertServiceItemActivity extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;
    private ActivityInsertServiceItemBinding binding;
    private EditText editServiceName, editServicePrice, editServiceDescription;
    private ImageView imagePreview;
    private Uri selectedImageUri;

    Button btnUpload, btnSubmit;

    private final ActivityResultLauncher<Intent> imagePickerLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    selectedImageUri = result.getData().getData();
                    imagePreview.setImageURI(selectedImageUri);
                }
            });

    private final String UPLOAD_URL = Config.BASE_URL + "services/add_service_item.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityInsertServiceItemBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        editServiceName = findViewById(R.id.editServiceName);
        editServicePrice = findViewById(R.id.editServicePrice);
        editServiceDescription = findViewById(R.id.editServiceDescription);
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
        String description = editServiceDescription.getText().toString().trim();

        if (name.isEmpty() || price.isEmpty() || selectedImageUri == null) {
            Toast.makeText(this, "Please fill all required fields and upload an image", Toast.LENGTH_SHORT).show();
            return;
        }

        SharedPreferences prefs = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        int serviceId = prefs.getInt("service_id", -1);

        if (serviceId == -1) {
            Toast.makeText(this, "Service ID not found in AppPrefs", Toast.LENGTH_SHORT).show();
            return;
        }

        // ✅ Firebase Storage upload
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();

        String extension = MimeTypeMap.getSingleton()
                .getExtensionFromMimeType(getContentResolver().getType(selectedImageUri));
        if (extension == null) extension = "jpg";

        String filename = "svc_" + System.currentTimeMillis() + "." + extension;
        StorageReference imageRef = storageRef.child("service_icons/" + filename);

        imageRef.putFile(selectedImageUri)
                .addOnSuccessListener(taskSnapshot -> {
                    imageRef.getDownloadUrl().addOnSuccessListener(downloadUri -> {
                        String downloadUrl = downloadUri.toString();
                        sendMetadataToBackend(serviceId, name, price, description, downloadUrl);
                    });
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Image upload failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }
    private void sendMetadataToBackend(int serviceId, String name, String price, String description, String imageUrl) {
        StringRequest request = new StringRequest(Request.Method.POST, Config.BASE_URL + "services/add_service_item.php",
                response -> {
                    Toast.makeText(this, "Service item added successfully", Toast.LENGTH_SHORT).show();
                    setResult(RESULT_OK);
                    finish();
                },
                error -> {
                    Toast.makeText(this, "Upload failed: " + error.getMessage(), Toast.LENGTH_LONG).show();
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("service_id", String.valueOf(serviceId));
                params.put("item_name", name);
                params.put("item_description", description);
                params.put("item_price", price);
                params.put("icon_url", imageUrl); // ✅ full Firebase image URL
                return params;
            }
        };

        Volley.newRequestQueue(this).add(request);
    }



}