package com.example.pdfreader;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ModifyBookTypeActivity extends AppCompatActivity {

    private EditText nameEditText, descriptionEditText;
    private Button updateButton;
    private DatabaseReference databaseBookTypes;
    private BookType bookType;
    private String originalName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_book_type);

        // Initialize Firebase Database
        databaseBookTypes = FirebaseDatabase.getInstance().getReference("bookTypes");

        // Initialize views
        nameEditText = findViewById(R.id.nameEditText);
        descriptionEditText = findViewById(R.id.descriptionEditText);
        updateButton = findViewById(R.id.updateButton);

        // Get the book type object from the intent
        bookType = (BookType) getIntent().getSerializableExtra("bookType");

        if (bookType == null) {
            Toast.makeText(this, "Failed to load book type data", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Store the original name to handle renaming
        originalName = bookType.getName();

        // Populate the fields with the book type data
        nameEditText.setText(bookType.getName());
        descriptionEditText.setText(bookType.getDescription());

        // Handle update button click
        updateButton.setOnClickListener(v -> updateBookType());
    }

    private void updateBookType() {
        String name = nameEditText.getText().toString().trim();
        String description = descriptionEditText.getText().toString().trim();

        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(description)) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // If the name has changed, delete the old entry and add a new one
        if (!originalName.equals(name)) {
            databaseBookTypes.child(originalName).removeValue().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    saveBookType(name, description);
                } else {
                    Toast.makeText(ModifyBookTypeActivity.this, "Failed to update book type: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        } else {
            saveBookType(name, description);
        }
    }

    private void saveBookType(String name, String description) {
        bookType.setName(name);
        bookType.setDescription(description);

        databaseBookTypes.child(name).setValue(bookType).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(ModifyBookTypeActivity.this, "Book type updated successfully", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(ModifyBookTypeActivity.this, "Failed to update book type: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}