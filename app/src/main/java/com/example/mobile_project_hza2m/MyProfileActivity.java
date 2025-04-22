package com.example.mobile_project_hza2m;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.mobile_project_hza2m.databinding.ActivityMyProfileBinding;
import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;



public class MyProfileActivity extends AppCompatActivity {

    private ActivityMyProfileBinding binding;
    private EditText fullName, email, phone, gender;
    private RadioGroup ageGroupRadio;
    private Button saveBtn, logoutBtn, editBtn;

    private static final String LOAD_URL = Config.BASE_URL + "profile/load_profile.php";
    private static final String SAVE_URL = Config.BASE_URL + "profile/update_profile.php";
    private static final String DELETE_URL = Config.BASE_URL + "profile/delete_account.php";

    private int userId;
    private String role;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMyProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        fullName = findViewById(R.id.editTextFullName);
        email = findViewById(R.id.editTextEmail);
        phone = findViewById(R.id.editTextPhone);
        gender = findViewById(R.id.editTextGender);
        ageGroupRadio = findViewById(R.id.radioGroupAge);
        saveBtn = findViewById(R.id.buttonSave);
        logoutBtn = findViewById(R.id.buttonLogout);
        editBtn = findViewById(R.id.buttonEdit);

        SharedPreferences prefs = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        role = prefs.getString("role", "user");
        userId = role.equals("provider") ? prefs.getInt("provider_id", -1) : prefs.getInt("user_id", -1);

        if (userId <= 0) {
            Toast.makeText(this, "Session expired. Please log in again.", Toast.LENGTH_LONG).show();
            startActivity(new Intent(this, UserLogin.class));
            finish();
            return;
        }

        loadProfile();
        enableEditing(false);

        saveBtn.setOnClickListener(v -> updateProfile());
        logoutBtn.setOnClickListener(v -> {
            getSharedPreferences("AppPrefs", MODE_PRIVATE).edit().clear().apply();
            startActivity(new Intent(this, UserLogin.class));
            finish();
        });

        editBtn.setOnClickListener(v -> enableEditing(true));

        findViewById(R.id.buttonChangePassword).setOnClickListener(v ->
                startActivity(new Intent(MyProfileActivity.this, ChangePasswordActivity.class)));

        findViewById(R.id.buttonDeleteAccount).setOnClickListener(v -> confirmDeleteAccount());
    }

    private void loadProfile() {
        ProgressDialog dialog = ProgressDialog.show(this, "", "Loading...", true);
        String url = LOAD_URL + "?user_id=" + userId + "&role=" + role;

        StringRequest request = new StringRequest(Request.Method.GET, url,
                response -> {
                    dialog.dismiss();
                    try {
                        JSONObject json = new JSONObject(response);
                        if (json.getBoolean("success")) {
                            if (role.equals("provider")) {
                                JSONObject provider = json.getJSONObject("provider");
                                fullName.setText(provider.getString("username"));
                                email.setText(provider.getString("contact_email"));
                                phone.setText(provider.optString("contact_number", ""));
                                gender.setVisibility(View.GONE);
                                ageGroupRadio.setVisibility(View.GONE);
                            } else {
                                JSONObject user = json.getJSONObject("user");
                                fullName.setText(user.getString("full_name"));
                                email.setText(user.getString("email"));
                                phone.setText(user.getString("phone_number"));
                                gender.setText(user.getString("gender"));

                                String ageGroup = user.getString("age_group");
                                if ("above".equals(ageGroup)) {
                                    ageGroupRadio.check(R.id.radioAbove);
                                } else {
                                    ageGroupRadio.check(R.id.radioBelow);
                                }
                            }
                        } else {
                            Toast.makeText(this, json.optString("message"), Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        Toast.makeText(this, "Failed to parse profile: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    dialog.dismiss();
                    Toast.makeText(this, "Failed to load profile", Toast.LENGTH_SHORT).show();
                });

        Volley.newRequestQueue(this).add(request);
    }

    private void updateProfile() {
        String full_name = fullName.getText().toString();
        String emailVal = email.getText().toString();
        String phoneVal = phone.getText().toString();
        String genderVal = gender.getText().toString();
        String ageGroup = (ageGroupRadio.getCheckedRadioButtonId() == R.id.radioAbove) ? "above" : "below";

        ProgressDialog dialog = ProgressDialog.show(this, "", "Updating...", true);

        StringRequest request = new StringRequest(Request.Method.POST, SAVE_URL,
                response -> {
                    dialog.dismiss();
                    Toast.makeText(this, "Profile updated", Toast.LENGTH_SHORT).show();
                },
                error -> {
                    dialog.dismiss();
                    Toast.makeText(this, "Update failed", Toast.LENGTH_SHORT).show();
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("user_id", String.valueOf(userId));
                params.put("role", role);
                params.put("full_name", full_name);
                params.put("email", emailVal);
                params.put("phone_number", phoneVal);

                if (role.equals("user")) {
                    params.put("gender", genderVal);
                    params.put("age_group", ageGroup);
                }

                return params;
            }
        };

        Volley.newRequestQueue(this).add(request);
    }

    private void enableEditing(boolean enabled) {
        fullName.setEnabled(enabled);
        email.setEnabled(enabled);
        phone.setEnabled(enabled);
        gender.setEnabled(enabled && role.equals("user"));
        for (int i = 0; i < ageGroupRadio.getChildCount(); i++) {
            ageGroupRadio.getChildAt(i).setEnabled(enabled && role.equals("user"));
        }
        saveBtn.setVisibility(enabled ? View.VISIBLE : View.GONE);
    }

    private void confirmDeleteAccount() {
        new AlertDialog.Builder(this)
                .setTitle("Confirm Deletion")
                .setMessage("Are you sure you want to delete your account permanently?")
                .setPositiveButton("Yes", (dialog, which) -> deleteAccount())
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void deleteAccount() {
        ProgressDialog dialog = ProgressDialog.show(this, "", "Deleting...", true);

        StringRequest request = new StringRequest(Request.Method.POST, DELETE_URL,
                response -> {
                    dialog.dismiss();
                    try {
                        JSONObject json = new JSONObject(response);
                        if (json.getBoolean("success")) {
                            Toast.makeText(this, "Account deleted", Toast.LENGTH_SHORT).show();
                            getSharedPreferences("AppPrefs", MODE_PRIVATE).edit().clear().apply();
                            startActivity(new Intent(this, UserLogin.class));
                            finish();
                        } else {
                            Toast.makeText(this, json.getString("message"), Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        Toast.makeText(this, "Error parsing server response", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    dialog.dismiss();
                    Toast.makeText(this, "Failed to connect to server", Toast.LENGTH_SHORT).show();
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> map = new HashMap<>();
                map.put("user_id", String.valueOf(userId));
                map.put("role", role);
                return map;
            }
        };

        Volley.newRequestQueue(this).add(request);
    }
}

