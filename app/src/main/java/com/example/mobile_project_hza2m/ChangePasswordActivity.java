package com.example.mobile_project_hza2m;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.Map;

public class ChangePasswordActivity extends AppCompatActivity {

    EditText oldPassword, newPassword, confirmPassword;
    Button changePasswordBtn;
    private static final String CHANGE_URL = "http://192.168.0.74/Mobile_submodule_backend/PHP/auth/change_password.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        oldPassword = findViewById(R.id.oldPassword);
        newPassword = findViewById(R.id.newPassword);
        confirmPassword = findViewById(R.id.confirmPassword);
        changePasswordBtn = findViewById(R.id.changePasswordBtn);

        SharedPreferences prefs = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        int userId = prefs.getInt("user_id", -1);

        changePasswordBtn.setOnClickListener(v -> {
            String oldPass = oldPassword.getText().toString();
            String newPass = newPassword.getText().toString();
            String confirmPass = confirmPassword.getText().toString();

            if (oldPass.isEmpty() || newPass.isEmpty() || confirmPass.isEmpty()) {
                Toast.makeText(this, "All fields required", Toast.LENGTH_SHORT).show();
                return;
            }
            if (!newPass.equals(confirmPass)) {
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                return;
            }

            ProgressDialog dialog = ProgressDialog.show(this, "", "Changing...", true);

            StringRequest request = new StringRequest(Request.Method.POST, CHANGE_URL,
                    response -> {
                        dialog.dismiss();
                        try {
                            JSONObject json = new JSONObject(response);
                            if (json.getBoolean("success")) {
                                Toast.makeText(this, "Password changed", Toast.LENGTH_SHORT).show();
                                finish();
                            } else {
                                Toast.makeText(this, json.getString("message"), Toast.LENGTH_LONG).show();
                            }
                        } catch (Exception e) {
                            Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
                        }
                    },
                    error -> {
                        dialog.dismiss();
                        Toast.makeText(this, "Network error", Toast.LENGTH_SHORT).show();
                    }) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> map = new HashMap<>();
                    map.put("user_id", String.valueOf(userId));
                    map.put("old_password", oldPass);
                    map.put("new_password", newPass);
                    return map;
                }
            };

            Volley.newRequestQueue(this).add(request);
        });
    }
}
