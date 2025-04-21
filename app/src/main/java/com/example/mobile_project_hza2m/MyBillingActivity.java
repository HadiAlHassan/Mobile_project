package com.example.mobile_project_hza2m;

import android.app.Activity;
import android.app.ProgressDialog;
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
    TextInputLayout textInputFirstName, textInputAmount;
    EditText editFirstName, editAmount;
    RadioGroup radioGroupCardType;
    Button btnSave, btnCancel;
    Spinner spinnerExpirationYear, spinnerExpirationMonth;

    private ProgressDialog progressDialog;
    private SharedPreferences billingPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMyBillingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        // Bind views
        textInputFirstName = findViewById(R.id.textInputFirstName);
        textInputAmount = findViewById(R.id.textInputAmount);
        editFirstName = findViewById(R.id.editFirstName);
        editAmount = findViewById(R.id.editAmount);
        btnSave = findViewById(R.id.btnSave);
        btnCancel = findViewById(R.id.btnCancel);
        radioGroupCardType = findViewById(R.id.radioGroupCardType);
        spinnerExpirationMonth = findViewById(R.id.spinnerExpirationMonth);
        spinnerExpirationYear = findViewById(R.id.spinnerExpirationYear);

        billingPrefs = getSharedPreferences("BillingPrefs", MODE_PRIVATE);
        loadBillingDetails();  // 👈 Load values when activity starts

        btnSave.setOnClickListener(v -> {
            saveBillingData();
            saveToLocalPrefs(); // 👈 Save locally for autofill next time
        });

        btnCancel.setOnClickListener(v -> clearForm());
    }

    private void saveBillingData() {
        SharedPreferences prefs = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        int userId = prefs.getInt("user_id", -1);
        if (userId <= 0) {
            Toast.makeText(this, "User ID not found", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!validateForm()) return;

        String amountStr = editAmount.getText().toString().trim();
        double amount = Double.parseDouble(amountStr);

        progressDialog = ProgressDialog.show(this, "Processing", "Adding money to wallet...", true);

        String url = Config.BASE_URL+"wallet/add_money_to_wallet.php";

        StringRequest request = new StringRequest(Request.Method.POST, url,
                response -> {
                    progressDialog.dismiss();
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
                        Toast.makeText(this, "Response error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                },
                error -> {
                    progressDialog.dismiss();
                    Toast.makeText(this, "Failed to connect to server", Toast.LENGTH_LONG).show();
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> map = new HashMap<>();
                map.put("user_id", String.valueOf(userId));
                map.put("amount", String.valueOf(amount));
                return map;
            }
        };

        Volley.newRequestQueue(this).add(request);
    }

    private void saveToLocalPrefs() {
        SharedPreferences.Editor editor = billingPrefs.edit();

        editor.putString("first_name", editFirstName.getText().toString());
        editor.putString("last_name", ((EditText) findViewById(R.id.editLastName)).getText().toString());
        editor.putString("card_number", ((EditText) findViewById(R.id.editCardNumber)).getText().toString());
        editor.putString("cvn", ((EditText) findViewById(R.id.editCvn)).getText().toString());
        editor.putString("address", ((EditText) findViewById(R.id.editAddressLine1)).getText().toString());
        editor.putString("city", ((EditText) findViewById(R.id.editCity)).getText().toString());
        editor.putString("country", ((EditText) findViewById(R.id.editCountry)).getText().toString());
        editor.putString("zip", ((EditText) findViewById(R.id.editZipCode)).getText().toString());
        editor.putString("phone", ((EditText) findViewById(R.id.editPhoneNb)).getText().toString());
        editor.putString("email", ((EditText) findViewById(R.id.editEmail)).getText().toString());
        editor.putInt("card_type", radioGroupCardType.getCheckedRadioButtonId());
        editor.putInt("exp_month", spinnerExpirationMonth.getSelectedItemPosition());
        editor.putInt("exp_year", spinnerExpirationYear.getSelectedItemPosition());

        editor.apply();
    }


    private void loadBillingDetails() {
        editFirstName.setText(billingPrefs.getString("first_name", ""));
        ((EditText) findViewById(R.id.editLastName)).setText(billingPrefs.getString("last_name", ""));
        ((EditText) findViewById(R.id.editCardNumber)).setText(billingPrefs.getString("card_number", ""));
        ((EditText) findViewById(R.id.editCvn)).setText(billingPrefs.getString("cvn", ""));
        ((EditText) findViewById(R.id.editAddressLine1)).setText(billingPrefs.getString("address", ""));
        ((EditText) findViewById(R.id.editCity)).setText(billingPrefs.getString("city", ""));
        ((EditText) findViewById(R.id.editCountry)).setText(billingPrefs.getString("country", ""));
        ((EditText) findViewById(R.id.editZipCode)).setText(billingPrefs.getString("zip", ""));
        ((EditText) findViewById(R.id.editPhoneNb)).setText(billingPrefs.getString("phone", ""));
        ((EditText) findViewById(R.id.editEmail)).setText(billingPrefs.getString("email", ""));

        int savedCardTypeId = billingPrefs.getInt("card_type", -1);
        if (savedCardTypeId != -1) {
            radioGroupCardType.check(savedCardTypeId);
        }

        spinnerExpirationMonth.setSelection(billingPrefs.getInt("exp_month", 0));
        spinnerExpirationYear.setSelection(billingPrefs.getInt("exp_year", 0));
    }


    private boolean validateForm() {
        boolean valid = true;

        if (editFirstName.getText().toString().trim().isEmpty()) {
            textInputFirstName.setError("Required");
            valid = false;
        } else {
            textInputFirstName.setError(null);
        }

        String amountStr = editAmount.getText().toString().trim();
        if (amountStr.isEmpty() || !amountStr.matches("\\d+(\\.\\d{1,2})?")) {
            textInputAmount.setError("Valid amount required");
            valid = false;
        } else {
            textInputAmount.setError(null);
        }

        return valid;
    }

    private void clearForm() {
        editFirstName.setText("");
        editAmount.setText("");
        ((EditText) findViewById(R.id.editCardNumber)).setText("");
        ((EditText) findViewById(R.id.editCvn)).setText("");
        ((EditText) findViewById(R.id.editLastName)).setText("");
        ((EditText) findViewById(R.id.editAddressLine1)).setText("");
        ((EditText) findViewById(R.id.editCity)).setText("");
        ((EditText) findViewById(R.id.editCountry)).setText("");
        ((EditText) findViewById(R.id.editZipCode)).setText("");
        ((EditText) findViewById(R.id.editPhoneNb)).setText("");
        ((EditText) findViewById(R.id.editEmail)).setText("");
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