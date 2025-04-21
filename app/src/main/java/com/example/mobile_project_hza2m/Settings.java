package com.example.mobile_project_hza2m;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SwitchCompat;

public class Settings extends AppCompatActivity {

    private static final String PREF_NAME = "AppPrefs";
    private SwitchCompat notificationsSwitch;
    private SwitchCompat darkModeSwitch;
    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Load preferences early to apply theme
        prefs = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        boolean darkMode = prefs.getBoolean("dark_mode_enabled", false);
        AppCompatDelegate.setDefaultNightMode(
                darkMode ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO
        );

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        notificationsSwitch = findViewById(R.id.switch_notifications);
        darkModeSwitch = findViewById(R.id.switch_darkmode);
        Button updateBtn = findViewById(R.id.update);

        // Apply saved preferences
        notificationsSwitch.setChecked(prefs.getBoolean("notifications_enabled", true));
        darkModeSwitch.setChecked(darkMode);

        // Toggle Notifications
        notificationsSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            prefs.edit().putBoolean("notifications_enabled", isChecked).apply();
            Toast.makeText(this,
                    isChecked ? getString(R.string.notifications_on) : getString(R.string.notifications_off),
                    Toast.LENGTH_SHORT).show();
        });

        // Toggle Dark Mode
        darkModeSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            prefs.edit().putBoolean("dark_mode_enabled", isChecked).apply();
            AppCompatDelegate.setDefaultNightMode(
                    isChecked ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO
            );
        });

        // Update Profile
        updateBtn.setOnClickListener(v -> startActivity(new Intent(this, MyProfileActivity.class)));

        // Navigations
        findViewById(R.id.security).setOnClickListener(v -> startActivity(new Intent(Settings.this, Security.class)));
        findViewById(R.id.privacy).setOnClickListener(v -> startActivity(new Intent(this, Privacy.class)));
        findViewById(R.id.language).setOnClickListener(v ->
                Toast.makeText(this, getString(R.string.only_english_available), Toast.LENGTH_SHORT).show());
        findViewById(R.id.feedback).setOnClickListener(v ->
                startActivity(new Intent(this, ContactUsActivity.class)));

        // Terms
        findViewById(R.id.terms).setOnClickListener(v ->
                showDialog(getString(R.string.terms_title), getString(R.string.terms_text)));

        // About
        findViewById(R.id.about).setOnClickListener(v ->
                showDialog(getString(R.string.about_title), getString(R.string.about_text)));

        // Invite
        findViewById(R.id.invite).setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse("smsto:"));
            intent.putExtra("sms_body", getString(R.string.invite_message));
            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivity(intent);
            }
        });
    }

    private void showDialog(String title, String message) {
        new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, (dialog, which) -> dialog.dismiss())
                .show();
    }
}
