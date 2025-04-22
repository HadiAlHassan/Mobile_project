package com.example.mobile_project_hza2m;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.mobile_project_hza2m.databinding.ActivityDisplayServicesBinding;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class DisplayServicesActivity extends AppCompatActivity {
    private List<ServiceCategory> categoryList;
    private AppBarConfiguration appBarConfiguration;
    private ActivityDisplayServicesBinding binding;

    private RecyclerView serviceRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDisplayServicesBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);
        serviceRecyclerView = findViewById(R.id.serviceRecyclerView);
        categoryList = new ArrayList<>();
        ServiceCategoryAdapter adapter = new ServiceCategoryAdapter(this, categoryList);
        serviceRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        serviceRecyclerView.setAdapter(adapter);

        loadServiceData(adapter);
    }

    private void loadServiceData(ServiceCategoryAdapter adapter) {
        String url = Config.BASE_URL + "services/get_services_grouped.php";

        Volley.newRequestQueue(this).add(new StringRequest(Request.Method.GET, url,
                response -> {
                    try {
                        JSONObject json = new JSONObject(response);
                        if (json.getBoolean("success")) {
                            JSONArray groups = json.getJSONArray("data");

                            for (int i = 0; i < groups.length(); i++) {
                                JSONObject group = groups.getJSONObject(i);
                                String categoryName = group.getString("category");

                                JSONArray services = group.getJSONArray("services");
                                List<Company> companies = new ArrayList<>();

                                for (int j = 0; j < services.length(); j++) {
                                    JSONObject s = services.getJSONObject(j);
                                    String name = s.getString("service_name");
                                    int serviceId = s.getInt("service_id");
                                    int fallbackDrawable = R.drawable.khadmatiico;

                                    companies.add(new Company(name, fallbackDrawable, categoryName, serviceId));
                                }


                                categoryList.add(new ServiceCategory(categoryName, companies));
                            }

                            adapter.notifyDataSetChanged();
                        } else {
                            Toast.makeText(this, "No services available", Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        Toast.makeText(this, "Parse error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                },
                error -> Toast.makeText(this, "Network error: " + error.getMessage(), Toast.LENGTH_LONG).show()
        ));
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
