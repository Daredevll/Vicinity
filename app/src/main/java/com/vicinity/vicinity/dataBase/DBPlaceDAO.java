package com.vicinity.vicinity.dataBase;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.vicinity.vicinity.model.Place;
import com.vicinity.vicinity.model.Review;

import java.util.ArrayList;

public class DBPlaceDAO implements IPlacesDAO {

    private SQLiteDBHelper dbHelper;

    public DBPlaceDAO(Context context){
        this.dbHelper = SQLiteDBHelper.getInstance(context);

    }

    @Override
    public void addPlace(Place place) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(SQLiteDBHelper.PLACES_KEY, place.getKey());
        values.put(SQLiteDBHelper.PLACES_NAME, place.getName());
        values.put(SQLiteDBHelper.PLACES_ADDRESS, place.getAddress());
        values.put(SQLiteDBHelper.PLACES_OPENING_HOURS, place.getOpeningHours());
        values.put(SQLiteDBHelper.PLACES_PHONE_NUMBER, place.getPhoneNumber());
        values.put(SQLiteDBHelper.PLACES_WEBSITE, place.getWebsite());
        values.put(SQLiteDBHelper.PLACES_RATING, place.getRating());
        values.put(SQLiteDBHelper.PLACES_LONGITUDE, place.getLongitude());
        values.put(SQLiteDBHelper.PLACES_LATITUDE, place.getLatitude());

        db.beginTransaction();

        db.insert(SQLiteDBHelper.PLACES_TABLE_NAME, null, values);

        for (Review review : place.getReviews()){
            this.addReview(place.getKey(), review);
        }

        for (String photo : place.getPhotoReferences()){
            this.addPhoto(place.getKey(), photo);
        }

        for (String category : place.getTypes()){
            this.addType(place.getKey(), category);
        }

        db.endTransaction();
    }
//    DB_NAME = "vicinity"

//    PLACES_TABLE_NAME
//    PLACES_KEY
//    PLACES_NAME
//    PLACES_ADDRESS
//    PLACES_OPENING_HOURS
//    PLACES_PHONE_NUMBER
//    PLACES_WEBSITE
//    PLACES_RATING
//    PLACES_LONGITUDE
//    PLACES_LATITUDE

    @Override
    public ArrayList<Place> getAllPlaces() {
        ArrayList<Place> places = new ArrayList<>();
        SQLiteDatabase db = this.dbHelper.getWritableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM " + SQLiteDBHelper.PLACES_TABLE_NAME, null);

        while (cursor.moveToNext()){

            int keyIndex = cursor.getColumnIndex(SQLiteDBHelper.PLACES_KEY);
            int nameIndex = cursor.getColumnIndex(SQLiteDBHelper.PLACES_NAME);
            int addressIndex = cursor.getColumnIndex(SQLiteDBHelper.PLACES_ADDRESS);

            Place place = new Place(cursor.getString(keyIndex), cursor.getString(nameIndex), cursor.getString(addressIndex));

            int openingHoursIndex = cursor.getColumnIndex(SQLiteDBHelper.PLACES_OPENING_HOURS);
            int phoneNumberIndex = cursor.getColumnIndex(SQLiteDBHelper.PLACES_PHONE_NUMBER);
            int websiteIndex = cursor.getColumnIndex(SQLiteDBHelper.PLACES_WEBSITE);
            int ratingIndex = cursor.getColumnIndex(SQLiteDBHelper.PLACES_RATING);
            int longIndex = cursor.getColumnIndex(SQLiteDBHelper.PLACES_LONGITUDE);
            int latIndex = cursor.getColumnIndex(SQLiteDBHelper.PLACES_LATITUDE);

            place.setOpeningHours(cursor.getString(openingHoursIndex));
            place.setPhoneNumber(cursor.getString(phoneNumberIndex));
            place.setWebsite(cursor.getString(websiteIndex));
            place.setRating(cursor.getDouble(ratingIndex));
            place.setLongitude(cursor.getDouble(longIndex));
            place.setLatitude(cursor.getDouble(latIndex));

            place.setPhotoReferences(this.getAllPhotos(place.getKey()));
            place.setTypes(this.getAllTypes(place.getKey()));
            place.setReviews(this.getAllReviews(place.getKey()));

            places.add(place);
        }

        return places;
    }

    @Override
    public void deletePlace(Place place) {

    }

    @Override
    public void updatePlace(Place oldPlace, Place newPlace) {

    }
//    String authorName;
//    String text;
//    double rating;
//    long time;
    private void addReview(String placeKey, Review review){
        SQLiteDatabase db = this.dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(SQLiteDBHelper.REVIEWS_AUTHOR, review.getAuthorName());
        values.put(SQLiteDBHelper.REVIEWS_TEXT, review.getText());
        values.put(SQLiteDBHelper.REVIEWS_RATING, review.getRating());
        values.put(SQLiteDBHelper.REVIEWS_TIME, review.getTime());
        values.put(SQLiteDBHelper.PLACES_KEY, placeKey);

        db.insert(SQLiteDBHelper.REVIEWS_TABLE_NAME, null, values);
    }

    private ArrayList<Review> getAllReviews(String placeKey){
        ArrayList<Review> reviews = new ArrayList<>();
        SQLiteDatabase db = this.dbHelper.getWritableDatabase();

        String[] columns = new String[]{SQLiteDBHelper.REVIEWS_AUTHOR,
                SQLiteDBHelper.REVIEWS_RATING,
                SQLiteDBHelper.REVIEWS_TEXT,
                SQLiteDBHelper.REVIEWS_TIME};

        Cursor cursor = db.query(SQLiteDBHelper.REVIEWS_TABLE_NAME,
                columns,
                "WHERE " + SQLiteDBHelper.PLACES_KEY + "=?", new String[]{placeKey},
                null, null, null);

        while (cursor.moveToNext()){
            int authorIndex = cursor.getColumnIndex(SQLiteDBHelper.REVIEWS_AUTHOR);
            int ratingIndex = cursor.getColumnIndex(SQLiteDBHelper.REVIEWS_RATING);
            int textIndex = cursor.getColumnIndex(SQLiteDBHelper.REVIEWS_TEXT);
            int timeIndex = cursor.getColumnIndex(SQLiteDBHelper.REVIEWS_TIME);

            reviews.add(new Review(cursor.getString(authorIndex),
                    cursor.getString(textIndex),
                    cursor.getDouble(ratingIndex),
                    cursor.getLong(timeIndex)));
        }

        cursor.close();
        return reviews;
    }

    private void deleteAllReviews(String placeKey){

    }

    private void addPhoto(String placeKey, String photoReference){
        SQLiteDatabase db = this.dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(SQLiteDBHelper.PHOTOS_REFERENCE, photoReference);
        values.put(SQLiteDBHelper.PLACES_KEY, placeKey);

        db.insert(SQLiteDBHelper.PHOTOS_TABLE_NAME, null, values);
    }

    private ArrayList<String> getAllPhotos(String placeKey){
        ArrayList<String> photos = new ArrayList<>();
        SQLiteDatabase db = this.dbHelper.getWritableDatabase();

        Cursor cursor = db.query(SQLiteDBHelper.PHOTOS_TABLE_NAME,
                new String[]{SQLiteDBHelper.PHOTOS_REFERENCE},
                "WHERE " + SQLiteDBHelper.PLACES_KEY + "=?",
                new String[]{placeKey},
                null, null, null);

        while (cursor.moveToNext()){
            photos.add(cursor.getString(cursor.getColumnIndex(SQLiteDBHelper.PHOTOS_REFERENCE)));
        }

        return photos;
    }

    private void deleteAllPhotos(String placeKey){

    }

    private void addType(String placeKey, String type){
        SQLiteDatabase db = this.dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(SQLiteDBHelper.LOCATION_TYPES_TYPE, type);
        values.put(SQLiteDBHelper.PLACES_KEY, placeKey);

        db.insert(SQLiteDBHelper.LOCATION_TYPES_TABLE_NAME, null, values);
    }

    private ArrayList<String> getAllTypes(String placeKey){
        ArrayList<String> types = new ArrayList<>();
        SQLiteDatabase db = this.dbHelper.getWritableDatabase();

        Cursor cursor = db.query(SQLiteDBHelper.LOCATION_TYPES_TABLE_NAME,
                new String[]{SQLiteDBHelper.LOCATION_TYPES_TYPE},
                "WHERE " + SQLiteDBHelper.PLACES_KEY + "=?",
                new String[]{placeKey},
                null, null, null);

        while (cursor.moveToNext()){
            types.add(cursor.getString(cursor.getColumnIndex(SQLiteDBHelper.LOCATION_TYPES_TYPE)));
        }

        return types;
    }

    private void deleteAllTypes(String placeKey){

    }
}
