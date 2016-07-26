package com.umanji.umanjiapp.ui.distribution;


import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
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
import com.umanji.umanjiapp.ui.channel.duty.DutyCreateActivity;
import com.umanji.umanjiapp.ui.channel.complex.ComplexActivity;
import com.umanji.umanjiapp.ui.channel.spot.SpotActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import de.greenrobot.event.EventBus;

public class DistributionFragment extends BaseFragment {
    private static final String TAG = "DistributionFragment";

    /****************************************************
     * View
     ****************************************************/

    private GoogleMap mMap;

    /****************************************************
     * Map
     ****************************************************/
    LatLng mCurrentMyPosition;

    /****************************************************
     * Etc
     ****************************************************/
    private ChannelData mUser;
    private JSONArray mMarkers;
    private ChannelData mCurrentChannel;
    private ChannelData mSelectedChannel;
    private ArrayList<ChannelData> mPosts;

    private boolean isBlock = false;
    private boolean isLoading = false;
    private int mPreFocusedItem = 0;

    private LatLng mLatLngByPoint = new LatLng(37.491361, 126.923978);
    private ChannelData mChannelByPoint;
    private Marker mMarkerByPoint;
    private Marker mFocusedMarker;

    private TextView mZoomLevelText;

    boolean mMapIsTouched = false;
    View mView;
    TouchableWrapper mTouchView;

    public static DistributionFragment newInstance(Bundle bundle) {
        DistributionFragment fragment = new DistributionFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = super.onCreateView(inflater, container, savedInstanceState);

        if (AuthHelper.isLogin(mActivity)) {

        } else {
            updateView();
        }

        initWidgets(mView);
        initMap();

        mTouchView = new TouchableWrapper(getActivity());
        mTouchView.addView(mView);
        return mTouchView;
    }

    @Override
    public View getView(LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.activity_distribution, container, false);
    }

    @Override
    public void initWidgets(View view) {
        mZoomLevelText = (TextView) view.findViewById(R.id.zoomLevelText);

    }

    @Override
    public void loadData() {

        loadMainMarkers();
    }

    @Override
    public void updateView() {

    }

    @Override
    public void onEvent(SuccessData event) {
        super.onEvent(event);

        switch (event.type) {
            case api_token_check:
            case api_channels_createCommunity:
            case api_channels_createComplex:
            case api_channels_createSpot:
            case api_channels_id_update:
            case api_channels_id_delete:
                mCurrentChannel = null;
                mSelectedChannel = null;
                loadData();
                break;
            case api_profile_id_update:
                mUser = new ChannelData(event.response);
                updateView();
                break;
            case api_noites_read:
                break;

            case api_channels_id_vote:
            case api_channels_id_like:

            case api_channels_id_unLike:

            case EVENT_LOOK_AROUND:
                ChannelData channelData = new ChannelData(event.response);
                LatLng latLng = new LatLng(channelData.getLatitude(), channelData.getLongitude());
                mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                mMap.animateCamera(CameraUpdateFactory.zoomTo(18), 2000, null);
                break;
        }
    }

    public void onEvent(ErrorData event) {

        switch (event.type) {
            case TYPE_ERROR_AUTH:
                Helper.startSigninActivity(mActivity, mCurrentMyPosition);
                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

        }
    }

    private void initMap() {
        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(mActivity.getBaseContext());

        if (status != ConnectionResult.SUCCESS) {

            int requestCode = 10;
            Dialog dialog = GooglePlayServicesUtil.getErrorDialog(status, mActivity, requestCode);
            dialog.show();

        } else {
            mMap = ((MapFragment) mActivity.getFragmentManager().findFragmentById(R.id.mMapFragment))
                    .getMap();

            int paddingInDp = 50;

            final float scale = getResources().getDisplayMetrics().density;
            int paddingInPx = (int) (paddingInDp * scale + 0.5f);

            mMap.setPadding(0, paddingInPx, 0, 0);
            mMap.getUiSettings().setMyLocationButtonEnabled(true);
            mMap.getUiSettings().setZoomControlsEnabled(true);
            mMap.setMyLocationEnabled(true);


            LocationManager locationManager = (LocationManager) mActivity.getSystemService(mActivity.LOCATION_SERVICE);
            Criteria criteria = new Criteria();
            String provider = locationManager.getBestProvider(criteria, true);

            double latitude = 36.068588;
            double longitude = 127.6359813;   // 용문역 36.3382285 127.3931581
//            35.844445,127.742355
//            36.068588,127.6359813

            try {
                Location location = locationManager.getLastKnownLocation(provider);

                if (location != null) {

                    latitude = location.getLatitude();
                    longitude = location.getLongitude();

                    mCurrentMyPosition = new LatLng(latitude, longitude);

                    CameraPosition cameraPosition = new CameraPosition.Builder()
                            .target(mCurrentMyPosition)
                            .zoom(7)
                            .bearing(90)
                            .tilt(40)
                            .build();
                    mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                }
            } catch (SecurityException e) {
                Log.e("SecurityException", "SecurityException 에러발생:" + e.toString());
            }

            LatLng latLng = new LatLng(latitude, longitude);
            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            mMap.animateCamera(CameraUpdateFactory.zoomTo(7), 2000, null);
        }

        initMapEvents();
    }

    /****************************************************
     * init Map Events
     ****************************************************/

    protected void initMapEvents() {

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng point) {

                if (mFocusedMarker != null) {
                    mFocusedMarker.remove();
                }
                if (mChannelByPoint != null) {
                    mChannelByPoint = null;
                }

                final int zoom = (int) mMap.getCameraPosition().zoom;

                mLatLngByPoint = point;

            }
        });

        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng point) {
            }
        });

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                isBlock = true;
                LatLng latLng = marker.getPosition();

                mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng), 500, null);

                if (marker.isInfoWindowShown()) {
                    marker.hideInfoWindow();
                } else {
                    marker.showInfoWindow();
                }

                try {
                    String idx = marker.getSnippet();
                    mSelectedChannel = new ChannelData(mMarkers.getJSONObject(Integer.valueOf(idx)));
                } catch (JSONException e) {
                    Log.e(TAG, "Error " + e.toString());
                }

                return true;
            }
        });

        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                String index = marker.getSnippet();

                ChannelData channelData;
                try {
                    if (TextUtils.equals(index, String.valueOf(MARKER_INDEX_BY_POST))) {
                        channelData = mCurrentChannel.getParent();
                    } else if (TextUtils.equals(index, String.valueOf(MARKER_INDEX_CLICKED))) {
                        channelData = mSelectedChannel;
                    } else {
                        channelData = new ChannelData(mMarkers.getJSONObject(Integer.valueOf(index)));
                    }

                    // To modify HERE!!!
                    //Helper.startActivity(mActivity, channelData);
                    Intent i = new Intent(mActivity, DutyCreateActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("channel", channelData.getJsonObject().toString());
                    bundle.putString("whichAction", "appointment");
                    i.putExtra("bundle", bundle);
                    startActivity(i);

                } catch (JSONException e) {
                    Log.e(TAG, "error " + e.toString());
                }
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


        mMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
            @Override
            public void onMapLoaded() {
                Log.d(TAG, "onMapLoaded: ");
            }
        });

        mMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
            @Override
            public void onCameraChange(CameraPosition position) {
                mZoomLevelText.setText("" + (int) position.zoom);
//                int zoom = (int) position.zoom;

                if(mMapIsTouched) return;

                if (isBlock) {
                    isBlock = false;
                } else {
                    loadData();
                }

            }
        });

        mMap.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
            @Override
            public void onMyLocationChange(Location location) {
                mCurrentMyPosition = new LatLng(location.getLatitude(), location.getLongitude());
            }
        });

    }

    private void loadMainMarkers() {
        //mProgress.show();

        try {
            JSONObject params = Helper.getZoomMinMaxLatLngParams(mMap);
            params.put("zoom", (int) mMap.getCameraPosition().zoom);
            params.put("limit", 100);
            params.put("sort", "point DESC");
            mApi.call(api_Staff_find, params, new AjaxCallback<JSONObject>() {
                @Override
                public void callback(String url, JSONObject json, AjaxStatus status) {
                    addChannelsToMap(json);
                }
            });


        } catch (JSONException e) {
            Log.e(TAG, "Error " + e.toString());
        }
        mProgress.hide();

    }
    private void addChannelsToMap(JSONObject jsonObject) {
        try {
            mMap.clear();

            mMarkers = jsonObject.getJSONArray("data");

            int idx = 0;

            if (mCurrentChannel != null) {
                if (Helper.isInVisibleResion(mMap, new LatLng(mCurrentChannel.getLatitude(), mCurrentChannel.getLongitude()))) {
                    mFocusedMarker = Helper.addMarkerToMap(mMap, mCurrentChannel, MARKER_INDEX_BY_POST, mActivity);
                } else {
                    mCurrentChannel = null;
                }
            }

            if (mSelectedChannel != null) {
                if (Helper.isInVisibleResion(mMap, new LatLng(mSelectedChannel.getLatitude(), mSelectedChannel.getLongitude()))) {
                    mFocusedMarker = Helper.addStaffToMap(mMap, mSelectedChannel, MARKER_INDEX_CLICKED, mActivity);
                } else {
                    mSelectedChannel = null;
                }
            }

            if (mMarkers != null) {
                for (; idx < mMarkers.length(); idx++) {
                    ChannelData channelData = new ChannelData(mMarkers.getJSONObject(idx));
                    Helper.addStaffToMap(mMap, channelData, idx, mActivity);
                }
            }

        } catch (JSONException e) {
            Log.e(TAG, "error " + e.toString());
        }

    }

    private class TouchableWrapper extends FrameLayout {
        public TouchableWrapper(Context context) {
            super(context);
        }

        @Override
        public boolean dispatchTouchEvent(MotionEvent ev) {
            switch (ev.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    mMapIsTouched = true;
                    break;

                case MotionEvent.ACTION_UP:
                    mMapIsTouched = false;
                    break;
            }

            return super.dispatchTouchEvent(ev);
        }
    }

}
