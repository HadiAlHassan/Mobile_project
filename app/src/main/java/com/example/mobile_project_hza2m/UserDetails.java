package com.example.mobile_project_hza2m;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.navigation.ui.AppBarConfiguration;


import com.google.android.material.snackbar.Snackbar;

import org.jetbrains.annotations.Nullable;

import java.io.IOException;

public class UserDetails extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 100;
    private ActivityResultLauncher<Intent> galleryLauncher;
    private ImageView imageView;
    private EditText fname, mname, lname, phone, address, username, pass, pass1;
    private RadioButton rb1, rb2;

    Button btnSignUp, btnUploadIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_user_details);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        imageView = findViewById(R.id.imageView);
        btnUploadIcon = findViewById(R.id.btnUploadIcon);
        btnSignUp = findViewById(R.id.btnsignup);

        fname = findViewById(R.id.fname);
        mname = findViewById(R.id.mname);
        lname = findViewById(R.id.lname);
        phone = findViewById(R.id.phone);
        address = findViewById(R.id.address);
        username = findViewById(R.id.username);
        pass = findViewById(R.id.pass);
        pass1 = findViewById(R.id.pass1);
        rb1 = findViewById(R.id.rb1);
        rb2 = findViewById(R.id.rb2);
        //button.setOnClickListener(v -> openGallery());
        //button1.setOnClickListener(v -> validateInputs());
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    private void validateInputs() {
        String nameInput = fname.getText().toString().trim();
        String mnamee = mname.getText().toString().trim();
        String lnamee = lname.getText().toString().trim();
        String phoneInput = phone.getText().toString().trim();
        String addressInput = address.getText().toString().trim();
        String usernameInput = username.getText().toString().trim();
        String passwordInput = pass.getText().toString();
        String confirmPasswordInput = pass1.getText().toString();


        if (nameInput.isEmpty()) {
            fname.setError("Name is required");
            showToast("Please enter your name.");
            return;
        }
        if (mnamee.isEmpty()) {
            mname.setError("Name is required");
            showToast("Please enter your name.");
            return;
        }
        if (lnamee.isEmpty()) {
            lname.setError("Name is required");
            showToast("Please enter your name.");
            return;
        }

        if (phoneInput.isEmpty()) {
            phone.setError("Phone number is required");
            showToast("Please enter your phone number.");
            return;
        }
        if (!phoneInput.matches("^\\d{8}$")) {
            phone.setError("Phone number should be an 8-digit number");
            return;
        }

        if (addressInput.isEmpty()) {
            address.setError("Address is required");
            showToast("Please enter your address.");
            return;
        }
        String emailPattern = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
        if (!addressInput.matches(emailPattern)) {
            address.setError("Please align by the email pattern format");
            return;
        }

        if (usernameInput.isEmpty()) {
            username.setError("Username is required");
            showToast("Please enter your username.");
            return;
        }

        if (passwordInput.isEmpty()) {
            pass.setError("Password is required");
            showToast("Please enter your password.");
            return;
        }
        String passpattern = "^(?=.*[A-Z])(?=.*[a-zA-Z0-9]).{6,}$";
        if (!passwordInput.matches(passpattern)) {
            pass.setError("Please note that your chosen password should be at least 6 alphanumerics long with one uppercase letter ");
            return;
        }


        if (confirmPasswordInput.isEmpty()) {
            pass1.setError("Please confirm your password");
            showToast("Please confirm your password.");
            return;
        }

        if (!passwordInput.equals(confirmPasswordInput)) {
            pass1.setError("Passwords do not match");
            showToast("Passwords mismatch.");
            return;
        }

        if (!rb1.isChecked() && !rb2.isChecked()) {
            showToast("Please select your age group.");
            return;
        }

        if (rb1.isChecked()) {
            showToast("You must be at least 18 years old to sign up.");
            return;
        }


        AlertDialog.Builder builder = new AlertDialog.Builder(UserDetails.this);
        builder.setTitle("Terms and Conditions");
        builder.setMessage("Please review and accept our Terms & Conditions before signing up:\n\n" +
                "1. You must be at least 18 years old and legally eligible to use this app.\n\n" +
                "2. You are responsible for your account, including all transactions and keeping your credentials secure.\n\n" +
                "3. Illegal use is strictly prohibited — including fraud, money laundering, or unauthorized transfers.\n\n" +
                "4. Fees may apply for specific transactions, transfers, or account services. These will be disclosed when applicable.\n\n" +
                "5. We may suspend or terminate your account for policy violations or suspicious activity.\n\n");

        builder.setPositiveButton("Accept", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                showToast("Sign-up successful! ");
                Intent success = new Intent(UserDetails.this, UserLogin.class);
                startActivity(success);
            }
        });

        builder.setNegativeButton("Reject", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                showToast("Sign-up failed: You must accept the community guidelines.");
            }
        });

        builder.setCancelable(false);
        builder.create().show();
    }


    private void showToast(String message) {
        Toast.makeText(UserDetails.this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            Uri imageUri = data.getData();
            imageView.setImageURI(imageUri);

            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}