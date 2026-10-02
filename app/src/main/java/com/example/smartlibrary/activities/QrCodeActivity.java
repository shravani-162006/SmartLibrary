package com.example.smartlibrary.activities;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.smartlibrary.database.DatabaseHelper;
import com.example.smartlibrary.databinding.ActivityQrCodeBinding;
import com.example.smartlibrary.models.BookRequest;
import com.example.smartlibrary.utils.DateUtils;
import com.example.smartlibrary.utils.QRCodeUtils;

public class QrCodeActivity extends AppCompatActivity {

    public static final String EXTRA_REQUEST_ID = "extra_request_id";

    private ActivityQrCodeBinding binding;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityQrCodeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        dbHelper = DatabaseHelper.getInstance(this);

        binding.btnBackQr.setOnClickListener(v -> finish());

        String requestId = getIntent().getStringExtra(EXTRA_REQUEST_ID);
        if (requestId != null) {
            BookRequest request = dbHelper.getRequestById(requestId);
            if (request != null) {
                displayQrPass(request);
            } else {
                Toast.makeText(this, "Request pass not found", Toast.LENGTH_SHORT).show();
                finish();
            }
        } else {
            finish();
        }
    }

    private void displayQrPass(BookRequest request) {
        binding.tvQrBookTitle.setText(request.getBookTitle());
        binding.tvQrUserName.setText("Student: " + request.getUserName());
        binding.tvQrRequestId.setText("Request ID: " + request.getRequestId());
        binding.tvQrExpiry.setText("Expires: " + DateUtils.formatDateTime(request.getExpiresAt()));

        String tokenPayload = QRCodeUtils.createSecureTokenPayload(
                request.getRequestId(), request.getBookId(), request.getUserId(), request.getExpiresAt()
        );

        Bitmap qrBitmap = QRCodeUtils.generateQRCode(tokenPayload, 500, 500);
        if (qrBitmap != null) {
            binding.imgQrCode.setImageBitmap(qrBitmap);
        }
    }
}
