package com.example.mobile_project_hza2m;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class UserLogin extends AppCompatActivity {

    EditText edUser, edPass;
    Button btnLogin, btnLogout, btnHome;

    private final String LOGIN_URL = Config.BASE_URL + "auth/login.php";
    private final String LOGOUT_URL = Config.BASE_URL + "auth/logout.php";

    private SharedPreferences prefs;

    private static final Map<String, Integer> serviceCategoryIdMap = new HashMap<>();
    static {
        serviceCategoryIdMap.put("ogero", 1);
        serviceCategoryIdMap.put("insurance", 2);
        serviceCategoryIdMap.put("streaming ", 3);
        serviceCategoryIdMap.put("telecommunication", 4);
        serviceCategoryIdMap.put("tuition", 5);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_user_login);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        edUser = findViewById(R.id.edUser);
        edPass = findViewById(R.id.edPass);
        btnLogin = findViewById(R.id.btn1Login);
        btnLogout = findViewById(R.id.btnLogout);
        btnHome = findViewById(R.id.btnHome);

        prefs = getSharedPreferences("AppPrefs", MODE_PRIVATE);

        // Auto-redirect if session exists
        String savedEmail = prefs.getString("saved_email", null);
        String savedPass = prefs.getString("saved_password", null);
        String role = prefs.getString("role", null);

        if (savedEmail != null && savedPass != null && role != null) {
            redirectToDashboard(role);
            finish();
            return;
        }

        btnLogin.setOnClickListener(v -> loginUser());
        btnLogout.setOnClickListener(v -> logoutUser());
        btnLogout.setEnabled(false);

        btnHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(UserLogin.this, HomeActivity.class));
            }
        });
    }

    private void loginUser() {
        String email = edUser.getText().toString().trim();
        String password = edPass.getText().toString();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show();
            return;
        }

        StringRequest request = new StringRequest(Request.Method.POST, LOGIN_URL,
                response -> {
                    Log.e("RAW_LOGIN_RESPONSE", "[" + response + "]");
                    try {
                        JSONObject json = new JSONObject(response);
                        if (json.has("success") && json.getBoolean("success")) {
                            String role = json.getString("role");

                            SharedPreferences.Editor editor = prefs.edit();
                            editor.putString("saved_email", email);
                            editor.putString("saved_password", password);
                            editor.putString("role", role);

                            switch (role) {
                                case "user":
                                    int userId = json.getInt("user_id");
                                    editor.putInt("user_id", userId);
                                    editor.apply();
                                    createWallet(userId);
                                    redirectToDashboard(role);
                                    break;

                                case "provider":
                                    int providerId = json.getInt("provider_id");
                                    String serviceCategory = json.getString("service_category");
                                    int categoryId = serviceCategoryIdMap.getOrDefault(serviceCategory, -1);
                                    int serviceId = json.optInt("service_id", -1);

                                    editor.putInt("provider_id", providerId);
                                    editor.putString("service_category", serviceCategory);
                                    editor.putInt("category_id", categoryId);
                                    editor.putInt("service_id", serviceId);
                                    editor.apply();
                                    redirectToDashboard(role);
                                    break;


                                case "admin":
                                    int adminId = json.getInt("admin_id");
                                    editor.putInt("admin_id", adminId);
                                    editor.apply();
                                    redirectToDashboard(role);
                                    break;
                            }
                            finish();
                        } else {
                            String msg = json.optString("message", "Login failed");
                            Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Error parsing response: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    error.printStackTrace();
                    Toast.makeText(this, "Network error: " + error.toString(), Toast.LENGTH_LONG).show();
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> map = new HashMap<>();
                map.put("email", email);
                map.put("password", password);
                return map;
            }
        };

        Volley.newRequestQueue(this).add(request);
    }

    private void redirectToDashboard(String role) {
        switch (role) {
            case "user":
                startActivity(new Intent(this, DisplayServicesActivity.class));
                break;
            case "provider":
                startActivity(new Intent(this, MyServicesActivity.class));
                break;
            case "admin":
                startActivity(new Intent(this, AdminDashboardActivity.class));
                break;
        }
    }

    private void logoutUser() {
        new AlertDialog.Builder(this)
                .setTitle("Logout?")
                .setMessage("Are you sure you want to log out?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    StringRequest request = new StringRequest(Request.Method.POST, LOGOUT_URL,
                            response -> {
                                SharedPreferences.Editor editor = prefs.edit();
                                editor.clear().apply();

                                edUser.setText("");
                                edPass.setText("");
                                edUser.setEnabled(true);
                                edPass.setEnabled(true);
                                btnLogin.setEnabled(true);
                                btnLogout.setEnabled(false);

                                Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(this, HomeActivity.class));
                            },
                            error -> {
                                error.printStackTrace();
                                Toast.makeText(this, "Logout failed", Toast.LENGTH_LONG).show();
                            });
                    Volley.newRequestQueue(this).add(request);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void createWallet(int userId) {
        String url = Config.BASE_URL + "wallet/create_wallet.php";

        StringRequest walletRequest = new StringRequest(Request.Method.POST, url,
                response -> Log.d("WALLET_RESPONSE", response),
                error -> Log.e("WALLET_ERROR", "Error creating wallet: " + error.getMessage())) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> map = new HashMap<>();
                map.put("user_id", String.valueOf(userId));
                return map;
            }
        };

        Volley.newRequestQueue(this).add(walletRequest);
    }
}
