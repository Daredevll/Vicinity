package com.vicinity.vicinity.controller.fragments;

import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.vicinity.vicinity.R;
import com.vicinity.vicinity.utilities.Constants;
import com.vicinity.vicinity.utilities.location.CustomLocationListener.LocationRequester;
import com.vicinity.vicinity.utilities.location.FetchAddressIntentService;
import com.vicinity.vicinity.utilities.location.LocationDetectionHelper;

public class MainFragment extends Fragment implements View.OnClickListener, GoogleApiClient.ConnectionCallbacks{

    /*
    The activity, in which the current fragment is created
    */
    private MainFragmentListener mListener;
    private GoogleApiClient mGoogleApiClient;

    private AddressResultReceiver mResultReceiver;

    View.OnClickListener sortingListener;

    private EditText addressField;

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
                        sortPopular.setTextColor(0x6EFFFFFF);
                        mListener.setSortPopular(false);
                        break;
                    case R.id.main_fragment_sort_filter_popular:
                        ((Button) v).setTextColor(0xFFFFFFFF);
                        sortNearest.setTextColor(0x6EFFFFFF);
                        mListener.setSortPopular(true);
                        break;
                }
            }
        };

        addressField = (EditText) layout.findViewById(R.id.city_input);

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
            addressField.setHint("Detecting current location...");
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
        if (mListener.getCurrentLocation() == null && addressField.getText().toString().isEmpty()){
            Toast.makeText(getActivity(), "Location not detected yet, please wait", Toast.LENGTH_SHORT).show();
            return;
        }
        switch (v.getId()){
            case R.id.restaurant_image_view:
                mListener.startResultsFragment(Constants.TYPE_RESTAURANT);
                break;
            case R.id.bar_image_view:
                mListener.startResultsFragment(Constants.TYPE_BAR);
                break;
            case R.id.cafe_image_view:
                mListener.startResultsFragment(Constants.TYPE_CAFE);
                break;
            case R.id.hotel_image_view:
                mListener.startResultsFragment(Constants.TYPE_HOTEL);
                break;
            case R.id.casino_image_view:
                mListener.startResultsFragment(Constants.TYPE_CASINO);
                break;
            case R.id.delivery_image_view:
                mListener.startResultsFragment(Constants.TYPE_DELIVERY);
                break;
            case R.id.fitness_image_view:
                mListener.startResultsFragment(Constants.TYPE_FITNESS);
                break;
            case R.id.pool_image_view:
                mListener.startResultsFragment(Constants.TYPE_POOL);
                break;
            case R.id.movie_image_view:
                mListener.startResultsFragment(Constants.TYPE_MOVIE);
                break;
        }
    }

    /**
     * Gets the location from the parent activity and starts IntentService to
     * retrieve the Address formatted by the default Locale of the device
     */
    public void setAddress() {
        if (!mListener.getReadableAddress().isEmpty()){
            addressField.setHint(mListener.getReadableAddress());
            return;
        }
        if (mGoogleApiClient.isConnected()) {
            Intent intent = new Intent(getActivity(), FetchAddressIntentService.class);
            Log.e("service", "Intent ...1");
            intent.putExtra(Constants.RECEIVER, mResultReceiver);
            Log.e("service", "Intent ...2");
            intent.putExtra(Constants.LOCATION_DATA_EXTRA, mListener.getCurrentLocation());
            Log.e("service", "Intent ...3");
            getActivity().startService(intent);
        }
        else {
            Log.e("service", "new apiClient instantiated and .connect() called");
            mGoogleApiClient.connect();
        }
    }


    // TODO: Get device location or user input for a location
        // TODO: Check if user has inputted a city, if not - get location
        // TODO: Call location manager
        // TODO: Create a Location object, pass it the retrieved coordinates for location or City if chosen


    // TODO: Pass the location and Place type data to the next Fragment


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
        void startResultsFragment(String queryType);
        void setReadableAddress(String address);
        String getReadableAddress();
        Boolean isSortPopular();
        void setSortPopular(Boolean popular);
    }

}
