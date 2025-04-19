package com.example.mobile_project_hza2m;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mobile_project_hza2m.databinding.ActivityMyBillingBinding;
import com.google.android.material.textfield.TextInputLayout;

public class MyBillingActivity extends AppCompatActivity {

    private ActivityMyBillingBinding binding;
    TextInputLayout textInputFirstName, textInputLastName, textInputAddressLine, textInputCity,
            textInputCountry, textInputZip, textInputPhoneNb, textInputEmail;
    EditText editFirstName, editLastName, editAddressLine1, editCity, editCountry, editZipCode,
            editPhoneNb, editEmail;
    RadioGroup radioGroupCardType;
    RadioButton radioButtonCardTypeVisa, radioButtonCardTypeMastercard, radioButtonCardTypeAmex;
    Button btnSave, btnCancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMyBillingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        textInputFirstName = findViewById(R.id.textInputFirstName);
        textInputLastName = findViewById(R.id.textInputLastName);
        textInputAddressLine = findViewById(R.id.textInputAddressLine);
        textInputCity = findViewById(R.id.textInputCity);
        textInputCountry = findViewById(R.id.textInputCountry);
        textInputZip = findViewById(R.id.textInputZip);
        textInputPhoneNb = findViewById(R.id.textInputPhoneNb);
        textInputEmail = findViewById(R.id.textInputEmail);

        editFirstName = findViewById(R.id.editFirstName);
        editLastName = findViewById(R.id.editLastName);
        editAddressLine1 = findViewById(R.id.editAddressLine1);
        editCity = findViewById(R.id.editCity);
        editCountry = findViewById(R.id.editCountry);
        editZipCode = findViewById(R.id.editZipCode);
        editPhoneNb = findViewById(R.id.editPhoneNb);
        editEmail = findViewById(R.id.editEmail);

        btnSave = findViewById(R.id.btnSave);
        btnCancel = findViewById(R.id.btnCancel);

        radioButtonCardTypeVisa = findViewById(R.id.radioButtonCardTypeVisa);
        radioButtonCardTypeMastercard = findViewById(R.id.radioButtonCardTypeMastercard);
        radioButtonCardTypeAmex = findViewById(R.id.radioButtonCardTypeAmex);

        radioGroupCardType = findViewById(R.id.radioGroupCardType);

        btnSave.setOnClickListener(v -> {
            // add save logic
            return;
        });

        btnCancel.setOnClickListener(v -> {
            // add cancel logic
            return;
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main2, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
