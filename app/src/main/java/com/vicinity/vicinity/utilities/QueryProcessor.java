package com.vicinity.vicinity.utilities;

import android.graphics.Bitmap;
import android.location.Location;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.location.places.Place;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;

/**
 * Created by Jovch on 28-Mar-16.
 *
 * A class to process the raw queries and returning an ArrayList<Object> generated with the query result
 * All the classes/activities/fragments which make queries to this class must implement <:@link>PlacesListRequester</:@link> interface
 * because the methods in this class work in a <code>Callback</code> manner
 */
public class QueryProcessor {

    private static final String BASE_URL_ETA_REQUEST = "https://maps.googleapis.com/maps/api/distancematrix/json?units=metric";


    private static final String BASE_URL = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?";
    private static final String MAPS_SERVICES_SERVER_KEY = "AIzaSyDHvkAJQni2X0Qdv6n9JYNEz-l7W6t5-WU";
    // https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=-33.8670522,151.1957362&radius=500&type=hotel&key=AIzaSyDHvkAJQni2X0Qdv6n9JYNEz-l7W6t5-WU


    PlacesListRequester listCustomer;
    private Location customerLocation;
    ArrayList<CustomPlace> retrievedPlaces;

    // TODO: make a request with the given Location and Type criteria
    // TODO: Fetch the response into a JSONObject
    // TODO: Fill the list with objects, generated while decoding the JSON, optionally call listCustomer.update(retrievedPlaces) with every iteration for a smoother effect, but no sorting

    private static QueryProcessor instance;

    public static QueryProcessor getInstance(){
        if (instance == null){
            instance = new QueryProcessor();
        }
        return instance;
    }



    public void fillResultsList(PlacesListRequester customer, Location location, String type, boolean isSortPopular){
        this.listCustomer = customer;
        this.customerLocation = location;
        retrievedPlaces = new ArrayList<CustomPlace>();
        String fullURI;
        String locationString = "location=" + location.getLatitude() + "," + location.getLongitude();
        String rankBy = "&rankby=distance";
        String radius = "&radius=3000";
        String placeType = "&type=" + type;
        String key = "&key=" + Constants.BROWSER_API_KEY;

        // TODO: Remove this dummy url after test
        if (isSortPopular) {
            fullURI = BASE_URL + locationString + radius + placeType + key;
        }
        else {
            fullURI = BASE_URL + locationString + rankBy + placeType + key;
        }

        Log.e("URL", "sending request as: " + fullURI); // fullURI is OK!
        new AsyncResultsFiller().execute(fullURI);

    }

    void updateCustomer(){
        Log.e("URL", "update listCustomer called");
        listCustomer.update(retrievedPlaces);
    }


    public class CustomPlace{


        public class Review{

            double rating;
            String authorName;
            String language;
            String profilePhotoUrl;
            String comment;
            long time;
            Bitmap avatar;

            public Review(String authorName, double rating, long time){
                this.authorName = authorName;
                this.rating = rating;
                this.time = time;
            }

            public void setLanguage(String language) {
                this.language = language;
            }

            public void setProfilePhotoUrl(String profilePhotoUrl) {
                if (!profilePhotoUrl.isEmpty()){
                    GooglePictureDownloader.getInstance().setBitmap(profilePhotoUrl, this);
                }
                this.profilePhotoUrl = profilePhotoUrl;
            }

            public Bitmap getAvatar() {
                return avatar;
            }

            public void setComment(String comment) {
                this.comment = comment;
            }

            public double getRating() {
                return rating;
            }

            public String getAuthorName() {
                return authorName;
            }

            public String getLanguage() {
                return language;
            }

            public String getProfilePhotoUrl() {
                return profilePhotoUrl;
            }

            public String getComment() {
                return comment;
            }

            public long getTime() {
                return time;
            }

            @Override
            public boolean equals(Object o) {
                if (this == o) return true;
                if (o == null || getClass() != o.getClass()) return false;

                Review review = (Review) o;

                if (Double.compare(review.rating, rating) != 0) return false;
                if (time != review.time) return false;
                return authorName.equals(review.authorName);

            }

            @Override
            public int hashCode() {
                int result;
                long temp;
                temp = Double.doubleToLongBits(rating);
                result = (int) (temp ^ (temp >>> 32));
                result = 31 * result + authorName.hashCode();
                result = 31 * result + (int) (time ^ (time >>> 32));
                return result;
            }

            public void setBmp(Bitmap bmp){
                this.avatar = bmp;
            }
        }

        ArrayList<Review> reviews;

        ArrayList<String > photosRefs;

        double longitude;
        double latitude;

        Place realPlace;

        String icon;
        String id;
        String name;
        String placeId;
        String reference;
        String[] types;
        String vicinity;
        String distance;
        String estimateTime;

        String localPhoneNumber;
        String internationalPhoneNumber;
        String website;
        String utcOffset;


        // Optional:

        float rating;
        boolean openNow;

        public CustomPlace(double longitude, double latitude, String icon, String id, String name, String place_id, String reference, String[] types, String vicinity) {
            this.longitude = longitude;
            this.latitude = latitude;
            this.icon = icon;
            this.id = id;
            this.name = name;
            this.placeId = place_id;
            this.reference = reference;
            this.types = types;
            this.vicinity = vicinity;
        }

        public void addDetails(String localPhone, String interPhone, String web, String utcOffset, List<Review> reviews){
            this.localPhoneNumber = localPhone;
            this.internationalPhoneNumber = interPhone;
            this.website = web;
            this.utcOffset = utcOffset;

            for (Review r: reviews){
                addReview(r);
            }
        }

        public void setEstimateTime(String estimateTime) {
            this.estimateTime = estimateTime;
        }

        public void setDistance(String distance) {
            this.distance = distance;
        }


        public String getDistance() {
            return distance;
        }


        void addReview(Review review){
            if (reviews == null){
                reviews = new ArrayList<Review>();
            }
            if (!reviews.contains(review)) {
                reviews.add(review);
            }
        }

        public void addPhotosRefs(ArrayList<String> photosRefs) {
            this.photosRefs = photosRefs;
        }

        public ArrayList<String> getPhotosRefs(){
            return this.photosRefs;
        }

        public ArrayList<Review> getReviews(){
            return this.reviews;
        }

        void addRating(float rating){
            this.rating = rating;
        }

        void addOpenNow(boolean openNow){
            this.openNow = openNow;
        }

        public double getLongitude() {
            return longitude;
        }

        public double getLatitude() {
            return latitude;
        }

        public String getIcon() {
            return icon;
        }

        public String getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public String getPlaceId() {
            return placeId;
        }

        public String getReference() {
            return reference;
        }

        public String[] getTypes() {
            return types;
        }

        public String getVicinity() {
            return vicinity;
        }

        public float getRating() {
            return rating;
        }

        public boolean isOpenNow() {
            return openNow;
        }

        public String getEstimateTime() {
            return this.estimateTime;
        }

        public Place getRealPlace() {
            return realPlace;
        }

        public void setRealPlace(Place realPlace) {
            this.realPlace = realPlace;
        }

        public String getLocalPhoneNumber() {
            return localPhoneNumber;
        }

        public String getInternationalPhoneNumber() {
            return internationalPhoneNumber;
        }

        public String getWebsite() {
            return website;
        }

        public String getUtcOffset() {
            return utcOffset;
        }

    }

    class AsyncResultsFiller extends AsyncTask<String, CustomPlace, ArrayList<CustomPlace>>{

        @Override
        protected ArrayList<CustomPlace> doInBackground(String... params) {


            Log.e("URL", "AsyncResultsFiller doInBackground started...");
            URL url = null;
            HttpURLConnection connection = null;
            Scanner sc = null;
            StringBuffer sb = null;
            JSONObject root = null;
            JSONArray resultsArray = null;

            ArrayList<CustomPlace> workingList = new ArrayList<CustomPlace>();

            double longitude;
            double latitude;

            String icon;
            String id;
            String name;
            String place_id;
            String reference;
            String[] types;
            String vicinity;
            String eta;

            // Optional:

            float rating;
            boolean openNow;

            try{
                url = new URL(params[0]);
                connection = (HttpURLConnection) url.openConnection();
                sc = new Scanner(connection.getInputStream());
                sb = new StringBuffer();

                while (sc.hasNextLine()){
                    sb.append(sc.nextLine());
                }

                root = new JSONObject(sb.toString());

                Log.e("URL", root.getString("status"));




                resultsArray = root.getJSONArray("results");

                Log.e("JSON", "" + resultsArray.length());

                // TODO: Implement NextPageToken to be able to request more than 20 results


                for (int i = 0; i < resultsArray.length(); i++){
                    JSONObject singleResult = resultsArray.getJSONObject(i);
                    longitude = singleResult.getJSONObject("geometry").getJSONObject("location").getDouble("lng");
                    latitude = singleResult.getJSONObject("geometry").getJSONObject("location").getDouble("lat");

                    icon = singleResult.getString("icon");

                    id = singleResult.getString("id");
                    name = singleResult.getString("name");
                    place_id = singleResult.getString("place_id");
                    reference = singleResult.getString("reference");

                    JSONArray typesArr = singleResult.getJSONArray("types");
                    types = new String[typesArr.length()];
                    for (int t = 0; t < typesArr.length(); t++){
                        types[t] = typesArr.getString(t).replace("_", " ");
                    }

                    vicinity = singleResult.getString("vicinity");

                    /*
                            Some additional data that is not always present so it should be validated before fetched:
                            "photos": [ {} ]
                            "rating":
                            "opening_hours": {
                                    "open_now": boolean
                                    "weekday_text": []
                            }
                            "price_level":

                     */

                    CustomPlace cp = new CustomPlace(longitude, latitude, icon, id, name, place_id, reference, types, vicinity);

                    if (singleResult.has("rating")){
                        cp.addRating((float) singleResult.getDouble("rating"));
                    }

                    if (singleResult.has("opening_hours")){
                        JSONObject oHours = singleResult.getJSONObject("opening_hours");
                        if (oHours.has("open_now")){
                            cp.addOpenNow(oHours.getBoolean("open_now"));
                        }
                    }

                    setDistanceAndEta(cp);

                    // Adds the newly constructed place to temp list
                    workingList.add(cp);

                    // Adds the place directly to the list to be returned and calls listCustomer.update with the new list
                    publishProgress(cp);


                    // TODO: Create CustomPlace object with fetched params
                    // TODO: call listCustomer.update here / executed on the UI Thread! /
                }

            } catch (MalformedURLException e) {
                Log.e("JSON", "Malformed URL!!!");
            } catch (IOException e) {
                Log.e("JSON", "IOException!!!");
            } catch (JSONException e) {
                e.printStackTrace();
            }


            Log.e("URL", "doInBackground finished");
            return workingList;
        }

        @Override
        protected void onProgressUpdate(CustomPlace... values) {
            retrievedPlaces.add(values[0]);
            updateCustomer();
        }

        @Override
        protected void onPostExecute(ArrayList<CustomPlace> workingList) {
            Collections.sort(retrievedPlaces, new Comparator<CustomPlace>() {
                @Override
                public int compare(CustomPlace lhs, CustomPlace rhs) {
                    return lhs.getDistance().compareTo(rhs.getDistance());
                }
            });
            updateCustomer();
        }
        //https://maps.googleapis.com/maps/api/distancematrix/json?units=metric&origins=42.6655101,23.89188969999998&destinations=43.6905615,25.9976592&key=AIzaSyDHvkAJQni2X0Qdv6n9JYNEz-l7W6t5-WU
        private void setDistanceAndEta(CustomPlace customPlace){
            String url_eta = BASE_URL_ETA_REQUEST;
            String origins = "&origins="+customPlace.getLatitude()+","+customPlace.getLongitude();
            String destinations = "&destinations="+customerLocation.getLatitude()+","+customerLocation.getLongitude();
            String key = "&key="+ Constants.BROWSER_API_KEY;

            String etaURL = url_eta+origins+destinations+key;

            URL url = null;
            HttpURLConnection connection = null;
            Scanner sc = null;
            StringBuffer sb = null;
            JSONObject root = null;

            try{
                url = new URL(etaURL);
                connection = (HttpURLConnection) url.openConnection();
                sc = new Scanner(connection.getInputStream());
                sb = new StringBuffer();

                while (sc.hasNextLine()){
                    sb.append(sc.nextLine());
                }

                root = new JSONObject(sb.toString());

                JSONObject element = root.getJSONArray("rows").getJSONObject(0).getJSONArray("elements").getJSONObject(0);

                customPlace.setDistance(element.getJSONObject("distance").getString("text"));
                customPlace.setEstimateTime(element.getJSONObject("duration").getString("text").replace(" hours", "h").replace(" mins", "min"));

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public interface PlacesListRequester {
        void update(ArrayList<CustomPlace> places);
    }
}
