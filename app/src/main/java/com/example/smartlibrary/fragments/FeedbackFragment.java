package com.example.smartlibrary.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.example.smartlibrary.database.DatabaseHelper;
import com.example.smartlibrary.databinding.FragmentFeedbackBinding;
import com.example.smartlibrary.models.FeedbackItem;
import com.example.smartlibrary.models.User;
import com.example.smartlibrary.utils.SessionManager;

import java.util.UUID;

public class FeedbackFragment extends Fragment {

    private FragmentFeedbackBinding binding;
    private DatabaseHelper dbHelper;
    private SessionManager sessionManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentFeedbackBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        dbHelper = DatabaseHelper.getInstance(requireContext());
        sessionManager = new SessionManager(requireContext());

        binding.btnSubmitFeedback.setOnClickListener(v -> submitFeedback());
    }

    private void submitFeedback() {
        float rating = binding.ratingBarFeedback.getRating();
        String comments = binding.etFeedbackComments.getText() != null ? binding.etFeedbackComments.getText().toString().trim() : "";

        if (comments.isEmpty()) {
            binding.tilFeedbackComments.setError("Please share your feedback or suggestions.");
            return;
        } else binding.tilFeedbackComments.setError(null);

        User user = sessionManager.getUserSession();
        String userId = user != null ? user.getUserId() : "user_101";
        String userName = user != null ? user.getName() : "Smart Student";

        FeedbackItem fb = new FeedbackItem(
                UUID.randomUUID().toString(),
                userId, userName, rating, comments, System.currentTimeMillis()
        );

        dbHelper.saveFeedback(fb);

        new AlertDialog.Builder(requireContext())
                .setTitle("Feedback Submitted")
                .setMessage("Thank you for your valuable feedback! We appreciate your support in improving Smart Library.")
                .setPositiveButton("OK", (dialog, which) -> {
                    binding.etFeedbackComments.setText("");
                    binding.ratingBarFeedback.setRating(5f);
                })
                .show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
