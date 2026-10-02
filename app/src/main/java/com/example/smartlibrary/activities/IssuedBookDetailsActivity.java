package com.example.smartlibrary.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.smartlibrary.database.DatabaseHelper;
import com.example.smartlibrary.databinding.ActivityIssuedBookDetailsBinding;
import com.example.smartlibrary.models.IssuedBook;
import com.example.smartlibrary.utils.DateUtils;

public class IssuedBookDetailsActivity extends AppCompatActivity {

    public static final String EXTRA_ISSUED_ID = "extra_issued_id";

    private ActivityIssuedBookDetailsBinding binding;
    private DatabaseHelper dbHelper;
    private IssuedBook currentIssuedBook;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityIssuedBookDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        dbHelper = DatabaseHelper.getInstance(this);

        binding.btnBackIssuedDetails.setOnClickListener(v -> finish());

        String issuedId = getIntent().getStringExtra(EXTRA_ISSUED_ID);
        if (issuedId != null) {
            for (IssuedBook b : dbHelper.getIssuedBookHistory("user_101")) {
                if (issuedId.equals(b.getIssuedBookId())) {
                    currentIssuedBook = b;
                    break;
                }
            }
        }

        if (currentIssuedBook != null) {
            displayIssuedDetails();
        } else {
            finish();
        }

        binding.btnSubmitLearning.setOnClickListener(v -> {
            Intent intent = new Intent(this, AddLearningPointActivity.class);
            intent.putExtra(AddLearningPointActivity.EXTRA_BOOK_ID, currentIssuedBook.getBookId());
            intent.putExtra(AddLearningPointActivity.EXTRA_BOOK_TITLE, currentIssuedBook.getBookTitle());
            startActivity(intent);
        });

        binding.btnConfirmReturn.setOnClickListener(v -> showReturnConfirmationDialog());
    }

    private void displayIssuedDetails() {
        binding.tvIssuedBookDetailsTitle.setText(currentIssuedBook.getBookTitle());
        binding.tvIssuedBookDetailsAuthor.setText("by " + currentIssuedBook.getBookAuthor());
        binding.tvIssuedBookIssueDate.setText("Issue Date: " + DateUtils.formatDate(currentIssuedBook.getIssueDate()));
        binding.tvIssuedBookDueDate.setText("Due Date: " + DateUtils.formatDate(currentIssuedBook.getDueDate()));

        double fine = DateUtils.calculateFine(currentIssuedBook.getDueDate());
        if (fine > 0) {
            binding.tvIssuedBookFine.setText("Overdue Fine: $" + String.format(java.util.Locale.getDefault(), "%.2f", fine));
            binding.tvIssuedBookFine.setTextColor(getColor(com.example.smartlibrary.R.color.danger));
        } else {
            binding.tvIssuedBookFine.setText("Overdue Fine: $0.00 (No Fines)");
            binding.tvIssuedBookFine.setTextColor(getColor(com.example.smartlibrary.R.color.success));
        }

        if (currentIssuedBook.getLearningPoint() != null && !currentIssuedBook.getLearningPoint().isEmpty()) {
            binding.tvIssuedBookLearning.setText("Key Insight: " + currentIssuedBook.getLearningPoint());
        } else {
            binding.tvIssuedBookLearning.setText("No learning point notes recorded yet.");
        }
    }

    private void showReturnConfirmationDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Return Book")
                .setMessage("Are you sure you want to return '" + currentIssuedBook.getBookTitle() + "' to the library?")
                .setPositiveButton("Return Book", (dialog, which) -> {
                    boolean success = dbHelper.returnIssuedBook(currentIssuedBook.getIssuedBookId(), "");
                    if (success) {
                        Toast.makeText(this, "Book returned successfully!", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}
