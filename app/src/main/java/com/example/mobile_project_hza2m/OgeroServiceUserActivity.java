package com.example.mobile_project_hza2m;

import android.os.Bundle;

import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.mobile_project_hza2m.databinding.ActivityOgeroServiceUserBinding;

public class OgeroServiceUserActivity extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;
    private ActivityOgeroServiceUserBinding binding;


    EditText editServiceId;
    Button btnSubscribe;

    String SUBSCRIBE_URL = "http://192.168.0.101/Mobile_submodule_backend/PHP/services/subscribe_service.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityOgeroServiceUserBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);

        /*binding.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAnchorView(R.id.fab)
                        .setAction("Action", null).show();
            }
        }); */

       // editServiceId = findViewById(R.id.editServiceId);  // create this EditText in XML
       // btnSubscribe = findViewById(R.id.btnSubscribe);    // create this Button in XML

       // btnSubscribe.setOnClickListener(v -> handleSubscription());

    }


}