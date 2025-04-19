package com.example.mobile_project_hza2m;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.widget.Button;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.mobile_project_hza2m.databinding.ActivityAdminDashboardBinding;

public class AdminDashboardActivity extends AppCompatActivity {

    Button AdminAddServiceType;
    Button AdminViewUsers;
    Button AdminViewProviders;

    Button AdminInsertAdmin;

    private AppBarConfiguration appBarConfiguration;
    private ActivityAdminDashboardBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityAdminDashboardBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);


        binding.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAnchorView(R.id.fab)
                        .setAction("Action", null).show();
            }
        });
        AdminAddServiceType = findViewById(R.id.btnAdminAddServiceType);
        AdminViewUsers = findViewById(R.id.btnAdminViewUsers);
        AdminViewProviders = findViewById(R.id.btnAdminViewProviders);
        AdminInsertAdmin = findViewById(R.id.btnAdminInsertAdmin);

        AdminAddServiceType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(AdminDashboardActivity.this, AdminAddServiceActivity.class);
                startActivity(i);
            }
        });
        AdminViewUsers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle button click
                Intent i = new Intent(AdminDashboardActivity.this, AdminViewUserActivity.class);
                startActivity(i);
            }


        });
        AdminViewProviders.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle button click
                Intent i = new Intent(AdminDashboardActivity.this, AdminViewProviderActivity.class);
                startActivity(i);
            }

        });
        AdminInsertAdmin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle button click
                Intent i = new Intent(AdminDashboardActivity.this, AdminInsertAdminActivity.class);
                startActivity(i);
            }

        });
    }
    }