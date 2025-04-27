package com.example.mobile_project_hza2m;

import android.os.Bundle;
import android.util.Log;
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
import java.util.Objects;

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

        setSupportActionBar(binding.toolbar); // ✅ First set the toolbar
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true); // ✅ Then use it

        recyclerView = findViewById(R.id.recyclerViewUsers);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new AdminUserAdapter(this, userList);
        recyclerView.setAdapter(adapter);

        fetchUsers();
    }


    private void fetchUsers() {
        String url = Config.BASE_URL + "admin/admin_get_all_users.php";

        userList.clear();

        StringRequest request = new StringRequest(Request.Method.GET, url,
                response -> {
                    try {
                        JSONObject jsonObject = new JSONObject(response); // ✅ parse full response
                        JSONArray array = jsonObject.getJSONArray("users"); // ✅ get users array

                        for (int i = 0; i < array.length(); i++) {
                            JSONObject obj = array.getJSONObject(i);
                            int id = obj.getInt("user_id"); // ✅ correct key
                            String name = obj.getString("username"); // ✅ correct key
                            userList.add(new UserAdmin(id, name));
                        }

                        if (!isFinishing() && !isDestroyed()) {
                            adapter.notifyDataSetChanged();
                        }
                    } catch (Exception e) {
                        if (!isFinishing() && !isDestroyed()) {
                            Log.e("PARSE_ERROR", e.toString());
                            Toast.makeText(this, "Parse error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                error -> {
                    if (!isFinishing() && !isDestroyed()) {
                        Toast.makeText(this, "Network error", Toast.LENGTH_SHORT).show();
                    }
                }
        );

        Volley.newRequestQueue(this).add(request);
    }

}
