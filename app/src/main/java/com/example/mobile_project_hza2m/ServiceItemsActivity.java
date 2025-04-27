package com.example.mobile_project_hza2m;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.mobile_project_hza2m.databinding.ActivityServiceItemsBinding;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ServiceItemsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ServiceItemAdapter adapter;
    private List<ServiceItem> serviceItemList;

    private ActivityServiceItemsBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityServiceItemsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.fab.setOnClickListener(view -> {
            Intent intent = new Intent(ServiceItemsActivity.this, InsertServiceItemActivity.class);
            startActivityForResult(intent, 1001); // 👈 launch with request code
        });



        recyclerView = findViewById(R.id.recyclerViewServiceItems);
        serviceItemList = new ArrayList<>();
        adapter = new ServiceItemAdapter(serviceItemList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        int serviceId = getIntent().getIntExtra("service_id", -1);
        if (serviceId != -1) {
            fetchItems(serviceId);
        } else {
            Toast.makeText(this, "Invalid service ID", Toast.LENGTH_SHORT).show();
        }

        // Enable swipe-to-delete for providers
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(
                new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
                    @Override
                    public boolean onMove(@NonNull RecyclerView recyclerView,
                                          @NonNull RecyclerView.ViewHolder viewHolder,
                                          @NonNull RecyclerView.ViewHolder target) {
                        return false;
                    }

                    @Override
                    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                        int position = viewHolder.getAdapterPosition();
                        ServiceItem item = serviceItemList.get(position);
                        deleteServiceItem(item.getItemId(), position);
                    }
                }
        );
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }

    private void fetchItems(int serviceId) {
        String url = Config.BASE_URL + "services/get_service_items.php?service_id=" + serviceId;

        StringRequest request = new StringRequest(Request.Method.GET, url,
                response -> {
                    try {
                        JSONObject json = new JSONObject(response);
                        if (json.getBoolean("success")) {
                            JSONArray itemsArray = json.getJSONArray("items");
                            serviceItemList.clear();

                            for (int i = 0; i < itemsArray.length(); i++) {
                                JSONObject item = itemsArray.getJSONObject(i);
                                serviceItemList.add(new ServiceItem(
                                        item.getInt("item_id"),
                                        item.getString("item_name"),
                                        item.getString("item_description"),
                                        item.getString("item_price"),
                                        item.optString("item_image", "")
                                ));
                            }

                            adapter.notifyDataSetChanged();
                        } else {
                            Toast.makeText(this, "No items found", Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        Toast.makeText(this, "Error parsing response", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(this, "Failed to fetch items", Toast.LENGTH_SHORT).show()
        );

        Volley.newRequestQueue(this).add(request);
    }

    private void deleteServiceItem(int itemId, int position) {
        String url = Config.BASE_URL + "services/delete_service_item.php";

        StringRequest request = new StringRequest(Request.Method.POST, url,
                response -> {
                    serviceItemList.remove(position);
                    adapter.notifyItemRemoved(position);
                    Toast.makeText(this, "Deleted", Toast.LENGTH_SHORT).show();
                },
                error -> Toast.makeText(this, "Failed to delete", Toast.LENGTH_SHORT).show()
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("item_id", String.valueOf(itemId));
                return params;
            }
        };

        Volley.newRequestQueue(this).add(request);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1001 && resultCode == RESULT_OK) {
            int serviceId = getIntent().getIntExtra("service_id", -1);
            if (serviceId != -1) {
                fetchItems(serviceId); // 🔁 re-fetch after new item is added
            }
        }
    }

}
