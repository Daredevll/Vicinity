package com.vicinity.vicinity.controller.fragments;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.vicinity.vicinity.utilities.location.FetchAddressIntentService;
import com.vicinity.vicinity.R;
import com.vicinity.vicinity.utilities.Constants;
import com.vicinity.vicinity.utilities.location.CustomLocationListener.LocationRequester;
import com.vicinity.vicinity.utilities.location.LocationDetectionHelper;

public class MainFragment extends Fragment implements View.OnClickListener, GoogleApiClient.ConnectionCallbacks{

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
                receivedAddress = resultData.getString(Constants.RESULT_DATA_KEY);

                Log.e("service", "received address is" + receivedAddress);
                addressField.setHint(receivedAddress.replace("\n", " "));
            }
        }
    }

    /*
        The activity, in which the current fragment is created
     */
    private MainFragmentListener mListener;
    private LocationRequester requesterActivity;
    private GoogleApiClient mGoogleApiClient;

    private AddressResultReceiver mResultReceiver;

    private EditText addressField;
    private Button findLocationButton;

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

    public MainFragment() {
        // Required empty public constructor
    }

    public MainFragment(Context context){
        this();
        mListener = (MainFragmentListener) context;
        requesterActivity = (LocationRequester) context;
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

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .build();


        addressField = (EditText) layout.findViewById(R.id.city_input);
        findLocationButton = (Button) layout.findViewById(R.id.find_my_location_button);

        restaurant = (ImageView) layout.findViewById(R.id.restaurant_image_view);
        bar = (ImageView) layout.findViewById(R.id.bar_image_view);
        cafe = (ImageView) layout.findViewById(R.id.cafe_image_view);
        hotel = (ImageView) layout.findViewById(R.id.hotel_image_view);
        casino = (ImageView) layout.findViewById(R.id.casino_image_view);
        delivery = (ImageView) layout.findViewById(R.id.delivery_image_view);
        gym = (ImageView) layout.findViewById(R.id.fitness_image_view);
        pool = (ImageView) layout.findViewById(R.id.pool_image_view);
        movies = (ImageView) layout.findViewById(R.id.movie_image_view);

        findLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LocationDetectionHelper.getInstance((LocationRequester) mListener).getLocation((LocationRequester) mListener);
            }
        });

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

//        PlaceAutocompleteFragment paf = (PlaceAutocompleteFragment) getFragmentManager().findFragmentById(R.id.main_place_autocomplete);
//
//        AutocompleteFilter filter = new AutocompleteFilter.Builder()
//                .setTypeFilter(AutocompleteFilter.TYPE_FILTER_ADDRESS)
//                .build();
//
//        paf.setFilter(filter);
//
//        paf.setOnPlaceSelectedListener(new PlaceSelectionListener() {
//            @Override
//            public void onPlaceSelected(Place place) {
//                selectedAddress = place.getAddress();
//            }
//
//            @Override
//            public void onError(Status status) {
//
//            }
//        });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.restaurant_image_view:
                //TODO: Restaurant....
                break;
            case R.id.bar_image_view:
                //TODO: bar....
                break;
            case R.id.cafe_image_view:
                //TODO: cafe....
                break;
            case R.id.hotel_image_view:
                //TODO: hotel....
                break;
            case R.id.casino_image_view:
                //TODO: casino....
                break;
            case R.id.delivery_image_view:
                //TODO: delivery....
                break;
            case R.id.fitness_image_view:
                //TODO: fitness....
                break;
            case R.id.pool_image_view:
                //TODO: pool....
                break;
            case R.id.movie_image_view:
                //TODO: movie....
                break;
        }
    }

    public void setAddress() {
        // TODO: Obtain the address by the location passed

        if (mGoogleApiClient.isConnected()) {
            // TODO: Start the IntentService here:
            Intent intent = new Intent(getActivity(), FetchAddressIntentService.class);
            Log.e("service", "Intent ...1");
            intent.putExtra(Constants.RECEIVER, mResultReceiver);
            Log.e("service", "Intent ...2");
            intent.putExtra(Constants.LOCATION_DATA_EXTRA, mListener.getCurrentLocation());
            Log.e("service", "Intent ...3");
            getActivity().startService(intent);
        }
        else {
            Log.e("service", "new apiClient innstantiated and .connect() called");
            mGoogleApiClient.connect();
        }




    }


    // TODO: Get device location or user input for a location
        // TODO: Check if user has inputted a city, if not - get location
        // TODO: Call location manager
        // TODO: Add permissions
        // TODO: Set up first NETWORK, then GPS provider to retrieve Longitude and Latitude
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
    }

}
