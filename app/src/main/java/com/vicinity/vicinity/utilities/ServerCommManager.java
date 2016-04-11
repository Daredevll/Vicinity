package com.vicinity.vicinity.utilities;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.vicinity.vicinity.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Created by Jovch on 06-Apr-16.
 */
public class ServerCommManager {

    private static ServerCommManager instance;

    public static ServerCommManager getInstance(){
        if (instance == null){
            instance = new ServerCommManager();
        }
        return instance;
    }

    private ServerCommManager(){

    }




    public void onSendGeneratedCode(final Context context, final String accId, final String code){
        new AsyncTask<Void, Void, Boolean>(){

            @Override
            protected Boolean doInBackground(Void... params) {
                String jsonString;
                URL url = null;
                HttpURLConnection con = null;

                try{
                    JSONObject outgoing = new JSONObject();
                    outgoing.put("generated_code", code);
                    outgoing.put("user_id", accId);

                    jsonString = outgoing.toString();


                    url = new URL(Constants.POST_CONFIRMATION_CODE_URL);
                    con = (HttpURLConnection) url.openConnection();

                    con.setDoOutput(true);
                    con.getOutputStream().write(jsonString.getBytes());
                    con.getInputStream();

                    if (con.getResponseCode() != Constants.STATUS_CODE_SUCCESS){
                        return false;
                    }

                    Log.e("Request", "Sending Generated Code From User successfully");

                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return true;
            }

            @Override
            protected void onPostExecute(Boolean verifySuccess) {
                if (verifySuccess){
                    Toast.makeText(context, R.string.successful_verification, Toast.LENGTH_SHORT).show();
                    DummyModelClass.LoginManager.getInstance().signOutAccount(context);
                    Toast.makeText(context, R.string.your_app_will_close, Toast.LENGTH_SHORT).show();
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                Thread.sleep(5000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            System.exit(0);
                        }
                    }).start();
                }
                else {
                    Toast.makeText(context, R.string.invalid_code, Toast.LENGTH_SHORT).show();
                }
            }

        }.execute();
    }




    /**
     * Makes a POST request with Json holding params to apply for new business place registration.
     * @param accId
     * @param placeId
     * @param placeLocalPhone
     */
    public void onPostRegisterBusiness(final String accId, final String placeId, final String placeLocalPhone, final String placeName, final String placeAddress){
        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... params) {
                String jsonString;
                URL url = null;
                HttpURLConnection con = null;
                try {
                    JSONObject outgoing = new JSONObject();
                    outgoing.put("user_id_goog", accId);
                    outgoing.put("place_id_goog", placeId);
                    outgoing.put("place_phone_loc", placeLocalPhone);
                    outgoing.put("place_name", placeName);
                    outgoing.put("place_address", placeAddress);

                    jsonString = outgoing.toString();

                    Log.e("DEBUG", "Json for registering business: " + jsonString);

                    url = new URL(Constants.POST_REGISTER_NEW_BUSINESS_URL);
                    con = (HttpURLConnection) url.openConnection();

                    con.setDoOutput(true);
                    con.setChunkedStreamingMode(0);

                    con.getOutputStream().write(jsonString.getBytes());

                    con.getInputStream();
                    Log.e("Request", "Request for registering new business sent successfully");

                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }
        }.execute();
    }



    /**
     * Makes a POST request with Json holding all params necessary for an Answer to a Reservation.
     * This method is invoked only when the Business replies to a notification message with a
     * new Reservation Request from a Customer.
     */
    public void onPostReservationAnswer(final String customerId, final String placeId, final int peopleCount, final String comment,
                                        final String time, final String date, final boolean confirmed, final String placeName) {

        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... params) {
                String jsonString;
                URL url = null;
                HttpURLConnection con = null;

                try {

                    JSONObject outgoing = new JSONObject();
                    outgoing.put("customer_id", customerId);
                    outgoing.put("restaurant_id", placeId);
                    outgoing.put("people_count", peopleCount);
                    outgoing.put("comment", comment);
                    outgoing.put("time", time);
                    outgoing.put("date", date);
                    outgoing.put("isConfirmed", confirmed);
                    outgoing.put("restaurant_name", placeName);

                    jsonString = outgoing.toString();


                    url = new URL(Constants.POST_RESERVATION_ANSWER_URL);
                    con = (HttpURLConnection) url.openConnection();

                    con.setDoOutput(true);
                    con.setChunkedStreamingMode(0);

                    con.getOutputStream().write(jsonString.getBytes());

                    Log.e("Request", "CODE ON ANSWER POST: " + con.getResponseCode());
                    con.getInputStream();

                    Log.e("Request", "Answer to reservation sent successfully");

                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                // TODO: Replace hard-coded msg with status message received as a response from the server!!!
            }

        }.execute();
    }





    /**
     * Makes a POST request with Json holding all params necessary for a new Reservation Request
     * @param userId
     * @param placeId
     * @param peopleCount
     * @param comment
     * @param date
     * @param time
     */
    public void onPostReservationRequest(final String userId, final String placeId, final String placeName, final int peopleCount, final String comment, final String date, final String time){
        new AsyncTask<Void, Void, Void>(){

            @Override
            protected Void doInBackground(Void... params) {
                String jsonString;
                URL url = null;
                HttpURLConnection con = null;

                try{
                    JSONObject outgoing = new JSONObject();
                    outgoing.put("customer_id", userId);
                    outgoing.put("restaurant_id", placeId);
                    outgoing.put("people_count", peopleCount);
                    outgoing.put("comment", comment);
                    outgoing.put("date", date);
                    outgoing.put("time", time);
                    outgoing.put("restaurant_name", placeName);

                    jsonString = outgoing.toString();


                    url = new URL(Constants.POST_RESERVATION_REQUEST_URL);
                    con = (HttpURLConnection) url.openConnection();

                    con.setDoOutput(true);
                    con.setChunkedStreamingMode(0);

                    con.getOutputStream().write(jsonString.getBytes());

                    con.getInputStream();


                    Log.e("Request", "Request for new reservation sent successfully");

                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                // TODO: Replace hard-coded msg with status message received as a response from the server!!!
            }

        }.execute();
    }





    /**
     * Makes a GET request with the current GoogleAcc ID to check if it's already in the app DB, waits for a response and passes its statusCode
     * @param context
     * @param userId
     */
    public void onLoginUser(final RequestSenderContext context, final String userId){
        new AsyncTask<String, Void, JSONObject>(){

            @Override
            protected JSONObject doInBackground(String... params) {
                URL url = null;
                HttpURLConnection con = null;
                Scanner sc = null;
                StringBuffer sb = null;
                JSONObject response = null;

                try{
                    Log.e("DEBUG", "onLoginUser entering first try{} in doInBackground: ");
                    url = new URL(Constants.GET_LOGIN_REQUEST_URL.replace("USER_ID", userId));
                    con = (HttpURLConnection) url.openConnection();

                    Log.e("DEBUG", "onLoginUser response code: " + String.valueOf(con.getResponseCode()));
                    if (con.getResponseCode() != Constants.STATUS_CODE_SUCCESS){
                        Log.e("DEBUG", "onLoginUser response code: " + String.valueOf(con.getResponseCode()));
                        return null;
                    }

                    sc = new Scanner(con.getInputStream());
                    sb = new StringBuffer();
                    while (sc.hasNextLine()){
                        sb.append(sc.nextLine());
                    }
                    Log.e("Request", "Request for login check sent successfully, response code is: " + con.getResponseCode());

                    response = new JSONObject(sb.toString());
                    Log.e("Response", "LoginResponse Json: " + sb.toString());


                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();

                }

                return response;
            }

            @Override
            protected void onPostExecute(JSONObject responseJson) {
                try {
                    if (responseJson == null){
                        Log.e("DEBUG", "onLoginUser server respones = null");
                        context.handleLoginResponse(Constants.STATUS_CODE_NOT_FOUND, false, null);
                    }
                    else {
                        if (responseJson.getBoolean("is_business")){
                            ArrayList<ShortPlace> placesOwned = new ArrayList<ShortPlace>();
                            JSONArray placesArr = responseJson.getJSONArray("places_owned");
                            for (int x = 0; x < placesArr.length(); x++){
                                String id = placesArr.getJSONObject(x).getString("place_id");
                                String name = placesArr.getJSONObject(x).getString("place_name");
                                String addr = placesArr.getJSONObject(x).getString("place_address");

                                placesOwned.add(new ShortPlace(id, name, addr));
                            }
                            context.handleLoginResponse(Constants.STATUS_CODE_SUCCESS, true, placesOwned);
                        }
                        else {
                            context.handleLoginResponse(Constants.STATUS_CODE_SUCCESS, false, null);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }.execute(userId);
    }





    /**
     * An interface providing CallBack availability. All the classes/fragments using calling this Class or waiting for a result returned by this Class
     * should implement the interface to be able to receive the result.
     *
     */
    public interface RequestSenderContext{
        void handleLoginResponse(int statusCode, boolean isBusiness, ArrayList<ShortPlace> ownedPlaces);
    }

}
