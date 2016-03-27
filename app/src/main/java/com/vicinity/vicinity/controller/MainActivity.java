package com.vicinity.vicinity.controller;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.vicinity.vicinity.R;
import com.vicinity.vicinity.controller.controllersupport.AppearanceManager;
import com.vicinity.vicinity.controller.fragments.MainFragment.*;
import com.vicinity.vicinity.controller.fragments.MainFragment;
import com.vicinity.vicinity.utilities.DummyModelClass;
import com.vicinity.vicinity.utilities.location.CustomLocationListener.*;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, MainFragmentListener, LocationRequester {


    Location currentLocation;

    DrawerLayout navLayout;
    LinearLayout navDrawer;
    Button signOut;

    CharSequence selectedAddress;

    MainFragment mainFragment;

    TextView drawerHome, drawerExit;

    ArrayList<TextView> drawerElements;
    Fragment containerFragment;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        navLayout = (DrawerLayout) findViewById(R.id.root_layout);
        navDrawer = (LinearLayout) findViewById(R.id.nav_drawer);
        navLayout.setBackgroundResource(AppearanceManager.getBackgroundID());
        drawerElements = new ArrayList<>();
        drawerHome = (TextView) findViewById(R.id.nav_go_home);
        drawerExit = (TextView) findViewById(R.id.nav_exit);
        drawerElements.add(drawerHome);
        drawerElements.add(drawerExit);

        mainFragment = new MainFragment(this);
        FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.main_content_view, mainFragment);
        ft.commit();


        drawerHome.setOnClickListener(this);
        drawerExit.setOnClickListener(this);

        ((TextView) findViewById(R.id.nav_logged_user_name)).setText(DummyModelClass.LoginManager.getInstance().getLoggedUsername(this));
        ((TextView) findViewById(R.id.nav_logged_user_email)).setText(DummyModelClass.LoginManager.getInstance().getLoggedEmail(this));

        signOut = (Button) findViewById(R.id.nav_sign_out);
        signOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DummyModelClass.LoginManager.getInstance().signOutAccount(MainActivity.this);
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
            }
        });


        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();

    }


    /* Overrides the back button press action to forbid the user to go back to the Intro Activity after entering */
    @Override
    public void onBackPressed() {

    }

    private void setDrawerElementActive(TextView view, int colorSelected, int colorDefault) {
        for (TextView tv : drawerElements) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                tv.setTextColor(getColor(colorDefault));
            } else {
                tv.setTextColor(getResources().getColor(colorDefault));
            }
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            view.setTextColor(getColor(colorSelected));
        } else {
            view.setTextColor(getResources().getColor(colorSelected));
        }
    }

    /* Implements the behavior of the Nav drawer buttons */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.nav_exit:
                //TODO: Close app
                break;
            case R.id.nav_go_home:
                //TODO: Set home Fragment
                break;
        }
        setDrawerElementActive((TextView) v, R.color.transparentWhite, R.color.colorWhite);
    }

    @Override
    public void receiveLocation(Location location) {
        this.currentLocation = location;
        mainFragment.setAddress();
        Log.e("Location", "location obtained: " + currentLocation.getLatitude() + ", " + currentLocation.getLongitude() + " accuracy: " + currentLocation.getAccuracy());
    }

    @Override
    public Location getCurrentLocation() {
        return currentLocation;
    }
}
