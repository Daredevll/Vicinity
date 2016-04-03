package com.vicinity.vicinity.model;


public class Review {

    private String authorName;
    private String text;
    private double rating;
    private long time;

    public Review(String authorName, String text, double rating, long time) {
        this.authorName = authorName;
        this.text = text;
        this.rating = rating;
        this.time = time;
    }

    public String getAuthorName() {
        return authorName;
    }

    public String getText() {
        return text;
    }

    public double getRating() {
        return rating;
    }

    public long getTime() {
        return time;
    }
}
