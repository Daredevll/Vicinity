package com.vicinity.secretarysms;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true){

                    checkQueries();

                    try {
                        Thread.sleep(30000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();





    }

    private void checkQueries(){
        URL url = null;
        HttpURLConnection con = null;
        Scanner sc = null;
        StringBuffer sb = null;

        try {
            url = new URL("http://vicinity-vicinity.rhcloud.com/Sms");
            con = (HttpURLConnection) url.openConnection();

            sc = new Scanner(con.getInputStream());
            sb = new StringBuffer();
            while (sc.hasNextLine()){
                sb.append(sc.nextLine());
            }

            if (sb.length() == 0){
                return;
            }

            JSONObject smsQ = new JSONObject(sb.toString());

            String phoneNumber = smsQ.getString("place_phone_loc");
            String code = smsQ.getString("generated_code");


            sendSms(phoneNumber, code);



        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void sendSms(String number, String code){

        String text = "Hello, your place-activation code is: " + code;

        // TODO: Send dat damn sms...

    }
































}
