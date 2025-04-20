package com.example.mobile_project_hza2m;

import android.app.AlertDialog;
import android.os.Bundle;

import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;

import android.view.View;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mobile_project_hza2m.databinding.ActivityAdminAddServiceBinding;

public class AdminAddServiceActivity extends AppCompatActivity {
RecyclerView recyclerViewCategories;
    private AppBarConfiguration appBarConfiguration;
    private ActivityAdminAddServiceBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityAdminAddServiceBinding.inflate(getLayoutInflater());
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

       /* AdminCategoryAdapter adapter = new AdminCategoryAdapter(this, categoryList, this::fetchCategories);
        recyclerViewCategories.setAdapter(adapter); */

    }


}