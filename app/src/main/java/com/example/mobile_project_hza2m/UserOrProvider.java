package com.example.mobile_project_hza2m;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RadioButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.navigation.ui.AppBarConfiguration;

import com.example.mobile_project_hza2m.databinding.ActivityUserOrProviderBinding;

public class UserOrProvider extends AppCompatActivity {
    ImageView back;
    RadioButton rb1, rb2;
    private AppBarConfiguration appBarConfiguration;
    private ActivityUserOrProviderBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_user_or_provider);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        binding = ActivityUserOrProviderBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());



         back= findViewById(R.id.back);
         rb1= findViewById(R.id.rb1);
         rb2= findViewById(R.id.rb2);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent on= new Intent(UserOrProvider.this,HomeActivity.class);
                startActivity(on);
            }


        });

        rb1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Intent in4= new Intent(UserOrProvider.this, UserDetails.class);
                startActivity(in4);
            }
        });

        rb2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in4= new Intent(UserOrProvider.this, ServiceProviderSignUpActivity.class);
                startActivity(in4);
            }
        });





    }
}