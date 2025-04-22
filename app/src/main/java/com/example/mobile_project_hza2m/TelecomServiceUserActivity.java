package com.example.mobile_project_hza2m;

import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class TelecomServiceUserActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private TelecomCardAdapter adapter;
    private ArrayList<TelecomCard> cards;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_telecom_service_user);

        // Setup toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Telecom Services");
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        // Setup RecyclerView
        recyclerView = findViewById(R.id.recyclerViewTelecomCards);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));

        cards = new ArrayList<>();
        adapter = new TelecomCardAdapter(this, cards, card -> {
            Toast.makeText(this, "Selected: " + card.getValue(), Toast.LENGTH_SHORT).show();
            // TODO: Open payment screen / subscription logic
        });
        recyclerView.setAdapter(adapter);

        int serviceId = getIntent().getIntExtra("service_id", -1);
        if (serviceId != -1) {
            fetchServiceItems(serviceId);
        } else {
            Toast.makeText(this, "Missing service ID", Toast.LENGTH_SHORT).show();
        }
    }

    private void fetchServiceItems(int serviceId) {
        String url = Config.BASE_URL + "services/get_service_items.php?service_id=" + serviceId;

        StringRequest request = new StringRequest(Request.Method.GET, url,
                response -> {
                    try {
                        JSONObject json = new JSONObject(response);
                        if (json.getBoolean("success")) {
                            JSONArray items = json.getJSONArray("items");
                            cards.clear();

                            for (int i = 0; i < items.length(); i++) {
                                JSONObject obj = items.getJSONObject(i);
                                String value = obj.getString("item_name"); // e.g., "$5"
                                String imageName = obj.optString("item_image", "touch_5usd.png"); // e.g., "touch_5usd.png"

                                int imageRes = getResources().getIdentifier(
                                        imageName.replace(".png", ""), "drawable", getPackageName());

                                if (imageRes == 0) imageRes = R.drawable.khadmatiico; // fallback

                                cards.add(new TelecomCard(value, imageRes));
                            }

                            adapter.notifyDataSetChanged();
                        } else {
                            Toast.makeText(this, "No service items found", Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        Toast.makeText(this, "Parse error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(this, "Network error", Toast.LENGTH_SHORT).show()
        );

        Volley.newRequestQueue(this).add(request);
    }
}
