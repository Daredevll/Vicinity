package com.vicinity.vicinity.controller.fragments;

import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.vicinity.vicinity.R;
import com.vicinity.vicinity.utilities.Constants;
import com.vicinity.vicinity.utilities.location.CustomLocationListener.LocationRequester;
import com.vicinity.vicinity.utilities.services.FetchAddressIntentService;
import com.vicinity.vicinity.utilities.location.LocationDetectionHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Scanner;

public class MainFragment extends Fragment implements View.OnClickListener, GoogleApiClient.ConnectionCallbacks{

    /*
    The activity, in which the current fragment is created
    */
    private MainFragmentListener mListener;
    private GoogleApiClient mGoogleApiClient;

    private AddressResultReceiver mResultReceiver;

    View.OnClickListener sortingListener;

    private AutoCompleteTextView addressField;
    private final ArrayList<String> entries = new ArrayList<>();

    Button sortPopular, sortNearest;

    private ImageView restaurant;
    private ImageView bar;
    private ImageView cafe;
    private ImageView hotel;
    private ImageView casino;
    private ImageView delivery;
    private ImageView gym;
    private ImageView pool;
    private ImageView movies;
    private EditText placeName;


    private String receivedAddress;

    @Override
    public void onAttach(Activity context) {
        Log.e("Fragment", "entering onAttach");
        super.onAttach(context);
        Log.e("Fragment", "super.onAttach finished");
        mListener = (MainFragmentListener) context;
        Log.e("Fragment", "mListener initialized");
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.e("service", "apiClient connected in MainFragment");
        setAddress();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    class AddressResultReceiver extends ResultReceiver{

        public AddressResultReceiver(Handler handler) {
            super(handler);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {
            Log.e("service", "Receiver in Fragment reached");
            if (resultCode == Constants.SUCCESS_RESULT){
                Log.e("service", "ResultCode is success");
                receivedAddress = resultData.getString(Constants.RESULT_DATA_KEY).replace("\n", " ");

                Log.e("service", "received address is" + receivedAddress);
                mListener.setReadableAddress(receivedAddress);
                addressField.setHint(receivedAddress);
            }
        }
    }


    public MainFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View layout = inflater.inflate(R.layout.fragment_main, container, false);


        mResultReceiver = new AddressResultReceiver(new Handler());

        sortingListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.main_fragment_sort_filter_nearest:
                        ((Button) v).setTextColor(0xFFFFFFFF);
                        sortPopular.setTextColor(0x4AFFFFFF);
                        mListener.setSortPopular(false);
                        break;
                    case R.id.main_fragment_sort_filter_popular:
                        ((Button) v).setTextColor(0xFFFFFFFF);
                        sortNearest.setTextColor(0x4AFFFFFF);
                        mListener.setSortPopular(true);
                        break;
                }
            }
        };

        addressField = (AutoCompleteTextView) layout.findViewById(R.id.city_input);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, entries);
        addressField.setAdapter(adapter);

        addressField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                new PlacesTask().execute(s.toString());

            }

            @Override
            public void afterTextChanged(Editable s) {
                Log.e("pp", "after text changed");

            }
        });

        addressField.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selected = entries.get(position);
                Log.e("pp", selected);
                mListener.setCityLocation(null);
                new AsyncTask<String, Void, Location>() {
                    @Override
                    protected Location doInBackground(String... params) {
                        Log.e("pp", "bkg " + params[0]);
                        String place = params[0].replaceAll(" ", "%20");
                        String path = "https://maps.googleapis.com/maps/api/place/textsearch/json?query=" + place + "&key=" + Constants.getBrowserApiKey();
                        URL url = null;
                        HttpURLConnection connection = null;
                        Scanner scanner = null;
                        StringBuilder builder = new StringBuilder();

                        try {
                            url = new URL(path);
                            connection = (HttpURLConnection) url.openConnection();
                            scanner = new Scanner(connection.getInputStream());
                            while (scanner.hasNextLine()) {
                                builder.append(scanner.nextLine());
                            }
                        } catch (MalformedURLException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        try {
                            Log.e("pp", "before JSON creation");
                            Log.e("pp", builder.toString());
                            JSONObject jObject = new JSONObject(builder.toString());
                            Log.e("pp", "JSON created");
                            Log.e("pp", jObject.getString("status"));
                            if (jObject.getString("status").equalsIgnoreCase("OK")) {
                                JSONArray jsonArray = jObject.getJSONArray("results");
                                Log.e("pp", jsonArray.length() + "");
                                if (jsonArray.length() > 0) {
                                    JSONObject city = jsonArray.getJSONObject(0);

                                    Location location = new Location("");
                                    location.setLatitude(city.getJSONObject("geometry").getJSONObject("location").getDouble("lat"));
                                    location.setLongitude(city.getJSONObject("geometry").getJSONObject("location").getDouble("lng"));

                                    Log.e("pp", city.getJSONObject("geometry").getJSONObject("location").getDouble("lat") + "");
                                    Log.e("pp", city.getJSONObject("geometry").getJSONObject("location").getDouble("lng") + "");

                                    return location;
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        return null;
                    }

                    @Override
                    protected void onPostExecute(Location latLng) {
                        super.onPostExecute(latLng);

                        mListener.setCityLocation(latLng);
                    }
                }.execute(selected);
            }
        });

        placeName = (EditText) layout.findViewById(R.id.place_name_input);
        Button goButton = (Button) layout.findViewById(R.id.fragment_main_go_button);

        goButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Validate if place name is inputted
                if (placeName.getText().toString().isEmpty()){
                    Toast.makeText(getActivity(), "If you haven't set a name use the categories below", Toast.LENGTH_SHORT).show();
                    return;
                }
                mListener.setCityName(addressField.getText().toString());
                Log.e("DEBUG", "SETTING CITY NAME AS : " + addressField.getText().toString());
                mListener.startResultsFragment("", placeName.getText().toString(), true);
            }
        });

        sortNearest = (Button) layout.findViewById(R.id.main_fragment_sort_filter_nearest);
        sortPopular = (Button) layout.findViewById(R.id.main_fragment_sort_filter_popular);

        sortPopular.setOnClickListener(sortingListener);
        sortNearest.setOnClickListener(sortingListener);


        // Checks the current sorting setting and loads the same
        if (mListener.isSortPopular() == null || mListener.isSortPopular()){
            sortPopular.callOnClick();
        }
        else {
            sortNearest.callOnClick();
        }

        restaurant = (ImageView) layout.findViewById(R.id.restaurant_image_view);
        bar = (ImageView) layout.findViewById(R.id.bar_image_view);
        cafe = (ImageView) layout.findViewById(R.id.cafe_image_view);
        hotel = (ImageView) layout.findViewById(R.id.hotel_image_view);
        casino = (ImageView) layout.findViewById(R.id.casino_image_view);
        delivery = (ImageView) layout.findViewById(R.id.delivery_image_view);
        gym = (ImageView) layout.findViewById(R.id.fitness_image_view);
        pool = (ImageView) layout.findViewById(R.id.pool_image_view);
        movies = (ImageView) layout.findViewById(R.id.movie_image_view);


        restaurant.setOnClickListener(this);
        bar.setOnClickListener(this);
        cafe.setOnClickListener(this);
        hotel.setOnClickListener(this);
        casino.setOnClickListener(this);
        delivery.setOnClickListener(this);
        gym.setOnClickListener(this);
        pool.setOnClickListener(this);
        movies.setOnClickListener(this);

        return layout;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        LocationDetectionHelper.getInstance((LocationRequester) mListener).getLocation((LocationRequester) mListener);

        mGoogleApiClient = mListener.getGoogleApiClient();

        if (mListener.getReadableAddress() != null && !mListener.getReadableAddress().isEmpty()){
            addressField.setHint(mListener.getReadableAddress());
        }
        else {
            addressField.setHint(getString(R.string.detecting_location));
        }

    }

    /**
     * Handles the Place Type clicks and initiates the parent activity to load
     * a ResultsFragment presenting a CardView with the results of the search based
     * on the location and type selected by the user
     * @param v the clicked View
     */
    @Override
    public void onClick(View v) {
        if (mListener.getDetectedLocation() == null && addressField.getText().toString().isEmpty()){
            Toast.makeText(getActivity(), R.string.location_not_detected_yet, Toast.LENGTH_SHORT).show();
            return;
        }

        String name = placeName.getText().toString();
        boolean searchByName = !name.isEmpty();
        mListener.setCityName(addressField.getText().toString());
        Log.e("DEBUG", "SETTING CITY NAME AS : " + addressField.getText().toString());
        switch (v.getId()){
            case R.id.restaurant_image_view:
                mListener.startResultsFragment(Constants.TYPE_RESTAURANT, name, searchByName);
                break;
            case R.id.bar_image_view:
                mListener.startResultsFragment(Constants.TYPE_BAR, name, searchByName);
                break;
            case R.id.cafe_image_view:
                mListener.startResultsFragment(Constants.TYPE_CAFE, name, searchByName);
                break;
            case R.id.hotel_image_view:
                mListener.startResultsFragment(Constants.TYPE_HOTEL, name, searchByName);
                break;
            case R.id.casino_image_view:
                mListener.startResultsFragment(Constants.TYPE_CASINO, name, searchByName);
                break;
            case R.id.delivery_image_view:
                mListener.startResultsFragment(Constants.TYPE_DELIVERY, name, searchByName);
                break;
            case R.id.fitness_image_view:
                mListener.startResultsFragment(Constants.TYPE_FITNESS, name, searchByName);
                break;
            case R.id.pool_image_view:
                mListener.startResultsFragment(Constants.TYPE_POOL, name, searchByName);
                break;
            case R.id.movie_image_view:
                mListener.startResultsFragment(Constants.TYPE_MOVIE, name, searchByName);
                break;
        }
    }

    /**
     * Gets the location from the parent activity and starts IntentService to
     * retrieve the Address formatted by the default Locale of the device
     */
    public void setAddress() {
        Log.e("DEBUG", "===========================                     MainFragment.setAddress() in  called             ============================");
        // TODO: Check what's going on with not setting location on restart
        if (!mListener.getReadableAddress().isEmpty()){
            addressField.setHint(mListener.getReadableAddress());
            Log.e("DEBUG", "===========================                     current readable address: " + mListener.getReadableAddress() + "         ============================");
            Log.e("DEBUG", "===========================                     current detected location: " + mListener.getDetectedLocation().toString() + "         ============================");
            return;
        }
        if (mGoogleApiClient.isConnected()) {
            Log.e("DEBUG", "===========================               MainFragment: ApiClient is connected, starting intent to fetch address in  called        ======================");
            Intent intent = new Intent(getActivity(), FetchAddressIntentService.class);
            Log.e("service", "Intent ...1");
            intent.putExtra(Constants.RECEIVER, mResultReceiver);
            Log.e("service", "Intent ...2");
            intent.putExtra(Constants.LOCATION_DATA_EXTRA, mListener.getDetectedLocation());
            Log.e("DEBUG", "======================                 detectedLocation is: " + (mListener.getDetectedLocation() == null?"NULL":"SET PROPERLY!") + "             ============================");
            Log.e("service", "Intent ...3");
            getActivity().startService(intent);
        }
        else {
            Log.e("service", "====================                 new apiClient instantiated and .connect() called");
            mGoogleApiClient.connect();
        }
    }


    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * Since this fragment uses LocationDetectionHelper.class, all the activities that
     * contain the fragment must also implement LocationRequester interface.
     */
    public interface MainFragmentListener {
        Location getCurrentLocation();
        GoogleApiClient getGoogleApiClient();
        void startResultsFragment(String queryType, String name, boolean isNameSearch);
        void setReadableAddress(String address);
        String getReadableAddress();
        Boolean isSortPopular();
        void setSortPopular(Boolean popular);
        void setDetectedLocation(Location location);
        void setCityLocation(Location cityLocation);
        Location getDetectedLocation();

        void setCityName(String cityName);
    }

    private class PlacesTask extends AsyncTask<String, Void, ArrayList<String>> {


        @Override
        protected ArrayList<String> doInBackground(String... params) {
            Log.e("pp", "doInBackground");

            ArrayList<String> predictions = new ArrayList<>();

            String path = "https://maps.googleapis.com/maps/api/place/autocomplete/json?input=" + params[0].replace(" ", "%20") + "&types=%28cities%29&key=" + Constants.getBrowserApiKey();
            URL url = null;
            HttpURLConnection connection = null;
            Scanner scanner = null;
            StringBuilder builder = new StringBuilder();

            try {
                url = new URL(path);
                connection = (HttpURLConnection) url.openConnection();
                scanner = new Scanner(connection.getInputStream());
                while (scanner.hasNextLine()){
                    builder.append(scanner.nextLine());
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                JSONObject jObject = new JSONObject(builder.toString());

                if (jObject.getString("status").equalsIgnoreCase("OK")){
                    JSONArray jsonArray = jObject.getJSONArray("predictions");

                    for (int i = 0; i < jsonArray.length(); i++){
                        JSONObject city = jsonArray.getJSONObject(i);

                        predictions.add(city.getString("description"));
                    }
                }
                else {
                    Constants.switchKeys();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return predictions;
        }

        @Override
        protected void onPostExecute(ArrayList<String> s) {
            super.onPostExecute(s);
            Log.e("pp", "onPostExecute");

            entries.clear();
            entries.addAll(s);

            ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, entries);
            addressField.setAdapter(adapter);
        }
    }

}
