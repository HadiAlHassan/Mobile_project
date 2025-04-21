package com.example.mobile_project_hza2m;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.mobile_project_hza2m.databinding.ActivityInsuranceServiceProviderBinding;
import com.example.mobile_project_hza2m.databinding.ActivityTelecomServiceProviderBinding;


public class TelecomServiceProviderActivity extends AppCompatActivity {

    com.example.mobile_project_hza2m.databinding.ActivityTelecomServiceProviderBinding binding;
    private ImageView imageViewProviderLogo, imageViewUpload;
    private EditText editTextCompany, editTextBankAccount, editTextServiceArea;
    private Button buttonSubmit;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityTelecomServiceProviderBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        binding.fabaddfund.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(TelecomServiceProviderActivity.this, AddServiceItemActivity.class);
                startActivity(i);
            }
        });

        // Toolbar setup
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Telecom Provider Info");
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
;
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        // Views
        imageViewProviderLogo = findViewById(R.id.imageViewProviderLogo);
        imageViewUpload = findViewById(R.id.imageViewUpload);
        editTextCompany = findViewById(R.id.editTextCompany);
        editTextBankAccount = findViewById(R.id.editTextBankAccount);
        editTextServiceArea = findViewById(R.id.editTextServiceArea);
        buttonSubmit = findViewById(R.id.buttonSubmitTelecom);

        // Handle image upload (optional)
        imageViewUpload.setOnClickListener(v -> {
            Toast.makeText(this, "Upload image clicked", Toast.LENGTH_SHORT).show();
            // TODO: Open image picker
        });

        // Handle submission
        buttonSubmit.setOnClickListener(v -> {
            String company = editTextCompany.getText().toString().trim();
            String bank = editTextBankAccount.getText().toString().trim();
            String area = editTextServiceArea.getText().toString().trim();

            if (company.isEmpty() || bank.isEmpty()) {
                Toast.makeText(this, "Please enter company and bank account", Toast.LENGTH_SHORT).show();
                return;
            }

            // TODO: Store or pass data to next step
            Toast.makeText(this, "Service submitted successfully!", Toast.LENGTH_LONG).show();
        });
    }
}
