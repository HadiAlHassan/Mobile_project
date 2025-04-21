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
    private final String LOGIN_URL = "http://192.168.0.74/Mobile_submodule_backend/PHP/auth/login.php";

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

        btnLogin.setOnClickListener(v -> loginUser());
        btnLogout.setOnClickListener(v -> logoutUser());
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

                            SharedPreferences prefs = getSharedPreferences("AppPrefs", MODE_PRIVATE);
                            SharedPreferences.Editor editor = prefs.edit();

                            switch (role) {
                                case "user":
                                    int userId = json.getInt("user_id");
                                    editor.putInt("user_id", userId);
                                    editor.putString("role", role);
                                    editor.apply();
                                    createWallet(userId);
                                    startActivity(new Intent(this, DisplayServicesActivity.class));
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

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(request);
    }

    private void logoutUser() {
        new AlertDialog.Builder(this)
                .setTitle("Logout?")
                .setMessage("Are you sure you want to log out?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    SharedPreferences prefs = getSharedPreferences("AppPrefs", MODE_PRIVATE);
                    prefs.edit().clear().apply();
                    Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show();
                    edUser.setText("");
                    edPass.setText("");
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void createWallet(int userId) {
        String url = "http://192.168.0.74/Mobile_submodule_backend/PHP/wallet/create_wallet.php";

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

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(walletRequest);
    }
}
