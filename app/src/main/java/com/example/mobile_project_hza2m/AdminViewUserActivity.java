package com.example.mobile_project_hza2m;

import android.os.Bundle;

import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.widget.Toast;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mobile_project_hza2m.databinding.ActivityAdminViewUserBinding;

import java.util.ArrayList;
import java.util.List;

public class AdminViewUserActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    SimpleUserAdapter adapter;
    List<User> userList;
    private AppBarConfiguration appBarConfiguration;
    private ActivityAdminViewUserBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityAdminViewUserBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);

        recyclerView = findViewById(R.id.recyclerViewUsers);
        userList = new ArrayList<>();

        // Sample data
        userList.add(new User("Ali", "ali@gmail.com"));
        userList.add(new User("Rana", "rana@hotmail.com"));

        adapter = new SimpleUserAdapter(userList, position -> {
            userList.remove(position);
            adapter.notifyItemRemoved(position);
            Toast.makeText(this, "User deleted", Toast.LENGTH_SHORT).show();
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
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