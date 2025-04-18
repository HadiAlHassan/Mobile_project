package com.example.mobile_project_hza2m;

import android.os.Bundle;

import com.example.mobile_project_hza2m.databinding.ActivityTuitionServiceUserBinding;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;

import android.view.View;

import androidx.navigation.ui.AppBarConfiguration;

import com.example.mobile_project_hza2m.databinding.ActivityTuitionServiceUserBinding;

public class TuitionServiceUserActivity extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;
    private ActivityTuitionServiceUserBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityTuitionServiceUserBinding.inflate(getLayoutInflater());
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
    }


}