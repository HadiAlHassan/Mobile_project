package com.example.mobile_project_hza2m;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.*;

public class ServiceProviderSignUpActivity extends AppCompatActivity {

    EditText editServiceProviderUsername, editServiceProviderEmail, editServiceProviderPassword;
    Spinner spinnerServiceType;
    Button btnServiceProviderSignUp;

    Map<String, Integer> categoryMap = new HashMap<>();
    List<String> categoryNames = new ArrayList<>();
    ProgressDialog progressDialog;

    SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_provider_sign_up);

        editServiceProviderUsername = findViewById(R.id.editServiceProviderUsername);
        editServiceProviderEmail = findViewById(R.id.editServiceProviderEmail);
        editServiceProviderPassword = findViewById(R.id.editServiceProviderPassword);
        spinnerServiceType = findViewById(R.id.spinnerServiceType);
        btnServiceProviderSignUp = findViewById(R.id.btnServiceProviderSignUp);

        prefs = getSharedPreferences("AppPrefs", MODE_PRIVATE);

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Please wait...");

        loadCategories();

        btnServiceProviderSignUp.setOnClickListener(v -> {
            String username = editServiceProviderUsername.getText().toString().trim();
            String email = editServiceProviderEmail.getText().toString().trim();
            String password = editServiceProviderPassword.getText().toString().trim();
            String selectedCategory = spinnerServiceType.getSelectedItem().toString();
            int categoryId = categoryMap.getOrDefault(selectedCategory, -1);

            if (username.isEmpty()) {
                editServiceProviderUsername.setError("Username is required");
                return;
            }
            if (email.isEmpty()) {
                editServiceProviderEmail.setError("Email is required");
                return;
            }
            if (password.isEmpty()) {
                editServiceProviderPassword.setError("Password is required");
                return;
            }

            registerProvider(username, email, password, selectedCategory, categoryId);
        });
    }

    private void loadCategories() {
        progressDialog.setMessage("Loading categories...");
        progressDialog.show();

        String url = Config.BASE_URL + "auth/get_categories.php";

        StringRequest request = new StringRequest(Request.Method.GET, url,
                response -> {
                    progressDialog.dismiss();
                    try {
                        JSONObject json = new JSONObject(response);
                        if (json.getBoolean("success")) {
                            JSONArray categories = json.getJSONArray("categories");
                            for (int i = 0; i < categories.length(); i++) {
                                JSONObject cat = categories.getJSONObject(i);
                                int id = cat.getInt("category_id");
                                String name = cat.getString("name");

                                categoryMap.put(name, id);
                                categoryNames.add(name);
                            }

                            ArrayAdapter<String> adapter = new ArrayAdapter<>(
                                    this, android.R.layout.simple_spinner_dropdown_item, categoryNames);
                            spinnerServiceType.setAdapter(adapter);
                        } else {
                            Toast.makeText(this, "Failed to load categories", Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        Toast.makeText(this, "Parse error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    progressDialog.dismiss();
                    Toast.makeText(this, "Network error while loading categories", Toast.LENGTH_SHORT).show();
                });

        Volley.newRequestQueue(this).add(request);
    }



    private void registerProvider(String username, String email, String password, String serviceCategory, int categoryId) {
        progressDialog.setMessage("Registering...");
        progressDialog.show();

        String url = Config.BASE_URL + "auth/provider_signup.php";

        StringRequest request = new StringRequest(Request.Method.POST, url,
                response -> {
                    progressDialog.dismiss();
                    try {
                        JSONObject json = new JSONObject(response);
                        if (json.getBoolean("success")) {
                            int providerId = json.getInt("provider_id");

                            // Save login info and role
                            SharedPreferences.Editor editor = prefs.edit();
                            editor.putInt("user_id", providerId); // ✅ allows MyProfileActivity to work with one ID
                            editor.putInt("provider_id", providerId);
                            editor.putString("saved_email", email);
                            editor.putString("saved_password", password);
                            editor.putString("role", "provider"); // ✅ to load correct profile
                            editor.putInt("category_id", categoryId);
                            editor.apply();

                            Toast.makeText(this, "Welcome! You’re now signed in.", Toast.LENGTH_SHORT).show();

                            startActivity(new Intent(this, MyServicesActivity.class));
                            finish();
                        } else {
                            Toast.makeText(this, json.getString("message"), Toast.LENGTH_LONG).show();
                        }
                    } catch (Exception e) {
                        Toast.makeText(this, "Parse error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                },
                error -> {
                    progressDialog.dismiss();
                    Toast.makeText(this, "Connection error", Toast.LENGTH_SHORT).show();
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> map = new HashMap<>();
                map.put("username", username);
                map.put("contact_email", email);
                map.put("password", password);
                map.put("service_category", serviceCategory);
                map.put("category_id", String.valueOf(categoryId));
                map.put("address", "");         // Default placeholder
                map.put("contact_number", "");  // Default placeholder
                return map;
            }
        };

        Volley.newRequestQueue(this).add(request);
    }



}
