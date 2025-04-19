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

    EditText editServiceProviderBusinessName, editServiceProviderEmail, editServiceProviderPassword,
            editServiceProviderPhoneNb, editServiceProviderAddress;
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
        editServiceProviderBusinessName = findViewById(R.id.editServiceProviderBusinessName);
        editServiceProviderEmail = findViewById(R.id.editServiceProviderEmail);
        editServiceProviderPassword = findViewById(R.id.editServiceProviderPassword);
        btnServiceProviderSignUp = findViewById(R.id.btnServiceProviderSignUp);
        editServiceProviderPhoneNb = findViewById(R.id.editServiceProviderPhoneNb);
        editServiceProviderAddress = findViewById(R.id.editServiceProviderAddress);

        btnServiceProviderSignUp.setOnClickListener(v -> {
            String businessName = editServiceProviderBusinessName.getText().toString();
            String email = editServiceProviderEmail.getText().toString();
            String password = editServiceProviderPassword.getText().toString();
            String phoneNb = editServiceProviderPhoneNb.getText().toString();
            String address = editServiceProviderAddress.getText().toString();

            // Add your sign-up logic here
            if (businessName.isEmpty()) {
                editServiceProviderBusinessName.setError("Business name is required");
            }
            else if (email.isEmpty()) {
                editServiceProviderEmail.setError("Email is required");
            }
            else if (password.isEmpty()) {
                editServiceProviderPassword.setError("Password is required");
            }
            else if (phoneNb.isEmpty()) {
                editServiceProviderPhoneNb.setError("Phone number is required");
            }
            else if (address.isEmpty()) {
                editServiceProviderAddress.setError("Address is required");
            }
        });
    }
}