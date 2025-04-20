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
import com.example.mobile_project_hza2m.databinding.ActivityAdminViewUserBinding;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class AdminViewUserActivity extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;
    private ActivityAdminViewUserBinding binding;

    RecyclerView recyclerView;
    AdminUserAdapter adapter;
    List<UserAdmin> userList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityAdminViewUserBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);

        recyclerView = findViewById(R.id.recyclerViewUsers);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new AdminUserAdapter(this, userList);
        recyclerView.setAdapter(adapter);

        fetchUsers();
    }

    private void fetchUsers() {
        String url = "http://192.168.0.104/Mobile_submodule_backend/PHP/admin/admin_get_all_users.php";

        userList.clear();

        StringRequest request = new StringRequest(Request.Method.GET, url,
                response -> {
                    try {
                        JSONArray array = new JSONArray(response);
                        for (int i = 0; i < array.length(); i++) {
                            JSONObject obj = array.getJSONObject(i);
                            int id = obj.getInt("id");
                            String name = obj.getString("name");
                            userList.add(new UserAdmin(id, name));
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
}
