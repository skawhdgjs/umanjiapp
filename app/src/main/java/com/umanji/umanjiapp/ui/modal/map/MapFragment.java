package com.umanji.umanjiapp.ui.modal.map;

import android.app.Dialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.bumptech.glide.Glide;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.umanji.umanjiapp.R;
import com.umanji.umanjiapp.helper.AuthHelper;
import com.umanji.umanjiapp.helper.Helper;
import com.umanji.umanjiapp.model.ChannelData;
import com.umanji.umanjiapp.model.ErrorData;
import com.umanji.umanjiapp.model.SuccessData;
import com.umanji.umanjiapp.ui.BaseFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import de.greenrobot.event.EventBus;

public class MapFragment extends BaseFragment {
    private static final String TAG = "MapFragment";


    /****************************************************
     *  View
     ****************************************************/
    private GoogleMap mMap;
    private LatLng mLatLng;

    private ChannelData mChannel;
    private JSONArray mMarkers;
    private String mMapType;


    private boolean isBlock = false;
    private boolean isLoading = false;

    public static MapFragment newInstance(Bundle bundle) {
        MapFragment fragment = new MapFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(getArguments() != null) {
            mMapType = getArguments().getString("mapType");
            String jsonString = getArguments().getString("channel");
            if(jsonString != null) {
                mChannel = new ChannelData(jsonString);
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);

        initMap();
        updateView();

        return view;
    }

    @Override
    public void initWidgets(View view) {
    }

    @Override
    public View getView(LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.activity_map, container, false);
    }

    @Override
    public void loadData() {
        isLoading = true;
        try {
            JSONObject params = Helper.getZoomMinMaxLatLngParams(mMap);
            params.put("zoom", (int) mMap.getCameraPosition().zoom);
            params.put("limit", 100);
            params.put("sort", "point DESC");
            mApi.call(api_main_findMarkers, params, new AjaxCallback<JSONObject>() {
                @Override
                public void callback(String url, JSONObject json, AjaxStatus status) {
                    isLoading = false;

                    addChannelsToMap(json);
                }
            });
        }catch (JSONException e) {
            Log.e(TAG, "Error " + e.toString());
        }
    }

    @Override
    public void updateView() {

    }


    /****************************************************
     *  Event Bus
     ****************************************************/

    public void onEvent(SuccessData event){

    }


    private void initMap() {
        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(mActivity.getBaseContext());

        if(status!= ConnectionResult.SUCCESS){

            int requestCode = 10;
            Dialog dialog = GooglePlayServicesUtil.getErrorDialog(status, mActivity, requestCode);
            dialog.show();

        }else {
            mMap = ((com.google.android.gms.maps.MapFragment) mActivity.getFragmentManager().findFragmentById(R.id.mMapFragment))
                    .getMap();

            mMap.getUiSettings().setMyLocationButtonEnabled(true);
            mMap.getUiSettings().setZoomControlsEnabled(true);
            mMap.setMyLocationEnabled(true);

            try {

                mLatLng = new LatLng(mChannel.getLatitude(), mChannel.getLongitude());
                CameraPosition cameraPosition = new CameraPosition.Builder()
                        .target(mLatLng)
                        .zoom(10)
                        .bearing(90)
                        .tilt(40)
                        .build();

                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

            }catch (SecurityException e) {
                Log.e("SecurityException", "SecurityException 에러발생:" + e.toString());
            }

            mMap.moveCamera(CameraUpdateFactory.newLatLng(mLatLng));
            mMap.animateCamera(CameraUpdateFactory.zoomTo(18), 2000, null);

        }


        initMapEvents();
    }

    private void initMapEvents() {

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                String index = marker.getSnippet();
                try {
                    ChannelData channelData = new ChannelData(mMarkers.getJSONObject(Integer.valueOf(index)));

                    JSONObject params = new JSONObject();
                    params.put("type", TYPE_LINK);
                    params.put("parent", mChannel.getId());
                    params.put("owner", channelData.getOwner().getId());
                    params.put("id", channelData.getId());
                    params.put("name", channelData.getName());

                    mApi.call(api_channels_id_link, params, new AjaxCallback<JSONObject>() {
                        @Override
                        public void callback(String url, JSONObject object, AjaxStatus status) {
                            super.callback(url, object, status);
                            //TODO:

                            mActivity.finish();
                        }
                    });

                } catch (JSONException e) {
                    Log.e(TAG, "error " + e.toString());
                }

                return true;
            }
        });

        mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
            @Override
            public View getInfoWindow(Marker arg0) {
                return null;
            }

            @Override
            public View getInfoContents(Marker marker) {

                View view = mActivity.getLayoutInflater().inflate(R.layout.widget_info_window, null);
                TextView name = (TextView) view.findViewById(R.id.wiSpotName);
                name.setText(marker.getTitle());

                return view;

            }
        });

        mMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
            @Override
            public void onCameraChange(CameraPosition position) {
                if(!isLoading) loadData();
            }
        });
    }

    private void addChannelsToMap(JSONObject jsonObject) {
        try {
            mMap.clear();
            mMarkers = jsonObject.getJSONArray("data");
            if(mMarkers != null) {
                for(int idx = 0; idx < mMarkers.length() ; idx ++ ) {
                    ChannelData channelData = new ChannelData(mMarkers.getJSONObject(idx));
                    Helper.addMarkerToMap(mMap, channelData, idx);
                }
            }

        }catch(JSONException e) {
            Log.e(TAG, "error " + e.toString());
        }

    }
}
