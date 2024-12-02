package com.example.pdfreader;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ListBookTypesActivity extends AppCompatActivity {

    private static final String TAG = "ListBookTypesActivity";

    private RecyclerView recyclerView;
    private BookTypeAdapter bookTypeAdapter;
    private List<BookType> bookTypeList;
    private DatabaseReference databaseBookTypes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_book_types);

        // Initialize Firebase Database
        databaseBookTypes = FirebaseDatabase.getInstance().getReference("bookTypes");

        // Initialize views
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        bookTypeList = new ArrayList<>();
        bookTypeAdapter = new BookTypeAdapter(bookTypeList, this);
        recyclerView.setAdapter(bookTypeAdapter);

        // Load book types from Firebase
        loadBookTypes();
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
                bookTypeAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(ListBookTypesActivity.this, "Failed to load book types", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void deleteBookType(BookType bookType) {
        Log.d(TAG, "Deleting book type: " + bookType.getName());
        Query query = databaseBookTypes.orderByChild("name").equalTo(bookType.getName());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    snapshot.getRef().removeValue().addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "Book type deleted successfully");
                            Toast.makeText(ListBookTypesActivity.this, "Book type deleted successfully", Toast.LENGTH_SHORT).show();
                            loadBookTypes(); // Refresh the list after deletion
                        } else {
                            Log.e(TAG, "Failed to delete book type: " + task.getException().getMessage());
                            Toast.makeText(ListBookTypesActivity.this, "Failed to delete book type", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "Failed to delete book type: " + databaseError.getMessage());
                Toast.makeText(ListBookTypesActivity.this, "Failed to delete book type", Toast.LENGTH_SHORT).show();
            }
        });
    }
}