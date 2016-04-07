package com.vicinity.vicinity.utilities;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
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




    public void onSendGeneratedCode(final String accId, final String code){
        new AsyncTask<Void, Void, Void>(){

            @Override
            protected Void doInBackground(Void... params) {
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

                    con.setRequestMethod("POST");
                    con.setDoInput(true);
                    con.setDoOutput(true);
                    con.setChunkedStreamingMode(0);

                    con.getOutputStream().write(jsonString.getBytes());

                    con.connect();


                    Log.e("Request", "Sending Generated Code From User successfully");

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
     * Makes a POST request with Json holding params to apply for new business place registration.
     * @param accId
     * @param placeId
     * @param placeLocalPhone
     */
    public void onPostRegisterBusiness(final String accId, final String placeId, final String placeLocalPhone){
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

                    jsonString = outgoing.toString();


                    url = new URL(Constants.POST_REGISTER_NEW_BUSINESS_URL);
                    con = (HttpURLConnection) url.openConnection();

                    con.setRequestMethod("POST");
                    con.setDoInput(true);
                    con.setDoOutput(true);
                    con.setChunkedStreamingMode(0);

                    con.getOutputStream().write(jsonString.getBytes());

                    con.connect();
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
    public void onPostReservationAnswer(final RequestSenderContext context, final String customerId, final String placeId, final String comment, final boolean confirmed) {
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
                    outgoing.put("comment", comment);
                    outgoing.put("isConfirmed", confirmed);

                    jsonString = outgoing.toString();


                    url = new URL(Constants.POST_RESERVATION_ANSWER_URL);
                    con = (HttpURLConnection) url.openConnection();

                    con.setRequestMethod("POST");
                    con.setDoInput(true);
                    con.setDoOutput(true);
                    con.setChunkedStreamingMode(0);

                    con.getOutputStream().write(jsonString.getBytes());

                    con.connect();

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

                    con.setRequestMethod("POST");
                    con.setDoInput(true);
                    con.setDoOutput(true);
                    con.setChunkedStreamingMode(0);

                    con.getOutputStream().write(jsonString.getBytes());

                    con.connect();

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
                    url = new URL(Constants.GET_LOGIN_REQUEST_URL.replace("USER_ID", userId));
                    con = (HttpURLConnection) url.openConnection();
                    
                    if (con.getResponseCode() != Constants.STATUS_CODE_SUCCESS){
                        return null;
                    }

                    sc = new Scanner(con.getInputStream());
                    sb = new StringBuffer();
                    while (sc.hasNextLine()){
                        sb.append(sc.nextLine());
                    }
                    Log.e("Request", "Request for login check sent successfully, response code is: " + con.getResponseCode());

                    response = new JSONObject(sb.toString());


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
                        context.handleLoginResponse(Constants.STATUS_CODE_NOT_FOUND, false);
                    }
                    else {
                        context.handleLoginResponse(Constants.STATUS_CODE_SUCCESS, responseJson.getBoolean("is_business"));
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
        void toastMsg(String msg);
        void handleLoginResponse(int statusCode, boolean isBusiness);
        // TODO: The next two methods should be implemented by the service that listens for messages on the server and maybe should be replaced by broadcastReceivers
        void receiveReservationRequest(JSONObject reservation);
        void receiveReservationAnswer(JSONObject answer);
    }

}
