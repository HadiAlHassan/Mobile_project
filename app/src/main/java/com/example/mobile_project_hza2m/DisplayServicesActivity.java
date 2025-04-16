package com.example.mobile_project_hza2m;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
        List<Company> ogeroCompanies = new ArrayList<>();
        ogeroCompanies.add(new Company("Ogero", R.drawable.ogero));
        categoryList.add(new ServiceCategory("Ogero Phone Bills", ogeroCompanies));

        // 🛡️ Insurance
        List<Company> insuranceCompanies = new ArrayList<>();
        insuranceCompanies.add(new Company("Bankers", R.drawable.insurance));
        insuranceCompanies.add(new Company("Allianz", R.drawable.insurance));
        categoryList.add(new ServiceCategory("Insurance", insuranceCompanies));

        // 🎓 Tuition Fees
        List<Company> tuitionCompanies = new ArrayList<>();
        tuitionCompanies.add(new Company("LAU", R.drawable.tuitionfees));
        tuitionCompanies.add(new Company("AUB", R.drawable.tuitionfees));
        categoryList.add(new ServiceCategory("Tuition Fees", tuitionCompanies));

        // 📺 Streaming Services
        List<Company> streamingCompanies = new ArrayList<>();
        streamingCompanies.add(new Company("Netflix", R.drawable.streaming));
        streamingCompanies.add(new Company("Shahid", R.drawable.streaming));
        streamingCompanies.add(new Company("OSN+", R.drawable.streaming));
        categoryList.add(new ServiceCategory("Streaming Services", streamingCompanies));
    }


    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        if (id==R.id.action_mywallet){
            Intent i= new Intent(DisplayServicesActivity.this, MyWalletActivity.class);
            startActivity(i);
        }
        if (id==R.id.action_myprofile){
            Intent i= new Intent(DisplayServicesActivity.this, MyProfileActivity.class);
            startActivity(i);
        }

        return super.onOptionsItemSelected(item);
    }

}