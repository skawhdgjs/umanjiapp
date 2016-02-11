package com.umanji.umanjiapp.ui.page.map;

import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.umanji.umanjiapp.helper.CommonHelper;
import com.umanji.umanjiapp.model.ChannelData;
import com.umanji.umanjiapp.model.ErrorData;
import com.umanji.umanjiapp.model.SuccessData;
import com.umanji.umanjiapp.ui.base.BaseFragment;

import org.json.JSONException;
import org.json.JSONObject;

import de.greenrobot.event.EventBus;

public class MapFragment extends BaseFragment {
    private static final String TAG = "MapFragment";


    /****************************************************
     *  View
     ****************************************************/
    private GoogleMap mMap;
    LatLng mLatLng;


    public static MapFragment newInstance(Bundle bundle) {
        MapFragment fragment = new MapFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_map, container, false);
        super.onCreateView(view);

        initMap();

        return view;
    }

    @Override
    public void updateView() {
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void loadData() {

    }

    @Override
    public void onEvent(SuccessData event) {

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
                        .zoom(15)
                        .bearing(90)
                        .tilt(40)
                        .build();

                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                CommonHelper.addMarkerToMap(mMap, mChannel, 0, true);

            }catch (SecurityException e) {
                Log.e("SecurityException", "SecurityException 에러발생:" + e.toString());
            }

            mMap.moveCamera(CameraUpdateFactory.newLatLng(mLatLng));
            mMap.animateCamera(CameraUpdateFactory.zoomTo(18), 2000, null);

        }


        initMapEvents();
    }

    private void initMapEvents() {

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

                    mProgress.show();
                    mApiHelper.call(api_channels_getByPoint, params, new AjaxCallback<JSONObject>() {
                        @Override
                        public void callback(String url, JSONObject json, AjaxStatus status) {
                            if (status.getCode() == 500) {
                                EventBus.getDefault().post(new ErrorData(TYPE_ERROR_AUTH, TYPE_ERROR_AUTH));
                            } else {
                                try {
                                    ChannelData channelData = new ChannelData(json);

                                    mChannel.setAddressJSONObject(channelData);
                                    JSONObject params = mChannel.getAddressJSONObject();
                                    params.put("id", mChannel.getId());

                                    mApiHelper.call(api_channels_spots_update, params, new AjaxCallback<JSONObject>() {
                                        @Override
                                        public void callback(String url, JSONObject json, AjaxStatus status) {
                                            if (status.getCode() == 500) {
                                                EventBus.getDefault().post(new ErrorData(TYPE_ERROR_AUTH, TYPE_ERROR_AUTH));
                                            } else {
                                                try {
                                                    ChannelData channelData = new ChannelData(json);

                                                    mChannel.setAddressJSONObject(channelData);
                                                    JSONObject params = mChannel.getAddressJSONObject();
                                                    params.put("id", mChannel.getId());

                                                    mApiHelper.call(api_channels_spots_update, params, new AjaxCallback<JSONObject>() {
                                                        @Override
                                                        public void callback(String url, JSONObject json, AjaxStatus status) {
                                                            if (status.getCode() == 500) {
                                                                EventBus.getDefault().post(new ErrorData(TYPE_ERROR_AUTH, TYPE_ERROR_AUTH));
                                                            } else {
                                                                Toast.makeText(mActivity, "주소가 변경되었습니다.", Toast.LENGTH_SHORT).show();
                                                                mActivity.finish();
                                                            }
                                                        }
                                                    });

                                                    mProgress.hide();

                                                }catch (JSONException e) {
                                                    Log.e(TAG, "Error " + e.toString());
                                                }

                                            }
                                        }
                                    });

                                }catch (JSONException e) {
                                    Log.e(TAG, "Error " + e.toString());
                                }

                            }
                        }
                    });

                } catch(JSONException e) {
                    Log.e(TAG, "error " + e.toString());
                }
            }
        });
    }
}
