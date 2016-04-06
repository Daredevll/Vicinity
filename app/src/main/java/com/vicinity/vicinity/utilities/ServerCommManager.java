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
















    public void onLoginUser(final RequestSenderContext context, final String userId){
        new AsyncTask<String, Void, JSONObject>(){

            @Override
            protected JSONObject doInBackground(String... params) {
                String jsonString;
                URL url = null;
                HttpURLConnection con = null;
                Scanner sc = null;
                StringBuffer sb = null;
                JSONObject response = null;

                try{
                    jsonString = new JSONObject().put("user_id_goog", userId).toString();
                    url = new URL(Constants.GET_LOGIN_REQUEST_URL);
                    con = (HttpURLConnection) url.openConnection();
                    con.getOutputStream().write(jsonString.getBytes());

                    con.connect();

                    sc = new Scanner(con.getInputStream());
                    sb = new StringBuffer();
                    while (sc.hasNextLine()){
                        sb.append(sc.nextLine());
                    }
                    Log.e("Request", "Request sent successfully");

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
                context.handleLoginResponse(responseJson);
            }
        }.execute(userId);
    }

    public interface RequestSenderContext{
        void handleLoginResponse(JSONObject response);
        void receiveReservationRequest(JSONObject reservation);
        void receiveReservationAnswer(JSONObject answer);
    }

}
