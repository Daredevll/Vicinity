package com.vicinity.vicinity.utilities.location;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;

import com.vicinity.vicinity.utilities.location.CustomLocationListener.LocationRequester;

/**
 * Created by Jovch on 27-Mar-16.
 */
public class LocationDetectionHelper  {

    LocationManager locManager;
    LocationListener locListener;
    String locProviderGps = LocationManager.GPS_PROVIDER;
    String locProviderNet = LocationManager.NETWORK_PROVIDER;

    LocationRequester requester;

    private static LocationDetectionHelper instance;

    public static LocationDetectionHelper getInstance(LocationRequester callingActivity){
        if (instance == null){
            instance = new LocationDetectionHelper(callingActivity);
        }
        else {
            instance.setLocationRequester(callingActivity);
        }
        return instance;
    }

    private LocationDetectionHelper(LocationRequester callingActivity) {
        this.requester = (LocationRequester) callingActivity;
        locManager = (LocationManager) ((Activity) callingActivity).getSystemService(Context.LOCATION_SERVICE);
        locListener = new CustomLocationListener(locManager, requester);
    }

    private void setLocationRequester(LocationRequester newRequester){
        this.requester = newRequester;
    }


    public void getLocation(LocationRequester lr, String city) {

    }

    public void getLocation(LocationRequester lr) {
        if (ActivityCompat.checkSelfPermission((Activity) requester, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission((Activity) requester, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locListener);
    }

}
