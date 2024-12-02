package com.example.pdfreader;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class BookTypeAdapter extends RecyclerView.Adapter<BookTypeAdapter.BookTypeViewHolder> {

    private static final String TAG = "BookTypeAdapter";

    private List<BookType> bookTypeList;
    private Context context;

    public BookTypeAdapter(List<BookType> bookTypeList, Context context) {
        this.bookTypeList = bookTypeList;
        this.context = context;
    }

    @NonNull
    @Override
    public BookTypeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_book_type, parent, false);
        return new BookTypeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BookTypeViewHolder holder, int position) {
        BookType bookType = bookTypeList.get(position);
        holder.nameTextView.setText(bookType.getName());
        holder.descriptionTextView.setText(bookType.getDescription());

        holder.deleteButton.setOnClickListener(v -> {
            Log.d(TAG, "Delete button clicked for book type: " + bookType.getName());
            if (context instanceof ListBookTypesActivity) {
                ((ListBookTypesActivity) context).deleteBookType(bookType);
            }
        });
    }

    @Override
    public int getItemCount() {
        return bookTypeList.size();
    }

    public static class BookTypeViewHolder extends RecyclerView.ViewHolder {

        TextView nameTextView, descriptionTextView;
        Button deleteButton;

        public BookTypeViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.nameTextView);
            descriptionTextView = itemView.findViewById(R.id.descriptionTextView);
            deleteButton = itemView.findViewById(R.id.deleteButton);
        }
    }
}