package com.example.mobile_project_hza2m;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.widget.Button;

import com.example.mobile_project_hza2m.databinding.ActivityHomeBinding;

public class HomeActivity extends AppCompatActivity {

    Button btnSignUp, btnLogin, btnTest;
    private ActivityHomeBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.fab.setOnClickListener(view ->
                startActivity(new Intent(HomeActivity.this, ContactUsActivity.class)));

        btnLogin = findViewById(R.id.btnLogin);
        btnSignUp = findViewById(R.id.btnSignUp);
        btnTest = findViewById(R.id.btntest);

        btnSignUp.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, UserOrProvider.class);
            startActivityForResult(intent, 10);
        });

        btnLogin.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, UserLogin.class);
            startActivityForResult(intent, 12);
        });

        btnTest.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, TelecomServiceUserActivity.class);
            startActivity(intent);
        });
    }



}





