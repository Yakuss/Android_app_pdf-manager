package com.example.pdfreader;

import android.graphics.Bitmap;
import android.graphics.pdf.PdfRenderer;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class ViewPdfActivity extends AppCompatActivity {

    private static final String TAG = "ViewPdfActivity";
    private ImageView pdfImageView;
    private Button prevButton, nextButton;
    private String pdfUrl;
    private PdfRenderer pdfRenderer;
    private PdfRenderer.Page currentPage;
    private ParcelFileDescriptor fileDescriptor;
    private int currentPageIndex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_pdf);

        pdfImageView = findViewById(R.id.pdfImageView);
        prevButton = findViewById(R.id.prevButton);
        nextButton = findViewById(R.id.nextButton);

        // Get the PDF URL from the intent
        pdfUrl = getIntent().getStringExtra("pdfUrl");

        if (pdfUrl == null || pdfUrl.isEmpty()) {
            Toast.makeText(this, "Failed to load PDF URL", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Load the PDF from the URL
        loadPdfFromUrl(pdfUrl);

        prevButton.setOnClickListener(v -> showPage(currentPageIndex - 1));
        nextButton.setOnClickListener(v -> showPage(currentPageIndex + 1));
    }

    private void loadPdfFromUrl(String pdfUrl) {
        new Thread(() -> {
            try {
                URL url = new URL(pdfUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setDoInput(true);
                connection.connect();

                InputStream inputStream = connection.getInputStream();
                File tempFile = File.createTempFile("tempPdf", ".pdf", getCacheDir());
                FileOutputStream outputStream = new FileOutputStream(tempFile);

                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }

                outputStream.close();
                inputStream.close();

                runOnUiThread(() -> displayPdf(tempFile));
            } catch (Exception e) {
                Log.e(TAG, "Error loading PDF: " + e.getMessage());
                runOnUiThread(() -> Toast.makeText(ViewPdfActivity.this, "Failed to load PDF", Toast.LENGTH_SHORT).show());
            }
        }).start();
    }

    private void displayPdf(File pdfFile) {
        try {
            fileDescriptor = ParcelFileDescriptor.open(pdfFile, ParcelFileDescriptor.MODE_READ_ONLY);
            pdfRenderer = new PdfRenderer(fileDescriptor);
            showPage(0);
        } catch (Exception e) {
            Log.e(TAG, "Error displaying PDF: " + e.getMessage());
            Toast.makeText(this, "Failed to display PDF", Toast.LENGTH_SHORT).show();
        }
    }

    private void showPage(int pageIndex) {
        if (pdfRenderer == null || pageIndex < 0 || pageIndex >= pdfRenderer.getPageCount()) {
            return;
        }

        if (currentPage != null) {
            currentPage.close();
        }

        currentPage = pdfRenderer.openPage(pageIndex);
        Bitmap bitmap = Bitmap.createBitmap(currentPage.getWidth(), currentPage.getHeight(), Bitmap.Config.ARGB_8888);
        currentPage.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY);
        pdfImageView.setImageBitmap(bitmap);

        currentPageIndex = pageIndex;
        updateNavigationButtons();
    }

    private void updateNavigationButtons() {
        prevButton.setEnabled(currentPageIndex > 0);
        nextButton.setEnabled(currentPageIndex < pdfRenderer.getPageCount() - 1);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            if (currentPage != null) {
                currentPage.close();
            }
            if (pdfRenderer != null) {
                pdfRenderer.close();
            }
            if (fileDescriptor != null) {
                fileDescriptor.close();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error closing PDF resources: " + e.getMessage());
        }
    }
}