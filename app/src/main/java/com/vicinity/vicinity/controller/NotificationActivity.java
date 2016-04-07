package com.vicinity.vicinity.controller;

import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.vicinity.vicinity.R;
import com.vicinity.vicinity.controller.controllersupport.recycler.NotificationsRecyclerAdapter;
import com.vicinity.vicinity.utilities.CustomNotificationElement;
import com.vicinity.vicinity.utilities.DummyModelClass;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.StreamCorruptedException;
import java.util.ArrayList;

public class NotificationActivity extends AppCompatActivity implements NotificationsRecyclerAdapter.NotificationsListActivity {

    RecyclerView recyclerView;
    NotificationsRecyclerAdapter adapter;
    Button dismissButton;
    TextView listEmpty;
    ArrayList<CustomNotificationElement> notificationsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        recyclerView = (RecyclerView) findViewById(R.id.notifications_recycler_view);
        listEmpty = (TextView) findViewById(R.id.notification_activity_no_new);
        dismissButton = (Button) findViewById(R.id.notifications_dismiss_activity_button);
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
        File f;

        if (DummyModelClass.LoginManager.getInstance().isLoggedUserTypeBusiness(this)){
            f = new File(Environment.getExternalStorageDirectory() + Environment.DIRECTORY_NOTIFICATIONS + "vicinityNotifsCacheBusiness");

        }
        else {
            f = new File(Environment.getExternalStorageDirectory() + Environment.DIRECTORY_NOTIFICATIONS + "vicinityNotifsCache");
        }


        if (!f.exists()){
            return list;
        }

        try {
            FileInputStream fis = new FileInputStream(f);
            ObjectInputStream ois = new ObjectInputStream(fis);
            list = (ArrayList<CustomNotificationElement>) ois.readObject();

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

    }
}
