package com.example.mobile_project_hza2m;

import android.os.Bundle;

import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.navigation.ui.AppBarConfiguration;

import com.example.mobile_project_hza2m.databinding.ActivityAdminInsertAdminBinding;

public class AdminInsertAdminActivity extends AppCompatActivity {
    EditText editTextName, editTextEmail, editTextPassword;
    Button buttonInsert;
    private AppBarConfiguration appBarConfiguration;
    private ActivityAdminInsertAdminBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityAdminInsertAdminBinding.inflate(getLayoutInflater());
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


        editTextName = findViewById(R.id.editTextAdminName);
        editTextEmail = findViewById(R.id.editTextAdminEmail);
        editTextPassword = findViewById(R.id.editTextAdminPassword);
        buttonInsert = findViewById(R.id.buttonInsertAdmin);

        buttonInsert.setOnClickListener(v -> {
            String name = editTextName.getText().toString().trim();
            String email = editTextEmail.getText().toString().trim();
            String password = editTextPassword.getText().toString().trim();

            if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            // TODO: Insert into DB or send to server
            Toast.makeText(this, "Admin " + name + " added!", Toast.LENGTH_SHORT).show();
            editTextName.setText("");
            editTextEmail.setText("");
            editTextPassword.setText("");
        });
    }


}