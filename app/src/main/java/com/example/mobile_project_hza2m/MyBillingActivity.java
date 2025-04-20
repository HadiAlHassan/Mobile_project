package com.example.mobile_project_hza2m;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.mobile_project_hza2m.databinding.ActivityMyBillingBinding;
import com.google.android.material.textfield.TextInputLayout;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class MyBillingActivity extends AppCompatActivity {

    private ActivityMyBillingBinding binding;
    TextInputLayout textInputFirstName, textInputLastName, textInputAddressLine, textInputCity,
            textInputCountry, textInputZip, textInputPhoneNb, textInputEmail, textInputAmount;
    EditText editFirstName, editLastName, editAddressLine1, editCity, editCountry, editZipCode,
            editPhoneNb, editEmail, editAmount;
    RadioGroup radioGroupCardType;
    RadioButton radioButtonCardTypeVisa, radioButtonCardTypeMastercard, radioButtonCardTypeAmex;
    Button btnSave, btnCancel;
    Spinner spinnerExpirationYear, spinnerExpirationMonth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMyBillingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        // Initialize views
        textInputFirstName = findViewById(R.id.textInputFirstName);
        textInputLastName = findViewById(R.id.textInputLastName);
        textInputAddressLine = findViewById(R.id.textInputAddressLine);
        textInputCity = findViewById(R.id.textInputCity);
        textInputCountry = findViewById(R.id.textInputCountry);
        textInputZip = findViewById(R.id.textInputZip);
        textInputPhoneNb = findViewById(R.id.textInputPhoneNb);
        textInputEmail = findViewById(R.id.textInputEmail);
        textInputAmount = findViewById(R.id.textInputAmount);

        editFirstName = findViewById(R.id.editFirstName);
        editLastName = findViewById(R.id.editLastName);
        editAddressLine1 = findViewById(R.id.editAddressLine1);
        editCity = findViewById(R.id.editCity);
        editCountry = findViewById(R.id.editCountry);
        editZipCode = findViewById(R.id.editZipCode);
        editPhoneNb = findViewById(R.id.editPhoneNb);
        editEmail = findViewById(R.id.editEmail);
        editAmount = findViewById(R.id.editAmount);

        btnSave = findViewById(R.id.btnSave);
        btnCancel = findViewById(R.id.btnCancel);

        radioButtonCardTypeVisa = findViewById(R.id.radioButtonCardTypeVisa);
        radioButtonCardTypeMastercard = findViewById(R.id.radioButtonCardTypeMastercard);
        radioButtonCardTypeAmex = findViewById(R.id.radioButtonCardTypeAmex);

        radioGroupCardType = findViewById(R.id.radioGroupCardType);
        spinnerExpirationMonth = findViewById(R.id.spinnerExpirationMonth);
        spinnerExpirationYear = findViewById(R.id.spinnerExpirationYear);

        btnSave.setOnClickListener(v -> saveBillingData());
        btnCancel.setOnClickListener(v -> clearForm());
    }

    private void saveBillingData() {
        SharedPreferences prefs = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        int userId = prefs.getInt("user_id", -1);
        if (userId <= 0) {
            Toast.makeText(this, "User ID not found", Toast.LENGTH_SHORT).show();
            return;
        }

        // Validation
        if (!validateForm()) return;

        String amountStr = editAmount.getText().toString().trim();
        double amount = Double.parseDouble(amountStr);

        String url = "http://192.168.0.101/Mobile_submodule_backend/PHP/wallet/add_money_to_wallet.php";

        StringRequest request = new StringRequest(Request.Method.POST, url,
                response -> {
                    try {
                        JSONObject json = new JSONObject(response);
                        if (json.getBoolean("success")) {
                            Toast.makeText(this, "Money added successfully!", Toast.LENGTH_SHORT).show();
                            setResult(Activity.RESULT_OK);
                            finish();
                        } else {
                            Toast.makeText(this, json.getString("message"), Toast.LENGTH_LONG).show();
                        }
                    } catch (Exception e) {
                        Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                },
                error -> Toast.makeText(this, "Failed to connect to server", Toast.LENGTH_LONG).show()
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("user_id", String.valueOf(userId));
                params.put("amount", String.valueOf(amount));
                return params;
            }
        };

        Volley.newRequestQueue(this).add(request);
    }

    private boolean validateForm() {
        boolean valid = true;

        if (editFirstName.getText().toString().trim().isEmpty()) {
            textInputFirstName.setError("Required");
            valid = false;
        } else textInputFirstName.setError(null);

        if (editAmount.getText().toString().trim().isEmpty() ||
                !editAmount.getText().toString().trim().matches("\\d+(\\.\\d{1,2})?")) {
            textInputAmount.setError("Valid amount required");
            valid = false;
        } else textInputAmount.setError(null);

        return valid;
    }

    private void clearForm() {
        editFirstName.setText("");
        editLastName.setText("");
        editAddressLine1.setText("");
        editCity.setText("");
        editCountry.setText("");
        editZipCode.setText("");
        editPhoneNb.setText("");
        editEmail.setText("");
        editAmount.setText("");
        ((EditText) findViewById(R.id.editCardNumber)).setText("");
        ((EditText) findViewById(R.id.editCvn)).setText("");
        radioGroupCardType.clearCheck();
        spinnerExpirationMonth.setSelection(0);
        spinnerExpirationYear.setSelection(0);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main2, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return item.getItemId() == R.id.action_settings || super.onOptionsItemSelected(item);
    }
}
