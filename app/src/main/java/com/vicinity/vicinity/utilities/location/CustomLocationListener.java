package com.vicinity.vicinity.utilities.location;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.widget.Toast;

import com.vicinity.vicinity.R;

/**
 * Created by Jovch on 27-Mar-16.
 * To instantiate the CustomLocationListener, you must provide a LocationDetectionHelper - the class which instantiates the LocationManager as well
 * as the active Activity which should receive the updated location. This is due to the fact, that
 * the Detect Location request may be performed by a fragment. In this case the fragment should provide
 * his parent activity to the LocationDetectionHelper instance
 */
public class CustomLocationListener implements LocationListener {

    /** On how many refreshes with GPS turned off to toast a suggestion to turn on the GPS */
    public static final int GPS_REMINDER_FREQUENCY = 5;

    private LocationRequester activity;
    private LocationManager locationManager;

    private int gpsReminderCounter;

    public CustomLocationListener(LocationManager locationManager, LocationRequester activeActivity) {
        this.activity = activeActivity;
        this.locationManager = locationManager;
        gpsReminderCounter = 0;
    }

    @Override
    public void onLocationChanged(Location location) {
        activity.receiveLocation(location);
        if (ActivityCompat.checkSelfPermission((Activity) activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission((Activity) activity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationManager.removeUpdates(this);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {
        if (provider.equals(LocationManager.GPS_PROVIDER)) {
            gpsReminderCounter = ++gpsReminderCounter%GPS_REMINDER_FREQUENCY;
            if (gpsReminderCounter == 1) {
                Toast.makeText((Activity) activity, R.string.enable_gps, Toast.LENGTH_SHORT).show();
            }
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
        }
        else {
            Toast.makeText((Activity) activity, R.string.unable_to_detecd_location, Toast.LENGTH_SHORT).show();
        }
        if (ActivityCompat.checkSelfPermission((Activity) activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission((Activity) activity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

    }

    public interface LocationRequester{
        void receiveLocation(Location location);
    }
}