package com.example.mobile_project_hza2m;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.mobile_project_hza2m.databinding.ActivityMyProfileBinding;

import java.util.Objects;

public class MyProfileActivity extends AppCompatActivity {

    private ActivityMyProfileBinding binding;
    private SharedPreferences prefs;

    private EditText fullName, email, phone, age, gender;
    private Button editBtn, saveBtn, logoutBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMyProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        prefs = getSharedPreferences("AppPrefs", MODE_PRIVATE);

        // Bind fields
        fullName = findViewById(R.id.editTextFullName);
        email = findViewById(R.id.editTextEmail);
        phone = findViewById(R.id.editTextPhone);
        age = findViewById(R.id.editTextAge);
        gender = findViewById(R.id.editTextGender);
        editBtn = findViewById(R.id.buttonEdit);
        saveBtn = findViewById(R.id.buttonSave);
        logoutBtn = findViewById(R.id.buttonLogout);

        loadProfile();

        editBtn.setOnClickListener(v -> enableEditing(true));
        saveBtn.setOnClickListener(v -> {
            saveProfile();
            enableEditing(false);
        });

        logoutBtn.setOnClickListener(v -> {
            prefs.edit().clear().apply();
            startActivity(new Intent(MyProfileActivity.this, UserLogin.class));
            finish();
        });
    }

    private void loadProfile() {
        fullName.setText(prefs.getString("full_name", ""));
        email.setText(prefs.getString("email", ""));
        phone.setText(prefs.getString("phone", ""));
        age.setText(prefs.getString("age", ""));
        gender.setText(prefs.getString("gender", ""));
    }

    private void saveProfile() {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("full_name", fullName.getText().toString());
        editor.putString("email", email.getText().toString());
        editor.putString("phone", phone.getText().toString());
        editor.putString("age", age.getText().toString());
        editor.putString("gender", gender.getText().toString());
        editor.apply();
    }

    private void enableEditing(boolean enable) {
        fullName.setEnabled(enable);
        email.setEnabled(enable);
        phone.setEnabled(enable);
        age.setEnabled(enable);
        gender.setEnabled(enable);
        saveBtn.setVisibility(enable ? View.VISIBLE : View.GONE);
    }
}
