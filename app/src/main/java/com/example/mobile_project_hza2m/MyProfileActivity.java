package com.example.mobile_project_hza2m;

import android.os.Bundle;

import com.example.mobile_project_hza2m.databinding.ActivityMyProfileBinding;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;

import android.view.View;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.mobile_project_hza2m.databinding.ActivityMyProfileBinding;

import java.util.Objects;

public class MyProfileActivity extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;
    private ActivityMyProfileBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMyProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);


    }


}