package com.example.mobile_project_hza2m;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.view.Menu;
import android.view.MenuItem;

import androidx.navigation.ui.AppBarConfiguration;

import com.example.mobile_project_hza2m.databinding.ActivityPrivacy1Binding;

public class Privacy extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;
    private ActivityPrivacy1Binding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityPrivacy1Binding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main2, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            startActivity(new Intent(this, Settings.class));
        }


        return super.onOptionsItemSelected(item);
    }
}