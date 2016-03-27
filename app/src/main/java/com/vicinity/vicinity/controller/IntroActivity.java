package com.vicinity.vicinity.controller;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.vicinity.vicinity.R;
import com.vicinity.vicinity.utilities.DummyModelClass;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public class IntroActivity extends AppCompatActivity {

    private static final String SERVER_KEY = "AIzaSyDHvkAJQni2X0Qdv6n9JYNEz-l7W6t5-WU";
    Button button, goToLogin;
    JSONObject rootJson;
    View rootView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);

        rootView = findViewById(R.id.intro_root_layout);
        button = (Button) findViewById(R.id.button);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AsyncTask<Void, Void, String>() {
                    @Override
                    protected String doInBackground(Void... params) {

                        URL url = null;
                        HttpURLConnection connection = null;
                        Scanner sc = null;
                        StringBuffer sb = null;
                        try{
                            url = new URL("https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=42.70,23.33&radius=500&type=car%20wash&key=" + SERVER_KEY);
                            connection = (HttpURLConnection) url.openConnection();
                            connection.connect();
                            sc = new Scanner(connection.getInputStream());
                            sb = new StringBuffer();

                            while (sc.hasNextLine()){
                                sb.append(sc.nextLine());
                            }

                            Log.e("Json", sb.toString());
                        }
                        catch (Exception e){
                            e.printStackTrace();
                        }
                        return sb.toString();
                    }

                    @Override
                    protected void onPostExecute(String jsonText) {
                        try {
                            rootJson = new JSONObject(jsonText);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }.execute();
            }
        });

        goToLogin = (Button) findViewById(R.id.goto_login);

        goToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(IntroActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });


        rootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent;
                if (DummyModelClass.LoginManager.getInstance().checkLogged(IntroActivity.this)){
                    intent = new Intent(IntroActivity.this, MainActivity.class);
                }
                else {
                    intent = new Intent(IntroActivity.this, LoginActivity.class);
                }
                startActivity(intent);
            }
        });

    }
}



// TODO: Create USERS structure with Clients and Businesses
// TODO: Create dummy class representing the server with his DB
// TODO: Create queries from the clients to the "Server"
// TODO: Create queries from the Businesses to the "Server"
// TODO: Create the "Server logic" to handle the queries and return JSONS