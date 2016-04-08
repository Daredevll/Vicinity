package com.vicinity.vicinity.utilities.services;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

import com.vicinity.vicinity.R;
import com.vicinity.vicinity.controller.NotificationActivity;
import com.vicinity.vicinity.utilities.Constants;
import com.vicinity.vicinity.utilities.CustomNotificationElement;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Scanner;

public class ReservationListenerService extends Service {

    private static final int RESERVATION_NOTIFICATION_ID = 102;

    // TODO: Setup the Service so it runs in background even when the app is killed or device rebooted

    public ReservationListenerService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //TODO: I'm trying to receive a Place_ID from the intent, but I'm setting an Account_ID before I start it. Make a query here to get A - All owned places, B - Current owned at location
        String placeIds = intent.getStringExtra(Constants.RESERVATION_LISTENER_EXTRA_PLACE_ID);
        initiate(placeIds);

        return START_REDELIVER_INTENT;
    }


    // TODO: Override equals() in CustomNotificationElement so it doesn't stack
    // TODO: Remove Notifications from list on dismiss/confirm/decline


    private void initiate(final String placeIds){
        new Thread(new Runnable() {
            @Override
            public void run() {
                String respondBody;
                while (true){
                    try {
                        Thread.sleep(10000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    Log.e("Request", "Sending Reservations Getting Request");
                    respondBody = onGetReservationRequest(placeIds);
                    if (respondBody != null){
                        addToNotificationJsonCache(respondBody);
                        createNotification();
                    }
                    else {
                        Log.e("Request", "No Reservations pending for this business");
                    }
                }
            }
        }).start();
    }




    /**
     * Creates and shows a Notification in the Notification Drawer of the User's device
     */
    private void createNotification() {
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.ic_event_note_white_48dp)
                                // TODO: Set the content title to "Reservation Approved/Declined" based on isConfirmed return
                        .setContentTitle("Reservation Received!")
                        .setContentText("A customer made sent a reservation request!")
                        .setAutoCancel(true);
// Creates an explicit intent for an Activity in your app
        Intent resultIntent = new Intent(this, NotificationActivity.class);

        resultIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
// The stack builder object will contain an artificial back stack for the
// started Activity.
// This ensures that navigating backward from the Activity leads out of
// your application to the Home screen.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
// Adds the back stack for the Intent (but not the Intent itself)
        stackBuilder.addParentStack(NotificationActivity.class);
// Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
// mId allows you to update the notification later on.
        mNotificationManager.notify(RESERVATION_NOTIFICATION_ID, mBuilder.build());
    }




    /**
     * Loads the current Unchecked Notifications List from the Cache File and adds the new CustomNotificationElement objects to the List.
     * Then saves the List back to the Cache File.
     * @param respondBody
     */
    private void addToNotificationJsonCache(String respondBody){
        ArrayList<CustomNotificationElement> notifs;

        File f = new File(Environment.getExternalStorageDirectory() + "/vicinityNotifsCacheBusiness");
        if (!f.exists()){
            try {
                f.createNewFile();
                notifs = new ArrayList<CustomNotificationElement>();
                FileOutputStream fos = new FileOutputStream(f);
                ObjectOutputStream oos = new ObjectOutputStream(fos);
                oos.writeObject(notifs);
                fos.close();
                oos.close();
                Log.e("File", "New file created, empty list written to new file.");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try {
            FileInputStream fis = new FileInputStream(f);
            ObjectInputStream ois = new ObjectInputStream(fis);
            notifs = (ArrayList<CustomNotificationElement>) ois.readObject();
            Log.e("Request", "Notifs arl Cache loaded from file");

            ois.close();
            fis.close();


            JSONArray notifsArray = new JSONArray(respondBody);

            String custId;
            String restId;
            int ppl;
            String comment;
            String time;
            String date;
            boolean isAnswer;
            boolean isConfirmed;
            String placeName;

            for (int i = 0; i < notifsArray.length(); i++){
                JSONObject singleNotif = notifsArray.getJSONObject(i);

                custId = singleNotif.getString("customer_id");
                restId = singleNotif.getString("restaurant_id");
                ppl = singleNotif.getInt("people_count");
                comment = singleNotif.getString("comment");
                time = singleNotif.getString("time");
                date = singleNotif.getString("date");
                isAnswer = false;
                placeName = singleNotif.getString("restaurant_name");

                notifs.add(new CustomNotificationElement(custId, restId, ppl, comment, time, date, false, isAnswer, placeName));
            }

            FileOutputStream fos = new FileOutputStream(f);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(notifs);
            Log.e("Request", "Notifs arl cache saved to file");

            fos.close();
            oos.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (StreamCorruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }




    public String onGetReservationRequest(final String placeId){

        URL url = null;
        HttpURLConnection con = null;
        Scanner sc = null;
        StringBuffer sb = null;
        JSONObject response = null;
        CustomNotificationElement notification = null;

        try{
            url = new URL(Constants.GET_RESERVATION_REQUEST_URL.replace("PLACE_ID", placeId));


            con = (HttpURLConnection) url.openConnection();

            Log.e("Request", "Request for new reservations sent from inGetReservationRequest()");

            if (con.getResponseCode() == Constants.STATUS_CODE_NOT_FOUND){
                Log.e("Request", "Status code is 404");
                return null;
            }

            sb = new StringBuffer();
            sc = new Scanner(con.getInputStream());
            while (sc.hasNextLine()){
                sb.append(sc.nextLine());
            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return sb.toString();
    }



}
