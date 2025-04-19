package com.example.mobile_project_hza2m;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.Menu;
import android.view.MenuItem;

import com.example.mobile_project_hza2m.databinding.ActivityDisplayServicesBinding;

import java.util.ArrayList;
import java.util.List;

public class DisplayServicesActivity extends AppCompatActivity {
    private List<ServiceCategory> categoryList;
    private AppBarConfiguration appBarConfiguration;
    private ActivityDisplayServicesBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityDisplayServicesBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);
        RecyclerView categoryRecyclerView = findViewById(R.id.serviceRecyclerView);
        categoryRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        categoryList = new ArrayList<>();
        loadServiceData();

        ServiceCategoryAdapter adapter = new ServiceCategoryAdapter(this, categoryList);
        categoryRecyclerView.setAdapter(adapter);
    }

    private void loadServiceData() {
        // 🧾 Ogero Phone Bills
        String ogeroType = "Ogero Phone Bills";
        List<Company> ogeroCompanies = new ArrayList<>();
        ogeroCompanies.add(new Company("Ogero", R.drawable.ogero, ogeroType));
        categoryList.add(new ServiceCategory(ogeroType, ogeroCompanies));

        // 🛡️ Insurance
        String insuranceType = "Insurance";
        List<Company> insuranceCompanies = new ArrayList<>();
        insuranceCompanies.add(new Company("Bankers", R.drawable.insurance, insuranceType));
        insuranceCompanies.add(new Company("Allianz", R.drawable.insurance, insuranceType));
        categoryList.add(new ServiceCategory(insuranceType, insuranceCompanies));

        // 🎓 Tuition Fees
        String tuitionType = "Tuition Fees";
        List<Company> tuitionCompanies = new ArrayList<>();
        tuitionCompanies.add(new Company("LAU", R.drawable.tuitionfees, tuitionType));
        tuitionCompanies.add(new Company("AUB", R.drawable.tuitionfees, tuitionType));
        categoryList.add(new ServiceCategory(tuitionType, tuitionCompanies));

        // 📺 Streaming Services
        String streamingType = "Streaming Services";
        List<Company> streamingCompanies = new ArrayList<>();
        streamingCompanies.add(new Company("Netflix", R.drawable.streaming, streamingType));
        streamingCompanies.add(new Company("Shahid", R.drawable.streaming, streamingType));
        streamingCompanies.add(new Company("OSN+", R.drawable.streaming, streamingType));
        categoryList.add(new ServiceCategory(streamingType, streamingCompanies));

        String TelecomType = "Telecommunication Services";
        List<Company> telecomCompanies = new ArrayList<>();
        telecomCompanies.add(new Company("Touch", R.drawable.touch, TelecomType));
        telecomCompanies.add(new Company("Alfa", R.drawable.alfa, TelecomType));
        categoryList.add(new ServiceCategory(TelecomType, telecomCompanies));
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
            return true;
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
