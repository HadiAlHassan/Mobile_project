package com.example.mobile_project_hza2m;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class ServiceProviderSignIn extends AppCompatActivity {

    EditText editServiceProviderUsername, editServiceProviderEmail, editServiceProviderPassword;
    Button btnServiceProviderSignUp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_service_provider_sign_in);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        editServiceProviderUsername = findViewById(R.id.editServiceProviderUsername);
        editServiceProviderEmail = findViewById(R.id.editServiceProviderEmail);
        editServiceProviderPassword = findViewById(R.id.editServiceProviderPassword);
        btnServiceProviderSignUp = findViewById(R.id.btnServiceProviderSignUp);
        btnServiceProviderSignUp.setOnClickListener(v -> {
            String username = editServiceProviderUsername.getText().toString();
            String email = editServiceProviderEmail.getText().toString();
            String password = editServiceProviderPassword.getText().toString();
            // Add your sign-up logic here
            if (username.isEmpty()) {
                editServiceProviderUsername.setError("Username is required");
            }
            else if (email.isEmpty()) {
                editServiceProviderEmail.setError("Email is required");
            }
            else if (password.isEmpty()) {
                editServiceProviderPassword.setError("Password is required");
            }
        });
    }
}