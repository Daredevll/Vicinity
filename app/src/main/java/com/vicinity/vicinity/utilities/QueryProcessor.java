package com.vicinity.vicinity.utilities;

import android.location.Location;
import android.os.AsyncTask;
import android.util.Log;

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
import java.util.Scanner;

/**
 * Created by Jovch on 28-Mar-16.
 *
 * A class to process the raw queries and returning an ArrayList<Object> generated with the query result
 * All the classes/activities/fragments which make queries to this class must implement <:@link>QueryInteractor</:@link> interface
 * because the methods in this class work in a <code>Callback</code> manner
 */
public class QueryProcessor {

    private static final String BASE_URL_ETA_REQUEST = "https://maps.googleapis.com/maps/api/distancematrix/json?units=metric";


    private static final String BASE_URL = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?";
    private static final String MAPS_SERVICES_SERVER_KEY = "AIzaSyDHvkAJQni2X0Qdv6n9JYNEz-l7W6t5-WU";
    // https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=-33.8670522,151.1957362&radius=500&type=hotel&key=AIzaSyDHvkAJQni2X0Qdv6n9JYNEz-l7W6t5-WU


    QueryInteractor customer;
    private Location customerLocation;
    ArrayList<CustomPlace> retrievedPlaces;

    // TODO: make a request with the given Location and Type criteria
    // TODO: Fetch the response into a JSONObject
    // TODO: Fill the list with objects, generated while decoding the JSON, optionally call customer.update(retrievedPlaces) with every iteration for a smoother effect, but no sorting

    private static QueryProcessor instance;

    public static QueryProcessor getInstance(){
        if (instance == null){
            instance = new QueryProcessor();
        }
        return instance;
    }


    public void processQuery(QueryInteractor customer, Location location, String type){
        this.customer = customer;
        this.customerLocation = location;
        retrievedPlaces = new ArrayList<CustomPlace>();
        String fullURI;
        String locationString = "location=" + location.getLatitude() + "," + location.getLongitude();
        String radius = "&radius=3000";
        String placeType = "&type=" + type;
        String key = "&key=" + Constants.BROWSER_API_KEY;

        // TODO: Remove this dummy url after test
        fullURI = BASE_URL+locationString+radius+placeType+key;
//        fullURI = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=-33.8670522,151.1957362&radius=500&type=hotel&key=AIzaSyDHvkAJQni2X0Qdv6n9JYNEz-l7W6t5-WU";


        Log.e("URL", "sending request as: " + fullURI); // fullURI is OK!
        new AsyncJsonFetcher().execute(fullURI);

    }

    void updateCustomer(){
        Log.e("URL", "update customer called");
        customer.update(retrievedPlaces);
    }






    public class CustomPlace{

        double longitude;
        double latitude;

        String icon;
        String id;
        String name;
        String place_id;
        String reference;
        String[] types;
        String vicinity;
        String distance;
        String estimateTime;

        public void setEstimateTime(String estimateTime) {
            this.estimateTime = estimateTime;
        }

        public void setDistance(String distance) {
            this.distance = distance;
        }


        public String getDistance() {
            return distance;
        }


        // Optional:

        float rating;
        boolean openNow;

        public CustomPlace(double longitude, double latitude, String icon, String id, String name, String place_id, String reference, String[] types, String vicinity) {
            this.longitude = longitude;
            this.latitude = latitude;
            this.icon = icon;
            this.id = id;
            this.name = name;
            this.place_id = place_id;
            this.reference = reference;
            this.types = types;
            this.vicinity = vicinity;
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

        public String getPlace_id() {
            return place_id;
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
    }

    class AsyncJsonFetcher extends AsyncTask<String, CustomPlace, ArrayList<CustomPlace>>{

        @Override
        protected ArrayList<CustomPlace> doInBackground(String... params) {


            Log.e("URL", "Async doInBackground started...");
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
                        types[t] = typesArr.getString(t);
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

                    // Adds the place directly to the list to be returned and calls customer.update with the new list
                    publishProgress(cp);


                    // TODO: Create CustomPlace object with fetched params
                    // TODO: call customer.update here / executed on the UI Thread! /
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

    public interface QueryInteractor{
        void update(ArrayList<CustomPlace> places);
    }
}
