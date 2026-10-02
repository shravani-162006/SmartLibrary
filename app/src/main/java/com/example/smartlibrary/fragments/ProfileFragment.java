package com.example.smartlibrary.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.smartlibrary.activities.EditProfileActivity;
import com.example.smartlibrary.activities.ForgotPasswordActivity;
import com.example.smartlibrary.activities.MainActivity;
import com.example.smartlibrary.databinding.FragmentProfileBinding;
import com.example.smartlibrary.models.User;
import com.example.smartlibrary.utils.SessionManager;

public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding;
    private SessionManager sessionManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        sessionManager = new SessionManager(requireContext());

        displayUserProfile();

        binding.btnEditProfile.setOnClickListener(v -> {
            startActivity(new Intent(requireContext(), EditProfileActivity.class));
        });

        binding.btnChangePassword.setOnClickListener(v -> {
            startActivity(new Intent(requireContext(), ForgotPasswordActivity.class));
        });

        binding.btnLogoutProfile.setOnClickListener(v -> {
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).confirmLogout();
            }
        });
    }

    private void displayUserProfile() {
        User user = sessionManager.getUserSession();
        if (user != null) {
            binding.tvProfileName.setText(user.getName());
            binding.tvProfileEmail.setText(user.getEmail());
            binding.tvProfileRoll.setText("Roll: " + user.getRollNumber());
            binding.tvProfilePhone.setText("Phone: " + user.getMobile());
            binding.tvProfileRole.setText("Account Role: " + (user.getRole() != null ? user.getRole().toUpperCase() : "STUDENT"));
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        displayUserProfile();
        if (getActivity() instanceof MainActivity) {
            ((MainActivity) getActivity()).updateNavHeader();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
