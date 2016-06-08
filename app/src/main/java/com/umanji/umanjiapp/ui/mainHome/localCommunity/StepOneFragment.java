package com.umanji.umanjiapp.ui.mainHome.localCommunity;


import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
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
import android.view.animation.AlphaAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
import com.umanji.umanjiapp.helper.FileHelper;
import com.umanji.umanjiapp.helper.Helper;
import com.umanji.umanjiapp.model.ChannelData;
import com.umanji.umanjiapp.model.ErrorData;
import com.umanji.umanjiapp.model.SuccessData;
import com.umanji.umanjiapp.ui.BaseFragment;
import com.umanji.umanjiapp.ui.channel.BaseChannelCreateFragment;
import com.umanji.umanjiapp.ui.channel.complex.ComplexActivity;
import com.umanji.umanjiapp.ui.channel.duty.DutyCreateActivity;
import com.umanji.umanjiapp.ui.channel.spot.SpotActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import de.greenrobot.event.EventBus;

public class StepOneFragment extends BaseFragment {
    private static final String TAG = "StepOneFragment";

    /****************************************************
     * View
     ****************************************************/

    private GoogleMap mMap;

    private ImageView mGoBackBtn;

    private TextView mZoomLevelText;
    private TextView mInfoTextPanel;


    /****************************************************
     * Map
     ****************************************************/
    LatLng mCurrentMyPosition;

    private int currentZoomLevel = 0;

    /****************************************************
     * Etc
     ****************************************************/
    private ChannelData mUser;
    private JSONArray mMarkers;
    private ChannelData mCurrentChannel;
    private ChannelData mSelectedChannel;
    private ChannelData mClickedChannel;

    protected boolean mClicked = false;


    private boolean isBlock = false;

    private LatLng mLatLngByPoint = new LatLng(37.491361, 126.923978);
    private ChannelData mChannelByPoint;
    private Marker mMarkerByPoint;
    private Marker mFocusedMarker;



    boolean mMapIsTouched = false;
    View mView;
    TouchableWrapper mTouchView;

    public static StepOneFragment newInstance(Bundle bundle) {
        StepOneFragment fragment = new StepOneFragment();
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
        return inflater.inflate(R.layout.activity_step_one, container, false);
    }

    @Override
    public void initWidgets(View view) {

        mGoBackBtn = (ImageView) view.findViewById(R.id.goBackBtn);
        mGoBackBtn.setOnClickListener(this);

        mZoomLevelText = (TextView) view.findViewById(R.id.mZoomLevelText);

        mInfoTextPanel = (TextView) view.findViewById(R.id.mInfoTextPanel);


    }

    @Override
    public void loadData() {

        loadMainMarkers();
    }

    @Override
    public void updateView() {

    }


    private AlphaAnimation buttonClick = new AlphaAnimation(0F, 1F);

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.goBackBtn:
                mGoBackBtn.startAnimation(buttonClick);
                buttonClick.setDuration(500);

                Intent mInt = new Intent(mActivity, CreateLocalCommunityActivity.class);
                startActivity(mInt);
                mActivity.finish();
                break;

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

            int paddingInDp = 20;

            final float scale = getResources().getDisplayMetrics().density;
            int paddingInPx = (int) (paddingInDp * scale + 1f);

            mMap.setPadding(0, paddingInPx, 0, 0);
            mMap.getUiSettings().setMyLocationButtonEnabled(true);
            mMap.getUiSettings().setZoomControlsEnabled(true);
            mMap.setMyLocationEnabled(true);

            initMyLocation();
        }

        initMapEvents();
    }

    protected void initMyLocation() {
        LocationManager locationManager = (LocationManager) mActivity.getSystemService(mActivity.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        String provider = locationManager.getBestProvider(criteria, true);

        double latitude = 37.642443934398;
        double longitude = 126.977429352700;

        try {

            String isFirst = FileHelper.getString(mActivity, "isFirst");
            Location location = null;
            if (TextUtils.isEmpty(isFirst)) {
                FileHelper.setString(mActivity, "isFirst", "checked");
            } else {
                location = locationManager.getLastKnownLocation(provider);
            }

            if (location != null) {
                latitude = location.getLatitude();
                longitude = location.getLongitude();
            }

            mCurrentMyPosition = new LatLng(latitude, longitude);


            CameraPosition cameraPosition;

            ChannelData homeChannel = null;
            if (getArguments() != null) {
                String jsonString = getArguments().getString("channel");
                if (jsonString != null) {
                    homeChannel = new ChannelData(jsonString);
                }
            }

            if (getArguments() != null){    // from home  getArguments().getString("iamFrom") != null

                latitude = homeChannel.getLatitude();
                longitude = homeChannel.getLongitude();

                cameraPosition = new CameraPosition.Builder()
                        .target(mCurrentMyPosition)
                        .zoom(18)
                        .bearing(90)
                        .tilt(40)
                        .build();
            } else {                                            // when start from home
                cameraPosition = new CameraPosition.Builder()
                        .target(mCurrentMyPosition)
                        .zoom(18)
                        .bearing(90)
                        .tilt(40)
                        .build();
            }


            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

        } catch (SecurityException e) {
            Log.e("SecurityException", "SecurityException 에러발생:" + e.toString());
        }

        LatLng latLng = new LatLng(latitude, longitude);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));

        if (getArguments() != null){
            mMap.animateCamera(CameraUpdateFactory.zoomTo(18), 2000, null);   // home
        } else {
            mMap.animateCamera(CameraUpdateFactory.zoomTo(18), 2000, null);    // when start from home
        }

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

                if (zoom >= 15 && zoom <= 21) {

                    try {
                        JSONObject params = new JSONObject();
                        params.put("latitude", mLatLngByPoint.latitude);
                        params.put("longitude", mLatLngByPoint.longitude);

                        mApi.call(api_channels_getByPoint, params, new AjaxCallback<JSONObject>() {
                            @Override
                            public void callback(String url, JSONObject object, AjaxStatus status) {
                                mChannelByPoint = new ChannelData(object);

                                if (TextUtils.isEmpty(mChannelByPoint.getId())) {
                                    if (AuthHelper.isLogin(mActivity)) {
                                        isBlock = true;

                                        mMarkerByPoint = Helper.addNewMarkerToMap(mMap, mChannelByPoint);
                                        LatLng tmpPoint = Helper.getAdjustedPoint(mMap, mLatLngByPoint);

                                        mMap.animateCamera(CameraUpdateFactory.newLatLng(tmpPoint), 100, null);

                                        if (isComplexCreatable(zoom)) {
                                            showCreateComplexDialog();
                                        } else if (isSpotCreatable(zoom)) {
                                            showCreateSpotDialog();
                                        }

                                    } else {
                                        Helper.startSigninActivity(mActivity, mCurrentMyPosition);
                                    }

                                } else {
                                    if (isComplexCreatable(zoom)) {
                                        startActivity(mChannelByPoint, TYPE_COMPLEX);
                                    } else if (isSpotCreatable(zoom)) {
                                        startActivity(mChannelByPoint, TYPE_SPOT);
                                    }

                                }
                            }
                        });
                    } catch (JSONException e) {
                        Log.e(TAG, "error " + e.toString());
                    }
                }

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

                    if (TextUtils.equals(idx, String.valueOf(MARKER_INDEX_CLICKED))) {
                        mClickedChannel = mSelectedChannel;
                    } else {
                        mClickedChannel = new ChannelData(mMarkers.getJSONObject(Integer.valueOf(idx)));
                    }

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
                currentZoomLevel = (int)position.zoom;
                if(mMapIsTouched) return;

                if (isBlock) {
                    isBlock = false;
                } else {

                    mZoomLevelText.setText("" + (int) position.zoom);

                    int zoom = (int) position.zoom;
                    // isPoliticTouchable

                    if (isNot(zoom)) {
                        mInfoTextPanel.setText("지금의 줌레벨에서는 장소를 만들수 없습니다");  // 15~17
                        mInfoTextPanel.setTextColor(getResources().getColor(R.color.red));
                        //mCreateSpotText.setVisibility(View.GONE);

                    } else {
                        switch(zoom){
                            case 15:
                                mInfoTextPanel.setText("축구장과 같은 넓은 장소를 만들수 있습니다 ");
                                mInfoTextPanel.setTextColor(Color.parseColor("#0099ff"));
                                break;

                            case 16:
                                mInfoTextPanel.setText("아직도 장소가 넓습니다. 실내공간을 선택할 경우 좀 더 아래로 가세요");
                                mInfoTextPanel.setTextColor(Color.parseColor("#0099ff"));
                                break;

                            case 17:
                                mInfoTextPanel.setText("이제 건물들이 보이기 시작합니다");
                                mInfoTextPanel.setTextColor(Color.parseColor("#0099ff"));
                                break;

                            case 18:
                                mInfoTextPanel.setText("여러분의 장소로 선택하실 건물을 찾아보세요");
                                mInfoTextPanel.setTextColor(Color.parseColor("#0099ff"));
                                break;

                            case 19:
                                mInfoTextPanel.setText("건물을 한 번 터치하시면 주소가 보입니다. 맞으시면 확인을 그렇지 않으면 취소를 누르세요");
                                mInfoTextPanel.setTextColor(Color.parseColor("#0099ff"));
                                break;

                            case 20:
                                mInfoTextPanel.setText("확인을 누르시면 다음 단계로 이동합니다");
                                mInfoTextPanel.setTextColor(Color.parseColor("#0099ff"));
                                break;
                        }
                    }

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
        try {
            JSONObject params = Helper.getZoomMinMaxLatLngParams(mMap);
            params.put("zoom", 18);
//            params.put("zoom", (int) mMap.getCameraPosition().zoom);
            params.put("limit", 20);
            params.put("sort", "point DESC");
            mApi.call(api_main_findMarkers, params, new AjaxCallback<JSONObject>() {
                @Override
                public void callback(String url, JSONObject json, AjaxStatus status) {
                    addChannelsToMap(json);
                }
            });
        } catch (JSONException e) {
            Log.e(TAG, "Error " + e.toString());
        }

    }


    private void addChannelsToMap(JSONObject jsonObject) {
        try {
            mMap.clear();

            mMarkers = jsonObject.getJSONArray("data");

            int idx = 0;

            if (mCurrentChannel != null) {
                if (Helper.isInVisibleResion(mMap, new LatLng(mCurrentChannel.getLatitude(), mCurrentChannel.getLongitude()))) {
                    mFocusedMarker = Helper.addMarkerToMapOnStepOne(mMap, mCurrentChannel, MARKER_INDEX_BY_POST);
                } else {
                    mCurrentChannel = null;
                }
            }

            if (mClickedChannel != null) {
                if (Helper.isInVisibleResion(mMap, new LatLng(mClickedChannel.getLatitude(), mClickedChannel.getLongitude()))) {
                    mFocusedMarker = Helper.addMarkerToMapOnStepOne(mMap, mClickedChannel, MARKER_INDEX_CLICKED);
                    mSelectedChannel = mClickedChannel;
                } else {
                    mSelectedChannel = null;
                    mClickedChannel = null;
                }
            }

            if (mMarkers != null) {
                for (; idx < mMarkers.length(); idx++) {
                    ChannelData channelData = new ChannelData(mMarkers.getJSONObject(idx));

                    if (mCurrentChannel != null && !TextUtils.equals(mCurrentChannel.getId(), channelData.getId())) {
                        Helper.addMarkerToMapOnStepOne(mMap, channelData, idx);
                    } else if (mSelectedChannel != null && !TextUtils.equals(mSelectedChannel.getId(), channelData.getId())) {
                        Helper.addMarkerToMapOnStepOne(mMap, channelData, idx);
                    } else {
                        Helper.addMarkerToMapOnStepOne(mMap, channelData, idx);
                    }
                }
            }

        } catch (JSONException e) {
            Log.e(TAG, "error " + e.toString());
        }

    }

//    ***

    private void showCreateSpotDialog() {
        mAlert.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try {
                    JSONObject params = mChannelByPoint.getAddressJSONObject();
                    params.put("type", TYPE_SPOT);
                    mApi.call(api_channels_createSpot, params, new AjaxCallback<JSONObject>() {
                        @Override
                        public void callback(String url, JSONObject object, AjaxStatus status) {
                            mChannelByPoint = new ChannelData(object);
                            if (mMarkerByPoint != null) mMarkerByPoint.remove();
                            startActivity(mChannelByPoint, TYPE_SPOT);  // startSpotActivity
                            mActivity.finish();

                            EventBus.getDefault().post(new SuccessData(api_channels_createSpot, object));
                        }
                    });
                    dialog.cancel();
                } catch (JSONException e) {
                    Log.e(TAG, "Error " + e.toString());
                }
            }
        });

        mAlert.setNegativeButton(R.string.exit, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mMarkerByPoint.remove();
                dialog.cancel();
            }
        });

        mAlert.setTitle("찾으신 곳의 주소가 맞습니까?");
        mAlert.setMessage(Helper.getFullAddress(mChannelByPoint));
        mAlert.show();
    }

    private void showCreateComplexDialog() {
        mAlert.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try {
                    JSONObject params = mChannelByPoint.getAddressJSONObject();
                    params.put("level", LEVEL_COMPLEX);
                    params.put("type", TYPE_COMPLEX);
                    mApi.call(api_channels_createComplex, params, new AjaxCallback<JSONObject>() {
                        @Override
                        public void callback(String url, JSONObject object, AjaxStatus status) {
                            mChannelByPoint = new ChannelData(object);
                            if (mMarkerByPoint != null) mMarkerByPoint.remove();
                            startActivity(mChannelByPoint, TYPE_COMPLEX);
//                            mActivity.finish();

                            EventBus.getDefault().post(new SuccessData(api_channels_createComplex, object));
                        }
                    });
                    dialog.cancel();
                } catch (JSONException e) {
                    Log.e(TAG, "Error " + e.toString());
                }
            }
        });

        mAlert.setNegativeButton(R.string.exit, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mMarkerByPoint.remove();
                dialog.cancel();
            }
        });

        mAlert.setTitle("단체를 만들 주소가 맞습니까?");
        mAlert.setMessage(Helper.getFullAddress(mChannelByPoint));
        mAlert.show();
    }

    private void startActivity(ChannelData channel, String type) {

        Intent intent = new Intent(mActivity, StepTwoActivity.class);

        Bundle bundle = new Bundle();
        bundle.putString("channel", channel.getJsonObject().toString());

        switch (type) {

            case TYPE_SPOT:
                bundle.putString("localType", "local_spot");
                break;
            case TYPE_COMPLEX:
                bundle.putString("localType", "local_complex");
                break;

        }

        intent.putExtra("bundle", bundle);
        intent.putExtra("enterAnim", R.anim.zoom_out);
        intent.putExtra("exitAnim", R.anim.zoom_in);

        startActivity(intent);
        mActivity.finish();
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
                mActivity.finish();
                Toast.makeText(mActivity,"Event occured", Toast.LENGTH_SHORT).show();
                break;
            case api_profile_id_update:
                mUser = new ChannelData(event.response);
                updateView();
                break;
            case api_noites_read:
                break;

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

    private static boolean isKeywordTouchable(int zoom) {
        if (zoom >= 2 && zoom <= 7) {
            return true;
        } else {
            return false;
        }
    }

    private static boolean isNot(int zoom) {
        if (zoom >= 2 && zoom <= 14) {
            return true;
        } else {
            return false;
        }
    }

    private static boolean isComplexCreatable(int zoom) {
        if (zoom >= 15 && zoom <= 17) {
            return true;
        } else {
            return false;
        }
    }

    private static boolean isSpotCreatable(int zoom) {
        if (zoom >= 18) {
            return true;
        } else {
            return false;
        }
    }

}
