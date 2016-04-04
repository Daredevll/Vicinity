package com.vicinity.vicinity.controller;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Places;
import com.vicinity.vicinity.R;
import com.vicinity.vicinity.controller.controllersupport.AppearanceManager;
import com.vicinity.vicinity.controller.fragments.DetailsFragment;
import com.vicinity.vicinity.controller.fragments.MainFragment;
import com.vicinity.vicinity.controller.fragments.MainFragment.MainFragmentListener;
import com.vicinity.vicinity.controller.fragments.ResultsFragment;
import com.vicinity.vicinity.utilities.Constants;
import com.vicinity.vicinity.utilities.DummyModelClass;
import com.vicinity.vicinity.utilities.QueryProcessor;
import com.vicinity.vicinity.utilities.QueryProcessor.CustomPlace;
import com.vicinity.vicinity.utilities.location.CustomLocationListener.LocationRequester;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Scanner;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, MainFragmentListener, LocationRequester,
        GoogleApiClient.ConnectionCallbacks, ResultsFragment.ResultsAndDetailsFragmentListener {

    @Override
    public void onConnected(Bundle bundle) {
        Log.e("service", "apiClient connected in MainActivity");
        mainFragment.setAddress();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    Location currentLocation;
    String readableAddress;
    String queryType;

    CustomPlace currentPlaceDetails;

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

        mainFragment = new MainFragment();
        Log.e("Fragment", "main frag inited");
        replaceFragment(mainFragment, true);

        Log.e("Fragment", "replaceFragment() finished");

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

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        client = new GoogleApiClient.Builder(this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .addApi(LocationServices.API)
                .addApi(Places.GEO_DATA_API)
                .addConnectionCallbacks(this)
                .build();

    }

    /**
     * Method to replace the current fragment with a new one, passed to the method
     * Purpose of this method is to isolate the parent View holding the fragment, to avoid mistakes
     * @param newFragment
     */
    private void replaceFragment(Fragment newFragment, boolean addToBackStack){
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.main_content_view, newFragment);
        if (addToBackStack){
            ft.addToBackStack(null);
        }
        ft.commit();
    }


//    /* Overrides the back button press action to forbid the user to go back to the Intro Activity after entering */
//    @Override
//    public void onBackPressed() {
//
//    }

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

    @Override
    public String getQueryType() {
        return this.queryType;
    }

    /**
     * Initiates the DetailsFragment, called when clicked on a certain row from ResultsFragment.
     * Calls getPlaceById from google API to retrieve website, phone and photos and initiates
     * the DetailsFragment when on callBack with Place result.
     */
    @Override
    public void startDetailsFragment(final QueryProcessor.CustomPlace place) {
        new PlaceDetailAsyncFetcher().execute(place);
    }

    @Override
    public GoogleApiClient getGoogleApiClient() {
        return this.client;
    }

    @Override
    public void startResultsFragment(String queryType) {
        this.queryType = queryType;
        ResultsFragment rf = new ResultsFragment();
        replaceFragment(rf, true);
    }

    @Override
    public void setReadableAddress(String address) {
        this.readableAddress = address;
    }

    @Override
    public String getReadableAddress() {
        if (this.readableAddress == null){
            return "";
        }
        return this.readableAddress;
    }


    @Override
    public CustomPlace getCurrentDetailPlace() {
        return this.currentPlaceDetails;
    }


    private class PlaceDetailAsyncFetcher extends AsyncTask<CustomPlace, Void, CustomPlace>{

        @Override
        protected CustomPlace doInBackground(CustomPlace... params) {
            String urlLink = "https://maps.googleapis.com/maps/api/place/details/json?placeid=PLACE_ID&key=BROWSER_KEY";
            CustomPlace cp = params[0];
            // TODO: Fetch the place details by ID

            //PLACE'S DETAILS FIELDS:

            String localPhoneNumber;
            String internationalPhoneNumber;
            String website;
            String utcOffset;

            // REVIEW'S FIELDS:

            double  reviewRating;
            String reviewAuthorName;
            String reviewLanguage;
            String reviewProfilePhotoUrl;
            String reviewComment;
            long   reviewTime;

            ArrayList<CustomPlace.Review> reviewsList = new ArrayList<>();



            URL url = null;
            HttpURLConnection connection = null;
            Scanner sc = null;
            StringBuffer sb = null;
            JSONObject root = null;
            JSONObject resultObject = null;

            try{
                url = new URL(urlLink.replace("PLACE_ID", cp.getPlaceId()).replace("BROWSER_KEY", Constants.BROWSER_API_KEY));
                connection = (HttpURLConnection) url.openConnection();
                sc = new Scanner(connection.getInputStream());
                sb = new StringBuffer();

                while (sc.hasNextLine()){
                    sb.append(sc.nextLine());
                }

                root = new JSONObject(sb.toString());
                resultObject = root.getJSONObject("result");
                Log.e("DETAILSLOG", "Root Json Fetched");

                localPhoneNumber = resultObject.getString("formatted_phone_number");
                internationalPhoneNumber = resultObject.getString("international_phone_number");
                website = resultObject.getString("website");
                utcOffset = resultObject.getString("utc_offset");

                JSONArray reviewsArr = resultObject.getJSONArray("reviews");

                for (int idx = 0; idx < reviewsArr.length(); idx++){
                    JSONObject singleRev = reviewsArr.getJSONObject(idx);

                    reviewAuthorName = singleRev.getString("author_name");
                    reviewLanguage = singleRev.getString("language");
                    reviewProfilePhotoUrl = singleRev.getString("profile_photo_url");
                    reviewRating = singleRev.getDouble("rating");
                    reviewComment = singleRev.getString("text");
                    reviewTime = singleRev.getLong("time");

                    CustomPlace.Review r = cp.new Review(reviewAuthorName, reviewRating, reviewTime);

                    r.setLanguage(reviewLanguage);
                    r.setComment(reviewComment);
                    r.setProfilePhotoUrl(reviewProfilePhotoUrl);
                    reviewsList.add(r);
                }


                cp.addDetails(localPhoneNumber, internationalPhoneNumber, website, utcOffset, reviewsList);
                Log.e("DETAILSLOG", "cp updated successfully");


            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return cp;
        }

        @Override
        protected void onPostExecute(CustomPlace inputPlace) {
            currentPlaceDetails = inputPlace;
            replaceFragment(new DetailsFragment(), true);
        }
    }
}
