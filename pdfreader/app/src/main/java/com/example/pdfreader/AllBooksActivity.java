package com.example.pdfreader;

import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class AllBooksActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private SimpleBookAdapter bookAdapter;
    private List<Book> bookList;
    private DatabaseReference databaseUsers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_books);

        // Initialize views
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        bookList = new ArrayList<>();
        bookAdapter = new SimpleBookAdapter(bookList, this);
        recyclerView.setAdapter(bookAdapter);

        // Initialize Firebase Database
        databaseUsers = FirebaseDatabase.getInstance().getReference("users");

        // Load all books from Firebase
        loadAllBooks();
    }

    private void loadAllBooks() {
        databaseUsers.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                bookList.clear();
                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    DatabaseReference userBooksRef = userSnapshot.getRef().child("books");
                    userBooksRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot booksSnapshot) {
                            for (DataSnapshot bookSnapshot : booksSnapshot.getChildren()) {
                                Book book = bookSnapshot.getValue(Book.class);
                                if (book != null) {
                                    bookList.add(book);
                                }
                            }
                            bookAdapter.notifyDataSetChanged();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            Toast.makeText(AllBooksActivity.this, "Failed to load books", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(AllBooksActivity.this, "Failed to load users", Toast.LENGTH_SHORT).show();
            }
        });
    }
}