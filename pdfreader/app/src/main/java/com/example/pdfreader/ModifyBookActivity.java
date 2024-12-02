package com.example.pdfreader;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ModifyBookActivity extends AppCompatActivity {

    private static final String TAG = "ModifyBookActivity";
    private EditText titleEditText, authorEditText, dateEditText, pdfUrlEditText, imageUrlEditText;
    private Spinner bookTypeSpinner;
    private Button updateButton;
    private DatabaseReference databaseBooks;
    private DatabaseReference databaseBookTypes;
    private List<BookType> bookTypeList;
    private FirebaseAuth firebaseAuth;
    private Book book;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_book);

        // Initialize Firebase Auth
        firebaseAuth = FirebaseAuth.getInstance();

        // Initialize Firebase Database
        databaseBookTypes = FirebaseDatabase.getInstance().getReference("bookTypes");

        // Initialize views
        titleEditText = findViewById(R.id.titleEditText);
        authorEditText = findViewById(R.id.authorEditText);
        dateEditText = findViewById(R.id.dateEditText);
        pdfUrlEditText = findViewById(R.id.pdfUrlEditText);
        imageUrlEditText = findViewById(R.id.imageUrlEditText);
        bookTypeSpinner = findViewById(R.id.bookTypeSpinner);
        updateButton = findViewById(R.id.updateButton);

        // Initialize book type list
        bookTypeList = new ArrayList<>();

        // Load book types from Firebase
        loadBookTypes();

        // Get the book object from the intent
        book = (Book) getIntent().getSerializableExtra("book");

        if (book == null) {
            Toast.makeText(this, "Failed to load book data", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }


        Log.d(TAG, "Received book: " + book.getId());

        // Populate the fields with the book data
        titleEditText.setText(book.getTitle());
        authorEditText.setText(book.getAuthor());
        dateEditText.setText(book.getDate());
        pdfUrlEditText.setText(book.getPdfUrl());
        imageUrlEditText.setText(book.getImageUrl());

        // Handle update button click
        updateButton.setOnClickListener(v -> updateBook());
    }

    private void loadBookTypes() {
        databaseBookTypes.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                bookTypeList.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    BookType bookType = postSnapshot.getValue(BookType.class);
                    bookTypeList.add(bookType);
                }
                ArrayAdapter<BookType> adapter = new ArrayAdapter<>(ModifyBookActivity.this, android.R.layout.simple_spinner_item, bookTypeList);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                bookTypeSpinner.setAdapter(adapter);

                // Set the selected book type
                int position = adapter.getPosition(book.getBookType());
                bookTypeSpinner.setSelection(position);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(ModifyBookActivity.this, "Failed to load book types", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateBook() {
        String title = titleEditText.getText().toString().trim();
        String author = authorEditText.getText().toString().trim();
        String date = dateEditText.getText().toString().trim();
        String pdfUrl = pdfUrlEditText.getText().toString().trim();
        String imageUrl = imageUrlEditText.getText().toString().trim();
        BookType selectedBookType = (BookType) bookTypeSpinner.getSelectedItem();

        if (TextUtils.isEmpty(title) || TextUtils.isEmpty(author) || TextUtils.isEmpty(date) || TextUtils.isEmpty(pdfUrl) || TextUtils.isEmpty(imageUrl) || selectedBookType == null) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(this, "No user is logged in", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = currentUser.getUid();
        databaseBooks = FirebaseDatabase.getInstance().getReference("users").child(userId).child("books");

        book.setTitle(title);
        book.setAuthor(author);
        book.setDate(date);
        book.setPdfUrl(pdfUrl);
        book.setImageUrl(imageUrl);
        book.setBookType(selectedBookType);

        Log.d(TAG, "Updating book: " + book.getId());

        databaseBooks.child(book.getId()).setValue(book).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(ModifyBookActivity.this, "Book updated successfully", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(ModifyBookActivity.this, "Failed to update book: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}