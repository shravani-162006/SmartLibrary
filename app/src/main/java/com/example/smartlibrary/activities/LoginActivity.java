package com.example.smartlibrary.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.smartlibrary.database.FirebaseManager;
import com.example.smartlibrary.databinding.ActivityLoginBinding;
import com.example.smartlibrary.models.User;
import com.example.smartlibrary.utils.SessionManager;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;
    private SessionManager sessionManager;
    private FirebaseManager firebaseManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        sessionManager = new SessionManager(this);
        firebaseManager = FirebaseManager.getInstance(this);

        binding.btnLogin.setOnClickListener(v -> performLogin());

        binding.tvRegister.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
        });

        binding.tvForgotPassword.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, ForgotPasswordActivity.class));
        });
    }

    private void performLogin() {
        String email = binding.etEmail.getText() != null ? binding.etEmail.getText().toString().trim() : "";
        String password = binding.etPassword.getText() != null ? binding.etPassword.getText().toString().trim() : "";

        if (email.isEmpty()) {
            binding.tilEmail.setError("Please enter a valid email address.");
            return;
        } else {
            binding.tilEmail.setError(null);
        }

        if (password.isEmpty()) {
            binding.tilPassword.setError("Password is required.");
            return;
        } else {
            binding.tilPassword.setError(null);
        }

        setLoading(true);

        firebaseManager.loginUser(email, password, new FirebaseManager.AuthCallback() {
            @Override
            public void onSuccess(User user) {
                setLoading(false);
                sessionManager.saveUserSession(user);
                Toast.makeText(LoginActivity.this, "Welcome back, " + user.getName() + "!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }

            @Override
            public void onFailure(String errorMessage) {
                setLoading(false);
                Toast.makeText(LoginActivity.this, "Authentication Failed: " + errorMessage, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void setLoading(boolean isLoading) {
        if (isLoading) {
            binding.progressBarLogin.setVisibility(View.VISIBLE);
            binding.btnLogin.setEnabled(false);
        } else {
            binding.progressBarLogin.setVisibility(View.GONE);
            binding.btnLogin.setEnabled(true);
        }
    }
}
