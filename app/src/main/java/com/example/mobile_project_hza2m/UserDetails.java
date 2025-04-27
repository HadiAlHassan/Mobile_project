package com.example.mobile_project_hza2m;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.*;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.jetbrains.annotations.Nullable;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class UserDetails extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 100;

    private ImageView imageView;
    private Bitmap selectedBitmap = null;

    private EditText fname, mname, lname, phone, address, username, pass, pass1;
    private RadioButton rb1, rb2;
    private Button btnSignUp, btnUploadIcon;

    private final String Base_Url = Config.BASE_URL + "auth/user_signup.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_user_details);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        imageView = findViewById(R.id.imageView);
        btnUploadIcon = findViewById(R.id.btnUploadIcon);
        btnSignUp = findViewById(R.id.btnsignup);
        fname = findViewById(R.id.fname);
        mname = findViewById(R.id.mname);
        lname = findViewById(R.id.lname);
        phone = findViewById(R.id.phone);
        address = findViewById(R.id.address);
        username = findViewById(R.id.username);
        pass = findViewById(R.id.pass);
        pass1 = findViewById(R.id.pass1);
        rb1 = findViewById(R.id.rb1);
        rb2 = findViewById(R.id.rb2);

        btnUploadIcon.setOnClickListener(v -> openGallery());
        btnSignUp.setOnClickListener(v -> validateInputs());
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    private void validateInputs() {
        if (fname.getText().toString().isEmpty()) { fname.setError("Required"); return; }
        if (mname.getText().toString().isEmpty()) { mname.setError("Required"); return; }
        if (lname.getText().toString().isEmpty()) { lname.setError("Required"); return; }
        if (phone.getText().toString().isEmpty()) { phone.setError("Required"); return; }
        if (!phone.getText().toString().matches("^\\d{8}$")) { phone.setError("Must be 8 digits"); return; }
        if (address.getText().toString().isEmpty()) { address.setError("Required"); return; }
        if (!address.getText().toString().matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) { address.setError("Invalid email"); return; }
        if (username.getText().toString().isEmpty()) { username.setError("Required"); return; }
        if (pass.getText().toString().isEmpty()) { pass.setError("Required"); return; }
        if (!pass.getText().toString().matches("^(?=.*[A-Z])(?=.*[a-zA-Z0-9]).{6,}$")) {
            pass.setError("Min 6 chars, 1 uppercase"); return;
        }
        if (pass1.getText().toString().isEmpty() || !pass.getText().toString().equals(pass1.getText().toString())) {
            pass1.setError("Passwords don't match"); return;
        }
        if (!rb1.isChecked() && !rb2.isChecked()) { showToast("Select your age group."); return; }
        if (rb1.isChecked()) { showToast("You must be 18+ to sign up."); return; }

        showTermsAndRegister();
    }

    private void showTermsAndRegister() {
        new AlertDialog.Builder(this)
                .setTitle("Terms and Conditions")
                .setMessage("Please accept the Terms:\n\n" +
                        "1. Must be 18+.\n2. Keep credentials secure.\n3. No illegal use.\n4. Fees may apply.\n5. We may suspend accounts.")
                .setCancelable(false)
                .setPositiveButton("Accept", (dialog, which) -> uploadProfileImageToFirebase())
                .setNegativeButton("Reject", (dialog, which) -> showToast("Sign-up cancelled."))
                .show();
    }

    private void uploadProfileImageToFirebase() {
        if (selectedBitmap == null) {
            showToast("Please upload a profile picture");
            return;
        }

        ProgressDialog dialog = ProgressDialog.show(this, "", "Uploading image...", true);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        selectedBitmap.compress(Bitmap.CompressFormat.JPEG, 70, baos);
        byte[] imageData = baos.toByteArray();

        String filename = "profile_" + System.currentTimeMillis() + ".jpg";
        StorageReference storageRef = FirebaseStorage.getInstance().getReference().child("profile_pictures/" + filename);

        storageRef.putBytes(imageData)
                .addOnSuccessListener(taskSnapshot ->
                        storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                            dialog.dismiss();
                            String imageUrl = uri.toString();
                            submitUserToBackend(imageUrl);
                        }))
                .addOnFailureListener(e -> {
                    dialog.dismiss();
                    showToast("Upload failed: " + e.getMessage());
                });
    }

    private void submitUserToBackend(String imageUrl) {
        String ageGroup = rb2.isChecked() ? "above" : "below";

        StringRequest request = new StringRequest(Request.Method.POST, Base_Url,
                response -> {
                    try {
                        JSONObject json = new JSONObject(response);
                        if (json.getBoolean("success")) {
                            int userId = json.getInt("user_id");
                            SharedPreferences.Editor editor = getSharedPreferences("AppPrefs", MODE_PRIVATE).edit();
                            editor.putInt("user_id", userId);
                            editor.putString("role", "user");
                            editor.apply();
                            Toast.makeText(this, "Registration successful!", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(this, UserLogin.class));
                        } else {
                            Toast.makeText(this, json.getString("message"), Toast.LENGTH_LONG).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Error parsing response", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {Toast.makeText(this, "Network Error: " + error.getMessage(), Toast.LENGTH_LONG).show();
                                    Log.e("Network Error", error.getMessage(), error);}
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("first_name", fname.getText().toString().trim());
                params.put("middle_name", mname.getText().toString().trim());
                params.put("last_name", lname.getText().toString().trim());
                params.put("username", username.getText().toString().trim());
                params.put("email", address.getText().toString().trim());
                params.put("password", pass.getText().toString());
                params.put("phone_number", phone.getText().toString().trim());
                params.put("address", address.getText().toString().trim());
                params.put("age_group", ageGroup);
                params.put("profile_image_url", imageUrl); // 🔥 Send Firebase image URL
                return params;
            }
        };

        Volley.newRequestQueue(this).add(request);
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            Uri imageUri = data.getData();
            imageView.setImageURI(imageUri);
            try {
                selectedBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
            } catch (IOException e) {
                e.printStackTrace();
                showToast("Image load failed");
            }
        }
    }
}
