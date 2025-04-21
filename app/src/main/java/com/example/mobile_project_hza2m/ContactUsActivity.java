package com.example.mobile_project_hza2m;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mobile_project_hza2m.databinding.ActivityContactUsBinding;

import java.util.HashMap;
import java.util.Map;

public class ContactUsActivity extends AppCompatActivity {

    private ActivityContactUsBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityContactUsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);

        EditText nameInput = findViewById(R.id.editName);
        EditText emailInput = findViewById(R.id.editEmail);
        EditText messageInput = findViewById(R.id.editMessage);
        Button submitBtn = findViewById(R.id.btnSubmit);

        submitBtn.setOnClickListener(v -> {
            String name = nameInput.getText().toString().trim();
            String email = emailInput.getText().toString().trim();
            String message = messageInput.getText().toString().trim();

            if (name.isEmpty() || email.isEmpty() || message.isEmpty()) {
                Snackbar.make(v, "All fields are required", Snackbar.LENGTH_SHORT).show();
                return;
            }

            sendContactMessage(name, email, message, v);
        });
    }

    private void sendContactMessage(String name, String email, String message, View view) {
        String url = Config.BASE_URL+"submit_contact_message.php";

        RequestQueue queue = Volley.newRequestQueue(this);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                response -> Snackbar.make(view, "Message sent successfully", Snackbar.LENGTH_LONG).show(),
                error -> Snackbar.make(view, "Failed to send message", Snackbar.LENGTH_LONG).show()
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("name", name);
                params.put("email", email);
                params.put("message", message);
                return params;
            }
        };

        queue.add(stringRequest);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        if (id == R.id.action_mywallet) {
            startActivity(new Intent(this, MyWalletActivity.class));
        }

        if (id == R.id.action_myprofile) {
            startActivity(new Intent(this, MyProfileActivity.class));
        }

        return super.onOptionsItemSelected(item);
    }
}
