package com.example.mobile_project_hza2m;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
    private ActivityDisplayServicesBinding binding;
    private AppBarConfiguration appBarConfiguration;
    private RecyclerView serviceRecyclerView;
    private ServiceCategoryAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDisplayServicesBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);

        serviceRecyclerView = findViewById(R.id.serviceRecyclerView);;

        categoryList = new ArrayList<>();
        adapter = new ServiceCategoryAdapter(this, categoryList);

        serviceRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        serviceRecyclerView.setAdapter(adapter);

        //loadServiceData();
        loadServiceData();
    }

    private void loadServiceData() {
        String url = Config.BASE_URL + "services/get_services_grouped.php";

        Volley.newRequestQueue(this).add(new StringRequest(Request.Method.GET, url,
                response -> {
                    Log.d("RawResponse", response); // ← Add this line
                    try {
                        JSONObject json = new JSONObject(response);
                        if (json.getBoolean("success")) {
                            JSONArray groups = json.getJSONArray("data");
                            categoryList.clear();

                            for (int i = 0; i < groups.length(); i++) {
                                JSONObject group = groups.getJSONObject(i);
                                String categoryName = group.getString("category");
                                int categoryId = group.getInt("category_id");
                                Log.e("Category33333 ID: ", String.valueOf(categoryId));

                                JSONArray services = group.getJSONArray("services");
                                List<Service> serviceGroupList = new ArrayList<>();

                                for (int j = 0; j < services.length(); j++) {
                                    try {
                                        JSONObject s = services.getJSONObject(j);
                                        Log.d("RawService2222", s.toString());

                                        int serviceId = s.getInt("service_id");
                                        String name = s.optString("service_name", "Untitled");
                                        String logoUrl = s.optString("logo_url", "");
                                        String description = s.optString("description", "");
                                        String bankAccount = s.optString("bank_account", "");

                                        serviceGroupList.add(new Service(
                                                serviceId, name, logoUrl, categoryName, description, bankAccount, categoryId
                                        ));
                                    } catch (Exception e) {
                                        Log.e("ServiceParseError", "Service skipped: " + e.getMessage());
                                    }
                                }


                                categoryList.add(new ServiceCategory(categoryName, serviceGroupList));
                                for (int k =0; serviceGroupList.size() > k; k++){
                                    Log.e("Response2: ", serviceGroupList.get(k).getServiceName());
                                }
                            }

                            adapter.notifyDataSetChanged();
                        } else {
                            Toast.makeText(this, "No services available", Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        Log.e("ParseError", e.getMessage());

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
            return true;
        }

        if (id == R.id.action_mywallet) {
            startActivity(new Intent(this, MyWalletActivity.class));
            return true;
        }

        if (id == R.id.action_myprofile) {
            startActivity(new Intent(this, MyProfileActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
