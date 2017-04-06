package com.umanji.umanjiapp.helper;

import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;

/**
 * Created by paul on 04/04/2017.
 */

public class MyLocationListener implements LocationListener {

    double latitude, longitude;

    public void onLocationChanged(Location loc) {
        latitude = loc.getLatitude();
        longitude = loc.getLongitude();

    }

    public void onProviderDisabled(String provider) {

    }

    public void onProviderEnabled(String provider) {

    }

    public void onStatusChanged(String provider, int status, Bundle extras) {
    }

}