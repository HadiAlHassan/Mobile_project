package com.example.mobile_project_hza2m;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

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

    private final String Base_Url = Config.BASE_URL+"auth/user_signup.php";

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

        // Binding UI elements
        imageView = findViewById(R.id.imageView);
        btnUploadIcon = findViewById(R.id.btnUploadIcon);
        btnSignUp = findViewById(R.id.btnsignup);
        fname = findViewById(R.id.fname);
        mname = findViewById(R.id.mname);
        lname = findViewById(R.id.lname);
        phone = findViewById(R.id.phone);
        address = findViewById(R.id.address); // used as email
        username = findViewById(R.id.username);
        pass = findViewById(R.id.pass);
        pass1 = findViewById(R.id.pass1);
        rb1 = findViewById(R.id.rb1);
        rb2 = findViewById(R.id.rb2);

        // Events
        btnUploadIcon.setOnClickListener(v -> openGallery());
        btnSignUp.setOnClickListener(v -> validateInputs());
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    private void validateInputs() {
        String fnameStr = fname.getText().toString().trim();
        String mnameStr = mname.getText().toString().trim();
        String lnameStr = lname.getText().toString().trim();
        String phoneStr = phone.getText().toString().trim();
        String emailStr = address.getText().toString().trim(); // used as email
        String usernameStr = username.getText().toString().trim();
        String passStr = pass.getText().toString();
        String pass1Str = pass1.getText().toString();

        if (fnameStr.isEmpty()) { fname.setError("Required"); return; }
        if (mnameStr.isEmpty()) { mname.setError("Required"); return; }
        if (lnameStr.isEmpty()) { lname.setError("Required"); return; }
        if (phoneStr.isEmpty()) { phone.setError("Required"); return; }
        if (!phoneStr.matches("^\\d{8}$")) { phone.setError("Must be 8 digits"); return; }
        if (emailStr.isEmpty()) { address.setError("Required"); return; }
        String emailPattern = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
        if (!emailStr.matches(emailPattern)) { address.setError("Invalid email format"); return; }
        if (usernameStr.isEmpty()) { username.setError("Required"); return; }
        if (passStr.isEmpty()) { pass.setError("Required"); return; }
        if (!passStr.matches("^(?=.*[A-Z])(?=.*[a-zA-Z0-9]).{6,}$")) {
            pass.setError("Min 6 chars, 1 uppercase"); return;
        }
        if (pass1Str.isEmpty() || !passStr.equals(pass1Str)) {
            pass1.setError("Passwords don't match"); return;
        }
        if (!rb1.isChecked() && !rb2.isChecked()) {
            showToast("Select your age group."); return;
        }
        if (rb1.isChecked()) {
            showToast("You must be 18+ to sign up."); return;
        }

        showTermsAndRegister();
    }

    private void showTermsAndRegister() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Terms and Conditions");
        builder.setMessage("Please accept the Terms:\n\n" +
                "1. Must be 18+.\n2. Keep credentials secure.\n3. No illegal use.\n4. Fees may apply.\n5. We may suspend accounts.");
        builder.setCancelable(false);
        builder.setPositiveButton("Accept", (dialog, which) -> registerUser());
        builder.setNegativeButton("Reject", (dialog, which) ->
                showToast("Sign-up failed: You must accept the community guidelines."));
        builder.show();
    }

    private void registerUser() {
        String profileImageEncoded = "";
        if (selectedBitmap != null) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            selectedBitmap.compress(Bitmap.CompressFormat.JPEG, 50, baos);
            byte[] imageBytes = baos.toByteArray();
            profileImageEncoded = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        }

        String ageGroup = rb2.isChecked() ? "above" : "below";

        String finalProfileImageEncoded = profileImageEncoded;
        StringRequest request = new StringRequest(Request.Method.POST, Base_Url,
                response -> {
                    Log.d("RAW_RESPONSE", response);  // <-- Add this line
                    try {
                        JSONObject json = new JSONObject(response);
                        if (json.getBoolean("success")) {
                            int userId = json.getInt("user_id");

                            SharedPreferences.Editor editor = getSharedPreferences("AppPrefs", MODE_PRIVATE).edit();
                            editor.putInt("user_id", userId); // ✅ important for profile & auth
                            editor.putString("role", "user"); // ✅ for unified role logic
                            editor.apply();

                            Toast.makeText(this, "Registration successful!", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(this, UserLogin.class));
                        } else {
                            Toast.makeText(this, json.getString("message"), Toast.LENGTH_LONG).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Error parsing response: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
                ,
                error -> Toast.makeText(this, "Network Error: " + error.getMessage(), Toast.LENGTH_LONG).show()
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
                params.put("profile_image", finalProfileImageEncoded);
                return params;
            }
        };

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(request);
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
