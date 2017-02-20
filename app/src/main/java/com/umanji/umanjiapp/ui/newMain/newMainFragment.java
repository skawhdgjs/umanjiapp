package com.umanji.umanjiapp.ui.newMain;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.umanji.umanjiapp.R;
import com.umanji.umanjiapp.model.ChannelData;
import com.umanji.umanjiapp.ui.BaseFragment;
import com.umanji.umanjiapp.ui.main.MainFragment;
import com.umanji.umanjiapp.ui.setting.home.HomeFragment;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by nam on 2017. 2. 20..
 */

public class newMainFragment extends BaseFragment implements SeekBar.OnSeekBarChangeListener {

    private SeekBar seekbar;
    private static final String TAG = "newMainActivity";
    private ChannelData mAddressChannel;

    private FragmentManager fm;
    private FragmentTransaction ft;
    private Fragment fr;

    private int zoom;
    private TextView mCurrentAddress;


    private String isInitLocationUsed = "false";
    private LatLng mCurrentMyPosition;


    private final int SIDO_ZOOM = 10;
    private final int GOGOUN_ZOOM = 12;
    private final int DONGMUN_ZOOM = 17;
    private final int NATION_ZOOM = 8;
    private final int WORLD_ZOOM = 4;

    public static newMainFragment newInstance(Bundle bundle) {
        newMainFragment fragment = new newMainFragment();
        fragment.setArguments(bundle);
        return fragment;
    }


    @Override
    public void initWidgets(View view) {
        seekbar = (SeekBar) view.findViewById(R.id.seekBar1);

        seekbar=(SeekBar)view.findViewById(R.id.seekBar1);
        seekbar.setOnSeekBarChangeListener(this);
        seekbar.setRotation(180);
        seekbar.setProgress(0);


        mCurrentAddress = (TextView) view.findViewById(R.id.address_newMain);

        myLocation();

        //default
        setAdressWithZoom(SIDO_ZOOM);




        fm = getFragmentManager();
        ft = fm.beginTransaction();
        ft.add(R.id.fragment_writeInfo, writeInfoFragment.newInstance(getActivity().getIntent().getBundleExtra("bundle")));
        ft.add(R.id.fragment_mainlist, sidoFragment.newInstance(getActivity().getIntent().getBundleExtra("bundle")));
        ft.commit();
    }


    private void setAdressWithZoom(int zoomInt){

        this.zoom = zoomInt;

        try {
            //LatLng center = mMap.getCameraPosition().target;
            JSONObject params = new JSONObject();
            params.put("latitude", mCurrentMyPosition.latitude);
            params.put("longitude", mCurrentMyPosition.longitude);

            mApi.call(api_channels_getByPoint, params, new AjaxCallback<JSONObject>() {
                @Override
                public void callback(String url, JSONObject object, AjaxStatus status) {
                    mAddressChannel = new ChannelData(object);

                    String getCenterAddress = mAddressChannel.getAddress();
                    String countryName = "바다";
                    String adminArea = null;
                    String localityName = null;
                    String thoroughfare = null;

                    if (getCenterAddress != null){
                        if(mAddressChannel.getCountryCode().length() > 1){
                            countryName = mAddressChannel.getCountryName();
                            adminArea = mAddressChannel.getAdminArea();
                            localityName = mAddressChannel.getLocality();
                            thoroughfare = mAddressChannel.getThoroughfare();
                        } else {
                            countryName = "";
                            adminArea = "바다";
                            localityName = "";
                            thoroughfare = "";
                        }

                    }

                    if (zoom >= 2 && zoom < 8) {          // 대한민국
                        if (countryName.length() > 1){
                            mCurrentAddress.setText(countryName);
                        } else {
                            mCurrentAddress.setText("바다");
                        }

                        //currentAddress = countryName;
                    } else if (zoom == 8) {     // 도
                        mCurrentAddress.setText(countryName + " " + adminArea);
                        //currentAddress = countryName + " " + adminArea + " 지역정보";
                    } else if (zoom > 8 && zoom < 11) {     // 서울시 , 시
                        mCurrentAddress.setText(adminArea + " " + localityName);
                        //currentAddress = adminArea + " " + localityName + " 지역정보";
                    } else if (zoom > 10 && zoom < 14) {   // 구
                        mCurrentAddress.setText(adminArea + " " + localityName);
                        //currentAddress = adminArea + " " + localityName + " 지역정보";
                    } else if (zoom > 13 && zoom <= 21) {  // 동
                        mCurrentAddress.setText(adminArea + " " + localityName + " " + thoroughfare);
                        //currentAddress = adminArea + " " + localityName + " " + thoroughfare + " 지역정보";
                    } else {
                        mCurrentAddress.setText("전세계 언어 주소");
                    }

                    Log.d(TAG,"currentAdress: "+mCurrentAddress.getText());
                }
            });

        } catch (JSONException e) {
            Log.e(TAG, "error " + e.toString());
        }
    }

    @Override
    public void loadData() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public View getView(LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.activity_new_main, container, false);
    }

    @Override
    public void updateView() {

    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if(progress >= 0 && progress < 20){
            seekBar.setProgress(0);
            setAdressWithZoom(DONGMUN_ZOOM);
            ft = fm.beginTransaction();
            ft.replace(R.id.fragment_mainlist, mundongFragment.newInstance(getActivity().getIntent().getBundleExtra("bundle")));
            ft.commit();
        } else if(progress >= 20 && progress < 30){
            seekBar.setProgress(25);
            setAdressWithZoom(GOGOUN_ZOOM);
            ft = fm.beginTransaction();
            ft.replace(R.id.fragment_mainlist, gogunFragment.newInstance(getActivity().getIntent().getBundleExtra("bundle")));
            ft.commit();
        } else if(progress >= 30 && progress < 60){
            seekBar.setProgress(50);
            setAdressWithZoom(SIDO_ZOOM);
            ft = fm.beginTransaction();
            ft.replace(R.id.fragment_mainlist, sidoFragment.newInstance(getActivity().getIntent().getBundleExtra("bundle")));
            ft.commit();
        } else if(progress >= 60 && progress < 80){
            seekBar.setProgress(75);
            setAdressWithZoom(NATION_ZOOM);
            ft = fm.beginTransaction();
            ft.replace(R.id.fragment_mainlist, nationFragment.newInstance(getActivity().getIntent().getBundleExtra("bundle")));
            ft.commit();
        } else if(progress >= 80 && progress <= 100){
            seekBar.setProgress(100);
            setAdressWithZoom(WORLD_ZOOM);
            ft = fm.beginTransaction();
            ft.replace(R.id.fragment_mainlist, worldFragment.newInstance(getActivity().getIntent().getBundleExtra("bundle")));
            ft.commit();
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        String position = "";
        if(seekBar.getProgress() == 0){
            position = "면동";
        } else if(seekBar.getProgress() == 25){
            position = "구군";
        } else if(seekBar.getProgress() == 50){
            position = "시도";
        } else if(seekBar.getProgress() == 75){
            position = "전국";
        } else if(seekBar.getProgress() == 100){
            position = "세계";
        }
        Toast.makeText(getActivity().getApplicationContext(),"seekbar touch stopped at : " + position, Toast.LENGTH_SHORT).show();
    }


    private void myLocation() {

        LocationManager locationManager = (LocationManager) mActivity.getSystemService(mActivity.LOCATION_SERVICE);
        LocationListener locationListener = new MyLocationListener();
        Criteria criteria = new Criteria();

        String provider = locationManager.getBestProvider(criteria, true);
        CameraPosition cameraPosition;
        Location location = null;

        if (ActivityCompat.checkSelfPermission(mActivity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mActivity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 35000, 10, locationListener);

        ChannelData homeChannel = null;

        double latitude = 37.5053403;
        double longitude = 126.9589435;  // 초등 37.5053403, 126.9589435  / 보라매공원 : 37.498039, 126.9220201
        LatLng latLng = null;
        if (isInitLocationUsed.equals("true")) {
            int intiLevel = 18;

            if (getArguments() != null) {
                String jsonString = getArguments().getString("channel");
                if (jsonString != null) {                // isInitLocationUsed = true :: MUST create Bundle from another activity.
                    homeChannel = new ChannelData(jsonString);
                    latitude = homeChannel.getLatitude();
                    longitude = homeChannel.getLongitude();
                    latLng = new LatLng(latitude, longitude);
                } else {
                    latitude = location.getLatitude();
                    longitude = location.getLongitude();
                    latLng = new LatLng(latitude, longitude);
                }

            } else {
                latLng = new LatLng(latitude, longitude);
                intiLevel = 10;
                Toast.makeText(mActivity, "Location Null", Toast.LENGTH_SHORT).show();
            }
            mCurrentMyPosition = new LatLng(latitude, longitude);


        } else {                   // init My Location for the first Time!!
            try {
                location = locationManager.getLastKnownLocation(provider);

                if (location != null) {
                    latitude = location.getLatitude();
                    longitude = location.getLongitude();
                    mCurrentMyPosition = new LatLng(latitude, longitude);
                } else {
                    mCurrentMyPosition = new LatLng(latitude, longitude);
                }

            } catch (SecurityException e) {
                Log.e("SecurityException", "SecurityException 에러발생:" + e.toString());
            }
        }
    }

    private final class MyLocationListener implements LocationListener {

        @Override
        public void onLocationChanged(Location locFromGps) {
            // called when the listener is notified with a location update from the GPS
        }

        @Override
        public void onProviderDisabled(String provider) {
            // called when the GPS provider is turned off (user turning off the GPS on the phone)
        }

        @Override
        public void onProviderEnabled(String provider) {
            // called when the GPS provider is turned on (user turning on the GPS on the phone)
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            // called when the status of the GPS provider changes
        }
    }
}
