package com.umanji.umanjiapp.helper;

import android.util.Log;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.VisibleRegion;

import org.json.JSONException;
import org.json.JSONObject;

public final class JsonHelper {
    private static final String TAG = "JsonHelper";

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
