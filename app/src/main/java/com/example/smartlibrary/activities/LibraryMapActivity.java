package com.example.smartlibrary.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.smartlibrary.databinding.ActivityLibraryMapBinding;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class LibraryMapActivity extends AppCompatActivity implements OnMapReadyCallback {

    public static final String EXTRA_LIBRARY_NAME = "extra_library_name";
    public static final String EXTRA_LIBRARY_ADDRESS = "extra_library_address";
    public static final String EXTRA_LIBRARY_HOURS = "extra_library_hours";
    public static final String EXTRA_LIBRARY_LAT = "extra_library_lat";
    public static final String EXTRA_LIBRARY_LNG = "extra_library_lng";

    private ActivityLibraryMapBinding binding;
    private String name = "Central Smart Library";
    private String address = "100 Academic Way, Campus Center";
    private String hours = "Mon-Sat: 8:00 AM - 10:00 PM";
    private double latitude = 37.7749;
    private double longitude = -122.4194;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLibraryMapBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.btnBackMap.setOnClickListener(v -> finish());

        if (getIntent().hasExtra(EXTRA_LIBRARY_NAME)) {
            name = getIntent().getStringExtra(EXTRA_LIBRARY_NAME);
            address = getIntent().getStringExtra(EXTRA_LIBRARY_ADDRESS);
            hours = getIntent().getStringExtra(EXTRA_LIBRARY_HOURS);
            latitude = getIntent().getDoubleExtra(EXTRA_LIBRARY_LAT, 37.7749);
            longitude = getIntent().getDoubleExtra(EXTRA_LIBRARY_LNG, -122.4194);
        }

        binding.tvMapBranchName.setText(name);
        binding.tvMapBranchAddress.setText(address);
        binding.tvMapBranchHours.setText(hours);

        binding.mapView.onCreate(savedInstanceState);
        binding.mapView.getMapAsync(this);

        binding.btnNavigateDirections.setOnClickListener(v -> openExternalMapsDirections());
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        try {
            LatLng location = new LatLng(latitude, longitude);
            googleMap.addMarker(new MarkerOptions().position(location).title(name).snippet(address));
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 15f));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void openExternalMapsDirections() {
        try {
            Uri gmmIntentUri = Uri.parse("geo:" + latitude + "," + longitude + "?q=" + Uri.encode(name + ", " + address));
            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
            mapIntent.setPackage("com.google.android.apps.maps");
            if (mapIntent.resolveActivity(getPackageManager()) != null) {
                startActivity(mapIntent);
            } else {
                Intent genericIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com/maps/search/?api=1&query=" + latitude + "," + longitude));
                startActivity(genericIntent);
            }
        } catch (Exception e) {
            Toast.makeText(this, "Could not open map navigation.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        binding.mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        binding.mapView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding.mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        binding.mapView.onLowMemory();
    }
}
