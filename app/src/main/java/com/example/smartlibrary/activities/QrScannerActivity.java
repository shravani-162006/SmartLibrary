package com.example.smartlibrary.activities;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.smartlibrary.database.DatabaseHelper;
import com.example.smartlibrary.databinding.ActivityQrScannerBinding;
import com.example.smartlibrary.models.BookRequest;
import com.example.smartlibrary.models.IssuedBook;
import com.google.zxing.ResultPoint;
import com.journeyapps.barcodescanner.BarcodeCallback;
import com.journeyapps.barcodescanner.BarcodeResult;

import org.json.JSONObject;

import java.util.List;

public class QrScannerActivity extends AppCompatActivity {

    private static final int CAMERA_PERMISSION_REQUEST = 1002;
    private ActivityQrScannerBinding binding;
    private DatabaseHelper dbHelper;
    private boolean isScanned = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityQrScannerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        dbHelper = DatabaseHelper.getInstance(this);

        binding.btnBackScanner.setOnClickListener(v -> finish());

        checkCameraPermission();
    }

    private void checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_REQUEST);
        } else {
            startScanner();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_PERMISSION_REQUEST) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startScanner();
            } else {
                Toast.makeText(this, "Camera permission is required to scan QR passes.", Toast.LENGTH_LONG).show();
                finish();
            }
        }
    }

    private void startScanner() {
        binding.barcodeScannerView.decodeContinuous(new BarcodeCallback() {
            @Override
            public void barcodeResult(BarcodeResult result) {
                if (result.getText() != null && !isScanned) {
                    isScanned = true;
                    processScannedQrToken(result.getText());
                }
            }

            @Override
            public void possibleResultPoints(List<ResultPoint> resultPoints) {
            }
        });
    }

    private void processScannedQrToken(String rawText) {
        try {
            String requestId = "";
            String bookId = "";
            String userId = "";

            if (rawText.startsWith("{")) {
                JSONObject json = new JSONObject(rawText);
                requestId = json.optString("requestId");
                bookId = json.optString("bookId");
                userId = json.optString("userId");
            } else if (rawText.contains(":")) {
                String[] parts = rawText.split(":");
                if (parts.length >= 3) {
                    requestId = parts[0];
                    bookId = parts[1];
                    userId = parts[2];
                }
            }

            if (requestId.isEmpty()) {
                Toast.makeText(this, "Invalid QR Code payload.", Toast.LENGTH_LONG).show();
                finish();
                return;
            }

            BookRequest request = dbHelper.getRequestById(requestId);
            if (request != null && "APPROVED".equalsIgnoreCase(request.getStatus())) {
                IssuedBook issuedBook = new IssuedBook();
                issuedBook.setIssuedBookId("ISS_" + System.currentTimeMillis());
                issuedBook.setUserId(request.getUserId());
                issuedBook.setBookId(request.getBookId());
                issuedBook.setRequestId(request.getRequestId());
                issuedBook.setBookTitle(request.getBookTitle());
                issuedBook.setBookAuthor(request.getBookAuthor());
                issuedBook.setBookCoverImage(request.getBookCoverImage());
                issuedBook.setIssueDate(System.currentTimeMillis());
                issuedBook.setDueDate(System.currentTimeMillis() + (14L * 24 * 3600 * 1000));
                issuedBook.setStatus("ISSUED");

                dbHelper.issueBookToUser(issuedBook);
                dbHelper.updateRequestStatus(requestId, "COMPLETED", null, 0, 0);

                Toast.makeText(this, "Book Successfully Issued: " + request.getBookTitle(), Toast.LENGTH_LONG).show();
                finish();
            } else {
                Toast.makeText(this, "QR pass is expired, invalid, or already used.", Toast.LENGTH_LONG).show();
                finish();
            }

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error processing QR pass: " + e.getMessage(), Toast.LENGTH_LONG).show();
            finish();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        binding.barcodeScannerView.resume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        binding.barcodeScannerView.pause();
    }
}
