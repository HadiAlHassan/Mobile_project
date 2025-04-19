package com.example.mobile_project_hza2m;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.widget.Button;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.mobile_project_hza2m.databinding.ActivityHomeBinding;

public class HomeActivity extends AppCompatActivity {
    Button btnSignUp;
    Button btnLogin;
   Button btnTest;
    private AppBarConfiguration appBarConfiguration;
    private ActivityHomeBinding binding;
    int SignUpRequestCode= 10;
    int LoginRequestCode= 12;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        btnLogin = findViewById(R.id.btnLogin);
        btnSignUp= findViewById(R.id.btnSignUp);
        btnTest = findViewById(R.id.btntest);

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(HomeActivity.this,UserOrProvider.class);
                int SignUpRequestCode= 10;
                startActivityForResult(intent, SignUpRequestCode);
            }

        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, UserLogin.class);
                startActivityForResult(intent, LoginRequestCode);
            }
        });

      btnTest.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               Intent intent = new Intent(HomeActivity.this, DisplayServicesActivity.class);
               startActivity(intent);
           }
        });



}}