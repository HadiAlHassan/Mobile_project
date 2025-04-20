package com.example.mobile_project_hza2m;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
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
import com.example.mobile_project_hza2m.databinding.ActivityUserOrProviderBinding;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class UserLogin extends AppCompatActivity {
    private ActivityUserOrProviderBinding binding;
    EditText edUser, edPass;
    Button btnLogin;

    private final String LOGIN_URL = "http://192.168.0.104/Mobile_submodule_backend/PHP/auth/login.php";


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

        btnLogin.setOnClickListener(v -> loginUser());



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
                    Log.e("RAW_LOGIN_RESPONSE", response); // raw clean log

                    try {
                        JSONObject json = new JSONObject(response);

                        if (json.getBoolean("success")) {
                            String role = json.getString("role");

                            // Optional: Store user info if needed
                            // String userId = json.optString("user_id", "");
                            // String name = json.optString("username", json.optString("full_name", ""));

                            Toast.makeText(this, "Login successful as " + role, Toast.LENGTH_SHORT).show();

                            // 🔁 Redirect based on role
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
                                default:
                                    Toast.makeText(this, "Unknown role: " + role, Toast.LENGTH_SHORT).show();
                            }

                            finish(); // close login activity
                        } else {
                            Toast.makeText(this, json.getString("message"), Toast.LENGTH_LONG).show();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Error parsing server response", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    error.printStackTrace();
                    Toast.makeText(this, "Network error: " + error.getMessage(), Toast.LENGTH_LONG).show();
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("email", email);
                params.put("password", password);
                return params;
            }
        };

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(request);
    }

}
