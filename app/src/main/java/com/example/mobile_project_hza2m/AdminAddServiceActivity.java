package com.example.mobile_project_hza2m;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.mobile_project_hza2m.databinding.ActivityAdminAddServiceBinding;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AdminAddServiceActivity extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;
    private ActivityAdminAddServiceBinding binding;

    RecyclerView recyclerViewCategories;
    List<CategoryModel> categoryList = new ArrayList<>();
    AdminCategoryAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityAdminAddServiceBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);

        recyclerViewCategories = findViewById(R.id.recyclerViewCategories);
        recyclerViewCategories.setLayoutManager(new LinearLayoutManager(this));
        adapter = new AdminCategoryAdapter(this, categoryList, this::fetchCategories);
        recyclerViewCategories.setAdapter(adapter);

        fetchCategories();

        binding.fab.setOnClickListener(view -> showAddCategoryDialog());
    }

    private void fetchCategories() {
        String url = "http://192.168.0.104/Mobile_submodule_backend/PHP/admin/admin_get_all_categories.php";

        categoryList.clear();

        StringRequest request = new StringRequest(Request.Method.GET, url,
                response -> {
                    try {
                        JSONArray array = new JSONArray(response);
                        for (int i = 0; i < array.length(); i++) {
                            JSONObject obj = array.getJSONObject(i);
                            int id = obj.getInt("id");
                            String name = obj.getString("name");
                            categoryList.add(new CategoryModel(id, name));
                        }
                        adapter.notifyDataSetChanged();
                    } catch (Exception e) {
                        Toast.makeText(this, "Parse error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(this, "Network error", Toast.LENGTH_SHORT).show()
        );

        Volley.newRequestQueue(this).add(request);
    }

    private void showAddCategoryDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add New Category");

        final EditText input = new EditText(this);
        input.setHint("Enter category name");
        builder.setView(input);

        builder.setPositiveButton("Add", (dialog, which) -> {
            String categoryName = input.getText().toString().trim();
            if (!categoryName.isEmpty()) {
                addCategoryToServer(categoryName);
            } else {
                Toast.makeText(this, "Category name cannot be empty", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        builder.show();
    }

    private void addCategoryToServer(String categoryName) {
        String url = "http://192.168.0.104/Mobile_submodule_backend/PHP/admin/admin_add_category.php";

        StringRequest request = new StringRequest(Request.Method.POST, url,
                response -> {
                    Toast.makeText(this, "Category added", Toast.LENGTH_SHORT).show();
                    fetchCategories();
                },
                error -> {
                    Toast.makeText(this, "Failed to add category", Toast.LENGTH_SHORT).show();
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("category_name", categoryName);
                return params;
            }
        };

        Volley.newRequestQueue(this).add(request);
    }
}
