package com.example.smartlibrary.activities;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.smartlibrary.database.DatabaseHelper;
import com.example.smartlibrary.databinding.ActivityEditProfileBinding;
import com.example.smartlibrary.models.User;
import com.example.smartlibrary.utils.SessionManager;

public class EditProfileActivity extends AppCompatActivity {

    private ActivityEditProfileBinding binding;
    private SessionManager sessionManager;
    private DatabaseHelper dbHelper;
    private User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEditProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        sessionManager = new SessionManager(this);
        dbHelper = DatabaseHelper.getInstance(this);

        binding.btnBackEditProfile.setOnClickListener(v -> finish());

        currentUser = sessionManager.getUserSession();
        if (currentUser != null) {
            binding.etEditName.setText(currentUser.getName());
            binding.etEditMobile.setText(currentUser.getMobile());
            binding.etEditRoll.setText(currentUser.getRollNumber());
        }

        binding.btnSaveProfile.setOnClickListener(v -> saveProfileChanges());
    }

    private void saveProfileChanges() {
        String name = binding.etEditName.getText() != null ? binding.etEditName.getText().toString().trim() : "";
        String mobile = binding.etEditMobile.getText() != null ? binding.etEditMobile.getText().toString().trim() : "";
        String roll = binding.etEditRoll.getText() != null ? binding.etEditRoll.getText().toString().trim() : "";

        if (name.isEmpty()) {
            binding.tilEditName.setError("Full name is required");
            return;
        } else binding.tilEditName.setError(null);

        if (mobile.isEmpty()) {
            binding.tilEditMobile.setError("Mobile number is required");
            return;
        } else binding.tilEditMobile.setError(null);

        if (roll.isEmpty()) {
            binding.tilEditRoll.setError("Roll number is required");
            return;
        } else binding.tilEditRoll.setError(null);

        if (currentUser != null) {
            currentUser.setName(name);
            currentUser.setMobile(mobile);
            currentUser.setRollNumber(roll);

            dbHelper.updateUserProfile(currentUser);
            sessionManager.saveUserSession(currentUser);

            Toast.makeText(this, "Profile updated successfully!", Toast.LENGTH_SHORT).show();
            finish();
        }
    }
}
