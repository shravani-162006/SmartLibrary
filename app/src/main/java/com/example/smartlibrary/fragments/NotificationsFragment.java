package com.example.smartlibrary.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.smartlibrary.adapters.NotificationAdapter;
import com.example.smartlibrary.database.DatabaseHelper;
import com.example.smartlibrary.databinding.FragmentNotificationsBinding;
import com.example.smartlibrary.models.NotificationItem;
import com.example.smartlibrary.utils.SessionManager;

import java.util.List;

public class NotificationsFragment extends Fragment {

    private FragmentNotificationsBinding binding;
    private DatabaseHelper dbHelper;
    private SessionManager sessionManager;
    private NotificationAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentNotificationsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        dbHelper = DatabaseHelper.getInstance(requireContext());
        sessionManager = new SessionManager(requireContext());

        adapter = new NotificationAdapter();
        binding.rvNotifications.setAdapter(adapter);

        loadNotifications();
    }

    private void loadNotifications() {
        String userId = sessionManager.getUserSession() != null ? sessionManager.getUserSession().getUserId() : "user_101";
        List<NotificationItem> list = dbHelper.getUserNotifications(userId);

        if (list.isEmpty()) {
            binding.layoutEmpty.emptyStateContainer.setVisibility(View.VISIBLE);
            binding.layoutEmpty.tvEmptyTitle.setText("No Notifications");
            binding.layoutEmpty.tvEmptyDescription.setText("You have no new alerts or announcements.");
            binding.rvNotifications.setVisibility(View.GONE);
        } else {
            binding.layoutEmpty.emptyStateContainer.setVisibility(View.GONE);
            binding.rvNotifications.setVisibility(View.VISIBLE);
            adapter.setNotifications(list);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
