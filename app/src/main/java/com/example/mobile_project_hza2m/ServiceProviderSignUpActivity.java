package com.example.mobile_project_hza2m;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class ServiceProviderSignUpActivity extends AppCompatActivity {
    Spinner spinner;


    EditText editServiceProviderUsername, editServiceProviderEmail, editServiceProviderPassword;
    Button btnServiceProviderSignUp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_service_provider_sign_up);
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

        spinner = findViewById(R.id.spinnerServiceType);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_dropdown_item,
                new String[] {
                        "Streaming Services",
                        "Telecom Services",
                        "Ogero Service",
                        "Tuition Payment Services"
                }
        );

        spinner.setAdapter(adapter);


        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedService = parent.getItemAtPosition(position).toString();

                if (selectedService.equals("Streaming Services")) {
                    // Handle Streaming Services
                    Toast.makeText(getApplicationContext(), "Streaming Services selected", Toast.LENGTH_SHORT).show();

                } else if (selectedService.equals("Telecom Services")) {
                    // Handle Telecom Services
                    Toast.makeText(getApplicationContext(), "Telecom Services selected", Toast.LENGTH_SHORT).show();

                } else if (selectedService.equals("Ogero Service")) {
                    // Handle Ogero Service
                    Toast.makeText(getApplicationContext(), "Ogero Service selected", Toast.LENGTH_SHORT).show();

                } else if (selectedService.equals("Tuition Payment Services")) {
                    // Handle Tuition Payment Services
                    Toast.makeText(getApplicationContext(), "Tuition Payment Services selected", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        btnServiceProviderSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), MyServicesActivity.class);
                i.putExtra("service", spinner.getSelectedItem().toString());
                startActivity(i);
            }
        });
    }
}