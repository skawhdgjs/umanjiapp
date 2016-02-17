package com.umanji.umanjiapp.helper;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

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
import com.umanji.umanjiapp.model.ErrorData;
import com.umanji.umanjiapp.ui.auth.SignupActivity;
import com.umanji.umanjiapp.ui.channel.community.CommunityActivity;
import com.umanji.umanjiapp.ui.channel.info.InfoActivity;
import com.umanji.umanjiapp.ui.channel.post.PostActivity;
import com.umanji.umanjiapp.ui.channel.post.create.PostCreateActivity;
import com.umanji.umanjiapp.ui.channel.profile.ProfileActivity;
import com.umanji.umanjiapp.ui.channel.spot.SpotActivity;

import org.json.JSONException;
import org.json.JSONObject;

import de.greenrobot.event.EventBus;

public final class Helper implements AppConfig {
    private static final String TAG      = "Helper";

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

        }

        return isAuthError;
    }


    public static LatLng getAdjustedPoint(GoogleMap map, LatLng point) {

        LatLng tmpPoint;
        int zoom = (int) map.getCameraPosition().zoom;
        switch(zoom){
            case 14:
                tmpPoint = new LatLng(point.latitude - 0.0006, point.longitude);
                break;
            case 15:
                tmpPoint = new LatLng(point.latitude - 0.0005, point.longitude);
                break;
            case 16:
                tmpPoint = new LatLng(point.latitude - 0.0004, point.longitude);
                break;
            case 17:
                tmpPoint = new LatLng(point.latitude - 0.0003, point.longitude);
                break;
            case 18:
                tmpPoint = new LatLng(point.latitude - 0.0002, point.longitude);
                break;
            case 19:
                tmpPoint = new LatLng(point.latitude - 0.0001, point.longitude);
                break;
            default:
                tmpPoint = new LatLng(point.latitude - 0.00005, point.longitude);
                break;
        }

        return tmpPoint;
    }

    public static Marker addNewMarkerToMap(GoogleMap map, ChannelData channelData) {
        LatLng point = new LatLng(channelData.getLatitude(), channelData.getLongitude());
        Marker marker;
        marker = map.addMarker(new MarkerOptions().position(point)
                .title("스팟생성")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_marker_aqua))
                .alpha(0.8f)  // default 1.0
                .anchor(0.45f, 1.0f));

        return marker;
    }

    public static Marker addMarkerToMap(GoogleMap map, ChannelData channelData, int index, boolean isDraggable) {
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
                        .draggable(isDraggable)
                        .alpha(0.8f)  // default 1.0
                        .anchor(0.45f, 1.0f));
                break;
            // ic_marker_blue
            case LEVEL_GUGUN:
                marker = map.addMarker(new MarkerOptions().position(point)
                        .title(name)
                        .snippet(String.valueOf(index))
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.gugun))
                        .draggable(isDraggable)
                        .alpha(0.8f)  // default 1.0
                        .anchor(0.45f, 1.0f));
                break;
            // ic_marker_yellow
            case LEVEL_DOSI:
                marker = map.addMarker(new MarkerOptions().position(point)
                        .title(name)
                        .snippet(String.valueOf(index))
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.city))
                        .draggable(isDraggable)
                        .alpha(0.8f)  // default 1.0
                        .anchor(0.45f, 1.0f));
                break;
            // ic_marker_red  LEVEL_COUNTRY

            case LEVEL_COUNTRY:
                marker = map.addMarker(new MarkerOptions().position(point)
                        .title(name)
                        .snippet(String.valueOf(index))
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.kr))
                        .draggable(isDraggable)
                        .alpha(0.8f)  // default 1.0
                        .anchor(0.45f, 1.0f));
                break;

            default:
                marker = map.addMarker(new MarkerOptions().position(point)
                        .title(name)
                        .snippet(String.valueOf(index))
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_marker_aqua))
                        .draggable(isDraggable)
                        .alpha(0.8f)  // default 1.0
                        .anchor(0.45f, 1.0f));
                break;

        }

        return marker;
    }
    public static Marker addMarkerToMap(GoogleMap map, ChannelData channelData, int index) {
        return addMarkerToMap(map, channelData, index, false);
    }


    public static void callAuthErrorEvent() {
        EventBus.getDefault().post(new ErrorData(TYPE_ERROR_AUTH, TYPE_ERROR_AUTH));
    }

    public static void startSignupActivity(Activity activity, LatLng position) {
        Intent intent = new Intent(activity, SignupActivity.class);
        Bundle bundle = new Bundle();
        bundle.putDouble("latitude", position.latitude);
        bundle.putDouble("longitude", position.longitude);
        intent.putExtra("bundle", bundle);
        activity.startActivity(intent);
    }

    public static void startPostCreateActivity(Activity activity, ChannelData channelData) {
        Intent intent = new Intent(activity, PostCreateActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("channel", channelData.getJsonObject().toString());
        bundle.putString("type", "POST");
        intent.putExtra("bundle", bundle);
        activity.startActivity(intent);
    }

    public static void startActivity(Activity activity, ChannelData channelData) {
        Intent intent = null;
        Bundle bundle = new Bundle();
        bundle.putString("channel", channelData.getJsonObject().toString());

        switch (channelData.getType()) {
            case TYPE_SPOT:
                intent = new Intent(activity, SpotActivity.class);
                intent.putExtra("enterAnim", R.anim.zoom_out);
                intent.putExtra("exitAnim", R.anim.zoom_in);
                break;
            case TYPE_SPOT_INNER:
                intent = new Intent(activity, SpotActivity.class);
                break;

            case TYPE_USER:
                intent = new Intent(activity, ProfileActivity.class);
                break;
            case TYPE_KEYWORD:
            case TYPE_COMMUNITY:
                intent = new Intent(activity, CommunityActivity.class);
                break;
            case TYPE_INFO_CENTER:
                intent = new Intent(activity, InfoActivity.class);
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

    public static String getFullAddress(ChannelData channelData) {
        return channelData.getCountryName() + " " + channelData.getAdminArea() + " " + channelData.getLocality() + " " + channelData.getThoroughfare() + " " + channelData.getFeatureName();
    }

    public static JSONObject getZoomMinMaxLatLngParams(GoogleMap map) {
        VisibleRegion visibleRegion = map.getProjection().getVisibleRegion();
        LatLng farRight = visibleRegion.farRight;
        LatLng nearLeft = visibleRegion.nearLeft;

        JSONObject params = new JSONObject();

        try {

            params.put("minLatitude", nearLeft.latitude);
            params.put("maxLatitude", farRight.latitude);
            params.put("minLongitude", nearLeft.longitude);
            params.put("maxLongitude", farRight.longitude);

        }catch(JSONException e) {
            Log.e(TAG, "Error " + e.toString());
        }
        return params;
    }

}
