package com.umanji.umanjiapp.helper;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.VisibleRegion;
import com.umanji.umanjiapp.AppConfig;
import com.umanji.umanjiapp.R;
import com.umanji.umanjiapp.model.ChannelData;
import com.umanji.umanjiapp.ui.page.auth.SignupActivity;
import com.umanji.umanjiapp.ui.page.channel.community.CommunityActivity;
import com.umanji.umanjiapp.ui.page.channel.info.InfoActivity;
import com.umanji.umanjiapp.ui.page.channel.post.PostActivity;
import com.umanji.umanjiapp.ui.page.channel.profile.ProfileActivity;
import com.umanji.umanjiapp.ui.page.channel.spot.SpotActivity;


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
            GoogleMap map = ((MapFragment) activity.getFragmentManager().findFragmentById(R.id.mMapFragment))
                    .getMap();

            double latitude = 0.0f;
            double longitude = 0.0f;
            Bundle bundle = new Bundle();

            if(map != null) {
                latitude =  map.getMyLocation().getLatitude();
                longitude = map.getMyLocation().getLongitude();
                bundle.putDouble("latitude", latitude);
                bundle.putDouble("longitude", longitude);
            }

            Intent intent = new Intent(activity, SignupActivity.class);
            intent.putExtra("bundle", bundle);
            activity.startActivity(intent);
        }

        return isAuthError;
    }


    public static Marker addMarkerToMap(GoogleMap map, ChannelData channelData, int index) {

        LatLng point = new LatLng(channelData.getLatitude(), channelData.getLongitude());
        Marker marker;
        String name = channelData.getName();
        if(TextUtils.isEmpty(name)) {
            name = "이름없음";
        }

        switch (channelData.getLevel()) {
            case LEVEL_DONG:
                marker = map.addMarker(new MarkerOptions().position(point)
                        .title(name)
                        .snippet(String.valueOf(index))
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.dong))
                        .alpha(0.7f)  // default 1.0
                        .anchor(0.45f, 1.0f));
                break;
            // ic_marker_blue
            case LEVEL_GUGUN:
                marker = map.addMarker(new MarkerOptions().position(point)
                        .title(name)
                        .snippet(String.valueOf(index))
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.gugun))
                        .alpha(0.7f)  // default 1.0
                        .anchor(0.45f, 1.0f));
                break;
            // ic_marker_yellow
            case LEVEL_DOSI:
                marker = map.addMarker(new MarkerOptions().position(point)
                        .title(name)
                        .snippet(String.valueOf(index))
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.city))
                        .alpha(0.7f)  // default 1.0
                        .anchor(0.45f, 1.0f));
                break;
            // ic_marker_red  LEVEL_COUNTRY

            case LEVEL_COUNTRY:
                marker = map.addMarker(new MarkerOptions().position(point)
                        .title(name)
                        .snippet(String.valueOf(index))
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.kr))
                        .alpha(0.7f)  // default 1.0
                        .anchor(0.45f, 1.0f));
                break;

            default:
                marker = map.addMarker(new MarkerOptions().position(point)
                        .title(name)
                        .snippet(String.valueOf(index))
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_marker_aqua))
                        .alpha(0.7f)  // default 1.0
                        .anchor(0.45f, 1.0f));
                break;

        }

        return marker;
    }


    public static void startActivity(Activity activity, ChannelData channelData) {
        Intent intent = null;
        Bundle bundle = new Bundle();
        bundle.putString("channel", channelData.getJsonObject().toString());

        switch (channelData.getType()) {
            case TYPE_SPOT:
            case TYPE_SPOT_INNER:
                intent = new Intent(activity, SpotActivity.class);
                break;
            case TYPE_COMMUNITY:
                intent = new Intent(activity, CommunityActivity.class);
                break;
            case TYPE_INFO_CENTER:
                intent = new Intent(activity, InfoActivity.class);
                break;
            case TYPE_USER:
                intent = new Intent(activity, ProfileActivity.class);
                break;
            case TYPE_MEMBER:
                intent = new Intent(activity, ProfileActivity.class);
                break;
            case TYPE_LIKE:
                intent = new Intent(activity, ProfileActivity.class);
            case TYPE_POST:
                intent = new Intent(activity, PostActivity.class);
                break;
        }

        intent.putExtra("bundle", bundle);
        activity.startActivity(intent);
    }

    public static String getShortenString(String str) {

        if(str.length() > 10) {
            return str.substring(0, 10) + "..";
        }else {
            return str;
        }
    }

    public static String getShortenString(String str, int size) {

        if(str.length() > size) {
            return str.substring(0, size) + "..";
        }else {
            return str;
        }
    }
}
