package com.example.drhoom.booklisting;

public class Book {
    private String mTitle;
    private String mAuthor;

    public String getTitle() {
        return mTitle;
    }

    public String getAuthor() {
        return mAuthor;
    }

    public Book(String title, String author) {
        mTitle = title;
        mAuthor = author;
    }
}
