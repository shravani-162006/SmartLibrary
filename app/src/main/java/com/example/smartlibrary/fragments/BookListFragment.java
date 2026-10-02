package com.example.smartlibrary.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;

import com.example.smartlibrary.activities.BookDetailActivity;
import com.example.smartlibrary.adapters.BookAdapter;
import com.example.smartlibrary.adapters.CategoryAdapter;
import com.example.smartlibrary.database.DatabaseHelper;
import com.example.smartlibrary.databinding.FragmentBooksBinding;
import com.example.smartlibrary.models.Book;

import java.util.Arrays;
import java.util.List;

public class BookListFragment extends Fragment {

    private FragmentBooksBinding binding;
    private DatabaseHelper dbHelper;
    private BookAdapter bookAdapter;
    private CategoryAdapter categoryAdapter;

    private String selectedCategory = "All";
    private String searchQuery = "";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentBooksBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        dbHelper = DatabaseHelper.getInstance(requireContext());

        setupCategoryChips();
        setupRecyclerView();
        setupSearchView();

        loadBooks();
    }

    private void setupCategoryChips() {
        List<String> categories = Arrays.asList(
                "All", "Personal Development", "Philosophy & Religion", "Business",
                "Psychology", "Marketing", "Fiction", "Parenting & Education",
                "Finance & Investments", "Nature & Science", "History & Politics"
        );
        categoryAdapter = new CategoryAdapter(categories, category -> {
            selectedCategory = category;
            loadBooks();
        });
        binding.rvCategoryChips.setAdapter(categoryAdapter);
    }

    private void setupRecyclerView() {
        bookAdapter = new BookAdapter(book -> {
            Intent intent = new Intent(requireContext(), BookDetailActivity.class);
            intent.putExtra(BookDetailActivity.EXTRA_BOOK_ID, book.getBookId());
            startActivity(intent);
        });
        binding.rvBookCatalog.setAdapter(bookAdapter);
    }

    private void setupSearchView() {
        binding.searchViewBooks.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchQuery = query;
                loadBooks();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                searchQuery = newText;
                loadBooks();
                return true;
            }
        });
    }

    private void loadBooks() {
        List<Book> books = dbHelper.searchBooks(searchQuery, selectedCategory);

        if (books.isEmpty()) {
            binding.layoutEmpty.emptyStateContainer.setVisibility(View.VISIBLE);
            binding.rvBookCatalog.setVisibility(View.GONE);
        } else {
            binding.layoutEmpty.emptyStateContainer.setVisibility(View.GONE);
            binding.rvBookCatalog.setVisibility(View.VISIBLE);
            bookAdapter.setBooks(books);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
