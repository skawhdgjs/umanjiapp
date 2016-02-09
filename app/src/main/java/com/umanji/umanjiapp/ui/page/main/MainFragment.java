package com.umanji.umanjiapp.ui.page.main;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
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

import com.bumptech.glide.Glide;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.makeramen.roundedimageview.RoundedImageView;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;
import com.umanji.umanjiapp.R;
import com.umanji.umanjiapp.gcm.GcmRegistrationIntentService;
import com.umanji.umanjiapp.helper.AuthHelper;
import com.umanji.umanjiapp.helper.CommonHelper;
import com.umanji.umanjiapp.helper.JsonHelper;
import com.umanji.umanjiapp.helper.UiHelper;
import com.umanji.umanjiapp.model.AuthData;
import com.umanji.umanjiapp.model.ChannelData;
import com.umanji.umanjiapp.model.ErrorData;
import com.umanji.umanjiapp.model.SuccessData;
import com.umanji.umanjiapp.ui.base.BaseFragment;
import com.umanji.umanjiapp.ui.fragment.posts.PostListAdapter;
import com.umanji.umanjiapp.ui.page.auth.SignupActivity;
import com.umanji.umanjiapp.ui.page.channel.community.CommunityActivity;
import com.umanji.umanjiapp.ui.page.channel.info.InfoActivity;
import com.umanji.umanjiapp.ui.page.channel.post.PostActivity;
import com.umanji.umanjiapp.ui.page.channel.profile.ProfileActivity;
import com.umanji.umanjiapp.ui.page.channel.spot.SpotActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MainFragment extends BaseFragment {
    private static final String TAG      = "MainFragment";

    private static final int DEFAULT_MIN_FLING_VELOCITY = 10000;
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    private final static String ZOOM_IN  = "ZOOM-IN";
    private final static String ZOOM_OUT = "ZOOM-OUT";


    /****************************************************
     *  Intent
     ****************************************************/
    private String mName;


    /****************************************************
     *  View
     ****************************************************/
    private GoogleMap               mMap;
    private PostListAdapter         mMainListAdapter;

    private SlidingUpPanelLayout    mSlidingUpPanelLayout;
    private LinearLayout            mHeaderPanel;
    private RoundedImageView        mAvatarImageBtn;
    private Button                  mNotyCountBtn;

    private TextView                mSignBtn;
    private Button                  mZoomBtn;


    private AlertDialog.Builder mAlert;




    /****************************************************
     *  For Etc.
     ****************************************************/
    private JSONArray           mMarkers;
    private ChannelData         mUser;
    private ChannelData         mCurrentChannel;
    private ArrayList<ChannelData> mPosts;

    private LatLng              mPoint;
    private Marker              mMarker;
    private boolean             isBlock = false;
    private boolean             isLoading = false;
    private int                 mPreFocusedItem = 0;

    private LatLng              mLatLng = new LatLng(37.491361, 126.923978);

    public static MainFragment newInstance(Bundle bundle) {
        MainFragment fragment = new MainFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(mChannel != null) {
            startActivityForPush(mChannel);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_main, container, false);
        super.onCreateView(view);

        initWidget(view);
        initMap();

        if(AuthHelper.isLogin(mContext)) {
            checkLoginToken();
        } else {
            updateView();
        }

        return view;
    }



    private void initWidget(View view) {
        RecyclerView rView = (RecyclerView) view.findViewById(R.id.mRecyclerView);

        final LinearLayoutManager mLayoutManager = new LinearLayoutManager(rView.getContext());
        rView.setLayoutManager(mLayoutManager);

        rView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {

                if (dy > 0) {
                    int visibleItemCount = mLayoutManager.getChildCount();
                    int totalItemCount = mLayoutManager.getItemCount();
                    int pastVisiblesItems = mLayoutManager.findFirstVisibleItemPosition();

                    ArrayList<ChannelData> channels = mMainListAdapter.getDocs();

                    int currentFocusedIndex = visibleItemCount + pastVisiblesItems;
                    if (currentFocusedIndex == mPreFocusedItem) return;
                    mPreFocusedItem = currentFocusedIndex;
                    if (channels.size() <= mPreFocusedItem) return;

                    ChannelData channelData = channels.get(pastVisiblesItems);

                    if (mCurrentChannel == null || (!TextUtils.equals(channelData.getId(), mCurrentChannel.getId()))) {
                        isBlock = true;

                        mCurrentChannel = channels.get(pastVisiblesItems);
                        mPoint = new LatLng(mCurrentChannel.getLatitude(), mCurrentChannel.getLongitude());

                        if (mMarker != null) {
                            mMarker.remove();
                        }

                        mMap.animateCamera(CameraUpdateFactory.newLatLng(mPoint), 500, null);

                        switch (mCurrentChannel.getLevel()) {
                            case LEVEL_DONG:
                                mMarker = mMap.addMarker(new MarkerOptions().position(mPoint)
                                        .title(mCurrentChannel.getName())
                                        .snippet(FOCUSED_ITEM_MARKER)
                                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_marker_blue))
                                        .anchor(0.45f, 1.0f));

                                break;
                            case LEVEL_GUGUN:
                                mMarker = mMap.addMarker(new MarkerOptions().position(mPoint)
                                        .title(mCurrentChannel.getName())
                                        .snippet(FOCUSED_ITEM_MARKER)
                                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_marker_yellow))
                                        .anchor(0.45f, 1.0f));

                                break;
                            case LEVEL_DOSI:
                                mMarker = mMap.addMarker(new MarkerOptions().position(mPoint)
                                        .title(mCurrentChannel.getName())
                                        .snippet(FOCUSED_ITEM_MARKER)
                                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_marker_red))
                                        .anchor(0.45f, 1.0f));
                                break;
                            default:
                                mMarker = mMap.addMarker(new MarkerOptions().position(mPoint)
                                        .title(mCurrentChannel.getName())
                                        .snippet(FOCUSED_ITEM_MARKER)
                                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_marker_aqua))
                                        .anchor(0.45f, 1.0f));
                                break;

                        }
                    }

                    if (!isLoading) {
                        Log.d(TAG, "mPreFocusedItem " + mPreFocusedItem);
                        Log.d(TAG, "totalItemCount " + totalItemCount);

                        if (mPreFocusedItem == (totalItemCount - 4)) {
                            Log.d(TAG, "isLoading = true");
                            isLoading = true;
                            mMainListAdapter.setCurrentPage(mMainListAdapter.getCurrentPage() + 1);
                            loadMoreMainPosts();
                        }
                    }
                }
            }
        });

        rView.setItemViewCacheSize(iItemViewCacheSize);

        mMainListAdapter = new PostListAdapter(getActivity(), this);
        mMainListAdapter.setType(TYPE_MAIN);
        rView.setAdapter(mMainListAdapter);

        mSlidingUpPanelLayout = (SlidingUpPanelLayout) view.findViewById(R.id.slidingUpPanelLayout);
        mSlidingUpPanelLayout.setPanelHeight(CommonHelper.dpToPixel(mActivity, 200));
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

        mAlert = new AlertDialog.Builder(mActivity);
    }
    @Override
    public void loadData() {
        if(AuthHelper.isLogin(mContext)) {
            loadNewNoties();
        }

        loadMainMarkers();
        loadMainPosts();
    }

    @Override
    public void updateView() {

        if(AuthHelper.isLogin(mContext)) {
            mSignBtn.setVisibility(View.GONE);
            mAvatarImageBtn.setVisibility(View.VISIBLE);
            String userPhoto = mUser.getPhoto();
            if(!TextUtils.isEmpty(userPhoto)) {
                Glide.with(mContext)
                        .load(userPhoto)
                        .placeholder(R.drawable.empty)
                        .animate(R.anim.abc_fade_in)
                        .override(40, 40)
                        .into(mAvatarImageBtn);
            }else {
                Glide.with(mContext)
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


    private void login(AuthData auth) {
        if (checkPlayServices()) {
            Intent intent = new Intent(mActivity, GcmRegistrationIntentService.class);
            mActivity.startService(intent);
        }

        mUser = auth.getUser();
        AuthHelper.login(mContext, auth);

        updateView();
    }

    private void logout() {
        mUser = null;
        AuthHelper.logout(mContext);

        updateView();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.mSignBtn:
                if(AuthHelper.isLogin(mContext)) {
                    logout();
                } else {



                    Intent intent = new Intent(getActivity(), SignupActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putDouble("latitude", mLatLng.latitude);
                    bundle.putDouble("longitude", mLatLng.longitude);
                    intent.putExtra("bundle", bundle);
                    startActivity(intent);
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

                Intent intent = new Intent(mContext, ProfileActivity.class);

                Bundle bundle = new Bundle();
                bundle.putString("channel", mUser.getJsonObject().toString());
                intent.putExtra("bundle", bundle);
                startActivityForResult(intent, UiHelper.CODE_MAIN_ACTIVITY);
                break;
        }
    }


    /****************************************************
     *  load data
     ****************************************************/

    private void loadNewNoties() {
        try {
            JSONObject params = new JSONObject();
            params.put("read", false);
            mApiHelper.call(api_noites_new_count, params);
        }catch (JSONException e) {
            Log.e(TAG, "Error " + e.toString());
        }
    }

    private void loadMainMarkers() {
        try {
            JSONObject params = JsonHelper.getZoomMinMaxLatLngParams(mMap);
            params.put("zoom", (int)mMap.getCameraPosition().zoom);

            if(mType != null) {
                params.put("type", mType);
            }else {
                params.put("type", TYPE_MAIN_MARKER);
            }

            if(mName != null) {
                params.put("name", mName);
            }

            params.put("sort", "point DESC");
            mApiHelper.call(api_channels_findMarkers, params);
        }catch (JSONException e) {
            Log.e(TAG, "Error " + e.toString());
        }

    }

    private void loadMainPosts() {
        mMainListAdapter.resetDocs();
        mMainListAdapter.setCurrentPage(0);
        loadMoreMainPosts();
    }

    private void loadMoreMainPosts() {
        try {
            JSONObject params = JsonHelper.getZoomMinMaxLatLngParams(mMap);
            params.put("type", TYPE_POST);
            params.put("page", mMainListAdapter.getCurrentPage());
            params.put("limit", loadingLimit);

            params.put("sort", "point DESC");
            mApiHelper.call(api_channels_findPosts, params);
        }catch (JSONException e) {
            Log.e(TAG, "Error " + e.toString());
        }
    }

    /****************************************************
     *  Event Bus
     ****************************************************/
    public void onEvent(ErrorData event){

        switch (event.type) {
            case TYPE_ERROR_AUTH:
                Intent intent = new Intent(getActivity(), SignupActivity.class);
                Bundle bundle = new Bundle();
                bundle.putDouble("latitude", mLatLng.latitude);
                bundle.putDouble("longitude", mLatLng.longitude);
                intent.putExtra("bundle", bundle);
                startActivity(intent);
                break;
        }
    }

    public void onEvent(SuccessData event){

        switch (event.type) {
            case api_token_check:
            case api_signin:
            case api_signup:
                AuthData auth = new AuthData(event.response);
                if(auth != null && auth.getToken() != null) login(auth);
                else logout();
                break;

            case api_logout:
                logout();
                break;

            case api_links_createPost:
                try {
                    String parentId = event.response.getString("parent");
                    JSONObject params = new JSONObject();
                    params.put("id", parentId);
                    mApiHelper.call(api_channels_get, params);
                } catch(JSONException e) {
                    Log.e(TAG, "error " + e.toString());
                }
                break;
            case api_channels_get:
                ChannelData channelData = new ChannelData(event.response);
                mMainListAdapter.updateDoc(channelData);
                mMainListAdapter.notifyDataSetChanged();
                break;
            case api_noites_new_count:
                int notyCount = event.response.optInt("data");
                if(notyCount > 0) {
                    mNotyCountBtn.setVisibility(View.VISIBLE);
                    mNotyCountBtn.setText(String.valueOf(notyCount));
                }else {
                    mNotyCountBtn.setVisibility(View.GONE);
                    mNotyCountBtn.setText("");
                }

                break;
            case api_channels_findMarkers:
                mMap.clear();
                addChannelsToMap(event.response);
                break;
            case api_channels_findPosts:
                isLoading = false;
                Log.d(TAG, "isLoading = false");
                addChannelsToList(event.response);
                break;
            case api_channels_getByPoint:
                mChannel = new ChannelData(event.response);
                if(TextUtils.isEmpty(mChannel.getId())) {

                    if(!CommonHelper.isAuthError(mActivity)) {
                        isBlock = true;

                        int zoom = (int) mMap.getCameraPosition().zoom;

                        switch(mChannel.getLevel()) {
                            case LEVEL_DONG:
                                mMarker = mMap.addMarker(new MarkerOptions().position(mPoint)
                                        .title("스팟생성")
                                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_marker_blue))
                                        .anchor(0.45f, 1.0f));

                                break;
                            case LEVEL_GUGUN:
                                mMarker = mMap.addMarker(new MarkerOptions().position(mPoint)
                                        .title("스팟생성")
                                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_marker_yellow))
                                        .anchor(0.45f, 1.0f));

                                break;
                            case LEVEL_DOSI:
                                mMarker = mMap.addMarker(new MarkerOptions().position(mPoint)
                                        .title("스팟생성")
                                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_marker_red))
                                        .anchor(0.45f, 1.0f));
                                break;
                            default:
                                mMarker = mMap.addMarker(new MarkerOptions().position(mPoint)
                                        .title("스팟생성")
                                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_marker_aqua))
                                        .anchor(0.45f, 1.0f));
                                break;
                        }


                        LatLng tmpPoint;
                        if(zoom == 18) {
                            tmpPoint = new LatLng(mPoint.latitude - 0.0002, mPoint.longitude);
                        } else if(zoom == 19) {
                            tmpPoint = new LatLng(mPoint.latitude - 0.0001, mPoint.longitude);
                        } else {
                            tmpPoint = new LatLng(mPoint.latitude - 0.00005, mPoint.longitude);
                        }
                        mMap.animateCamera(CameraUpdateFactory.newLatLng(tmpPoint), 100, null);

                        showCreateSpotDialog();
                    }else {
                        Intent intent = new Intent(getActivity(), SignupActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putDouble("latitude", mLatLng.latitude);
                        bundle.putDouble("longitude", mLatLng.longitude);
                        intent.putExtra("bundle", bundle);
                        startActivity(intent);
                    }

                }else {
                    startSpotActivity();
                }

                break;
            case api_channels_createSpot:
                mChannel = new ChannelData(event.response);
                if(mMarker != null) mMarker.remove();
                startSpotActivity();
                break;
            case api_profile_id_update:
                mUser = new ChannelData(event.response);
                updateView();
                break;
        }
    }




    /****************************************************
     *  init default map & listview
     ****************************************************/

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

            mMap.setPadding(0,paddingInPx,0,0);
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

                    mLatLng = new LatLng(latitude, longitude);

                    CameraPosition cameraPosition = new CameraPosition.Builder()
                            .target(mLatLng)
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

                if(mMarker != null) {
                    mMarker.remove();
                }
                if(mChannel != null) {
                    mChannel = null;
                }

                mPoint = point;
                int zoom = (int) mMap.getCameraPosition().zoom;

                if (isSpotCreatable(zoom)) {

                    final LatLng fPoint = point;

                    try {
                        JSONObject params = new JSONObject();
                        params.put("latitude", point.latitude);
                        params.put("longitude", point.longitude);

                        mApiHelper.call(api_channels_getByPoint, params);
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

                ChannelData doc;
                try {

                    if(index.equals(FOCUSED_ITEM_MARKER)) {
                        doc = mCurrentChannel.getParent();
                    }else {
                        doc = new ChannelData(mMarkers.getJSONObject(Integer.valueOf(index)));
                    }
                    Bundle bundle = new Bundle();
                    bundle.putString("channel", doc.getJsonObject().toString());

                    if (doc != null) {

                        Intent intent = null;
                        switch (doc.getType()) {
                            case TYPE_SPOT:
                            case TYPE_SPOT_INNER:
                                intent = new Intent(mContext, SpotActivity.class);
                                intent.putExtra("enterAnim", R.anim.zoom_out);
                                intent.putExtra("exitAnim", R.anim.zoom_in);
                                break;

                            case TYPE_INFO_CENTER:
                                intent = new Intent(mContext, InfoActivity.class);
                                break;

                            case TYPE_COMMUNITY:
                                intent = new Intent(mContext, CommunityActivity.class);
                                break;
                            case TYPE_POST:
                                intent = new Intent(mContext, PostActivity.class);
                                break;
                        }
                        intent.putExtra("bundle", bundle);
                        startActivityForResult(intent, UiHelper.CODE_MAIN_ACTIVITY);
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
                if (isBlock) {
                    isBlock = false;
                } else {
                    TextView zoomLevelText = (TextView) mActivity.findViewById(R.id.mZoomLevelText);
                    TextView createSpotText = (TextView) mActivity.findViewById(R.id.mCreateSpotText);
                    Button zoomBtn = (Button) mActivity.findViewById(R.id.mZoomBtn);

                    zoomLevelText.setText("Zoom: " + (int) position.zoom);

                    if (isSpotCreatable((int) position.zoom)) {
                        createSpotText.setVisibility(View.VISIBLE);
                        zoomBtn.setText(ZOOM_OUT);
                    } else {
                        createSpotText.setVisibility(View.GONE);
                        zoomBtn.setText(ZOOM_IN);
                    }

                    mMainListAdapter.setCurrentPage(0);
                    loadData();
                }
            }
        });

        mMap.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
            @Override
            public void onMyLocationChange(Location location) {
                mLatLng = new LatLng(location.getLatitude(), location.getLongitude());
            }
        });

    }




    /****************************************************
     *  Private Methods
     ****************************************************/

    private void startActivityForPush(ChannelData channelData) {
        if(!TextUtils.isEmpty(mId) && !TextUtils.isEmpty(mType)) {

            Intent intent = null;
            Bundle bundle = new Bundle();
            bundle.putString("channel", channelData.getJsonObject().toString());

            switch (mType) {
                case TYPE_SPOT:
                case TYPE_SPOT_INNER:
                    intent = new Intent(mActivity, SpotActivity.class);
                    break;
                case TYPE_COMMUNITY:
                    intent = new Intent(mActivity, CommunityActivity.class);
                    break;
                case TYPE_INFO_CENTER:
                    intent = new Intent(mActivity, InfoActivity.class);
                    break;
                case TYPE_USER:
                    intent = new Intent(mActivity, ProfileActivity.class);
                    break;
                case TYPE_MEMBER:
                    intent = new Intent(mActivity, ProfileActivity.class);
                    break;
                case TYPE_POST:
                    intent = new Intent(mActivity, PostActivity.class);
                    break;
            }


            intent.putExtra("bundle", bundle);
            startActivityForResult(intent, UiHelper.CODE_MAIN_ACTIVITY);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        loadData();
    }



    public static void zoom(GoogleMap map, int level) {
        map.animateCamera(CameraUpdateFactory.zoomTo(level), 1000, null);
    }

    public static boolean isSpotCreatable(int zoom ) {
        if(zoom >= 18) {
            return true;
        }else {
            return false;
        }
    }

    private void addChannelsToMap(JSONObject jsonObject) {
        try {
            mMarkers = jsonObject.getJSONArray("data");

            int idx = 0;

            if(mCurrentChannel != null) {
                if(CommonHelper.isInVisibleResion(mMap, new LatLng(mCurrentChannel.getLatitude(), mCurrentChannel.getLongitude()))) {
                    CommonHelper.addMarkerToMap(mMap, mCurrentChannel, idx);
                    idx++;
                }else {
                    mCurrentChannel = null;
                }
            }

            if(mMarkers != null) {
                for(; idx < mMarkers.length() ; idx ++ ) {
                    ChannelData channelData = new ChannelData(mMarkers.getJSONObject(idx));

                    CommonHelper.addMarkerToMap(mMap, channelData, idx);
                }
            }

        }catch(JSONException e) {
            Log.e(TAG, "error " + e.toString());
        }

    }

    private void addChannelsToList(JSONObject jsonObject) {
        try {
            JSONArray jsonArray = jsonObject.getJSONArray("data");
            for(int idx = 0; idx < jsonArray.length(); idx++) {
                JSONObject jsonDoc = jsonArray.getJSONObject(idx);
                ChannelData doc = new ChannelData(jsonDoc);

                mMainListAdapter.addBottom(doc);
            }

            Log.d(TAG, "notifyDataSetChanged");
            mMainListAdapter.notifyDataSetChanged();

        }catch(JSONException e) {
            Log.e(TAG, "error " + e.toString());
        }

    }

    private void startSpotActivity() {
        Intent intent = new Intent(getActivity(), SpotActivity.class);

        Bundle bundle = new Bundle();
        bundle.putString("channel", mChannel.getJsonObject().toString());
        intent.putExtra("bundle", bundle);
        intent.putExtra("enterAnim", R.anim.zoom_out);
        intent.putExtra("exitAnim", R.anim.zoom_in);

        startActivityForResult(intent, UiHelper.CODE_CHANNEL_ACTIVITY);
    }

    private void showCreateSpotDialog() {
        mAlert.setPositiveButton(R.string.spot_create_btn, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try {
                    JSONObject params = mChannel.getAddressJSONObject();
                    params.put("type", TYPE_SPOT);
                    mApiHelper.call(api_channels_createSpot, params);
                    dialog.cancel();
                } catch (JSONException e) {
                    Log.e(TAG, "Error " + e.toString());
                }
            }
        });

        mAlert.setNegativeButton(R.string.exit, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mMarker.remove();
                dialog.cancel();
            }
        });

        mAlert.setTitle(R.string.spot_create_confirm);
        mAlert.show();
    }

    private void checkLoginToken() {
        try {
            JSONObject params = new JSONObject();
            params.put("access_token", AuthHelper.getToken(mContext));
            mApiHelper.call(api_token_check, params);
        }catch (JSONException e) {
            Log.e(TAG, "error " + e.toString());
        }

    }

    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(mActivity);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, mActivity,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Log.i(TAG, "This device is not supported.");
            }
            return false;
        }
        return true;
    }
}
