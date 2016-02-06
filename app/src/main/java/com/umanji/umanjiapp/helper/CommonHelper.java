package com.umanji.umanjiapp.helper;

import android.app.Activity;
import android.content.Intent;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.VisibleRegion;
import com.umanji.umanjiapp.AppConfig;
import com.umanji.umanjiapp.R;
import com.umanji.umanjiapp.model.ChannelData;
import com.umanji.umanjiapp.ui.page.auth.SignupActivity;


public final class CommonHelper implements AppConfig{


    public static boolean isInVisibleResion(GoogleMap map, LatLng point) {
        VisibleRegion visibleRegion = map.getProjection().getVisibleRegion();
        LatLng farRight = visibleRegion.farRight;
        LatLng nearLeft = visibleRegion.nearLeft;

        double minLatitude = nearLeft.latitude;
        double maxLatitude = farRight.latitude;
        double minLongitude = nearLeft.longitude;
        double maxLongitude = farRight.longitude;

        if( point.latitude > minLatitude && point.latitude <= maxLatitude && point.longitude > minLongitude && point.longitude <= maxLongitude){
            return true;
        }else {
            return false;
        }

    }

    public static int dpToPixel(Activity activity, int dp) {
        final float scale = activity.getResources().getDisplayMetrics().density;
        int px = (int) (dp * scale + 0.5f);
        return px;
    }

    public static boolean isAuthError(Activity activity) {

        boolean isAuthError = !AuthHelper.isLogin(activity);
        if(isAuthError) {
            Intent intent = new Intent(activity, SignupActivity.class);
            activity.startActivity(intent);
        }

        return isAuthError;
    }


    public static Marker addMarkerToMap(GoogleMap map, ChannelData channelData, int index) {

        LatLng point = new LatLng(channelData.getLatitude(), channelData.getLongitude());
        Marker marker;
        switch (channelData.getLevel()) {
            case LEVEL_DONG:
                marker = map.addMarker(new MarkerOptions().position(point)
                        .title(channelData.getName())
                        .snippet(String.valueOf(index))
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_marker_blue))
                        .anchor(0.45f, 1.0f));

                break;
            case LEVEL_GUGUN:
                marker = map.addMarker(new MarkerOptions().position(point)
                        .title(channelData.getName())
                        .snippet(String.valueOf(index))
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_marker_yellow))
                        .anchor(0.45f, 1.0f));

                break;
            case LEVEL_DOSI:
                marker = map.addMarker(new MarkerOptions().position(point)
                        .title(channelData.getName())
                        .snippet(String.valueOf(index))
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_marker_red))
                        .anchor(0.45f, 1.0f));
                break;
            default:
                marker = map.addMarker(new MarkerOptions().position(point)
                        .title(channelData.getName())
                        .snippet(String.valueOf(index))
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_marker_aqua))
                        .anchor(0.45f, 1.0f));
                break;

        }

        return marker;
    }
}
