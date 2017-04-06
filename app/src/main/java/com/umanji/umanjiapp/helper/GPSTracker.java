package com.umanji.umanjiapp.helper;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;

/**
 * Created by paul on 04/04/2017.
 */

public class GPSTracker extends Service implements LocationListener {

    private Context context;
    boolean isGPSEnabled = false;


    boolean isNetworkEnabled = false;
    boolean canGetLocation = false;

    Location location;
    double latitude, longitude;

    LocationManager locationManager;
    AlertDialogManager am = new AlertDialogManager();

    public GPSTracker(Context context) {
        this.context = context;
        getLocation();
    }

    private void getLocation() {
        // TODO Auto-generated method stub
        try {
            locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

            isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

            isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);


            if (!isGPSEnabled && !isNetworkEnabled) {
                String newlocation;
                LocationManager mlocManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                if (mlocManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
                    newlocation = LocationManager.PASSIVE_PROVIDER;
                else
                    newlocation = LocationManager.GPS_PROVIDER;
                LocationListener mlocListener = new MyLocationListener();
                mlocManager.requestLocationUpdates(newlocation, 0, 0, mlocListener);
                mlocManager.requestLocationUpdates(newlocation, 0, 1, mlocListener);
                Location locate = mlocManager
                        .getLastKnownLocation(LocationManager.GPS_PROVIDER);
                if (locate != null) {
                    latitude = locate.getLatitude();
                    longitude = locate.getLongitude();

                } else {
                    locate = mlocManager
                            .getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
                    if (locate != null) {
                        latitude = locate.getLatitude();
                        longitude = locate.getLongitude();

                    }
                }

            } else {
                this.canGetLocation = true;

                if (isNetworkEnabled) {

                    if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions((Activity)context, new String[]{
                                android.Manifest.permission.ACCESS_FINE_LOCATION
                        }, 10);}
                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 60000, 3, this);

                    if (locationManager != null){
                        location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        if (location != null){
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();
                        }
                    }
                }

                if (isGPSEnabled){
                    if (location == null){
                        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 60000, 3, this);
                        if (locationManager != null){
                            location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                            if (location != null){
                                latitude = location.getLatitude();
                                longitude = location.getLongitude();
                            }
                        }
                    }
                } else {
                    showAlertDialog();
                }
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return;
    }
    public void showSettingsAlert(){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(GPSTracker.this);

        // Setting Dialog Title
        alertDialog.setTitle("GPS is settings");

        // Setting Dialog Message
        alertDialog.setMessage("GPS is not enabled. Do you want to go to settings menu?");

        // Setting Icon to Dialog
        //alertDialog.setIcon(R.drawable.delete);

        // On pressing Settings button
        alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog,int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
                dialog.cancel();
            }
        });

        // on pressing cancel button
        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        // Showing Alert Message
        alertDialog.show();
    }

    public void showAlertDialog(){
        am.showAlertDialog(GPSTracker.this, "GPS Setting", "Gps is not enabled. Do you want to enabled it ?", false);
    }
    public double getLatitude(){
        if (location != null){
            latitude = location.getLatitude();
        }

        return latitude;
    }

    public double getLongitude(){
        if (location != null){
            longitude = location.getLongitude();
        }

        return longitude;
    }

    public boolean canGetLocation(){
        return this.canGetLocation;
    }
    @Override
    public void onLocationChanged(Location location) {
        // TODO Auto-generated method stub
        if (location != null){
            this.location = location;
        }
    }

    @Override
    public void onProviderDisabled(String provider) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onProviderEnabled(String provider) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        // TODO Auto-generated method stub

    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        return null;
    }

}