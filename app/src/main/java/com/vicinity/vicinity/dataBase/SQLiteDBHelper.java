package com.vicinity.vicinity.dataBase;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SQLiteDBHelper extends SQLiteOpenHelper {

    static final int DB_VERSION = 1;

    static final String DB_NAME = "vicinity";
    static final String PLACES_TABLE_NAME = "places";
    static final String PLACES_KEY = "key";
    static final String PLACES_NAME = "name";
    static final String PLACES_ADDRESS = "address";
    static final String PLACES_OPENING_HOURS = "openingHours";
    static final String PLACES_PHONE_NUMBER = "phoneNumber";
    static final String PLACES_WEBSITE = "website";
    static final String PLACES_RATING = "rating";
    static final String PLACES_LONGITUDE = "longitude";
    static final String PLACES_LATITUDE = "latitude";

    static final String REVIEWS_TABLE_NAME = "reviews";
    static final String REVIEWS_ID = "id";
    static final String REVIEWS_TEXT = "text";
    static final String REVIEWS_TIME = "time";
    static final String REVIEWS_RATING = "rating";
    static final String REVIEWS_AUTHOR = "reviews_author";

    static final String PHOTOS_TABLE_NAME = "photos";
    static final String PHOTOS_ID = "id";
    static final String PHOTOS_REFERENCE = "reference";

    static final String LOCATION_TYPES_TYPE = "locationType";
    static final String LOCATION_TYPES_TABLE_NAME = LOCATION_TYPES_TYPE + "s";
    static final String LOCATION_TYPES_ID = "id";


    static SQLiteDBHelper instance;


    private SQLiteDBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    public static SQLiteDBHelper getInstance(Context context){
        if (instance == null){
            instance = new SQLiteDBHelper(context);
        }

        return instance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + PLACES_TABLE_NAME + " (" +
                PLACES_KEY + " VARCHAR(255) PRIMARY KEY, " +
                PLACES_NAME + " VARCHAR(255)," +
                PLACES_ADDRESS + " VARCHAR(255)," +
                PLACES_OPENING_HOURS + " VARCHAR(255)," +
                PLACES_PHONE_NUMBER + " VARCHAR(255)," +
                PLACES_WEBSITE + " VARCHAR(255)," +
                PLACES_RATING + " DOUBLE PRECISION UNSIGNED," +
                PLACES_LONGITUDE + " DOUBLE PRECISION," +
                PLACES_LATITUDE + " DOUBLE PRECISION)");

        db.execSQL("CREATE TABLE " + REVIEWS_TABLE_NAME + " (" +
                REVIEWS_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                PLACES_KEY + " VARCHAR(255), " +
                REVIEWS_AUTHOR + " VARCHAR(255), " +
                REVIEWS_TEXT + " TEXT, " +
                REVIEWS_RATING + " DOUBLE PRECISION UNSIGNED, " +
                REVIEWS_TIME + " UNSIGNED BIG INT," +
                "FOREIGN KEY (" + PLACES_KEY + ") REFERENCES " + PLACES_TABLE_NAME + "(" + PLACES_KEY +  "));");

        db.execSQL("CREATE TABLE " + PHOTOS_TABLE_NAME + " (" +
                PHOTOS_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                PLACES_KEY + " VARCHAR(255)," +
                PHOTOS_REFERENCE + " TEXT," +
                "FOREIGN KEY (" + PLACES_KEY + ") REFERENCES " + PLACES_TABLE_NAME + "(" + PLACES_KEY +  "));");

        db.execSQL("CREATE TABLE " + LOCATION_TYPES_TABLE_NAME + " (" +
                LOCATION_TYPES_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                PLACES_KEY + " VARCHAR(255)," +
                LOCATION_TYPES_TYPE + " TEXT," +
                "FOREIGN KEY (" + PLACES_KEY + ") REFERENCES " + PLACES_TABLE_NAME + "(" + PLACES_KEY +  "));");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE" + PLACES_TABLE_NAME + ";");
        db.execSQL("DROP TABLE" + REVIEWS_TABLE_NAME + ";");
        db.execSQL("DROP TABLE" + PHOTOS_TABLE_NAME + ";");
        db.execSQL("DROP TABLE" + LOCATION_TYPES_TABLE_NAME + ";");
    }
}
