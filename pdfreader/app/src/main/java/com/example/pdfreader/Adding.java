package com.example.pdfreader;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

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

public class Adding extends AppCompatActivity {

    private EditText titleEditText, authorEditText, dateEditText, pdfUrlEditText, imageUrlEditText;
    private Spinner bookTypeSpinner;
    private Button addButton;
    private DatabaseReference databaseBooks;
    private DatabaseReference databaseBookTypes;
    private List<BookType> bookTypeList;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adding);

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
        addButton = findViewById(R.id.addButton);

        // Initialize book type list
        bookTypeList = new ArrayList<>();

        // Load book types from Firebase
        loadBookTypes();

        // Handle add button click
        addButton.setOnClickListener(v -> addBook());
    }

    private void loadBookTypes() {
        databaseBookTypes.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                bookTypeList.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    BookType bookType = postSnapshot.getValue(BookType.class);
                    bookTypeList.add(bookType);
                }
                ArrayAdapter<BookType> adapter = new ArrayAdapter<>(Adding.this, android.R.layout.simple_spinner_item, bookTypeList);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                bookTypeSpinner.setAdapter(adapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(Adding.this, "Failed to load book types", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addBook() {
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

        String id = databaseBooks.push().getKey();
        Book book = new Book(title, author, date, pdfUrl, imageUrl, selectedBookType);

        if (id != null) {
            databaseBooks.child(id).setValue(book).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(Adding.this, "Book added successfully", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(Adding.this, "Failed to add book: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        }
    }
}