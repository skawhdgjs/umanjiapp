package com.umanji.umanjiapp.ui.main;


import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.bumptech.glide.Glide;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.makeramen.roundedimageview.RoundedImageView;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;
import com.umanji.umanjiapp.R;
import com.umanji.umanjiapp.helper.AuthHelper;
import com.umanji.umanjiapp.helper.Helper;
import com.umanji.umanjiapp.model.AuthData;
import com.umanji.umanjiapp.model.ChannelData;
import com.umanji.umanjiapp.model.ErrorData;
import com.umanji.umanjiapp.model.SuccessData;
import com.umanji.umanjiapp.ui.BaseFragment;
import com.umanji.umanjiapp.ui.channel._fragment.posts.PostListAdapter;
import com.umanji.umanjiapp.ui.channel.complex.ComplexActivity;
import com.umanji.umanjiapp.ui.channel.profile.ProfileActivity;
import com.umanji.umanjiapp.ui.channel.spot.SpotActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import de.greenrobot.event.EventBus;

public class MainFragment extends BaseFragment {
    private static final String TAG      = "MainFragment";

    /****************************************************
     *  View
     ****************************************************/
    private View mNoticePanel;

    private GoogleMap mMap;

    private PostListAdapter mAdapter;

    private SlidingUpPanelLayout mSlidingUpPanelLayout;
    private LinearLayout mHeaderPanel;
    private RoundedImageView mAvatarImageBtn;
    private Button mNotyCountBtn;

    private TextView mSignBtn;
    private Button mZoomBtn;

    private AlertDialog.Builder mAlert;


    /****************************************************
     *  Map
     ****************************************************/
    LatLng mCurrentMyPosition;

    /****************************************************
     *  Etc
     ****************************************************/
    private ChannelData         mUser;
    private JSONArray           mMarkers;
    private ChannelData         mCurrentChannel;
    private ArrayList<ChannelData> mPosts;


    private boolean             isBlock = false;
    private boolean             isLoading = false;
    private int                 mPreFocusedItem = 0;

    private LatLng              mLatLngByPoint = new LatLng(37.491361, 126.923978);
    private ChannelData         mChannelByPoint;
    private Marker              mMarkerByPoint;
    private Marker              mMarkerByPost;
    private LatLng              mPointByPost;



    public static MainFragment newInstance(Bundle bundle) {
        MainFragment fragment = new MainFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);

        initMainListView(view);

        if(AuthHelper.isLogin(mActivity)) {
            loginByToken();
        } else {
            updateView();
        }

        initWidgets(view);
        initMap();



        return view;
    }

    @Override
    public View getView(LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.activity_main, container, false);
    }

    @Override
    public void initWidgets(View view) {
        mNoticePanel = view.findViewById(R.id.noticePanel);

        mSlidingUpPanelLayout = (SlidingUpPanelLayout) view.findViewById(R.id.slidingUpPanelLayout);
        mSlidingUpPanelLayout.setPanelHeight(Helper.dpToPixel(mActivity, 200));
        mSlidingUpPanelLayout.setAnchorPoint(0.7f);
        mSlidingUpPanelLayout.setMinFlingVelocity(DEFAULT_MIN_FLING_VELOCITY);


        mHeaderPanel= (LinearLayout) view.findViewById(R.id.headerPanel);
        mHeaderPanel.setOnClickListener(this);


        mSignBtn = (TextView) view.findViewById(R.id.mSignBtn);
        mSignBtn.setOnClickListener(this);


        mZoomBtn = (Button) view.findViewById(R.id.mZoomBtn);
        mZoomBtn.setOnClickListener(this);

        mAvatarImageBtn = (RoundedImageView) view.findViewById(R.id.mAvatarImageBtn);
        mAvatarImageBtn.setOnClickListener(this);

        mNotyCountBtn = (Button) view.findViewById(R.id.mNotyCount);
        mNotyCountBtn.setOnClickListener(this);
        mNotyCountBtn.setText("0");

        mAlert = new AlertDialog.Builder(mActivity);
    }

    @Override
    public void loadData() {
        if(AuthHelper.isLogin(mActivity)) {
            loadNewNoties();
        }

        loadMainMarkers();
        loadMainPosts();
    }

    public void loadMoreData() {

    }

    @Override
    public void updateView() {

        if(AuthHelper.isLogin(mActivity)) {
            mSignBtn.setVisibility(View.GONE);
            mAvatarImageBtn.setVisibility(View.VISIBLE);
            String userPhoto = mUser.getPhoto();
            if(!TextUtils.isEmpty(userPhoto)) {
                Glide.with(mActivity)
                        .load(userPhoto)
                        .placeholder(R.drawable.empty)
                        .animate(R.anim.abc_fade_in)
                        .override(40, 40)
                        .into(mAvatarImageBtn);
            }else {
                Glide.with(mActivity)
                        .load(R.drawable.avatar_default_0)
                        .placeholder(R.drawable.empty)
                        .animate(R.anim.abc_fade_in)
                        .override(40, 40)
                        .into(mAvatarImageBtn);
            }
        }else {
            mSignBtn.setVisibility(View.VISIBLE);
            mAvatarImageBtn.setVisibility(View.GONE);
        }
    }

    @Override
    public void onEvent(SuccessData event) {


        switch (event.type) {
            case api_token_check:
            case api_signin:
            case api_signup:
                AuthData auth = new AuthData(event.response);
                if (auth != null && auth.getToken() != null) login(auth);
                else logout();
                break;

            case api_logout:
                logout();
                break;
            case api_channels_createComplex:
            case api_channels_createSpot:
            case api_channels_id_update:
                loadData();
                break;
            case api_profile_id_update:
                mUser = new ChannelData(event.response);
                updateView();
                break;
            case api_noites_read:
                mNotyCountBtn.setVisibility(View.GONE);
                mNotyCountBtn.setText("0");
                break;

            case api_channels_id_vote:
            case api_channels_id_like:
                Helper.showNoticePanel(mActivity, mNoticePanel, POINT_DEFAULT + " 포인트 증가");
                break;

            case api_channels_id_unLike:
                Helper.showNoticePanel(mActivity, mNoticePanel, POINT_DEFAULT + " 포인트 감소");
                break;

            case EVENT_LOOK_AROUND:
                ChannelData channelData = new ChannelData(event.response);
                LatLng latLng = new LatLng(channelData.getLatitude(), channelData.getLongitude());
                mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                mMap.animateCamera(CameraUpdateFactory.zoomTo(18), 2000, null);
                break;
        }
    }

    public void onEvent(ErrorData event){

        switch (event.type) {
            case TYPE_ERROR_AUTH:
                Helper.startSignupActivity(mActivity, mCurrentMyPosition);
                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.mSignBtn:
                if (AuthHelper.isLogin(mActivity)) {
                    logout();
                } else {
                    Helper.startSignupActivity(mActivity, mCurrentMyPosition);
                }
                break;

            case R.id.mZoomBtn:
                if(mZoomBtn.getText().equals(ZOOM_IN)) {
                    mMap.animateCamera(CameraUpdateFactory.zoomTo(18), 1000, null);
                }else {
                    mMap.animateCamera(CameraUpdateFactory.zoomTo(10), 1000, null);
                }
                break;

            case R.id.mNotyCount:
            case R.id.mAvatarImageBtn:

                Intent intent = new Intent(mActivity, ProfileActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("channel", mUser.getJsonObject().toString());
                bundle.putInt("newNoticeCount", getNewNoticeCount());
                intent.putExtra("bundle", bundle);
                startActivity(intent);
                break;
        }
    }


    private void initMap() {
        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(mActivity.getBaseContext());

        if(status!= ConnectionResult.SUCCESS){

            int requestCode = 10;
            Dialog dialog = GooglePlayServicesUtil.getErrorDialog(status, mActivity, requestCode);
            dialog.show();

        }else {
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

            double latitude     = 37.491361;
            double longitude    = 126.923978;

            try {
                Location location = locationManager.getLastKnownLocation(provider);

                if(location != null) {

                    latitude = location.getLatitude();
                    longitude = location.getLongitude();

                    mCurrentMyPosition = new LatLng(latitude, longitude);

                    CameraPosition cameraPosition = new CameraPosition.Builder()
                            .target(mCurrentMyPosition)
                            .zoom(10)
                            .bearing(90)
                            .tilt(40)
                            .build();
                    mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                }
            }catch (SecurityException e) {
                Log.e("SecurityException", "SecurityException 에러발생:" + e.toString());
            }

            LatLng latLng = new LatLng(latitude, longitude);
            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            mMap.animateCamera(CameraUpdateFactory.zoomTo(10), 2000, null);
        }


        initMapEvents();
    }


    /****************************************************
     *  init Map Events
     ****************************************************/

    protected void initMapEvents() {

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng point) {

                if(mMarkerByPost != null) {
                    mMarkerByPost.remove();
                }
                if(mChannelByPoint != null) {
                    mChannelByPoint = null;
                }

                mLatLngByPoint = point;
                final int zoom = (int) mMap.getCameraPosition().zoom;

                if (zoom >= 15 && zoom <= 21) {

                    try {
                        JSONObject params = new JSONObject();
                        params.put("latitude", mLatLngByPoint.latitude);
                        params.put("longitude", mLatLngByPoint.longitude);

                        mApi.call(api_channels_getByPoint, params, new AjaxCallback<JSONObject>() {
                            @Override
                            public void callback(String url, JSONObject object, AjaxStatus status) {
                                mChannelByPoint = new ChannelData(object);

                                if(TextUtils.isEmpty(mChannelByPoint.getId())) {
                                    if(AuthHelper.isLogin(mActivity)) {
                                        isBlock = true;

                                        mMarkerByPoint = Helper.addNewMarkerToMap(mMap, mChannelByPoint);
                                        LatLng tmpPoint = Helper.getAdjustedPoint(mMap, mLatLngByPoint);

                                        mMap.animateCamera(CameraUpdateFactory.newLatLng(tmpPoint), 100, null);

                                        if(isComplexCreatable(zoom)) {
                                            showCreateComplexDialog();
                                        }else if(isSpotCreatable(zoom)) {
                                            showCreateSpotDialog();
                                        }

                                    } else {
                                        Helper.startSignupActivity(mActivity, mCurrentMyPosition);
                                    }

                                }else {
                                    if(isComplexCreatable(zoom)) {
                                        startSpotActivity(mChannelByPoint, TYPE_COMPLEX);
                                    }else if(isSpotCreatable(zoom)) {
                                        startSpotActivity(mChannelByPoint, TYPE_SPOT);
                                    }

                                }
                            }
                        });
                    } catch(JSONException e) {
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

                return true;
            }
        });

        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                String index = marker.getSnippet();

                ChannelData channelData;
                try {
                    if (TextUtils.equals(index, String.valueOf(POST_MARKER_INDEX))) {
                        channelData = mCurrentChannel.getParent();
                    } else {
                        channelData = new ChannelData(mMarkers.getJSONObject(Integer.valueOf(index)));
                    }

                    Helper.startActivity(mActivity, channelData);

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
                if (isBlock) {
                    isBlock = false;
                } else {
                    TextView zoomLevelText = (TextView) mActivity.findViewById(R.id.mZoomLevelText);
                    TextView createSpotText = (TextView) mActivity.findViewById(R.id.mCreateSpotText);
                    TextView createComplexText = (TextView) mActivity.findViewById(R.id.mCreateComplexText);
                    Button zoomBtn = (Button) mActivity.findViewById(R.id.mZoomBtn);

                    zoomLevelText.setText("Zoom: " + (int) position.zoom);

                    int zoom = (int) position.zoom;
                    if (isComplexCreatable(zoom)) {
                        createComplexText.setVisibility(View.VISIBLE);
                        createSpotText.setVisibility(View.GONE);
                        zoomBtn.setText(ZOOM_IN);
                    } else if (isSpotCreatable(zoom)) {
                        createComplexText.setVisibility(View.GONE);
                        createSpotText.setVisibility(View.VISIBLE);
                        zoomBtn.setText(ZOOM_OUT);
                    } else {
                        createComplexText.setVisibility(View.GONE);
                        createSpotText.setVisibility(View.GONE);
                        zoomBtn.setText(ZOOM_IN);
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

    private void loadMainPosts() {
        mAdapter.resetDocs();
        mAdapter.setCurrentPage(0);

        loadMoreMainPosts();


        Handler mHandler = new Handler();
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                loadMoreMainPosts();

                Handler mHandler = new Handler();
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        loadMoreMainPosts();
                    }
                }, 500);
            }
        }, 500);
    }

    private void loadMoreMainPosts() {
        isLoading = true;

        try {
            JSONObject params = Helper.getZoomMinMaxLatLngParams(mMap);
            params.put("page", mAdapter.getCurrentPage());
            params.put("sort", "point DESC");

            mApi.call(api_main_findPosts, params, new AjaxCallback<JSONObject>() {
                @Override
                public void callback(String url, JSONObject object, AjaxStatus status) {
                    if(status.getCode() == 500) {
                        EventBus.getDefault().post(new ErrorData(TYPE_ERROR_AUTH, TYPE_ERROR_AUTH));
                    }else {
                        try {
                            JSONArray jsonArray = object.getJSONArray("data");
                            for(int idx = 0; idx < jsonArray.length(); idx++) {
                                JSONObject jsonDoc = jsonArray.getJSONObject(idx);
                                ChannelData doc = new ChannelData(jsonDoc);
                                mAdapter.addBottom(doc);
                            }

                            isLoading = false;
                            mAdapter.notifyDataSetChanged();
                        } catch (JSONException e) {
                            Log.e(TAG, "Error " + e.toString());
                        }
                    }
                }
            });
        }catch (JSONException e) {
            Log.e(TAG, "Error " + e.toString());
        }

        mAdapter.setCurrentPage(mAdapter.getCurrentPage() + 1);
    }

    private void loginByToken() {
        try {
            JSONObject params = new JSONObject();
            params.put("access_token", AuthHelper.getToken(mActivity));
            mApi.call(api_token_check, params, new AjaxCallback<JSONObject>() {
                @Override
                public void callback(String url, JSONObject object, AjaxStatus status) {
                    AuthData auth = new AuthData(object);
                    if (auth != null && auth.getToken() != null) login(auth);
                    else logout();
                }
            });
        }catch (JSONException e) {
            Log.e(TAG, "error " + e.toString());
        }

    }

    private void login(AuthData auth) {
//        if (checkPlayServices()) {
//            Intent intent = new Intent(mActivity, GcmRegistrationIntentService.class);
//            mActivity.startService(intent);
//        }

        mUser = auth.getUser();
        AuthHelper.login(mActivity, auth);

        updateView();
    }

    private void logout() {
        mUser = null;
        AuthHelper.logout(mActivity);

        updateView();
    }

    private static boolean isComplexCreatable(int zoom ) {
        if(zoom >= 14 && zoom <= 17) {
            return true;
        }else {
            return false;
        }
    }

    private static boolean isSpotCreatable(int zoom ) {
        if(zoom >= 18) {
            return true;
        }else {
            return false;
        }
    }



    private void loadMainMarkers() {
        try {
            JSONObject params = Helper.getZoomMinMaxLatLngParams(mMap);
            params.put("zoom", (int) mMap.getCameraPosition().zoom);
            params.put("limit", 100);
            params.put("sort", "point DESC");
            mApi.call(api_main_findMarkers, params, new AjaxCallback<JSONObject>() {
                @Override
                public void callback(String url, JSONObject json, AjaxStatus status) {
                    addChannelsToMap(json);
                }
            });
        }catch (JSONException e) {
            Log.e(TAG, "Error " + e.toString());
        }

    }


    private void addChannelsToMap(JSONObject jsonObject) {
        try {
            mMap.clear();

            mMarkers = jsonObject.getJSONArray("data");

            int idx = 0;

            if(mCurrentChannel != null) {
                if(Helper.isInVisibleResion(mMap, new LatLng(mCurrentChannel.getLatitude(), mCurrentChannel.getLongitude()))) {
                    mMarkerByPost = Helper.addMarkerToMap(mMap, mCurrentChannel, POST_MARKER_INDEX);
                }else {
                    mCurrentChannel = null;
                }
            }

            if(mMarkers != null) {
                for(; idx < mMarkers.length() ; idx ++ ) {
                    ChannelData channelData = new ChannelData(mMarkers.getJSONObject(idx));
                    Helper.addMarkerToMap(mMap, channelData, idx);
                }
            }

        }catch(JSONException e) {
            Log.e(TAG, "error " + e.toString());
        }

    }

    private void addChannelsToList(JSONObject jsonObject) {
//        try {
//            JSONArray jsonArray = jsonObject.getJSONArray("data");
//            for(int idx = 0; idx < jsonArray.length(); idx++) {
//                JSONObject jsonDoc = jsonArray.getJSONObject(idx);
//                ChannelData doc = new ChannelData(jsonDoc);
//
//                mMainListAdapter.addBottom(doc);
//            }
//
//            Log.d(TAG, "notifyDataSetChanged");
//            mMainListAdapter.notifyDataSetChanged();
//
//        }catch(JSONException e) {
//            Log.e(TAG, "error " + e.toString());
//        }

    }


    private void showCreateComplexDialog() {
        mAlert.setPositiveButton(R.string.complex_create_btn, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try {
                    JSONObject params = mChannelByPoint.getAddressJSONObject();
                    params.put("type", TYPE_COMPLEX);
                    params.put("level", LEVEL_COMPLEX);
                    mApi.call(api_channels_createComplex, params, new AjaxCallback<JSONObject>() {
                        @Override
                        public void callback(String url, JSONObject object, AjaxStatus status) {
                            mChannelByPoint = new ChannelData(object);
                            if (mMarkerByPoint != null) mMarkerByPoint.remove();
                            startSpotActivity(mChannelByPoint, TYPE_COMPLEX);

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

        mAlert.setTitle(R.string.complex_create_confirm);
        mAlert.setMessage(Helper.getFullAddress(mChannelByPoint));
        mAlert.show();
    }

    private void showCreateSpotDialog() {
        mAlert.setPositiveButton(R.string.spot_create_btn, new DialogInterface.OnClickListener() {
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
                            startSpotActivity(mChannelByPoint, TYPE_SPOT);

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

        mAlert.setTitle(R.string.spot_create_confirm);
        mAlert.setMessage(Helper.getFullAddress(mChannelByPoint));
        mAlert.show();
    }

    private void startSpotActivity(ChannelData channel, String type) {
        Intent intent = null;

        switch (type) {
            case TYPE_SPOT:
                intent = new Intent(getActivity(), SpotActivity.class);
                break;
            case TYPE_COMPLEX:
                intent = new Intent(getActivity(), ComplexActivity.class);
                break;
            default:
                intent = new Intent(getActivity(), SpotActivity.class);
                break;
        }

        Bundle bundle = new Bundle();
        bundle.putString("channel", channel.getJsonObject().toString());
        intent.putExtra("bundle", bundle);
        intent.putExtra("enterAnim", R.anim.zoom_out);
        intent.putExtra("exitAnim", R.anim.zoom_in);

        startActivity(intent);
    }

    protected void addOnScrollListener(RecyclerView rView) {
        final LinearLayoutManager mLayoutManager = new LinearLayoutManager(rView.getContext());
        rView.setLayoutManager(mLayoutManager);

        rView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0) {
                    int visibleItemCount = mLayoutManager.getChildCount();
                    int totalItemCount = mLayoutManager.getItemCount();
                    int pastVisiblesItems = mLayoutManager.findFirstVisibleItemPosition();

                    ArrayList<ChannelData> channels = mAdapter.getDocs();

                    int currentFocusedIndex = visibleItemCount + pastVisiblesItems;
                    if (currentFocusedIndex == mPreFocusedItem) return;
                    mPreFocusedItem = currentFocusedIndex;
                    if (channels.size() <= mPreFocusedItem) return;

                    ChannelData channelData = channels.get(pastVisiblesItems);

                    if (mCurrentChannel == null || (!TextUtils.equals(channelData.getId(), mCurrentChannel.getId()))) {
                        isBlock = true;

                        mCurrentChannel = channels.get(pastVisiblesItems);
                        mPointByPost = new LatLng(mCurrentChannel.getLatitude(), mCurrentChannel.getLongitude());

                        if (mMarkerByPost != null) {
                            mMarkerByPost.remove();
                        }

                        mMap.animateCamera(CameraUpdateFactory.newLatLng(mPointByPost), 500, null);
                        mMarkerByPost = Helper.addMarkerToMap(mMap, mCurrentChannel, POST_MARKER_INDEX);
                    }

                    if (!isLoading) {
                        if (mPreFocusedItem >= (totalItemCount - 3)) {
                            loadMoreMainPosts();
                        }
                    }
                }
            }
        });

    }


    protected RecyclerView initMainListView(View view) {
        RecyclerView rView = (RecyclerView) view.findViewById(R.id.recyclerView);
        rView.setItemViewCacheSize(iItemViewCacheSize);
        mAdapter = new PostListAdapter(mActivity, this);
        rView.setAdapter(mAdapter);

        addOnScrollListener(rView);
        return rView;
    }

    private void loadNewNoties() {
        try {
            JSONObject params = new JSONObject();
            params.put("read", false);
            mApi.call(api_noites_new_count, params, new AjaxCallback<JSONObject>(){
                @Override
                public void callback(String url, JSONObject object, AjaxStatus status) {
                    super.callback(url, object, status);
                    int notyCount = object.optInt("data");
                    if(notyCount > 0) {
                        mNotyCountBtn.setVisibility(View.VISIBLE);
                        mNotyCountBtn.setText(String.valueOf(notyCount));
                    }else {
                        mNotyCountBtn.setVisibility(View.GONE);
                        mNotyCountBtn.setText("0");
                    }
                }
            });
        }catch (JSONException e) {
            Log.e(TAG, "Error " + e.toString());
        }
    }

    private int getNewNoticeCount() {
        return Integer.parseInt(mNotyCountBtn.getText().toString());
    }
}
