package com.example.mobile_project_hza2m;

import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import androidx.appcompat.widget.Toolbar;

public class TelecomServiceUserActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private TelecomCardAdapter adapter;
    private ArrayList<TelecomCard> cards;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_telecom_service_user); // make sure this includes content_telecom_service_user

        // Toolbar setup
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Telecom Services"); // ✅ Set page title
        setSupportActionBar(toolbar);

        // ✅ Enable back arrow
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        // ✅ Handle back click
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        recyclerView = findViewById(R.id.recyclerViewTelecomCards);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));

        cards = new ArrayList<>();
        cards.add(new TelecomCard("$5", R.drawable.touch_5usd));
        cards.add(new TelecomCard("$10", R.drawable.touch_10usd));
        cards.add(new TelecomCard("$30", R.drawable.touch_30usd));
        cards.add(new TelecomCard("$60", R.drawable.touch_60usd));

        adapter = new TelecomCardAdapter(this, cards, card -> {
            Toast.makeText(this, "Selected: " + card.getValue(), Toast.LENGTH_SHORT).show();
            // TODO: Open payment screen / subscription logic
        });

        recyclerView.setAdapter(adapter);
    }
}
