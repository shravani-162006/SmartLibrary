package com.example.smartlibrary.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.smartlibrary.R;
import com.example.smartlibrary.database.DatabaseHelper;
import com.example.smartlibrary.databinding.ActivityBookDetailsBinding;
import com.example.smartlibrary.models.Book;
import com.example.smartlibrary.models.BookRequest;
import com.example.smartlibrary.models.User;
import com.example.smartlibrary.utils.SessionManager;

import java.util.Locale;

public class BookDetailActivity extends AppCompatActivity {

    public static final String EXTRA_BOOK_ID = "extra_book_id";

    private ActivityBookDetailsBinding binding;
    private DatabaseHelper dbHelper;
    private SessionManager sessionManager;
    private Book currentBook;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityBookDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        dbHelper = DatabaseHelper.getInstance(this);
        sessionManager = new SessionManager(this);

        setSupportActionBar(binding.toolbarDetails);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
        binding.toolbarDetails.setNavigationOnClickListener(v -> finish());

        String bookId = getIntent().getStringExtra(EXTRA_BOOK_ID);
        if (bookId != null) {
            currentBook = dbHelper.getBookById(bookId);
        }

        if (currentBook != null) {
            displayBookDetails();
        } else {
            Toast.makeText(this, "Book details not found", Toast.LENGTH_SHORT).show();
            finish();
        }

        binding.btnRequestBook.setOnClickListener(v -> showRequestConfirmationDialog());
    }

    private void displayBookDetails() {
        binding.tvDetailsTitle.setText(currentBook.getTitle());
        binding.tvDetailsAuthor.setText("by " + currentBook.getAuthor());
        binding.tvDetailsCategory.setText(currentBook.getCategory());
        binding.tvDetailsRating.setText(String.format(Locale.getDefault(), "★ %.1f Rating", currentBook.getRating()));
        binding.tvDetailsDescription.setText(currentBook.getDescription());
        binding.tvDetailsIsbn.setText("ISBN: " + currentBook.getIsbn());
        binding.tvDetailsPublisher.setText("Publisher: " + currentBook.getPublisher() + " (" + currentBook.getPublicationYear() + ")");
        binding.tvDetailsCopies.setText("Total Copies: " + currentBook.getTotalCopies() + " | Available: " + currentBook.getAvailableCopies());
        binding.tvDetailsLocation.setText("Location: " + currentBook.getLocationId());

        if (currentBook.getCoverImage() != null && !currentBook.getCoverImage().trim().isEmpty()) {
            Glide.with(this)
                    .load(currentBook.getCoverImage())
                    .placeholder(R.drawable.ic_book)
                    .into(binding.imgDetailsCover);
        }

        if (currentBook.getAvailableCopies() <= 0) {
            binding.btnRequestBook.setEnabled(false);
            binding.btnRequestBook.setText("CURRENTLY UNAVAILABLE");
        }
    }

    private void showRequestConfirmationDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Request Book")
                .setMessage("Would you like to place a borrowing request for '" + currentBook.getTitle() + "'?")
                .setPositiveButton("Request Now", (dialog, which) -> submitBookRequest())
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void submitBookRequest() {
        User user = sessionManager.getUserSession();
        if (user == null) {
            Toast.makeText(this, "Please log in to request books", Toast.LENGTH_SHORT).show();
            return;
        }

        BookRequest request = new BookRequest();
        request.setRequestId("REQ_" + System.currentTimeMillis());
        request.setUserId(user.getUserId());
        request.setUserName(user.getName());
        request.setBookId(currentBook.getBookId());
        request.setBookTitle(currentBook.getTitle());
        request.setBookAuthor(currentBook.getAuthor());
        request.setBookCoverImage(currentBook.getCoverImage());
        request.setStatus("APPROVED"); // Auto-approve for demo pass generation
        request.setRequestedAt(System.currentTimeMillis());
        request.setApprovedAt(System.currentTimeMillis());
        request.setExpiresAt(System.currentTimeMillis() + (24L * 3600 * 1000));
        request.setQrToken("QR_" + request.getRequestId() + "_" + currentBook.getBookId());

        boolean success = dbHelper.createBookRequest(request);
        if (success) {
            Toast.makeText(this, "Request approved! QR Pass generated.", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(this, QrCodeActivity.class);
            intent.putExtra(QrCodeActivity.EXTRA_REQUEST_ID, request.getRequestId());
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(this, "You already have an active request for this book.", Toast.LENGTH_LONG).show();
        }
    }
}
