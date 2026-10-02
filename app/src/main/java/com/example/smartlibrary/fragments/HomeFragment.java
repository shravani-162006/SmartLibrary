package com.example.smartlibrary.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.smartlibrary.activities.BookDetailActivity;
import com.example.smartlibrary.activities.MainActivity;
import com.example.smartlibrary.adapters.BookAdapter;
import com.example.smartlibrary.adapters.CategoryAdapter;
import com.example.smartlibrary.database.DatabaseHelper;
import com.example.smartlibrary.databinding.FragmentHomeBinding;
import com.example.smartlibrary.models.Book;
import com.example.smartlibrary.models.User;
import com.example.smartlibrary.utils.SessionManager;

import java.util.Arrays;
import java.util.List;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private DatabaseHelper dbHelper;
    private SessionManager sessionManager;
    private BookAdapter recentBookAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        dbHelper = DatabaseHelper.getInstance(requireContext());
        sessionManager = new SessionManager(requireContext());

        User user = sessionManager.getUserSession();
        if (user != null) {
            binding.tvHomeUserName.setText(user.getName());
        }

        binding.btnNotificationHeader.setOnClickListener(v -> {
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).loadFragment(new NotificationsFragment(), "Notifications");
            }
        });

        binding.cardHomeSearch.setOnClickListener(v -> {
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).loadFragment(new BookListFragment(), "Available Books");
            }
        });

        // Click listeners for stat cards
        binding.cardAvailableBooks.setOnClickListener(v -> {
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).loadFragment(new BookListFragment(), "Available Books");
            }
        });

        binding.cardIssuedBooks.setOnClickListener(v -> {
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).loadFragment(new IssuedBooksFragment(), "Issued Books");
            }
        });

        binding.cardPendingRequests.setOnClickListener(v -> {
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).loadFragment(new PendingRequestsFragment(), "Pending Requests");
            }
        });

        binding.cardLibraryLocations.setOnClickListener(v -> {
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).loadFragment(new LibraryLocationsFragment(), "Library Locations");
            }
        });

        binding.tvViewAllBooks.setOnClickListener(v -> {
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).loadFragment(new BookListFragment(), "Available Books");
            }
        });

        setupCategories();
        setupRecentBooks();
        loadDashboardStats();
    }

    private void setupCategories() {
        List<String> categories = Arrays.asList(
                "Personal Development", "Philosophy & Religion", "Business",
                "Psychology", "Marketing", "Fiction", "Parenting & Education"
        );
        CategoryAdapter categoryAdapter = new CategoryAdapter(categories, category -> {
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).loadFragment(new BookListFragment(), "Available Books");
            }
        });
        binding.rvHomeCategories.setAdapter(categoryAdapter);
    }

    private void setupRecentBooks() {
        recentBookAdapter = new BookAdapter(book -> {
            Intent intent = new Intent(requireContext(), BookDetailActivity.class);
            intent.putExtra(BookDetailActivity.EXTRA_BOOK_ID, book.getBookId());
            startActivity(intent);
        });
        binding.rvRecentBooks.setAdapter(recentBookAdapter);
    }

    private void loadDashboardStats() {
        List<Book> books = dbHelper.getAllBooks();
        binding.tvStatAvailableCount.setText(books.size() + " Books");
        recentBookAdapter.setBooks(books);

        String userId = sessionManager.getUserSession() != null ? sessionManager.getUserSession().getUserId() : "user_101";
        int issuedCount = dbHelper.getActiveIssuedBooks(userId).size();
        binding.tvStatIssuedCount.setText(issuedCount + " Borrowed");

        int pendingCount = dbHelper.getUserRequests(userId).size();
        binding.tvStatPendingCount.setText(pendingCount + " Requests");

        int locationCount = dbHelper.getAllLibraries().size();
        binding.tvStatLocationsCount.setText(locationCount + " Branches");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
