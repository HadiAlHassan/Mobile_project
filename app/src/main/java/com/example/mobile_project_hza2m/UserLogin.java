package com.example.mobile_project_hza2m;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class UserLogin extends AppCompatActivity {

    EditText edUser, edPass;
    Button btnLogin, btnLogout;
    private final String LOGIN_URL = Config.BASE_URL+"auth/login.php";
    private static final String LOGOUT_URL = Config.BASE_URL+"auth/logout.php";

    private SharedPreferences prefs;

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

        prefs = getSharedPreferences("AppPrefs", MODE_PRIVATE);

        String savedEmail = prefs.getString("saved_email", null);
        String savedPass = prefs.getString("saved_password", null);
        String role = prefs.getString("role", null);

        // ✅ If logged in, redirect based on role
        if (savedEmail != null && savedPass != null && role != null) {
            switch (role) {
                case "user":
                    startActivity(new Intent(this, MyProfileActivity.class));
                    break;
                case "provider":
                    startActivity(new Intent(this, MyServicesActivity.class));
                    break;
                case "admin":
                    startActivity(new Intent(this, AdminDashboardActivity.class));
                    break;
            }
            finish(); // Prevent going back to login
            return;
        }

        // Else normal login flow
        btnLogin.setOnClickListener(v -> loginUser());
        btnLogout.setOnClickListener(v -> logoutUser());
        btnLogout.setEnabled(false);  // Only enabled if session exists
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
                            Toast.makeText(this, "Login successful as " + role, Toast.LENGTH_SHORT).show();

                            SharedPreferences.Editor editor = prefs.edit();
                            editor.putString("saved_email", email);
                            editor.putString("saved_password", password);
                            editor.putString("role", role);

                            switch (role) {
                                case "user":
                                    int userId = json.getInt("user_id");
                                    editor.putInt("user_id", userId);
                                    editor.putString("role", role);
                                    editor.apply();

                                    // Only users get wallet
                                    createWallet(userId);

                                    startActivity(new Intent(this, MyProfileActivity.class));
                                    break;

                                case "provider":
                                    int providerId = json.getInt("provider_id");
                                    editor.putInt("provider_id", providerId);
                                    editor.putString("role", role);
                                    editor.apply();
                                    startActivity(new Intent(this, MyServicesActivity.class));
                                    break;

                                case "admin":
                                    int adminId = json.getInt("admin_id");
                                    editor.putInt("admin_id", adminId);
                                    editor.putString("role", role);
                                    editor.apply();
                                    startActivity(new Intent(this, AdminDashboardActivity.class));
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
                }
        ) {
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

    private void logoutUser() {
        new AlertDialog.Builder(this)
                .setTitle("Logout?")
                .setMessage("Are you sure you want to log out?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    StringRequest request = new StringRequest(Request.Method.POST, LOGOUT_URL,
                            response -> {
                                SharedPreferences.Editor editor = prefs.edit();
                                editor.remove("saved_email");
                                editor.remove("saved_password");
                                editor.remove("user_id");
                                editor.remove("provider_id");
                                editor.remove("admin_id");
                                editor.remove("role");
                                editor.apply();

                                edUser.setText("");
                                edPass.setText("");
                                edUser.setEnabled(true);
                                edPass.setEnabled(true);
                                btnLogin.setEnabled(true);
                                btnLogout.setEnabled(false);

                                Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show();
                            },
                            error -> {
                                error.printStackTrace();
                                Toast.makeText(this, "Logout failed (server unreachable)", Toast.LENGTH_LONG).show();
                            }
                    );
                    Volley.newRequestQueue(this).add(request);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void createWallet(int userId) {
        String url = Config.BASE_URL+"wallet/create_wallet.php";

        StringRequest walletRequest = new StringRequest(Request.Method.POST, url,
                response -> Log.d("WALLET_RESPONSE", response),
                error -> Log.e("WALLET_ERROR", "Error creating wallet: " + error.getMessage())
        ) {
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
