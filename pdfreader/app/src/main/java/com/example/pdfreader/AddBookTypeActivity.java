package com.example.pdfreader;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AddBookTypeActivity extends AppCompatActivity {

    private EditText nameEditText, descriptionEditText;
    private Button addButton;
    private DatabaseReference databaseBookTypes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_book_type);

        // Initialize Firebase Database
        databaseBookTypes = FirebaseDatabase.getInstance().getReference("bookTypes");

        // Initialize views
        nameEditText = findViewById(R.id.nameEditText);
        descriptionEditText = findViewById(R.id.descriptionEditText);
        addButton = findViewById(R.id.addButton);

        // Handle add button click
        addButton.setOnClickListener(v -> addBookType());
    }

    private void addBookType() {
        String name = nameEditText.getText().toString().trim();
        String description = descriptionEditText.getText().toString().trim();

        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(description)) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        String id = databaseBookTypes.push().getKey();
        BookType bookType = new BookType(name, description);

        if (id != null) {
            databaseBookTypes.child(id).setValue(bookType).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(AddBookTypeActivity.this, "Book type added successfully", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(AddBookTypeActivity.this, "Failed to add book type: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        }
    }
}