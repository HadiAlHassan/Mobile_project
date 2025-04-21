package com.example.mobile_project_hza2m;

import android.content.Intent;
import android.os.Bundle;

import com.example.mobile_project_hza2m.databinding.ActivityHomeBinding;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;

import android.view.View;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.mobile_project_hza2m.databinding.ActivityInsuranceServiceProviderBinding;

public class InsuranceServiceProviderActivity extends AppCompatActivity {

    private ActivityInsuranceServiceProviderBinding binding;


    private AppBarConfiguration appBarConfiguration;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityInsuranceServiceProviderBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        binding.fabaddfund.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(InsuranceServiceProviderActivity.this, AddServiceItemActivity.class);
                startActivity(i);
            }
        });
    }


}