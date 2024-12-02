package com.example.pdfreader;

import java.io.Serializable;

public class BookType implements Serializable {
    private String name;
    private String description;

    // Empty constructor for Firebase
    public BookType() {}

    public BookType(String name, String description) {
        this.name = name;
        this.description = description;
    }

    // Getters and setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return name;
    }
}