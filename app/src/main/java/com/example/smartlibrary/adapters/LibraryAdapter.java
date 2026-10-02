package com.example.smartlibrary.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartlibrary.databinding.ItemLibraryBinding;
import com.example.smartlibrary.models.LibraryLocation;

import java.util.ArrayList;
import java.util.List;

public class LibraryAdapter extends RecyclerView.Adapter<LibraryAdapter.LibraryViewHolder> {

    public interface OnLibraryClickListener {
        void onCallClick(LibraryLocation location);
        void onMapClick(LibraryLocation location);
    }

    private List<LibraryLocation> list = new ArrayList<>();
    private final OnLibraryClickListener listener;

    public LibraryAdapter(OnLibraryClickListener listener) {
        this.listener = listener;
    }

    public void setLibraries(List<LibraryLocation> newList) {
        this.list = newList != null ? newList : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public LibraryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemLibraryBinding binding = ItemLibraryBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new LibraryViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull LibraryViewHolder holder, int position) {
        LibraryLocation loc = list.get(position);
        holder.binding.tvLibraryName.setText(loc.getName());
        holder.binding.tvLibraryAddress.setText(loc.getAddress());
        holder.binding.tvLibraryHours.setText(loc.getOpeningHours());
        holder.binding.tvLibraryPhone.setText("Phone: " + loc.getPhone());

        holder.binding.btnCallLibrary.setOnClickListener(v -> {
            if (listener != null) listener.onCallClick(loc);
        });

        holder.binding.btnViewOnMap.setOnClickListener(v -> {
            if (listener != null) listener.onMapClick(loc);
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class LibraryViewHolder extends RecyclerView.ViewHolder {
        final ItemLibraryBinding binding;

        LibraryViewHolder(ItemLibraryBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
