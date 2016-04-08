package com.vicinity.secretarysms;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

public class MainActivity extends AppCompatActivity {

    TextView log;
    TextView count;
    TextView monitor;
    Button startButton;

    boolean isStarted;

    public static int counter = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        monitor = (TextView) findViewById(R.id.monitor);

        startButton = (Button) findViewById(R.id.start);

        log = (TextView) findViewById(R.id.text_log);
        count = (TextView) findViewById(R.id.smsSent);

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isStarted){
                    isStarted = !isStarted;
                }
                else {
                    isStarted = !isStarted;
                    new WorkThread().start();
                }
                monitor.setText("Service is: " + (isStarted?"RUNNING...":"STOPPED..."));
            }
        });


    }

    private void checkQueries(){
        URL url = null;
        HttpURLConnection con = null;
        Scanner sc = null;
        StringBuffer sb = null;

        try {
            url = new URL("http://vicinity-vicinity.rhcloud.com/SMSServlet");
            con = (HttpURLConnection) url.openConnection();


            sb = new StringBuffer();
            sc = new Scanner(con.getInputStream());

            while (sc.hasNextLine()){
                sb.append(sc.nextLine());
            }

            if (sb.length() == 0){
                Log.e("SMS", "No new SMS querries.. sleeping");
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

    private void sendSms(final String number, final String code){

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.e("SMS", "Sending sms to " + number + " with code " + code);
                String text = "Hello, your place-activation code is: " + code;

                try {
                    SmsManager sManager = SmsManager.getDefault();
                    sManager.sendTextMessage(number, null, text, null, null);
                }
                catch (Exception e){
                    Log.e("SMS", e.getMessage());
                }
                count.setText(String.valueOf(++counter));
                log.setText(log.getText() + "\nSMS Sent to:    " + number);
            }
        });


    }


    class WorkThread extends Thread{
        @Override
        public void run() {
            while (isStarted){

                Log.e("SMS", "Checking for SMS queries..");
                checkQueries();

                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }




























}
