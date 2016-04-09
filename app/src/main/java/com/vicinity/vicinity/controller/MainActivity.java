package com.vicinity.vicinity.controller;

import android.content.Intent;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
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
import com.vicinity.vicinity.utilities.ShortPlace;
import com.vicinity.vicinity.utilities.location.CustomLocationListener.LocationRequester;
import com.vicinity.vicinity.utilities.services.AnswerListenerService;
import com.vicinity.vicinity.utilities.services.ReservationListenerService;

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

    String loggedUserId;
    boolean loggedUserTypeBusiness;


    private Boolean popular;

    ArrayList<CustomPlace> currentSearchResults;
    CustomPlace currentPlaceDetails;

    DrawerLayout navLayout;
    RelativeLayout navDrawer;
    Button signOut;

    CharSequence selectedAddress;

    MainFragment mainFragment;

    TextView drawerHome, drawerExit, drawerRegBusiness;
    TextView drawerMyPlaces, drawerVerifyPlace;

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        loggedUserId = DummyModelClass.LoginManager.getInstance().getLoggedUserId(this);
        loggedUserTypeBusiness = DummyModelClass.LoginManager.getInstance().isLoggedUserTypeBusiness(this);
        currentSearchResults = new ArrayList<CustomPlace>();

        Intent serviceIntent;
        if (loggedUserTypeBusiness){
            serviceIntent = new Intent(this, ReservationListenerService.class);
            StringBuffer placesIds = new StringBuffer();
            for (ShortPlace p: DummyModelClass.LoginManager.getInstance().getBusinessUserOwnedPlaces()){
                placesIds.append(p.getId() + ",");
            }
            String ids = placesIds.substring(0, placesIds.length()-1);
            serviceIntent.putExtra(Constants.RESERVATION_LISTENER_EXTRA_PLACE_ID, ids);
            startService(serviceIntent);
        }
        serviceIntent = new Intent(this, AnswerListenerService.class);
        serviceIntent.putExtra(Constants.ANSWER_LISTENER_EXTRA_USER_ID, loggedUserId);

        startService(serviceIntent);

        navLayout = (DrawerLayout) findViewById(R.id.root_layout);
        navDrawer = (RelativeLayout) findViewById(R.id.nav_drawer);
        navLayout.setBackgroundResource(AppearanceManager.getBackgroundID());
        drawerHome = (TextView) findViewById(R.id.nav_go_home);
        drawerExit = (TextView) findViewById(R.id.nav_exit);
        drawerRegBusiness = (TextView) findViewById(R.id.nav_register_business);
        drawerMyPlaces = (TextView) findViewById(R.id.nav_my_places);
        drawerVerifyPlace = (TextView) findViewById(R.id.nav_verify_place);

        if (DummyModelClass.LoginManager.getInstance().isLoggedUserTypeBusiness(this)){
            drawerMyPlaces.setVisibility(View.VISIBLE);
        }
        else {
            drawerMyPlaces.setVisibility(View.VISIBLE);
        }


        DrawerLayout.DrawerListener dListener = new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                if (currentPlaceDetails != null){
                    drawerRegBusiness.setVisibility(View.VISIBLE);
                }
                else {
                    drawerRegBusiness.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onDrawerOpened(View drawerView) {

            }

            @Override
            public void onDrawerClosed(View drawerView) {

            }

            @Override
            public void onDrawerStateChanged(int newState) {

            }
        };

        navLayout.addDrawerListener(dListener);

        mainFragment = new MainFragment();
        Log.e("Fragment", "main frag inited");
        replaceFragment(mainFragment, true);

        Log.e("Fragment", "replaceFragment() finished");

        drawerHome.setOnClickListener(this);
        drawerExit.setOnClickListener(this);
        drawerRegBusiness.setOnClickListener(this);
        drawerVerifyPlace.setOnClickListener(this);
        drawerMyPlaces.setOnClickListener(this);


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
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.main_content_view, newFragment);
        if (true){
            ft.addToBackStack(null);
        }
        ft.commit();
    }


    /* Implements the behavior of the Nav drawer buttons */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.nav_exit:
                //TODO: Close app
                break;
            case R.id.nav_go_home:
                for (int i = getSupportFragmentManager().getBackStackEntryCount(); i > 1; i--){
                    onBackPressed();
                }
                break;
            case R.id.nav_register_business:
                Intent intent = new Intent(getApplicationContext(), RegBusinessActivity.class);
                intent.putExtra(Constants.REGISTER_BUSINESS_ACTIVITY_VERIFY_BOOLEAN_EXTRA, false);
                intent.putExtra("PLACE_ID", currentPlaceDetails.getPlaceId());
                intent.putExtra("ACC_ID", DummyModelClass.LoginManager.getInstance().getLoggedUserId(getApplicationContext()));
                intent.putExtra("PLACE_LOC_PHONE", currentPlaceDetails.getInternationalPhoneNumber());
                intent.putExtra("PLACE_NAME", currentPlaceDetails.getName());
                intent.putExtra("PLACE_ADDRESS", currentPlaceDetails.getVicinity());
                startActivity(intent);
                break;
            case R.id.nav_verify_place:
                Intent verifyIntent = new Intent(getApplicationContext(), RegBusinessActivity.class);
                verifyIntent.putExtra(Constants.REGISTER_BUSINESS_ACTIVITY_VERIFY_BOOLEAN_EXTRA, true);
                verifyIntent.putExtra("ACC_ID", DummyModelClass.LoginManager.getInstance().getLoggedUserId(this));
                startActivity(verifyIntent);
                break;
            case R.id.nav_my_places:
                // TODO: Make something thats yet to be done wtf...
                break;
        }
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

    @Override
    public void setCurrentSearchResults(ArrayList<CustomPlace> currentResults) {
        this.currentSearchResults = currentResults;
    }

    @Override
    public ArrayList<CustomPlace> getCurrentSearchResults() {
        return this.currentSearchResults;
    }

    @Override
    public Boolean isSortPopular() {
        return this.popular;
    }


    public void setSortPopular(Boolean popular) {
        this.popular = popular;
    }

    private class PlaceDetailAsyncFetcher extends AsyncTask<CustomPlace, Void, CustomPlace>{

        @Override
        protected CustomPlace doInBackground(CustomPlace... params) {
            String urlLink = "https://maps.googleapis.com/maps/api/place/details/json?placeid=PLACE_ID&key=BROWSER_KEY";
            CustomPlace cp = params[0];

            //PLACE'S DETAILS FIELDS:

            String localPhoneNumber = null;
            String internationalPhoneNumber = null;
            String website = null;
            String utcOffset;

            // REVIEW'S FIELDS:

            double  reviewRating;
            String reviewAuthorName;
            String reviewLanguage;
            String reviewProfilePhotoUrl = null;
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
                System.out.println(root.toString());

                if (resultObject.has("formatted_phone_number")){
                    localPhoneNumber = resultObject.getString("formatted_phone_number");
                }
                if (resultObject.has("international_phone_number")){
                    internationalPhoneNumber = resultObject.getString("international_phone_number");
                }
                if (resultObject.has("website")){
                    website = resultObject.getString("website");
                }
                else {
                    website = null;
                }

                /*
                    Fetches photos references
                 */
                if (resultObject.has("photos")) {
                    ArrayList<String> photosRefs = new ArrayList<>();
                    JSONArray photos = resultObject.getJSONArray("photos");
                    for (int z = 0; z < photos.length(); z++){
                        JSONObject photo = photos.getJSONObject(z);
                        photosRefs.add(photo.getString("photo_reference"));
                    }
                    cp.addPhotosRefs(photosRefs);
                }


                utcOffset = resultObject.getString("utc_offset");

                JSONArray reviewsArr = resultObject.getJSONArray("reviews");




                /*
                    Fetches the reviews
                 */
                for (int idx = 0; idx < reviewsArr.length(); idx++){
                    JSONObject singleRev = reviewsArr.getJSONObject(idx);

                    reviewAuthorName = singleRev.getString("author_name");
                    reviewLanguage = singleRev.getString("language");
                    if (singleRev.has("profile_photo_url")) {
                        reviewProfilePhotoUrl = singleRev.getString("profile_photo_url");
                    }
                    else {
                        reviewProfilePhotoUrl = null;
                    }
                    reviewRating = singleRev.getDouble("rating");
                    reviewComment = singleRev.getString("text");
                    reviewTime = singleRev.getLong("time");

                    CustomPlace.Review r = cp.new Review(reviewAuthorName, reviewRating, reviewTime);

                    r.setLanguage(reviewLanguage);
                    r.setComment(reviewComment);
                    if (reviewProfilePhotoUrl != null) {
                        r.setProfilePhotoUrl(reviewProfilePhotoUrl);
                    }
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

    /*
        Implements logic to prohibit the "backing" from the main fragment of the main activity and also clears the saved
        searches if went back to main fragment
     */
    @Override
    public void onBackPressed() {

        int count = getSupportFragmentManager().getBackStackEntryCount();

        if (count == 1) {

        } else {
            if (count == 2) {
                currentSearchResults.clear();
            }
            else if (count == 3){
                currentPlaceDetails = null;
                ((DetailsFragment) getSupportFragmentManager().getFragments()).spin = false;
            }
            getSupportFragmentManager().popBackStack();
        }

    }
}
