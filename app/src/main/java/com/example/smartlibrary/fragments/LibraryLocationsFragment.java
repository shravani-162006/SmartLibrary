package com.example.smartlibrary.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.smartlibrary.activities.LibraryMapActivity;
import com.example.smartlibrary.adapters.LibraryAdapter;
import com.example.smartlibrary.database.DatabaseHelper;
import com.example.smartlibrary.databinding.FragmentLibraryLocationsBinding;
import com.example.smartlibrary.models.LibraryLocation;

import java.util.List;

public class LibraryLocationsFragment extends Fragment {

    private FragmentLibraryLocationsBinding binding;
    private DatabaseHelper dbHelper;
    private LibraryAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentLibraryLocationsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        dbHelper = DatabaseHelper.getInstance(requireContext());

        adapter = new LibraryAdapter(new LibraryAdapter.OnLibraryClickListener() {
            @Override
            public void onCallClick(LibraryLocation location) {
                try {
                    Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + location.getPhone()));
                    startActivity(intent);
                } catch (Exception e) {
                    Toast.makeText(requireContext(), "Could not launch phone dialer", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onMapClick(LibraryLocation location) {
                Intent intent = new Intent(requireContext(), LibraryMapActivity.class);
                intent.putExtra(LibraryMapActivity.EXTRA_LIBRARY_NAME, location.getName());
                intent.putExtra(LibraryMapActivity.EXTRA_LIBRARY_ADDRESS, location.getAddress());
                intent.putExtra(LibraryMapActivity.EXTRA_LIBRARY_HOURS, location.getOpeningHours());
                intent.putExtra(LibraryMapActivity.EXTRA_LIBRARY_LAT, location.getLatitude());
                intent.putExtra(LibraryMapActivity.EXTRA_LIBRARY_LNG, location.getLongitude());
                startActivity(intent);
            }
        });

        binding.rvLibraryLocations.setAdapter(adapter);

        loadLibraries();
    }

    private void loadLibraries() {
        List<LibraryLocation> libraries = dbHelper.getAllLibraries();
        if (libraries.isEmpty()) {
            binding.layoutEmpty.emptyStateContainer.setVisibility(View.VISIBLE);
            binding.rvLibraryLocations.setVisibility(View.GONE);
        } else {
            binding.layoutEmpty.emptyStateContainer.setVisibility(View.GONE);
            binding.rvLibraryLocations.setVisibility(View.VISIBLE);
            adapter.setLibraries(libraries);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
