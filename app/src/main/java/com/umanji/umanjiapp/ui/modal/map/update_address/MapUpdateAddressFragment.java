package com.umanji.umanjiapp.ui.modal.map.update_address;

import android.Manifest;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
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

public class MapUpdateAddressFragment extends BaseFragment {
    private static final String TAG = "MapUpdateAddress";


    /****************************************************
     *  View
     ****************************************************/
    private GoogleMap mMap;
    private LatLng mLatLng;

    private String mMapType;
    private ChannelData mChannel;

    private ChannelData mSelectedChannel;
    private Marker mSelectedMarker;


    public static MapUpdateAddressFragment newInstance(Bundle bundle) {
        MapUpdateAddressFragment fragment = new MapUpdateAddressFragment();
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
        return inflater.inflate(R.layout.activity_map_update_address, container, false);
    }

    @Override
    public void loadData() {
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

            mMap.getUiSettings().setZoomControlsEnabled(true);

            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(true);

            initMyLocation();
        }


        initMapEvents();
    }

    protected void initMyLocation() {
        try {

            mLatLng = new LatLng(mChannel.getLatitude(), mChannel.getLongitude());
            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(mLatLng)
                    .zoom(15)
                    .bearing(90)
                    .tilt(40)
                    .build();

            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            Helper.addMarkerToMap(mMap, mChannel, 0, true);

        }catch (SecurityException e) {
            Log.e("SecurityException", "SecurityException 에러발생:" + e.toString());
        }

        mMap.moveCamera(CameraUpdateFactory.newLatLng(mLatLng));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(18), 1000, null);
    }

    private void initMapEvents() {

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng point) {
                mLatLng = point;
                try {
                    JSONObject params = new JSONObject();
                    params.put("latitude", mLatLng.latitude);
                    params.put("longitude", mLatLng.longitude);

                    mApi.call(api_channels_getByPoint, params, new AjaxCallback<JSONObject>() {
                        @Override
                        public void callback(String url, JSONObject json, AjaxStatus status) {
                            if (status.getCode() == 500) {
                                EventBus.getDefault().post(new ErrorData(TYPE_ERROR_AUTH, TYPE_ERROR_AUTH));
                            } else {
                                mSelectedChannel = new ChannelData(json);
                                showUpdateDialog();
                            }
                        }
                    });

                } catch(JSONException e) {
                    Log.e(TAG, "error " + e.toString());
                }

            }
        });

        mMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
            @Override
            public void onMarkerDragStart(Marker marker) {

            }

            @Override
            public void onMarkerDrag(Marker marker) {

            }

            @Override
            public void onMarkerDragEnd(Marker marker) {
                mLatLng = marker.getPosition();
                try {
                    JSONObject params = new JSONObject();
                    params.put("latitude", mLatLng.latitude);
                    params.put("longitude", mLatLng.longitude);

                    mApi.call(api_channels_getByPoint, params, new AjaxCallback<JSONObject>() {
                        @Override
                        public void callback(String url, JSONObject json, AjaxStatus status) {
                            if (status.getCode() == 500) {
                                EventBus.getDefault().post(new ErrorData(TYPE_ERROR_AUTH, TYPE_ERROR_AUTH));
                            } else {
                                mSelectedChannel = new ChannelData(json);
                                showUpdateDialog();
                            }
                        }
                    });

                } catch (JSONException e) {
                    Log.e(TAG, "error " + e.toString());
                }
            }
        });
    }

    private void showUpdateDialog() {
        mAlert.setPositiveButton(R.string.spot_update_address_ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(final DialogInterface dialog, int which) {
                try {
                    mChannel.setAddressJSONObject(mSelectedChannel);
                    JSONObject params = mChannel.getAddressJSONObject();
                    params.put("id", mChannel.getId());

                    mProgress.show();
                    mApi.call(api_channels_id_update, params, new AjaxCallback<JSONObject>() {
                        @Override
                        public void callback(String url, JSONObject json, AjaxStatus status) {
                            if (status.getCode() == 500) {
                                EventBus.getDefault().post(new ErrorData(TYPE_ERROR_AUTH, TYPE_ERROR_AUTH));
                            } else {
                                Toast.makeText(mActivity, "주소가 변경되었습니다.", Toast.LENGTH_SHORT).show();
                                mActivity.finish();
                                EventBus.getDefault().post(new SuccessData(api_channels_id_update, json));
                                mProgress.hide();
                            }

                            dialog.cancel();
                        }
                    });
                } catch (JSONException e) {
                    Log.e(TAG, "Error " + e.toString());
                }
            }
        });

        mAlert.setNegativeButton(R.string.exit, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mSelectedMarker.remove();
                dialog.cancel();
            }
        });

        mAlert.setTitle(R.string.spot_update_address_confirm);
        mAlert.setMessage(Helper.getFullAddress(mSelectedChannel));
        mAlert.show();
    }
}
