package com.vicinity.vicinity.controller;

import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.vicinity.vicinity.R;
import com.vicinity.vicinity.controller.controllersupport.recycler.NotificationsRecyclerAdapter;
import com.vicinity.vicinity.controller.fragments.ReservationAnswerDialog;
import com.vicinity.vicinity.utilities.Constants;
import com.vicinity.vicinity.utilities.CustomNotificationElement;
import com.vicinity.vicinity.utilities.interfaces.Blurable;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class NotificationActivity extends AppCompatActivity implements NotificationsRecyclerAdapter.NotificationsListActivity, Blurable, View.OnClickListener {

    RecyclerView recyclerView;
    NotificationsRecyclerAdapter adapter;

    Button exitButton, homeButton;
    Button latestToggleButton, biggestToggleButton, soonestToggleButton;

    TextView listEmpty;
    ArrayList<CustomNotificationElement> notificationsList;
    ArrayList<Button> toggles;
    File f;
    ViewGroup rootView;
    boolean businessNotification;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        rootView = (ViewGroup) findViewById(R.id.notification_root_view);

        exitButton = (Button) findViewById(R.id.notifications_activity_exit_button);
        homeButton = (Button) findViewById(R.id.notifications_activity_home_button);
        latestToggleButton = (Button) findViewById(R.id.notifications_activity_sort_latest);
        biggestToggleButton = (Button) findViewById(R.id.notifications_activity_sort_biggest);
        soonestToggleButton = (Button) findViewById(R.id.notifications_activity_sort_soonest);

        toggles = new ArrayList<>();
        toggles.add(latestToggleButton);
        toggles.add(biggestToggleButton);
        toggles.add(soonestToggleButton);

        businessNotification = getIntent().getBooleanExtra(Constants.NOTIFICATION_INTENT_BUSINESS_TYPE_EXTRA, false);
        Log.e("DEBUG", "The notification activity recieved the intent, the availaiblity of BOOL EXTRA IS: " + getIntent().hasExtra(Constants.NOTIFICATION_INTENT_BUSINESS_TYPE_EXTRA));
        Log.e("DEBUG", "==========      The value of getIntent().getBooleanExtra() itself is: " + businessNotification);

        latestToggleButton.setOnClickListener(this);
        biggestToggleButton.setOnClickListener(this);
        soonestToggleButton.setOnClickListener(this);

        recyclerView = (RecyclerView) findViewById(R.id.notifications_recycler_view);
        listEmpty = (TextView) findViewById(R.id.notification_activity_no_new);
        notificationsList = loadCachedNotifications();

        adapter = new NotificationsRecyclerAdapter(this, notificationsList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);



        setVisibilities();
    }

    private void setVisibilities(){
        if (notificationsList.size() == 0){
            recyclerView.setVisibility(View.INVISIBLE);
            listEmpty.setVisibility(View.VISIBLE);
        }
        else {
            recyclerView.setVisibility(View.VISIBLE);
            listEmpty.setVisibility(View.INVISIBLE);
        }
    }










    private ArrayList<CustomNotificationElement> loadCachedNotifications(){

        ArrayList<CustomNotificationElement> list = new ArrayList<CustomNotificationElement>();



        if (businessNotification){
            f = new File(Environment.getExternalStorageDirectory() + "/vicinityNotifsCacheBusiness");
            Log.e("NotificationActivity", "business file called");
        }
        else {
            f = new File(Environment.getExternalStorageDirectory() + "/vicinityNotifsCacheClient");
            Log.e("NotificationActivity", "client file called");
        }

        if (!f.exists()){
            Log.e("NotificationActivity", "f.exists() returned false");
            return list;
        }

        try {
            FileInputStream fis = new FileInputStream(f);
            ObjectInputStream ois = new ObjectInputStream(fis);

            list = (ArrayList<CustomNotificationElement>) ois.readObject();

            Log.e("NotificationActivity", "notifs list read from file");

            Log.e("NotificationActivity", "current list elements: " + list.size());

            fis.close();
            ois.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (StreamCorruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return list;
    }












    @Override
    public void triggerAnswer(CustomNotificationElement notificationToBeAnswered) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.addToBackStack(null);

        ReservationAnswerDialog rad = ReservationAnswerDialog.newInstance(notificationToBeAnswered);
        rad.show(ft, "RESERVEDIALOG");

    }



    public void removeNotification(CustomNotificationElement notifToRemove){
        notificationsList.remove(notifToRemove);
        adapter.notifyDataSetChanged();
        saveToFile(notificationsList);
    }



    private void saveToFile(final ArrayList<CustomNotificationElement> listToSave){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    FileOutputStream fos = new FileOutputStream(f);
                    ObjectOutputStream oos = new ObjectOutputStream(fos);
                    oos.writeObject(listToSave);
                    fos.close();
                    oos.close();

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }



    @Override
    public ViewGroup getRootView() {
        return rootView;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.notifications_activity_sort_latest:
                Collections.sort(notificationsList, new Comparator<CustomNotificationElement>() {
                    @Override
                    public int compare(CustomNotificationElement lhs, CustomNotificationElement rhs) {
                        return 0;
                    }
                });
                break;
            case R.id.notifications_activity_sort_biggest:
                Collections.sort(notificationsList, new Comparator<CustomNotificationElement>() {
                    @Override
                    public int compare(CustomNotificationElement lhs, CustomNotificationElement rhs) {
                        return lhs.getPeopleCount() - rhs.getPeopleCount();
                    }
                });
                break;
            case R.id.notifications_activity_sort_soonest:
                Collections.sort(notificationsList, new Comparator<CustomNotificationElement>() {
                    @Override
                    public int compare(CustomNotificationElement lhs, CustomNotificationElement rhs) {
                        if (lhs.getDate().compareTo(rhs.getDate()) == 0){
                            return lhs.getTime().compareTo(rhs.getTime());
                        }
                        return lhs.getDate().compareTo(rhs.getDate());
                    }
                });
                break;
        }
        toggleLights((Button) v);
    }

    private void toggleLights(Button b){
        for (Button but: toggles){
            but.setTextColor(0xaa777777);
        }
        b.setTextColor(0xffffffff);
    }
}
