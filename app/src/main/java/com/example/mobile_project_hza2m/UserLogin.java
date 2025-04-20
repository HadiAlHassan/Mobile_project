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

    private final String LOGIN_URL = "http://192.168.0.101/Mobile_submodule_backend/PHP/auth/login.php";


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
                    Log.e("RAW_LOGIN_RESPONSE", "[" + response + "]"); // Log it clearly with brackets
                    try {
                        JSONObject json = new JSONObject(response);
                        if (json.getBoolean("success")) {
                            String role = json.getString("role");
                            Toast.makeText(this, "Login successful as " + role, Toast.LENGTH_SHORT).show();

                            // 🔄 Redirect based on role
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
                            finish(); // optional: close login screen
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Error parsing response: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
                ,
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
}
