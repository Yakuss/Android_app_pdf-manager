package com.example.pdfreader;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize FirebaseAuth
        firebaseAuth = FirebaseAuth.getInstance();

        // Find the TextView, Buttons, and FloatingActionButton
        textView = findViewById(R.id.textView);
        Button logoutButton = findViewById(R.id.logoutButton);
        Button addBookTypeButton = findViewById(R.id.addBookTypeButton);
        Button listBooksButton = findViewById(R.id.listBooksButton);
        Button listBookTypesButton = findViewById(R.id.listBookTypesButton);
        Button allBooksButton = findViewById(R.id.allBooksButton);
        FloatingActionButton fab = findViewById(R.id.fab);

        // Get the current user
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser != null) {
            // Get the user's name
            String userName = currentUser.getDisplayName();
            if (userName == null || userName.isEmpty()) {
                userName = "User";
            }
            textView.setText("Welcome, " + userName + "!");
        } else {
            textView.setText("No user is logged in.");
        }

        // Handle logout button click
        logoutButton.setOnClickListener(v -> logoutUser());

        // Handle FloatingActionButton click
        fab.setOnClickListener(v -> navigateToAddingActivity());

        // Handle addBookTypeButton click
        addBookTypeButton.setOnClickListener(v -> navigateToAddBookTypeActivity());

        // Handle listBooksButton click
        listBooksButton.setOnClickListener(v -> navigateToListBooksActivity());

        // Handle listBookTypesButton click
        listBookTypesButton.setOnClickListener(v -> navigateToListBookTypesActivity());

        // Handle allBooksButton click
        allBooksButton.setOnClickListener(v -> navigateToAllBooksActivity());
    }

    private void logoutUser() {
        // Sign out from Firebase Auth
        firebaseAuth.signOut();

        // Navigate back to Login activity
        Intent intent = new Intent(MainActivity.this, Login.class);
        startActivity(intent);
        finish();
    }

    private void navigateToAddingActivity() {
        // Navigate to AddingActivity
        Intent intent = new Intent(MainActivity.this, Adding.class);
        startActivity(intent);
    }

    private void navigateToAddBookTypeActivity() {
        // Navigate to AddBookTypeActivity
        Intent intent = new Intent(MainActivity.this, AddBookTypeActivity.class);
        startActivity(intent);
    }

    private void navigateToListBooksActivity() {
        // Navigate to ListBooksActivity
        Intent intent = new Intent(MainActivity.this, ListBooksActivity.class);
        startActivity(intent);
    }

    private void navigateToListBookTypesActivity() {
        // Navigate to ListBookTypesActivity
        Intent intent = new Intent(MainActivity.this, ListBookTypesActivity.class);
        startActivity(intent);
    }

    private void navigateToAllBooksActivity() {
        // Navigate to AllBooksActivity
        Intent intent = new Intent(MainActivity.this, AllBooksActivity.class);
        startActivity(intent);
    }
}