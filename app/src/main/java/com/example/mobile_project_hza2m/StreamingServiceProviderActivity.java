package com.example.mobile_project_hza2m;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.mobile_project_hza2m.databinding.ActivityTelecomServiceProviderBinding;

public class StreamingServiceProviderActivity extends AppCompatActivity {

    com.example.mobile_project_hza2m.databinding.ActivityStreamingServiceProviderBinding   binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = com.example.mobile_project_hza2m.databinding.ActivityStreamingServiceProviderBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        binding.fabaddfund.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(StreamingServiceProviderActivity.this, AddServiceItemActivity.class);
                startActivity(i);
            }
        });
    }
}