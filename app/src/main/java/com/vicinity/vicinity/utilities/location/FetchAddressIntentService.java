package com.vicinity.vicinity.utilities.location;

import android.app.IntentService;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.util.Log;

import com.vicinity.vicinity.utilities.Constants;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 * Created by Jovch on 27-Mar-16.
 */
public class FetchAddressIntentService extends IntentService {


    protected ResultReceiver mReceiver;

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public FetchAddressIntentService(String name) {
        super(name);
    }

    public FetchAddressIntentService(){
        super("ISName");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String errorMessage = "";
        Log.e("service", "service started");
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        Log.e("service", "geocoder inited");
        mReceiver = intent.getParcelableExtra(Constants.RECEIVER);
        Log.e("service", "mReceiver obtained");
        Location location = intent.getParcelableExtra(Constants.LOCATION_DATA_EXTRA);
        Log.e("service", "location obtained");
        List<Address> addresses = null;
        try {
            addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            Log.e("service", "addresses got from location");


        } catch (IOException|IllegalArgumentException e) {
            Log.e("ADDRESS", e.getMessage());
            errorMessage = e.getMessage();
        }

        if (addresses == null || addresses.isEmpty()){

            Log.e("service", "No address found at the given location!");
            deliverResultToReceiver(Constants.FAILURE_RESULT, "No address found at the given location!");
        }
        else {
            Address addr = addresses.get(0);
            StringBuffer sb = new StringBuffer();

            for (int i = 0; i < addr.getMaxAddressLineIndex(); i++){
                sb.append(addr.getAddressLine(i) + "\n");
            }

            deliverResultToReceiver(Constants.SUCCESS_RESULT, sb.toString());
        }

    }


    private void deliverResultToReceiver(int resultCode, String message) {

        Log.e("service", "delivery to mReceiver called");
        Bundle bundle = new Bundle();
        bundle.putString(Constants.RESULT_DATA_KEY, message);
        mReceiver.send(resultCode, bundle);
    }

}


































