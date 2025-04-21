package com.example.mobile_project_hza2m;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.mobile_project_hza2m.databinding.ActivityAdminViewProviderBinding;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class AdminViewProviderActivity extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;
    private ActivityAdminViewProviderBinding binding;

    RecyclerView recyclerView;
    AdminProviderAdapter adapter;
    List<Provider> providerList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityAdminViewProviderBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);

        recyclerView = findViewById(R.id.recyclerViewProviders);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new AdminProviderAdapter(this, providerList);
        recyclerView.setAdapter(adapter);

        fetchProviders();
    }

    private void fetchProviders() {
        String url = Config.BASE_URL+"admin/admin_get_all_providers.php";

        providerList.clear();

        StringRequest request = new StringRequest(Request.Method.GET, url,
                response -> {
                    try {
                        JSONArray array = new JSONArray(response);
                        for (int i = 0; i < array.length(); i++) {
                            JSONObject obj = array.getJSONObject(i);
                            int id = obj.getInt("id");
                            String name = obj.getString("business_name");
                            providerList.add(new Provider(id, name));
                        }
                        if (providerList.isEmpty()) {
                            Toast.makeText(this, "No service providers yet.", Toast.LENGTH_SHORT).show();
                        }
                        adapter.notifyDataSetChanged();
                    } catch (Exception e) {
                        e.printStackTrace(); // Debug
                        Toast.makeText(this, "Parse error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                },
                error -> {
                    error.printStackTrace(); // Debug
                    Toast.makeText(this, "Network error: " + error.getMessage(), Toast.LENGTH_LONG).show();
                }

        );

        Volley.newRequestQueue(this).add(request);
    }
}

