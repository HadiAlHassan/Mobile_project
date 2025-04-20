package com.example.mobile_project_hza2m;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SwitchCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.mobile_project_hza2m.databinding.ActivitySettingsBinding;

public class Settings extends AppCompatActivity {
    SwitchCompat notifications;
    Button update,security;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        SwitchCompat notifications = findViewById(R.id.switch_notifications);
        update = findViewById(R.id.update);



        notifications.setOnCheckedChangeListener((buttonView, isChecked) -> {
            Toast.makeText(this, isChecked ? "Notifications On" : "Notifications Off", Toast.LENGTH_SHORT).show();
        });


        update.setOnClickListener(view -> {
            Toast.makeText(this, "Update Profile clicked", Toast.LENGTH_SHORT).show();
        });




      findViewById(R.id.security).setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
              Intent i= new Intent(Settings.this, Security.class);
              startActivity(i);
          }
      });

        findViewById(R.id.privacy).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i1=new Intent(Settings.this,Privacy.class);
                startActivity(i1);
            }
        });


        findViewById(R.id.language).setOnClickListener(view ->
                Toast.makeText(this, "Only English is available currently.", Toast.LENGTH_SHORT).show());

        findViewById(R.id.feedback).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i2= new Intent(Settings.this, Feedback.class);
                startActivity(i2);
            }
        });

        findViewById(R.id.terms).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                builder.setTitle("Terms & Conditions");
                builder.setMessage("Please review and accept our Terms & Conditions before signing up:\n\n" +
                        "1. You must be at least 18 years old and legally eligible to use this app.\n\n" +
                        "2. You are responsible for your account, including all transactions and keeping your credentials secure.\n\n" +
                        "3. Illegal use is strictly prohibited — including fraud, money laundering, or unauthorized transfers.\n\n" +
                        "4. Fees may apply for specific transactions, transfers, or account services. These will be disclosed when applicable.\n\n" +
                        "5. We may suspend or terminate your account for policy violations or suspicious activity.\n\n");

                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });


                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

        findViewById(R.id.about).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                builder.setTitle("About Us");
                builder.setMessage("Khadamati is a fast, secure, and user-friendly platform that connects users with trusted service providers to simplify daily payments.");

                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });


                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });


        findViewById(R.id.invite).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(Settings.this,"Heading towards your SMS app", Toast.LENGTH_SHORT).show();
                Intent smsIntent = new Intent(Intent.ACTION_VIEW);
                smsIntent.setData(Uri.parse("smsto:")); // This opens SMS app with no number filled
                smsIntent.putExtra("sms_body", "Hey peer! I'm inviting you to freely download Khadamti as a one for all app (one app for all your payments)");

                if (smsIntent.resolveActivity(getPackageManager()) != null) {
                    startActivity(smsIntent);
                }

            }
        });
    }
}
