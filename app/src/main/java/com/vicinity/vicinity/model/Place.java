package com.vicinity.vicinity.model;


import java.util.ArrayList;

public class Place {

    private String key;
    private String name;
    private String address;
    private String openingHours;
    private String phoneNumber;
    private String website;
    private ArrayList<String> photoReferences;
    private ArrayList<String> types;
    private double rating;
    private boolean isOpen;
    private double longitude;
    private double latitude;
    private ArrayList<Review> reviews;

    public Place(String key, String name, String address) {
        this.key = key;
        this.name = name;
        this.address = address;
    }

    public ArrayList<Review> getReviews() {
        return reviews;
    }

    public String getKey() {
        return key;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public String getOpeningHours() {
        return openingHours;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getWebsite() {
        return website;
    }

    public ArrayList<String> getPhotoReferences() {
        return photoReferences;
    }

    public ArrayList<String> getTypes() {
        return types;
    }

    public double getRating() {
        return rating;
    }

    public boolean isOpen() {
        return isOpen;
    }

    public double getLongitude() {
        return longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setOpeningHours(String openingHours) {
        this.openingHours = openingHours;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public void setPhotoReferences(ArrayList<String> photoReferences) {
        this.photoReferences = photoReferences;
    }

    public void setTypes(ArrayList<String> types) {
        this.types = types;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public void setIsOpen(boolean isOpen) {
        this.isOpen = isOpen;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public void setReviews(ArrayList<Review> reviews) {
        this.reviews = reviews;
    }
}
