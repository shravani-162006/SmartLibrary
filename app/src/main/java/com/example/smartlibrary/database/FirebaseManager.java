package com.example.smartlibrary.database;

import android.content.Context;
import android.util.Log;

import com.example.smartlibrary.models.Book;
import com.example.smartlibrary.models.BookRequest;
import com.example.smartlibrary.models.IssuedBook;
import com.example.smartlibrary.models.User;
import com.google.firebase.auth.FirebaseAuth;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class FirebaseManager {

    private static final String TAG = "FirebaseManager";
    private static FirebaseManager instance;

    private FirebaseAuth mAuth;
    private DatabaseHelper sqliteHelper;

    public interface AuthCallback {
        void onSuccess(User user);
        void onFailure(String errorMessage);
    }

    public interface ActionCallback {
        void onSuccess();
        void onFailure(String errorMessage);
    }

    private FirebaseManager(Context context) {
        sqliteHelper = DatabaseHelper.getInstance(context.getApplicationContext());
        try {
            mAuth = FirebaseAuth.getInstance();
        } catch (Exception e) {
            Log.e(TAG, "Firebase Auth not available: " + e.getMessage());
        }
    }

    public static synchronized FirebaseManager getInstance(Context context) {
        if (instance == null) {
            instance = new FirebaseManager(context);
        }
        return instance;
    }

    public void loginUser(String email, String password, AuthCallback callback) {
        if (mAuth != null) {
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful() && mAuth.getCurrentUser() != null) {
                            User user = sqliteHelper.getUserByEmail(email);
                            if (user == null) {
                                user = new User(mAuth.getCurrentUser().getUid(), "Smart Student", email, "+1 555-0199", "SL-2026-889", password, "", "student", System.currentTimeMillis());
                                sqliteHelper.registerUser(user);
                            }
                            callback.onSuccess(user);
                        } else {
                            User localUser = sqliteHelper.authenticateUser(email, password);
                            if (localUser != null) {
                                callback.onSuccess(localUser);
                            } else {
                                String err = task.getException() != null ? task.getException().getMessage() : "Invalid email or password";
                                callback.onFailure(err);
                            }
                        }
                    });
        } else {
            User localUser = sqliteHelper.authenticateUser(email, password);
            if (localUser != null) {
                callback.onSuccess(localUser);
            } else {
                callback.onFailure("Invalid email or password");
            }
        }
    }

    public void registerUser(User user, AuthCallback callback) {
        if (mAuth != null) {
            mAuth.createUserWithEmailAndPassword(user.getEmail(), user.getPassword())
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful() && mAuth.getCurrentUser() != null) {
                            user.setUserId(mAuth.getCurrentUser().getUid());
                            sqliteHelper.registerUser(user);
                            callback.onSuccess(user);
                        } else {
                            boolean success = sqliteHelper.registerUser(user);
                            if (success) {
                                callback.onSuccess(user);
                            } else {
                                String err = task.getException() != null ? task.getException().getMessage() : "Registration failed";
                                callback.onFailure(err);
                            }
                        }
                    });
        } else {
            boolean success = sqliteHelper.registerUser(user);
            if (success) {
                callback.onSuccess(user);
            } else {
                callback.onFailure("Registration failed locally.");
            }
        }
    }

    public void sendPasswordResetEmail(String email, ActionCallback callback) {
        if (mAuth != null) {
            mAuth.sendPasswordResetEmail(email)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            callback.onSuccess();
                        } else {
                            String err = task.getException() != null ? task.getException().getMessage() : "Failed to send reset email";
                            callback.onFailure(err);
                        }
                    });
        } else {
            User user = sqliteHelper.getUserByEmail(email);
            if (user != null) {
                callback.onSuccess();
            } else {
                callback.onFailure("User email not found");
            }
        }
    }

    public void logout() {
        if (mAuth != null) {
            mAuth.signOut();
        }
    }
}
