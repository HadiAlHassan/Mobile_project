package com.example.mobile_project_hza2m;

import android.os.Bundle;

import com.example.mobile_project_hza2m.databinding.ActivityMyWalletBinding;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;

import android.view.View;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.mobile_project_hza2m.databinding.ActivityMyWalletBinding;

import java.util.Objects;

public class MyWalletActivity extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;
    private ActivityMyWalletBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMyWalletBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

    }

}