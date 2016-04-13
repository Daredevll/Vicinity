package com.vicinity.vicinity.utilities.commmanagers;

import android.location.Location;
import android.os.AsyncTask;
import android.util.Log;

import com.vicinity.vicinity.utilities.Constants;
import com.vicinity.vicinity.utilities.CustomPlace;

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
import java.util.Random;
import java.util.Scanner;

/**
 * Created by Jovch on 28-Mar-16.
 *
 * A class to process the raw queries and returning an ArrayList<Object> generated with the query result
 * All the classes/activities/fragments which make queries to this class must implement <:@link>PlacesListRequester</:@link> interface
 * because the methods in this class work in a <code>Callback</code> manner
 */
public class QueryProcessingManager {

    private static final String BASE_URL_ETA_REQUEST = "https://maps.googleapis.com/maps/api/distancematrix/json?units=metric";


    private static final String BASE_URL = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?";
    private static final String MAPS_SERVICES_SERVER_KEY = "AIzaSyDHvkAJQni2X0Qdv6n9JYNEz-l7W6t5-WU";
    private static final String BASE_URL_NAME_SEARCH = "https://maps.googleapis.com/maps/api/place/textsearch/json?query=";



    // https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=-33.8670522,151.1957362&radius=500&type=hotel&key=AIzaSyDHvkAJQni2X0Qdv6n9JYNEz-l7W6t5-WU

    private Random dummyRandom = new Random();

    PlacesListRequester listCustomer;
    private Location customerLocation;
    private boolean searchingByName;
    private String placeName;
    ArrayList<CustomPlace> retrievedPlaces;

    // TODO: make a request with the given Location and Type criteria
    // TODO: Fetch the response into a JSONObject
    // TODO: Fill the list with objects, generated while decoding the JSON, optionally call listCustomer.update(retrievedPlaces) with every iteration for a smoother effect, but no sorting

    private static QueryProcessingManager instance;
    private String placeType;
    private boolean sortPopular;

    public static QueryProcessingManager getInstance(){
        if (instance == null){
            instance = new QueryProcessingManager();
        }
        return instance;
    }

    public void fillResultsList(PlacesListRequester customer, Location location, String type, String searchedPlaceName, boolean isSortPopular, boolean searchingByName){
        this.searchingByName = searchingByName;
        this.listCustomer = customer;
        this.customerLocation = location;
        this.placeType = type;
        retrievedPlaces = new ArrayList<CustomPlace>();
        this.placeName = searchedPlaceName;
        this.sortPopular = isSortPopular;


        String fullURI;
        String locationString = "&location=" + location.getLatitude() + "," + location.getLongitude();
        String rankBy = "&rankby=distance";
        String radius = "&radius=3000";
        String placeType = "&type=" + type;
        String nameOfPlace = ("&name=" + placeName).replace(" ", "%20");
        String key = "&key=" + Constants.getBrowserApiKey();

        if (searchingByName && type.isEmpty()){
            //https://maps.googleapis.com/maps/api/place/textsearch/json?query=&location=42.6650237,23.2701468&radius=3000&key=AIzaSyD2Y3r7nmfqonC60cF5rdaUh-ZCZhUcSRY
            fullURI = BASE_URL_NAME_SEARCH + placeName + locationString + radius + key;
            Log.e("DEBUG", "=====================  (searchingByName && type.isEmpty())  ==============================");
            Log.e("DEBUG", " FULL URL LOOKS LIKE: " + fullURI);
        }
        else if (searchingByName && !type.isEmpty()){
            //https://maps.googleapis.com/maps/api/place/nearbysearch/json?&location=42.6650282,23.2701288&radius=3000&type=restaurant&name=Happy&key=AIzaSyD2Y3r7nmfqonC60cF5rdaUh-ZCZhUcSRY
            fullURI = BASE_URL + locationString + radius + placeType + nameOfPlace + key;
            Log.e("DEBUG", "=====================  (searchingByName && !type.isEmpty())  ==============================");
            Log.e("DEBUG", " FULL URL LOOKS LIKE: " + fullURI);
        }
        else {
            fullURI = BASE_URL + locationString + radius + placeType + key;
            Log.e("DEBUG", "=====================  NOT SEARCHING BY NAME  ==============================");
            Log.e("DEBUG", " FULL URL LOOKS LIKE: " + fullURI);
        }

        // SETS THE SEARCH BASED OT USER PREFERENCES /RELEVANCE OR DISTANCE
        if (!isSortPopular && !type.isEmpty()) {
            fullURI = fullURI.replace(radius, rankBy);
        }

        Log.e("URL", "sending request as: " + fullURI); // fullURI is OK!
        new AsyncResultsFiller().execute(fullURI);

    }

    void updateCustomer(){
        Log.e("URL", "update listCustomer called");
        listCustomer.update(retrievedPlaces);
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
                Log.e("URL", "STATUS CODE:"  + String.valueOf(connection.getResponseCode()));

                if (root.getString("status").equals("OVER_QUERY_LIMIT")){
                    Log.e("DEBUG", "==========     Status code != OK, switching keys...     ==========");
                    Constants.switchKeys();
                    fillResultsList(listCustomer, customerLocation, placeType, placeName, sortPopular, searchingByName);
                    return null;
                }




                resultsArray = root.getJSONArray("results");

                Log.e("JSON", "" + resultsArray.length());



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

                    if (singleResult.has("vicinity")) {
                        vicinity = singleResult.getString("vicinity");
                    } else if (singleResult.has("formatted_address")){
                        vicinity = singleResult.getString("formatted_address");
                    } else {
                        vicinity = "";
                    }

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

                    Log.e("pp", "reading results done");
                    // TODO: Create CustomPlace object with fetched params
                    // TODO: call listCustomer.update here / executed on the UI Thread! /
                }

            } catch (MalformedURLException e) {
                Log.e("JSON", "Malformed URL!!!");
                Log.e("pp", e.getMessage());
            } catch (IOException e) {
                Log.e("JSON", "IOException!!!");
                Log.e("pp", e.getMessage());
            } catch (JSONException e) {
                e.printStackTrace();
                Log.e("pp", e.getMessage());
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
            if (workingList == null){
                return;
            }
            Collections.sort(retrievedPlaces, new Comparator<CustomPlace>() {
                @Override
                public int compare(CustomPlace lhs, CustomPlace rhs) {
                    return lhs.getDistance().compareTo(rhs.getDistance());
                }
            });
            updateCustomer();
        }

        //https://maps.googleapis.com/maps/api/distancematrix/json?units=metric&origins=42.6655101,23.89188969999998&destinations=43.6905615,25.9976592&key=AIzaSyDHvkAJQni2X0Qdv6n9JYNEz-l7W6t5-WU
        // TODO: Figure out a Workaround to lower the queries to ETA API /current limit 2500/day/ Maybe Cache em somehow
        private void setDistanceAndEta(CustomPlace customPlace){
            String url_eta = BASE_URL_ETA_REQUEST;
            String origins = "&origins="+customPlace.getLatitude()+","+customPlace.getLongitude();
            String destinations = "&destinations="+customerLocation.getLatitude()+","+customerLocation.getLongitude();
            String key = "&key="+ Constants.getBrowserApiKey();

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

                Log.e("DISTANCE", root.toString());

                JSONObject element = root.getJSONArray("rows").getJSONObject(0).getJSONArray("elements").getJSONObject(0);

                /*
                    Real data being fetched by the next two rows... Comment when testing to prevent Google Maps Distance Matrix API Queries leak!!!
                 */
                customPlace.setDistance(element.getJSONObject("distance").getString("text"));
                customPlace.setEstimateTime(element.getJSONObject("duration").getString("text").replace(" hours", "h").replace(" mins", "min"));

                /*
                    Dummy data filled when Distance Matrix API is unavailable due to Quota limit reached. For this to work properly and avoid
                    Quota usage but dummy data presented, when using this dummies, you must cut out the above code for calling the API from URL and HttpURLConnection.
                 */
//                customPlace.setDistance(String.valueOf(((double) dummyRandom.nextInt(29)+1)/10) + "km");
//                customPlace.setEstimateTime(String.valueOf(dummyRandom.nextInt(58)+1) + "min");

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                Constants.switchKeys();
                Log.e("DEBUG", "ETA GOT SCREWED...");
                customPlace.setDistance(String.valueOf(((double) dummyRandom.nextInt(10)+4)/2) + "km");
                customPlace.setEstimateTime(String.valueOf(dummyRandom.nextInt(10) + 40) + "min");
                e.printStackTrace();
            }
        }
    }

    public interface PlacesListRequester {
        void update(ArrayList<CustomPlace> places);
    }
}
