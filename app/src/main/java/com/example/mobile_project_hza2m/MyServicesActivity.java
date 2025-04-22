package com.example.mobile_project_hza2m;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
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

    private int providerId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_services);

        recyclerView = findViewById(R.id.recyclerViewMyServices);
        fabAddService = findViewById(R.id.fabAddMyService);
        tvServiceType = findViewById(R.id.tvServiceType);
        textViewEmpty = findViewById(R.id.textViewEmpty);
        editTextSearch = findViewById(R.id.editTextSearch);

        SharedPreferences prefs = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        int categoryId = prefs.getInt("category_id", -1);
        providerId = prefs.getInt("provider_id", -1);

        String categoryLabel = getCategoryLabel(categoryId);
        tvServiceType.setText("Service Category: " + categoryLabel);

        serviceList = new ArrayList<>();
        adapter = new MyServiceAdapter(serviceList, (service, position) -> {
            serviceList.remove(position);
            adapter.notifyItemRemoved(position);
            deleteService(service.getId());
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
                                    "$" + svc.getDouble("price"),
                                    R.drawable.ic_launcher_foreground
                            ));
                        }

                        adapter.setOriginalList(serviceList);
                        adapter.notifyDataSetChanged();
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
}
