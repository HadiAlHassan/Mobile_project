package com.example.mobile_project_hza2m;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.mobile_project_hza2m.databinding.ActivityMyServicesBinding;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MyServicesActivity extends AppCompatActivity {
    private TextView tvServiceType, textViewEmpty;
    private EditText editTextSearch;
    private RecyclerView recyclerView;
    private MyServiceAdapter adapter;
    private List<Services> serviceList;
    private FloatingActionButton fabAddService;

    private ActivityMyServicesBinding binding;
    private int providerId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMyServicesBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);

        recyclerView = findViewById(R.id.recyclerViewMyServices);
        fabAddService = findViewById(R.id.fabAddMyService);
        tvServiceType = findViewById(R.id.tvServiceType);
        textViewEmpty = findViewById(R.id.textViewEmpty);
        editTextSearch = findViewById(R.id.editTextSearch);

        SharedPreferences prefs = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        int categoryId = prefs.getInt("category_id", -1);
        providerId = prefs.getInt("provider_id", -1);

        if (providerId == -1) {
            Toast.makeText(this, "Invalid provider. Please log in again.", Toast.LENGTH_LONG).show();
            startActivity(new Intent(this, UserLogin.class));
            finish();
            return;
        }

        String categoryLabel = getCategoryLabel(categoryId);
        tvServiceType.setText("Service Category: " + categoryLabel);

        serviceList = new ArrayList<>();
        adapter = new MyServiceAdapter(serviceList, (service, position) -> {
            new AlertDialog.Builder(this)
                    .setTitle("Delete Service")
                    .setMessage("Are you sure you want to delete this service?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        serviceList.remove(position);
                        adapter.notifyItemRemoved(position);
                        deleteService(service.getId());
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        }, this);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        fabAddService.setOnClickListener(v -> {
            switch (categoryId) {
                case 1: startActivity(new Intent(this, OgeroServiceProviderActivity.class)); break;
                case 2: startActivity(new Intent(this, InsuranceServiceProviderActivity.class)); break;
                case 3: startActivity(new Intent(this, StreamingServiceProviderActivity.class)); break;
                case 4: startActivity(new Intent(this, TelecomServiceProviderActivity.class)); break;
                case 5: startActivity(new Intent(this, TuitionServiceProviderActivity.class)); break;
                default: break;
            }
        });

        editTextSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapter.filter(s.toString());
            }
            @Override public void afterTextChanged(Editable s) {}
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        editTextSearch.setText("");
        fetchProviderServices();
    }

    private void fetchProviderServices() {
        String url = Config.BASE_URL + "services/get_provider_services.php?provider_id=" + providerId;
        StringRequest request = new StringRequest(Request.Method.GET, url,
                response -> {
                    try {
                        JSONObject obj = new JSONObject(response);
                        JSONArray array = obj.getJSONArray("services");

                        serviceList.clear();
                        for (int i = 0; i < array.length(); i++) {
                            JSONObject svc = array.getJSONObject(i);
                            serviceList.add(new Services(
                                    svc.getInt("service_id"),
                                    svc.getString("service_name"),
                                    R.drawable.ic_launcher_foreground
                            ));
                        }

                        Collections.sort(serviceList, Comparator.comparing(Services::getName));
                        adapter.setOriginalList(serviceList);
                        adapter.notifyDataSetChanged();
                        textViewEmpty.setText("No services found. Click + to add one.");
                        textViewEmpty.setVisibility(serviceList.isEmpty() ? View.VISIBLE : View.GONE);
                    } catch (Exception e) {
                        Toast.makeText(this, "Error parsing services", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(this, "Fetch failed", Toast.LENGTH_SHORT).show()
        );
        Volley.newRequestQueue(this).add(request);
    }

    private void deleteService(int serviceId) {
        String url = Config.BASE_URL + "services/delete_service.php";
        StringRequest request = new StringRequest(Request.Method.POST, url,
                response -> {
                    Toast.makeText(this, "Service deleted", Toast.LENGTH_SHORT).show();
                    fetchProviderServices();
                },
                error -> Toast.makeText(this, "Delete failed", Toast.LENGTH_SHORT).show()
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("service_id", String.valueOf(serviceId));
                return params;
            }
        };
        Volley.newRequestQueue(this).add(request);
    }

    private String getCategoryLabel(int categoryId) {
        switch (categoryId) {
            case 1: return "Ogero";
            case 2: return "Insurance";
            case 3: return "Streaming";
            case 4: return "Telecom";
            case 5: return "Tuition";
            default: return "General";
        }
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
            startActivity(new Intent(this, Settings.class));
        } else if (id == R.id.action_mywallet) {
            startActivity(new Intent(this, MyWalletActivity.class));
        } else if (id == R.id.action_myprofile) {
            startActivity(new Intent(this, MyProfileActivity.class));
        }

        return super.onOptionsItemSelected(item);
    }
}
