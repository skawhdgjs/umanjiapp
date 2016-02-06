package com.umanji.umanjiapp.ui.page.map;

import android.app.Dialog;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.umanji.umanjiapp.R;
import com.umanji.umanjiapp.helper.AuthHelper;
import com.umanji.umanjiapp.helper.CommonHelper;
import com.umanji.umanjiapp.model.SuccessData;
import com.umanji.umanjiapp.ui.base.BaseFragment;

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
                        .zoom(10)
                        .bearing(90)
                        .tilt(40)
                        .build();

                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                CommonHelper.addMarkerToMap(mMap, mChannel, 0);

            }catch (SecurityException e) {
                Log.e("SecurityException", "SecurityException 에러발생:" + e.toString());
            }

            mMap.moveCamera(CameraUpdateFactory.newLatLng(mLatLng));
            mMap.animateCamera(CameraUpdateFactory.zoomTo(10), 2000, null);

        }


//        initMapEvents();
    }
}
