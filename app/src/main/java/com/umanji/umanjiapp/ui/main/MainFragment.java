package com.umanji.umanjiapp.ui.main;


import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.AlphaAnimation;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
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
import com.umanji.umanjiapp.analytics.ApplicationController;
import com.umanji.umanjiapp.gcm.GcmRegistrationIntentService;
import com.umanji.umanjiapp.helper.AuthHelper;
import com.umanji.umanjiapp.helper.FileHelper;
import com.umanji.umanjiapp.helper.GridAdapter;
import com.umanji.umanjiapp.helper.Helper;
import com.umanji.umanjiapp.model.AuthData;
import com.umanji.umanjiapp.model.ChannelData;
import com.umanji.umanjiapp.model.ErrorData;
import com.umanji.umanjiapp.model.PaulBusData;
import com.umanji.umanjiapp.model.SuccessData;
import com.umanji.umanjiapp.ui.BaseFragment;
import com.umanji.umanjiapp.ui.channel.BaseTabAdapter;
import com.umanji.umanjiapp.ui.channel._fragment.communities.CommunityListKeywordFragment;
import com.umanji.umanjiapp.ui.channel._fragment.posts.PostListAdapter;
import com.umanji.umanjiapp.ui.channel._fragment.posts.PostListKeywordFragment;
import com.umanji.umanjiapp.ui.channel.bottomWindow.BottomMainActivity;
import com.umanji.umanjiapp.ui.channel.complex.ComplexActivity;
import com.umanji.umanjiapp.ui.channel.profile.ProfileActivity;
import com.umanji.umanjiapp.ui.channel.spot.SpotActivity;
import com.umanji.umanjiapp.ui.main.search.SearchActivity;
import com.umanji.umanjiapp.ui.modal.WebViewActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import cn.pedant.SweetAlert.SweetAlertDialog;
import de.greenrobot.event.EventBus;

public class MainFragment extends BaseFragment {
    private static final String TAG = "MainFragment";

    /****************************************************
     * Top View
     ****************************************************/
    private ImageView mAvatarImageBtn;
    private LinearLayout mSearchLayout;
    private FrameLayout mKeywordCommunityToolbar;
    private TextView mKeywordTitle;

    private TextView mUmanji;
    private TextView mSearch;
    private ImageView mCenterCircle;

    private View mNoticePanel;
    private TextView mCurrentAddress;
    private String currentAddress;

    private ImageView mGuideImageView01;

    /****************************************************
     * Map
     ****************************************************/
    private GoogleMap mMap;

    private RoundedImageView mZoomBtn;

    private ImageView mInterior;
    private String mInteriorStatus = "비활성화";
    private ImageView mTowerCrane;
    private String mTowerCraneStatus = "비활성화";
//    private ImageView mEye;
//    private ImageView mSay;


    private Marker mDraggableMarker;

    private TextView mZoomLevelText;
    private TextView mInfoTextPanel;
    private LatLng mCurrentMyPosition;
    private LatLng mLatLngByPoint = new LatLng(37.498039, 126.9220201);  //보라매공원
    private int currentZoomLevel = 0;
    // 참새어린이공원  37.498039, 126.9220201 / 대한민국 정보센터 37.642443934398, 126.977429352700  / 보라매 공원 37.498039, 126.9220201  / 초등 37.5053403, 126.9589435

    private static final String[] LOCATION_PERMS = {
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
    };

    private static final String[] PERMS = {
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA,
            Manifest.permission.INTERNET,
    };

    private static final int INITIAL_REQUEST = 10;
    private static final int PERMS_REQUEST = INITIAL_REQUEST + 2;

    public static final int OUT_OF_SERVICE = 0;
    public static final int TEMPORARILY_UNAVAILABLE = 1;
    public static final int AVAILABLE = 2;

    /****************************************************
     * Footer View
     ****************************************************/
    private android.widget.RelativeLayout.LayoutParams layoutParams;

    private SlidingUpPanelLayout mSlidingUpPanelLayout;
    private RelativeLayout mHeaderPanel;
    private Button mNotyCountBtn;
    private ImageView mPanelArrowImage;

    /****************************************************
     * Post and Community Posts
     ****************************************************/
    private LinearLayout mMainListContainer;
    private LinearLayout mCommunityListContainer;

    /****************************************************
     * PageViewer
     ****************************************************/
    protected ChannelData mChannel;

    protected BaseTabAdapter mCommunityAdapter;

    protected ViewPager mViewPager;
    protected TabLayout mTabLayout;
    private String mTabType = "";
    protected int mCurrentTapPosition = 0;

    /****************************************************
     * View
     ****************************************************/
    /*
    private LinearLayout mCommunityGoToPanel;
    private ImageView mCommunityCountryBtn;
    private ImageView mCommunityAdminBtn;
    private ImageView mCommunityLocalityBtn;
    private ImageView mCommunityThoroughBtn;
*/
    private ChannelData mHomeChannel;

    //    private ImageView mInfoButton;
    private LinearLayout mLauncherLevel2;
    private LinearLayout mLauncherLevel3;
    private LinearLayout mLauncherLevel4;
    private LinearLayout mLauncherLevel5;
    private LinearLayout mLauncherLevel6;
    private LinearLayout mLauncherLevel7;
    private LinearLayout mLauncherLevel8;

    // Level 2
    private ChannelData mEnvironmentChannel;
    private ChannelData mEnergyChannel;

    private ImageView mEnvironmentImageView;
    private ImageView mEnergyImageView;
    // Level 3
    private ChannelData mSpiritualChannel;
    private ImageView mSpiritualImageView;

    // Level 4
    private ChannelData mHistoryChannel;
    private ImageView mHistoryImageView;

    // Level 5
    private ChannelData mUnityChannel;
    private ImageView mUnityImageView;

    // Level 6
    private ChannelData mHealthChannel;
    private ChannelData mPoliticsChannel;

    private ImageView mHealthImageView;
    private ImageView mPoliticsImageView;
    // Level 7
    private ChannelData mClimbChannel;
    private ChannelData mGolfChannel;

    private ImageView mClimbImageView;
    private ImageView mGolfImageView;
    // Level 8
    private ChannelData mEtcChannel;

    private ImageView mEtcImageView;

    /****************************************************
     * Controler
     ****************************************************/
    private Button mToCommunityBtn;
    private String communityName;

    private ImageView mCommunityCloseBtn;


    /****************************************************
     * Etc
     ****************************************************/
    private ChannelData mUser;
    private JSONArray mMarkers;
    private JSONArray mAds;
    private ChannelData mCurrentChannel;
    private ChannelData mSelectedChannel;
    private ChannelData mClickedChannel;
    private ChannelData mAdChannel;
    private ChannelData mAddressChannel;

    private ArrayList<ChannelData> mPosts;

    private boolean isBlock = false;
    private boolean isLoading = false;
    private int mPreFocusedItem = 0;

    private String mSlidingState = SLIDING_COLLAPSED;

    private ChannelData mChannelByPoint;
    private Marker mMarkerByPoint;
    private Marker mFocusedMarker;
    private LatLng mPointByPost;

    private String mChannelIdForPush;
    private ImageView mAdsImage;
    private LinearLayout mlayout;
    private ImageView mTalk;

    boolean mMapIsTouched = false;
    private boolean isKeywordCommunityMode;
    boolean isTalkMode = false;
    boolean isTalkFlag = false;
    boolean mTalkExpanded = false;
    boolean touchedOnce = false;
    private String isInitLocationUsed = "false";

    private JSONArray jsonArrayBottom;
    private JSONObject getMinMaxParams;
    private SharedPreferences sharedpreferences;
    public static final String MyPREFERENCES = "MyPrefs";

    View mView;
    TouchableWrapper mTouchView;

    /****************************************************
     * from other Activity
     ****************************************************/

    public static MainFragment newInstance(Bundle bundle) {
        MainFragment fragment = new MainFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String fromHomeUser;
        sharedpreferences = this.getActivity().getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        isInitLocationUsed = sharedpreferences.getString("isInitLocationUsed", "empty");

        if (getArguments() != null) {
            fromHomeUser = getArguments().getString("id");
            if (fromHomeUser != null) {
                mUser = new ChannelData(fromHomeUser);
            }
            String jsonString = getArguments().getString("channel");
            if (jsonString != null) {
                mChannel = new ChannelData(jsonString);
            }

            if (getArguments().getString("type") != null) {
//******************************************************************** isKeywordCommunityMode
                isKeywordCommunityMode = true;

                if (mChannel == null) {

                } else {
                    communityName = mChannel.getName();

                }
            }
//******************************************************************** normalMode = TalkMode
            mTabType = getArguments().getString("tabType");
        }

        Tracker t = ((ApplicationController) mActivity.getApplication()).getTracker();
        t.setScreenName("MainActivity");
        t.send(new HitBuilders.AppViewBuilder().build());

    }

    @Override
    public void onStart() {
        super.onStart();
        GoogleAnalytics.getInstance(mActivity).reportActivityStart(mActivity);
    }

    @Override
    public void onStop() {
        super.onStop();
        GoogleAnalytics.getInstance(mActivity).reportActivityStop(mActivity);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = super.onCreateView(inflater, container, savedInstanceState);

//        initMainListView(mView);

        if (AuthHelper.isLogin(mActivity)) {
            loginByToken();
        } else {
            updateView();
        }

        initWidgets(mView);


        int currentapiVersion = android.os.Build.VERSION.SDK_INT;
        if (currentapiVersion >= android.os.Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(mActivity, Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                    PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(mActivity, Manifest.permission.READ_EXTERNAL_STORAGE) ==
                            PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(mActivity, Manifest.permission.CAMERA) ==
                            PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(mActivity, Manifest.permission.INTERNET) ==
                            PackageManager.PERMISSION_GRANTED) {
                initMap();

            } else {
                requestPermissions(PERMS, PERMS_REQUEST);
            }
        } else {
            initMap();
        }

        if (!TextUtils.isEmpty(mChannelIdForPush)) {
            startActivityForPush();
        }


        mApi.call(api_system_version, new AjaxCallback<JSONObject>() {
            @Override
            public void callback(String url, JSONObject json, AjaxStatus status) {
                JSONObject data = json;
                if (json.optJSONObject("data") != null) {
                    data = json.optJSONObject("data");
                }

                int version = data.optInt("value");

                if (APP_VERSION < version) {
                    new SweetAlertDialog(mActivity, SweetAlertDialog.WARNING_TYPE)
                            .setTitleText("신규 기능 업데이트")
                            .setContentText("마켓에서 새로운 버젼을 다운받아 주세요.")
                            .setConfirmText("마켓으로 이동하기")
                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sDialog) {
                                    sDialog.dismissWithAnimation();
                                    final String appPackageName = mActivity.getPackageName();
                                    try {
                                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                                    } catch (android.content.ActivityNotFoundException anfe) {
                                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                                    }
                                }
                            })
                            .show();
                }
            }
        });

        mTouchView = new TouchableWrapper(getActivity());
        mTouchView.addView(mView);
        return mTouchView;
    }

    protected void startActivityForPush() {
        try {
            JSONObject params = new JSONObject();
            params.put("id", mChannelIdForPush);

            mApi.call(api_channels_get, params, new AjaxCallback<JSONObject>() {
                @Override
                public void callback(String url, JSONObject object, AjaxStatus status) {
                    ChannelData channelData = new ChannelData(object);

                    switch (channelData.getType()) {
                        case TYPE_POST:
                            Helper.startActivity(mActivity, channelData);
                            break;
                        case TYPE_MEMBER:
                        case TYPE_LINK:
                        case TYPE_SURVEY:
                            Helper.startActivity(mActivity, channelData.getParent());
                            break;
                        default:
                            Helper.startActivity(mActivity, channelData);
                            break;
                    }

                    mChannelIdForPush = "";
                }
            });
        } catch (JSONException e) {
            Log.e(TAG, "error " + e.toString());
        }
    }

    @Override
    public View getView(LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.activity_main, container, false);
    }

    @Override
    public View getView() {
        return mView;
    }

    @Override
    public void initWidgets(View view) {

        mAlert = new AlertDialog.Builder(mActivity);

        mUmanji = (TextView) view.findViewById(R.id.logo);
        mUmanji.setOnClickListener(this);
        mCenterCircle = (ImageView) view.findViewById(R.id.centerCircle);
        mCurrentAddress = (TextView) view.findViewById(R.id.currentAddress);

        mKeywordCommunityToolbar = (FrameLayout) view.findViewById(R.id.keywordCommunityToolbar);
        mKeywordTitle = (TextView) view.findViewById(R.id.keywordTitle);
        mSearchLayout = (LinearLayout) view.findViewById(R.id.searchLayout);
/*

        mCommunityGoToPanel = (LinearLayout) view.findViewById(R.id.communityGotoPanel);
        mCommunityCountryBtn = (ImageView) view.findViewById(R.id.communityCountry);
        mCommunityCountryBtn.setOnClickListener(this);
        mCommunityAdminBtn = (ImageView) view.findViewById(R.id.communityAdmin);
        mCommunityAdminBtn.setOnClickListener(this);
        mCommunityLocalityBtn = (ImageView) view.findViewById(R.id.communityLocality);
        mCommunityLocalityBtn.setOnClickListener(this);
        mCommunityThoroughBtn = (ImageView) view.findViewById(R.id.communityThorough);
        mCommunityThoroughBtn.setOnClickListener(this);
*/

        mInterior = (ImageView) view.findViewById(R.id.interior);
        mInterior.setOnClickListener(this);
        mTowerCrane = (ImageView) view.findViewById(R.id.towerCrane);
        mTowerCrane.setOnClickListener(this);
//        mSay = (ImageView) view.findViewById(R.id.say);
//        mSay.setOnClickListener(this);
//        mEye = (ImageView) view.findViewById(R.id.eye);
//        mEye.setOnClickListener(this);

        mSearch = (TextView) view.findViewById(R.id.search);
        mSearch.setOnClickListener(this);
        mNoticePanel = view.findViewById(R.id.noticePanel);

        mSlidingUpPanelLayout = (SlidingUpPanelLayout) view.findViewById(R.id.slidingUpPanelLayout);
        mSlidingUpPanelLayout.setPanelHeight(Helper.dpToPixel(mActivity, 0));  // before 48
        mSlidingUpPanelLayout.setAnchorPoint(0.7f);
        mSlidingUpPanelLayout.setMinFlingVelocity(DEFAULT_MIN_FLING_VELOCITY);

        mHeaderPanel = (RelativeLayout) view.findViewById(R.id.headerPanel);
        mHeaderPanel.setOnClickListener(this);

        mZoomBtn = (RoundedImageView) view.findViewById(R.id.mZoomBtn);
        mZoomBtn.setTag(ZOOM_IN);
        mZoomBtn.setOnClickListener(this);

        mAvatarImageBtn = (ImageView) view.findViewById(R.id.userPhoto);
        mAvatarImageBtn.setOnClickListener(this);

        mNotyCountBtn = (Button) view.findViewById(R.id.mNotyCount);
        mNotyCountBtn.setOnClickListener(this);
        mNotyCountBtn.setText("0");

        mZoomLevelText = (TextView) view.findViewById(R.id.mZoomLevelText);

        mInfoTextPanel = (TextView) view.findViewById(R.id.mInfoTextPanel);
        mInfoTextPanel.setSelected(true);

        mAdsImage = (ImageView) view.findViewById(R.id.ads_image);
        mAdsImage.setOnClickListener(this);

        mlayout = (LinearLayout) view.findViewById(R.id.mainListContainer);

//        mInfoButton = (ImageView) view.findViewById(R.id.infoButton);
//        mInfoButton.setOnClickListener(this);

        mToCommunityBtn = (Button) view.findViewById(R.id.toCommunityBtn);

        mPanelArrowImage = (ImageView) view.findViewById(R.id.panel_arrow);
        mCommunityCloseBtn = (ImageView) view.findViewById(R.id.community_close_button);
        mCommunityCloseBtn.setOnClickListener(this);

        mMainListContainer = (LinearLayout) view.findViewById(R.id.mainListContainer);
        mCommunityListContainer = (LinearLayout) view.findViewById(R.id.communityListContainer);

        mLauncherLevel2 = (LinearLayout) view.findViewById(R.id.keyword_launcher_level2);
        mLauncherLevel3 = (LinearLayout) view.findViewById(R.id.keyword_launcher_level3);
        mLauncherLevel4 = (LinearLayout) view.findViewById(R.id.keyword_launcher_level4);
        mLauncherLevel5 = (LinearLayout) view.findViewById(R.id.keyword_launcher_level5);
        mLauncherLevel6 = (LinearLayout) view.findViewById(R.id.keyword_launcher_level6);
        mLauncherLevel7 = (LinearLayout) view.findViewById(R.id.keyword_launcher_level7);
        mLauncherLevel8 = (LinearLayout) view.findViewById(R.id.keyword_launcher_level8);


        // Level 2
        mEnvironmentImageView = (ImageView) view.findViewById(R.id.environment);
        mEnergyImageView = (ImageView) view.findViewById(R.id.energy);
        mEnvironmentImageView.setOnClickListener(this);
        mEnergyImageView.setOnClickListener(this);


        // Level 3
        mSpiritualImageView = (ImageView) view.findViewById(R.id.spiritual);
        mSpiritualImageView.setOnClickListener(this);

        // Level 4
        mHistoryImageView = (ImageView) view.findViewById(R.id.history);
        mHistoryImageView.setOnClickListener(this);

        // Level 5
        mUnityImageView = (ImageView) view.findViewById(R.id.unity);
        mUnityImageView.setOnClickListener(this);

        // Level 6
        mHealthImageView = (ImageView) view.findViewById(R.id.health);
        mPoliticsImageView = (ImageView) view.findViewById(R.id.politics);

        mHealthImageView.setOnClickListener(this);
        mPoliticsImageView.setOnClickListener(this);

        // Level 7
        mClimbImageView = (ImageView) view.findViewById(R.id.climb);
//        mClimbImageView.setBackgroundColor(Color.parseColor("#11000000"));
        mGolfImageView = (ImageView) view.findViewById(R.id.golf);
//        mGolfImageView.setBackgroundColor(Color.parseColor("#aa000000"));

        mClimbImageView.setOnClickListener(this);
        mGolfImageView.setOnClickListener(this);

        // Level 8
//        mEtcImageView = (ImageView) view.findViewById(R.id.keyword_etc);
//        mEtcImageView.setOnClickListener(this);

        mTalk = (ImageView) view.findViewById(R.id.talk);
        mTalk.setOnClickListener(this);

// $$
//        mViewPager = (ViewPager) view.findViewById(R.id.viewPaper);
//        mTabLayout = (TabLayout) view.findViewById(R.id.tabs);
//        mTabAdapter = new BaseTabAdapter(getActivity().getSupportFragmentManager());
//        addFragmentToTabAdapter(mTabAdapter, "keywordCommunity");
//        addFragmentToTabAdapter(mTabAdapter, "talk");
//        mViewPager.setAdapter(mTabAdapter);
//        mTabLayout.setupWithViewPager(mViewPager);
//
//        setTabSelect();
//
//        onTabSelected(mTabLayout);

    }

    @Override
    public void loadData() {
        if (AuthHelper.isLogin(mActivity)) {
            loadNewNoties();
        }

        loadMainMarkers();
        loadMainAds();
//        loadMainPosts();

        int zoom = (int) mMap.getCameraPosition().zoom;

        if (isKeywordTouchable(zoom)) {
            isTalkMode = false;
        } else {
            isTalkMode = true;
        }

    }

    @Override
    public void updateView() {
        if (AuthHelper.isLogin(mActivity)) {
            mAvatarImageBtn.setVisibility(View.VISIBLE);
            String userPhoto = mUser.getPhoto();
            if (!TextUtils.isEmpty(userPhoto)) {
                Glide.with(mActivity)
                        .load(userPhoto)
                        .placeholder(R.drawable.empty)
                        .animate(R.anim.abc_fade_in)
                        .thumbnail(1f)
                        .into(mAvatarImageBtn);
            } else {
                Glide.with(mActivity)
                        .load(R.drawable.avatar_default_0)
                        .placeholder(R.drawable.empty)
                        .override(40, 40)
                        .into(mAvatarImageBtn);
            }
        } else {
            Glide.with(mActivity)
                    .load(R.drawable.icon_user_person)
                    .animate(R.anim.abc_fade_in)
                    .override(40, 40)
                    .into(mAvatarImageBtn);
        }

        /*
        *
        *  Keyword community Mode
        * */

        if (isKeywordCommunityMode) {
            JSONObject params1 = null;
            try {
                if (mMap == null) {
                    String tempString = sharedpreferences.getString("MyParams", "empty");
                    params1 = new JSONObject(tempString);
                } else {
                    params1 = Helper.getZoomMinMaxLatLngParams(mMap);
                }

                params1.put("name", communityName);
                params1.put("type", TYPE_POST);
//                params1.put("id", mAdChannel.getParent().getId());

//                api_findCommunity
//                api_channels_get
//                api_keyword_findPosts

                mApi.call(api_keyword_findPosts, params1, new AjaxCallback<JSONObject>() {
                    @Override
                    public void callback(String url, JSONObject json, AjaxStatus status) {
                        mChannel = new ChannelData(json);
                        initTabAdapter(mView, mChannel);
                    }
                });


            } catch (JSONException e) {
                Log.e(TAG, "error " + e.toString());
            }

            communityName = mChannel.getName();


            mCommunityCloseBtn.setVisibility(View.VISIBLE);
//            mToCommunityBtn.setVisibility(View.VISIBLE);
//            mCommunityGoToPanel.setVisibility(View.VISIBLE);
            mMainListContainer.setVisibility(View.GONE);
            mCommunityListContainer.setVisibility(View.VISIBLE);
            mLauncherLevel8.setVisibility(View.VISIBLE);
            mSearchLayout.setVisibility(View.GONE);
            mKeywordCommunityToolbar.setVisibility(View.VISIBLE);
            mKeywordTitle.setText(communityName);
            getKeywordCommunityData();  // keywordCommunityMode 일때 키워드 전문가 tab 추가
//            loadCommunityMarkers(communityName);
//            mSlidingUpPanelLayout.setPanelHeight(Helper.dpToPixel(mActivity, 120));
        }
    }

    private void getKeywordCommunityData() {
        isLoading = true;

        mMainListContainer.setVisibility(View.GONE);
//        mCommunityListContainer.setVisibility(View.VISIBLE);

        JSONObject params = null;
        try {
            if (mMap == null) {
                String tempString = sharedpreferences.getString("MyParams", "empty");
                params = new JSONObject(tempString);
            } else {
                params = Helper.getZoomMinMaxLatLngParams(mMap);
            }
            params.put("type", TYPE_COMMUNITY);
            params.put("limit", 1);

            getMinMaxParams = params;
//            EventBus.getDefault().post(new PaulBusData("talk", getMinMaxParams));
// api_channels_communities_num
            mApi.call(api_main_findPosts, params, new AjaxCallback<JSONObject>() {
                @Override
                public void callback(String url, JSONObject json, AjaxStatus status) {
                    if (status.getCode() == 500) {
                        EventBus.getDefault().post(new ErrorData(TYPE_ERROR_AUTH, TYPE_ERROR_AUTH));
                    } else {
                        try {
                            JSONArray jsonArray = json.getJSONArray("data");
                            jsonArrayBottom = jsonArray;
                            if (jsonArray.length() != 0) {
                                isTalkFlag = true;
                                mTalk.setImageResource(R.drawable.button_kakao);

                                mChannel = new ChannelData(json);

                            } else {

                                isTalkFlag = false;
                                mTalk.setImageResource(R.drawable.button_kakao_black);

                            }

                            isLoading = false;
                            //mTalkAdapter.notifyDataSetChanged();
                        } catch (JSONException e) {
                            Log.e(TAG, "Error " + e.toString());
                        }
                    }
                }
            });


        } catch (JSONException e) {
            Log.e(TAG, "error " + e.toString());
        }
    }


    private void getTalkData() {
        isLoading = true;

//        Log.d("Paul", sharedpreferences.getString("MyParams", "none"));

        mMainListContainer.setVisibility(View.GONE);
//        mCommunityListContainer.setVisibility(View.VISIBLE);

        try {

            JSONObject params = Helper.getZoomMinMaxLatLngParams(mMap);
            params.put("page", 0);  //mTalkAdapter.getCurrentPage()
            params.put("limit", 1);

            String paramsToString = params.toString();
            SharedPreferences.Editor editor = sharedpreferences.edit();

            editor.putString("MyParams", paramsToString);
            editor.commit();

            getMinMaxParams = params;
//            api_main_findPosts
//            api_channels_findPosts
//            EventBus.getDefault().post(new PaulBusData("talk", getMinMaxParams));

            mApi.call(api_main_findPosts, params, new AjaxCallback<JSONObject>() {
                @Override
                public void callback(String url, JSONObject json, AjaxStatus status) {
                    if (status.getCode() == 500) {
                        EventBus.getDefault().post(new ErrorData(TYPE_ERROR_AUTH, TYPE_ERROR_AUTH));
                    } else {
                        try {
                            JSONArray jsonArray = json.getJSONArray("data");
                            jsonArrayBottom = jsonArray;
                            if (jsonArray.length() != 0) {
                                isTalkFlag = true;
                                mTalk.setImageResource(R.drawable.button_kakao);

                                mChannel = new ChannelData(json);

                            } else {

                                isTalkFlag = false;
                                mTalk.setImageResource(R.drawable.button_kakao_black);

                            }

                            isLoading = false;
                            //mTalkAdapter.notifyDataSetChanged();
                        } catch (JSONException e) {
                            Log.e(TAG, "Error " + e.toString());
                        }
                    }
                }
            });


        } catch (JSONException e) {
            Log.e(TAG, "error " + e.toString());
        }
    }

//    sliding control
//    mSlidingUpPanelLayout.setPanelHeight(Helper.dpToPixel(mActivity, 0));
//
//

    @Override
    public void onEvent(SuccessData event) {
        super.onEvent(event);

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
            case api_channels_createCommunity:
            case api_channels_createComplex:
            case api_channels_createSpot:
            case api_channels_id_update:
            case api_channels_id_delete:
                mCurrentChannel = null;
                mSelectedChannel = null;
                mClickedChannel = null;
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

            case api_channels_getByPoint:
                break;

            case api_channels_communities_num:
                Toast.makeText(mActivity, "done", Toast.LENGTH_SHORT).show();
                // get do it getshow()... Grid view
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
/*  Event Bus Default response

    public void onEvent(PaulBusData event) {
        String mty = event.type;
        String msy = event.key;
        Log.d("Paul", mty + " " + msy);
    }
*/

    private AlphaAnimation buttonClick = new AlphaAnimation(0F, 1F);

    @Override
    public void onClick(View v) {  // paul onclick
        Bundle bundle = new Bundle();

        String tempName = getResources().getResourceName(v.getId());
        // com.umanji.umanjiapp:id/climb

        String extractName = Helper.extractKeyword(tempName);

        String keywordGroup = "Environment, Energy, Spiritual, History, Unity, Health, Politics, Climb, Golf";

        if (keywordGroup.contains(extractName)) {
            int zoom = (int) mMap.getCameraPosition().zoom;

            switch (extractName) {
                case "Environment":
                    mEnvironmentImageView.startAnimation(buttonClick);
                    communityName = mEnvironmentChannel.getName();
                    mLauncherLevel2.setVisibility(View.GONE);
                    initTabAdapter(mView, mEnvironmentChannel);
                    break;
                case "Energy":
                    mEnergyImageView.startAnimation(buttonClick);
                    communityName = mEnergyChannel.getName();
                    mLauncherLevel2.setVisibility(View.GONE);
                    initTabAdapter(mView, mEnergyChannel);
                    break;
                case "Spiritual":
                    mSpiritualImageView.startAnimation(buttonClick);
                    communityName = mSpiritualChannel.getName();
                    mLauncherLevel3.setVisibility(View.GONE);
                    initTabAdapter(mView, mSpiritualChannel);
                    break;
                case "History":
                    mHistoryImageView.startAnimation(buttonClick);
                    communityName = mHistoryChannel.getName();
                    mLauncherLevel4.setVisibility(View.GONE);
                    initTabAdapter(mView, mHistoryChannel);
                    break;
                case "Unity":
                    mUnityImageView.startAnimation(buttonClick);
                    communityName = mUnityChannel.getName();
                    mLauncherLevel5.setVisibility(View.GONE);
                    initTabAdapter(mView, mUnityChannel);
                    break;
                case "Health":
                    mHealthImageView.startAnimation(buttonClick);
                    communityName = mHealthChannel.getName();
                    mLauncherLevel6.setVisibility(View.GONE);
                    initTabAdapter(mView, mHealthChannel);
                    break;
                case "Politics":
                    mPoliticsImageView.startAnimation(buttonClick);
                    communityName = mPoliticsChannel.getName();
                    mLauncherLevel6.setVisibility(View.GONE);
                    initTabAdapter(mView, mPoliticsChannel);
                    break;
                case "Climb":
                    mClimbImageView.startAnimation(buttonClick);
                    communityName = mClimbChannel.getName();         // 등산
                    mLauncherLevel7.setVisibility(View.GONE);
                    initTabAdapter(mView, mClimbChannel);
                    break;
                case "Golf":
                    mGolfImageView.startAnimation(buttonClick);
                    communityName = mGolfChannel.getName();
                    mLauncherLevel7.setVisibility(View.GONE);
                    initTabAdapter(mView, mGolfChannel);
                    break;
            }

            isKeywordCommunityMode = true;
//            mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
            mKeywordCommunityToolbar.setVisibility(View.VISIBLE);
            mCommunityCloseBtn.setVisibility(View.VISIBLE);
//            mToCommunityBtn.setVisibility(View.VISIBLE);
//            mCommunityGoToPanel.setVisibility(View.VISIBLE);
            mMainListContainer.setVisibility(View.GONE);
//            mCommunityListContainer.setVisibility(View.VISIBLE);
            mSearchLayout.setVisibility(View.GONE);
            mLauncherLevel8.setVisibility(View.VISIBLE);
            buttonClick.setDuration(500);
            mKeywordTitle.setText(communityName);
            updateCommunityBtn(zoom);
            loadCommunityMarkers(communityName);
        }

        switch (v.getId()) {

            case R.id.community_close_button:
                int zoom = (int) mMap.getCameraPosition().zoom;
                switch (zoom) {
                    case 2:
                        mLauncherLevel2.setVisibility(View.VISIBLE);
                        break;
                    case 3:
                        mLauncherLevel3.setVisibility(View.VISIBLE);
                        break;
                    case 4:
                        mLauncherLevel4.setVisibility(View.VISIBLE);
                        break;
                    case 5:
                        mLauncherLevel5.setVisibility(View.VISIBLE);
                        break;
                    case 6:
                        mLauncherLevel6.setVisibility(View.VISIBLE);
                        break;
                    case 7:
                        mLauncherLevel7.setVisibility(View.VISIBLE);
                        break;
                }
//                mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);         // mapy type :: MAP_TYPE_NORMAL / MAP_TYPE_TERRAIN / MAP_TYPE_SATELLITE / MAP_TYPE_HYBRID /
                mCommunityCloseBtn.setVisibility(View.GONE);        // 오른쪽 닫기 버튼
//                mToCommunityBtn.setVisibility(View.GONE);           // 커뮤니티 정보센터 바로가기
//                mCommunityGoToPanel.setVisibility(View.GONE);       // 커뮤니티 정보센터 바로가기 새로운 버전
                mCommunityListContainer.setVisibility(View.GONE);   // 커뮤니티 포스트 Tab
                mKeywordCommunityToolbar.setVisibility(View.GONE);                // Title의 '커뮤니티'
                mMainListContainer.setVisibility(View.VISIBLE);     // main에서 아래 post
                mSearchLayout.setVisibility(View.VISIBLE);          // search bar
                mLauncherLevel8.setVisibility(View.VISIBLE);           // talk 보임
                isKeywordCommunityMode = false;
                loadData();
                break;

            case R.id.logo:
                mUmanji.startAnimation(buttonClick);
                buttonClick.setDuration(500);

                Intent webInt = new Intent(mActivity, WebViewActivity.class);
                webInt.putExtra("url", "http://umanji.com/2016/06/17/manual0001/");
                mActivity.startActivity(webInt);
                break;

            case R.id.userPhoto:
                mAvatarImageBtn.startAnimation(buttonClick);
                buttonClick.setDuration(500);

                if (AuthHelper.isLogin(mActivity) && mUser != null) {
                    Intent intent = new Intent(mActivity, ProfileActivity.class);
                    bundle.putString("channel", mUser.getJsonObject().toString());
                    bundle.putInt("newNoticeCount", getNewNoticeCount());
                    if (getNewNoticeCount() > 0) {
                        bundle.putString("tabType", TAB_NOTIES);
                    } else {
                        bundle.putString("tabType", TAB_SPOTS);
                    }

                    intent.putExtra("bundle", bundle);
                    startActivity(intent);
                } else {
                    Helper.startSigninActivity(mActivity, mCurrentMyPosition);
                }
                break;
/*

            case R.id.keyword_etc:
                mEtcImageView.startAnimation(buttonClick);
                buttonClick.setDuration(500);

                mProgress.show();

                showCommunityPanel();
                break;
*/

            case R.id.talk:
                mTalk.startAnimation(buttonClick);
                buttonClick.setDuration(500);

                Intent bottomIntent;
                bottomIntent = new Intent(mActivity, BottomMainActivity.class);
                if (isTalkFlag) {
//                    mSlidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
//                    mTalkExpanded = true;
                    if (isKeywordCommunityMode == false) {
                        bottomIntent.putExtra("type", "talkMode");
                    } else {
                        bottomIntent.putExtra("type", "keywordCommunityMode");
                        bottomIntent.putExtra("keywordName", communityName);
                    }
                    bottomIntent.putExtra("params", getMinMaxParams.toString());
                    bottomIntent.putExtra("currentAddress", currentAddress);
//                    intent.putExtra("channels", jsonArrayBottom.toString());
                    startActivity(bottomIntent);

                    mTouchView.setEnabled(false);
                } else if (isKeywordCommunityMode) {
                    String divisionTalk = "talk";
                    showTutorialDialog(divisionTalk);
                } else {
                    String divisionTalk = "talk";
                    showTutorialDialog(divisionTalk);

                }
                break;

            case R.id.headerPanel:
                /*
                if (TextUtils.equals(mSlidingState, SLIDING_COLLAPSED)) {
                    mSlidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.ANCHORED);
                    mSlidingState = SLIDING_ANCHORED;

                } else if (TextUtils.equals(mSlidingState, SLIDING_ANCHORED)) {
                    mSlidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
                    mSlidingState = SLIDING_COLLAPSED;
*/

                if (TextUtils.equals(mSlidingState, SLIDING_COLLAPSED)) {
                    mSlidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
                    mSlidingState = SLIDING_EXPANDED;
                    mPanelArrowImage.setImageResource(R.drawable.ic_arrow_down);

                } else if (TextUtils.equals(mSlidingState, SLIDING_EXPANDED)) {
                    mSlidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
                    mSlidingState = SLIDING_COLLAPSED;
                    mPanelArrowImage.setImageResource(R.drawable.ic_arrow_up);
                    /*
                    *
                    * EXPANDED,
                    * COLLAPSED,    oo
                    * ANCHORED,     oo
                    * HIDDEN,
                    * DRAGGING
                    * */
                }

                break;
            case R.id.mZoomBtn:
                if (mZoomBtn.getTag().equals(ZOOM_IN)) {
                    mMap.animateCamera(CameraUpdateFactory.zoomTo(18), 1000, null);
                } else {
                    mMap.animateCamera(CameraUpdateFactory.zoomTo(10), 1000, null);
                }
                break;

            case R.id.mNotyCount:
                if (AuthHelper.isLogin(mActivity) && mUser != null) {
                    Intent intent = new Intent(mActivity, ProfileActivity.class);
//                    Bundle bundle = new Bundle();
                    bundle.putString("channel", mUser.getJsonObject().toString());
                    bundle.putInt("newNoticeCount", getNewNoticeCount());
                    if (getNewNoticeCount() > 0) {
                        bundle.putString("tabType", TAB_NOTIES);
                    } else {
                        bundle.putString("tabType", TAB_SPOTS);
                    }

                    intent.putExtra("bundle", bundle);
                    startActivity(intent);
                } else {
                    Helper.startSigninActivity(mActivity, mCurrentMyPosition);
                }
                break;

            case R.id.ads_image:
                Intent webIntent = new Intent(mActivity, WebViewActivity.class);
                mActivity.startActivity(webIntent);
                break;
/*

            case R.id.infoButton:
                zoom = (int) mMap.getCameraPosition().zoom;

                Intent mwebInt = new Intent(mActivity, WebViewActivity.class);
                switch (zoom) {
                    case 15:
                        mwebInt.putExtra("url", "http://blog.naver.com/mothcar/220715838911"); // 복합단지 설명
                        break;
                    default:
                        mwebInt.putExtra("url", "http://blog.naver.com/mothcar/220720111996");  // 일반 사용설명
                }

                mActivity.startActivity(mwebInt);
                break;
*/


            case R.id.search:
                Intent searchIntent = new Intent(mActivity, SearchActivity.class);
                startActivity(searchIntent);
                break;

            case R.id.interior:
                mInterior.startAnimation(buttonClick);
                buttonClick.setDuration(500);
                Toast.makeText(mActivity, mInteriorStatus, Toast.LENGTH_SHORT).show();
                String division = "interior";
                showTutorialDialog(division);
                break;

            case R.id.towerCrane:
                mTowerCrane.startAnimation(buttonClick);
                buttonClick.setDuration(500);
                Toast.makeText(mActivity, mTowerCraneStatus, Toast.LENGTH_SHORT).show();
                String division2 = "towerCrane";
                showTutorialDialog(division2);
                break;

          /*  case R.id.say:
                mSay.startAnimation(buttonClick);
                buttonClick.setDuration(500);
                Toast.makeText(mActivity, mTowerCraneStatus, Toast.LENGTH_SHORT).show();
                String division3 = "say";
                showTutorialDialog(division3);
                break;*/

      /*      case R.id.eye:
                mEye.startAnimation(buttonClick);
                buttonClick.setDuration(500);
                Toast.makeText(mActivity, mTowerCraneStatus, Toast.LENGTH_SHORT).show();
                String division4 = "eye";
                showTutorialDialog(division4);
                break;*/
/*

            case R.id.communityCountry:
                mCommunityCountryBtn.startAnimation(buttonClick);
                buttonClick.setDuration(500);
                Toast.makeText(mActivity, "country", Toast.LENGTH_SHORT).show();

                break;
            case R.id.communityAdmin:
                mCommunityAdminBtn.startAnimation(buttonClick);
                buttonClick.setDuration(500);
                Toast.makeText(mActivity, "admin", Toast.LENGTH_SHORT).show();

                break;
            case R.id.communityLocality:
                mCommunityLocalityBtn.startAnimation(buttonClick);
                buttonClick.setDuration(500);
                Toast.makeText(mActivity, "Locality", Toast.LENGTH_SHORT).show();

                break;
            case R.id.communityThorough:
                mCommunityThoroughBtn.startAnimation(buttonClick);
                buttonClick.setDuration(500);
                Toast.makeText(mActivity, "Thorough", Toast.LENGTH_SHORT).show();

                break;
*/

        }
    }

    /*
    *
    * 외부링크 리스트 : link list
    *
    * 우만지 일반 설명 :   http://umanji.com/2016/06/17/manual0001/
    * 레벨별 입력 :       http://umanji.com/2016/06/22/input_level_explain/ ‎
    * 복합단지 설명 :     http://blog.naver.com/mothcar/220715838911
    * 일반 설명 : http://blog.naver.com/mothcar/220720111996
    *
    * */

    private void showComplexTutorialDialog() {

        final Dialog dialog = new Dialog(mActivity);
        dialog.setContentView(R.layout.dialog_complex_alert);
        TextView title = (TextView) dialog.findViewById(android.R.id.title);
        title.setText("사용 설명");
//        title.setBackgroundResource(R.drawable.gradient);
        title.setPadding(10, 10, 10, 10);
        title.setGravity(Gravity.CENTER); // this is required to bring it to center.
        title.setTextSize(22);

        TextView text = (TextView) dialog.findViewById(R.id.content);
        final TextView tutorial = (TextView) dialog.findViewById(R.id.tutorial);

        text.setText("복합단지는 10,000포인트 이상부터 생성 가능합니다");

        tutorial.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                tutorial.startAnimation(buttonClick);
                buttonClick.setDuration(500);

                Intent webInt = new Intent(mActivity, WebViewActivity.class);
                webInt.putExtra("url", "http://umanji.com/2016/06/22/input_level_explain/ ");
                mActivity.startActivity(webInt);
            }
        });

        Button dialogButton = (Button) dialog.findViewById(R.id.dialogButtonOK);
        // if button is clicked, close the custom dialog
        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();

    }

    private void showCommunityPanel() {

        final int zoom = (int) mMap.getCameraPosition().zoom;

        try {
            JSONObject params = Helper.getZoomMinMaxLatLngParams(mMap);
//            JSONObject params = new JSONObject();
            params.put("type", TYPE_COMMUNITY);
            params.put("limit", 50);
//            params.put("level", zoom);
            params.put("sort", "point DESC");


//            api_channels_communities_num
//            api_findCommunity  :: Does'n work.
//            api_main_findDistributions  :: all
//            api_channels_get  :: need ID
            mApi.call(api_channels_communities_num, params, new AjaxCallback<JSONObject>() {
                @Override
                public void callback(String url, JSONObject object, AjaxStatus status) {

                    ArrayList<ChannelData> mList = new ArrayList<>();
                    if (status.getCode() == 500) {
                        EventBus.getDefault().post(new ErrorData(TYPE_ERROR_AUTH, TYPE_ERROR_AUTH));
                    } else {
                        try {
                            JSONArray jsonArray = object.getJSONArray("data");

                            for (int idx = 0; idx < jsonArray.length(); idx++) {
                                JSONObject jsonDoc = null;

                                try {
                                    jsonDoc = jsonArray.getJSONObject(idx);
                                    ChannelData doc = new ChannelData(jsonDoc);

                                    mList.add(doc);

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }

                            final Dialog dialog = new Dialog(mActivity);
                            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                            dialog.setContentView(R.layout.dialog_community_panel);

                            final ImageView mMyCommunityCountryBtn;
                            mMyCommunityCountryBtn = (ImageView) dialog.findViewById(R.id.communityCountry);
                            mMyCommunityCountryBtn.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    mMyCommunityCountryBtn.startAnimation(buttonClick);
                                    buttonClick.setDuration(500);
                                    Toast.makeText(mActivity, "clicked", Toast.LENGTH_SHORT).show();
                                }
                            });
                            final ImageView mMyCommunityAdminBtn;
                            mMyCommunityAdminBtn = (ImageView) dialog.findViewById(R.id.communityAdmin);
                            mMyCommunityAdminBtn.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    mMyCommunityAdminBtn.startAnimation(buttonClick);
                                    buttonClick.setDuration(500);
                                    Toast.makeText(mActivity, "clicked", Toast.LENGTH_SHORT).show();
                                }
                            });
                            final ImageView mMyCommunityLocalityBtn;
                            mMyCommunityLocalityBtn = (ImageView) dialog.findViewById(R.id.communityLocality);
                            mMyCommunityLocalityBtn.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    mMyCommunityLocalityBtn.startAnimation(buttonClick);
                                    buttonClick.setDuration(500);
                                    Toast.makeText(mActivity, "clicked", Toast.LENGTH_SHORT).show();
                                }
                            });
                            final ImageView mMyCommunityThoroughBtn;
                            mMyCommunityThoroughBtn = (ImageView) dialog.findViewById(R.id.communityThorough);
                            mMyCommunityThoroughBtn.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    mMyCommunityThoroughBtn.startAnimation(buttonClick);
                                    buttonClick.setDuration(500);
                                    Toast.makeText(mActivity, "clicked", Toast.LENGTH_SHORT).show();
                                }
                            });

                            GridView gridView = (GridView) dialog.findViewById(R.id.gridView1);

                            gridView.setAdapter(new GridAdapter(mActivity, mList));
                            gridView.setNumColumns(4);
                            gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                    ChannelData gridChannel = (ChannelData) parent.getItemAtPosition(position);
                                    Helper.startActivity(mActivity, gridChannel);

//                                    dialog.dismiss();
                                }
                            });

                            dialog.show();
                            mProgress.hide();

                        } catch (JSONException e) {
                            Log.e(TAG, "Error " + e.toString());
                        }
                    }
                }
            });
        } catch (JSONException e) {
            Log.e(TAG, "Error " + e.toString());
        }

    }

    private void showTutorialDialog(String division) {

        final Dialog dialog = new Dialog(mActivity);
        dialog.setContentView(R.layout.dialog_create_tutorial);
        TextView title = (TextView) dialog.findViewById(android.R.id.title);
        title.setText("활동에 대한 사용설명");
        title.setPadding(10, 10, 10, 10);
        title.setGravity(Gravity.CENTER); // this is required to bring it to center.
        title.setTextSize(22);

        Button okBtn = (Button) dialog.findViewById(R.id.dialogOK);

        TextView mMoveMessage1 = (TextView) dialog.findViewById(R.id.contentsLine1);
        TextView mMoveMessage2 = (TextView) dialog.findViewById(R.id.contentsLine2);

        if (division.equals("interior")) {
            mMoveMessage1.setText("일반 건물과 상점과 같은 장소");
            mMoveMessage2.setText("줌레벨 18 ~ 21단계");

            okBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    dialog.cancel();

                }
            });

        } else if (division.equals("towerCrane")) {
            mMoveMessage1.setText("대학교, 공원, 골프장과 같은 넓은 장소");
            mMoveMessage2.setText("줌레벨 15 ~ 17단계");

            okBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    dialog.cancel();

                }
            });

        } else if (division.equals("talk")) {
            mMoveMessage1.setText("이곳에는 아직 정보가 없습니다");
            okBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    dialog.cancel();

                }
            });
        } else {
            mMoveMessage1.setText("구경하기 : 지역과 건물에서의 커뮤니티를 자유롭게 여행하세요");
            okBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    dialog.cancel();

                }
            });
        }

        dialog.show();

    }

    private void loadCommunityMarkers(String communityName) {
        try {
            JSONObject params = Helper.getZoomMinMaxLatLngParams(mMap);
            params.put("keywords", communityName);

            mApi.call(api_main_findDistributions, params, new AjaxCallback<JSONObject>() {
                @Override
                public void callback(String url, JSONObject json, AjaxStatus status) {
                    addCommunityToMap(json);
//                    mCommunityChannel = new ChannelData(json);
                }
            });
        } catch (JSONException e) {
            Log.e(TAG, "Error " + e.toString());
        }
    }

    private void addCommunityToMap(JSONObject jsonObject) {
        try {
            mMap.clear();

            mMarkers = jsonObject.getJSONArray("data");

            if (mMarkers != null) {
                for (int idx = 0; idx < mMarkers.length(); idx++) {
                    ChannelData channelData = new ChannelData(mMarkers.getJSONObject(idx));
                    Helper.addMarkerToMapOnKeyword(mMap, channelData, idx, mActivity);
                }
            }

        } catch (JSONException e) {
            Log.e(TAG, "error " + e.toString());
        }

    }

    private int getNewNoticeCount() {
        return Integer.parseInt(mNotyCountBtn.getText().toString());
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case PERMS_REQUEST:
                if (canAccessLocation()) {
                    initMap();
                }
                break;
        }
    }

    private boolean canAccessLocation() {
        return (hasPermission(Manifest.permission.ACCESS_FINE_LOCATION));
    }

    private boolean hasPermission(String perm) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        } else if (PackageManager.PERMISSION_GRANTED == mActivity.checkSelfPermission(perm)) {
            return true;
        }

        return false;
    }

    private void initMap() {
        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(mActivity.getBaseContext());

        if (status != ConnectionResult.SUCCESS) {

            int requestCode = 10;
            Dialog dialog = GooglePlayServicesUtil.getErrorDialog(status, mActivity, requestCode);
            dialog.show();

        } else {
            mMap = ((MapFragment) mActivity.getFragmentManager().findFragmentById(R.id.mMapFragment)).getMap();

            int paddingInDp = 100;

            final float scale = getResources().getDisplayMetrics().density;
            int paddingInPx = (int) (paddingInDp * scale + 0.5f);

            mMap.setPadding(0, paddingInPx, 0, 0);
            mMap.getUiSettings().setZoomControlsEnabled(true);
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(true);

            initMyLocation();
        }

        initMapEvents();
    }

    protected void initMyLocation() {
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

        double latitude = 37.498039;
        double longitude = 126.9220201;  // 37.498039, 126.9220201
        LatLng latLng = null;

        if (isInitLocationUsed.equals("true")) {

            if(getArguments() != null){
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
                mCurrentMyPosition = new LatLng(latitude, longitude);
                cameraPosition = new CameraPosition.Builder()
                        .target(mCurrentMyPosition)
                        .zoom(18)
                        .bearing(90)
                        .tilt(40)
                        .build();
                mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                mMap.animateCamera(CameraUpdateFactory.zoomTo(18), 2000, null);
            } else {
                Toast.makeText(mActivity, "Location Null", Toast.LENGTH_SHORT).show();
            }


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
                cameraPosition = new CameraPosition.Builder()
                        .target(mCurrentMyPosition)
                        .zoom(12)
                        .bearing(90)
                        .tilt(40)
                        .build();
                mMap.moveCamera(CameraUpdateFactory.newLatLng(mCurrentMyPosition));
                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                mMap.animateCamera(CameraUpdateFactory.zoomTo(12), 2000, null);
            } catch (SecurityException e) {
                Log.e("SecurityException", "SecurityException 에러발생:" + e.toString());
            }
            SharedPreferences.Editor editor = sharedpreferences.edit();
            editor.putString("isInitLocationUsed", "true");
            editor.commit();

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

    /****************************************************
     * init Map Events
     ****************************************************/

    protected void mapClickEvent(LatLng point) {
        mProgress.setMessage("확대하실 곳의 주소를 찾고 있습니다...");
        mProgress.show();

        if (mFocusedMarker != null) {
            mFocusedMarker.remove();
        }
        if (mChannelByPoint != null) {
            mChannelByPoint = null;
        }

        final int zoom = (int) mMap.getCameraPosition().zoom;

        if (mUser != null) {
            if (isComplexCreatable(zoom) && mUser.getPoint() < POINT_CREATE_COMPLEX) {
                int gapPoint = POINT_CREATE_COMPLEX - mUser.getPoint();

                showComplexTutorialDialog();

                mProgress.hide();
                return;
            }
        }

        mLatLngByPoint = point;

        if (zoom >= 15 && zoom <= 21) {
            mProgress.setMessage("장소를 만드실 곳의 주소를 찾고 있습니다...");
            mProgress.show();

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
                                mProgress.hide();
                            }

                        } else {
                            if (isComplexCreatable(zoom)) {
                                startSpotActivity(mChannelByPoint, TYPE_COMPLEX);
                                mProgress.hide();
                            } else if (isSpotCreatable(zoom)) {
                                startSpotActivity(mChannelByPoint, TYPE_SPOT);
                                mProgress.hide();
                            }
                        }
                    }
                });
            } catch (JSONException e) {
                Log.e(TAG, "error " + e.toString());
            }
        } else if (zoom >= 2 && zoom <= 9) {        // zoom >=2 && zoom <=12
            try {
                JSONObject params = new JSONObject();
                params.put("latitude", mLatLngByPoint.latitude);
                params.put("longitude", mLatLngByPoint.longitude);

                mApi.call(api_channels_getByPoint, params, new AjaxCallback<JSONObject>() {
                    @Override
                    public void callback(String url, JSONObject object, AjaxStatus status) {
                        mChannelByPoint = new ChannelData(object);
                        String division = "levelFirst";
                        showJumpDialog(division);
                    }
                });
            } catch (JSONException e) {
                Log.e(TAG, "error " + e.toString());
            }

        } else {                    // zoom >=12 && zoom <=14

            try {
                JSONObject params = new JSONObject();
                params.put("latitude", mLatLngByPoint.latitude);
                params.put("longitude", mLatLngByPoint.longitude);

                mApi.call(api_channels_getByPoint, params, new AjaxCallback<JSONObject>() {
                    @Override
                    public void callback(String url, JSONObject object, AjaxStatus status) {
                        mChannelByPoint = new ChannelData(object);
                        String division = "levelSecond";
                        showJumpDialog(division);
                    }
                });
            } catch (JSONException e) {
                Log.e(TAG, "error " + e.toString());
            }
        }

    }

    protected void initMapEvents() {
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng point) {
                mapClickEvent(point);
            }
        });

        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng point) {
                mapClickEvent(point);
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
                    if (isKeywordCommunityMode) {
                        marker.setInfoWindowAnchor(.5f, 1.0f);
                    }
                    marker.showInfoWindow();
                }

                try {
                    String idx = marker.getSnippet();

                    if (TextUtils.equals(idx, String.valueOf(MARKER_INDEX_CLICKED))) {
                        mClickedChannel = mSelectedChannel;
                    } else {
                        if (mClickedChannel != null) {
                            mClickedChannel = new ChannelData(mMarkers.getJSONObject(Integer.valueOf(idx)));
                        } else {
                        }
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

                ChannelData channelData = null;
                try {
                    if (TextUtils.equals(index, String.valueOf(MARKER_INDEX_BY_POST))) {
                        channelData = mCurrentChannel.getParent();
                        Helper.startActivity(mActivity, channelData);
                    } else if (TextUtils.equals(index, String.valueOf(MARKER_INDEX_CLICKED))) {
                        channelData = mSelectedChannel;
                        Helper.startActivity(mActivity, channelData);
                    } else {
                        if (marker.isDraggable()) {
                            marker.hideInfoWindow();
                        } else {
                            channelData = new ChannelData(mMarkers.getJSONObject(Integer.valueOf(index)));
                            Helper.startActivity(mActivity, channelData);
                        }
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
                mZoomLevelText.setText("" + (int) position.zoom);
                int zoom = (int) position.zoom;

                LatLng center = mMap.getCameraPosition().target;
                getCountryDivision(center.latitude, center.longitude);
                getCenterAddress(center, zoom);
//************************************************************************************************** isKeywordCommunityMode
                if (isKeywordCommunityMode) {
                    if (mMapIsTouched) return;
                    mCenterCircle.setVisibility(View.GONE);

                    if (isBlock) {
                        isBlock = false;
                    } else {

                        if (isComplexCreatable(zoom)) {
                            mZoomBtn.setImageResource(R.drawable.zoom_in);
                            mZoomBtn.setTag(ZOOM_IN);
                            mCenterCircle.setVisibility(View.VISIBLE);
                        } else if (isSpotCreatable(zoom)) {
                            mZoomBtn.setImageResource(R.drawable.zoom_out);
                            mZoomBtn.setTag(ZOOM_OUT);
                            mCenterCircle.setVisibility(View.GONE);
                        } else if (isPoliticTouchable(zoom)) {
                            mZoomBtn.setImageResource(R.drawable.zoom_out);
                            mZoomBtn.setTag(ZOOM_OUT);
                        } else {
                            mZoomBtn.setImageResource(R.drawable.zoom_in);
                            mZoomBtn.setTag(ZOOM_IN);
                        }


                        updateCommunityBtn(zoom);
                        getKeywordCommunityData();  //kakao button toggle
                        loadCommunityMarkers(communityName);
                    }
//************************************************************************************************** isTalkMode (normalMode)
                } else {                  // start main
                    currentZoomLevel = (int) position.zoom;
                    mCenterCircle.setVisibility(View.VISIBLE);
                    getTalkData();

                    if (mMapIsTouched) return;

                    if (isBlock) {
                        isBlock = false;
                    } else {

                        if (isComplexCreatable(zoom)) {
                            mInterior.setImageResource(R.drawable.interior_black);
                            mTowerCrane.setImageResource(R.drawable.tower_crane);
//                            mInfoTextPanel.setTextColor(getResources().getColor(R.color.gray_text));
//                            mInfoTextPanel.setTextSize(15);
                            //mCreateSpotText.setVisibility(View.GONE);
                            mZoomBtn.setImageResource(R.drawable.zoom_in);
                            mZoomBtn.setTag(ZOOM_IN);
                            mCenterCircle.setVisibility(View.VISIBLE);
                        } else if (isSpotCreatable(zoom)) {
                            mInterior.setImageResource(R.drawable.interior);
                            mTowerCrane.setImageResource(R.drawable.tower_crane_black);
                            mInfoTextPanel.setTextColor(getResources().getColor(R.color.red));
                            mInfoTextPanel.setTextSize(15);
                            mZoomBtn.setImageResource(R.drawable.zoom_out);
                            mZoomBtn.setTag(ZOOM_OUT);
                            mCenterCircle.setVisibility(View.GONE);
                        } else if (isKeywordTouchable(zoom)) {
                            //mCreateComplexText.setVisibility(View.GONE);
                            mInfoTextPanel.setText("지역 소식");
                            mInfoTextPanel.setTextSize(20);
                            mInfoTextPanel.setTextColor(getResources().getColor(R.color.gray_text));
                            mZoomBtn.setImageResource(R.drawable.zoom_out);
                            mZoomBtn.setTag(ZOOM_OUT);
                        } else {
                            mZoomBtn.setImageResource(R.drawable.zoom_in);
                            mZoomBtn.setTag(ZOOM_IN);
                        }

                        getKeywordCommunityBtn(zoom);

                        loadData();
                    }
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

    private String getCountryDivision(double LATITUDE, double LONGITUDE) {
        String strAdd = "";
        Geocoder geocoder = new Geocoder(mActivity, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(LATITUDE, LONGITUDE, 1);
            if (addresses != null) {
                Address returnedAddress = addresses.get(0);
                StringBuilder strReturnedAddress = new StringBuilder("");

                for (int i = 0; i < returnedAddress.getMaxAddressLineIndex(); i++) {
                    strReturnedAddress.append(returnedAddress.getAddressLine(i)).append("\n");
                }
                strAdd = strReturnedAddress.toString();
                Log.d("Paul", "CountryCode :" + returnedAddress);
                Log.d("Paul", "CountryCode :" + returnedAddress.getCountryCode());
                Log.d("Paul", "CountryName :" + returnedAddress.getCountryName());
                Log.d("Paul", "admin       :" + returnedAddress.getAdminArea());
                Log.d("Paul", "Locality    :" + returnedAddress.getLocality());
                Log.d("Paul", "Thoroughfare:" + returnedAddress.getThoroughfare());
                Log.d("Paul", "Feature     :" + returnedAddress.getFeatureName());
//                Log.d("Paul", "############");
            } else {
                Log.d("Paul", "No Address returned!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("Paul", "Canont get Address!");
        }
        return strAdd;
    }


    protected void getCenterAddress(LatLng center, final int zoom) {
        try {
            JSONObject params = new JSONObject();
            params.put("latitude", center.latitude);
            params.put("longitude", center.longitude);

            mApi.call(api_channels_getByPoint, params, new AjaxCallback<JSONObject>() {
                @Override
                public void callback(String url, JSONObject object, AjaxStatus status) {
                    mAddressChannel = new ChannelData(object);

                    String countryName = mAddressChannel.getCountryName();
                    String adminArea = mAddressChannel.getAdminArea();
                    String localityName = mAddressChannel.getLocality();
                    String thoroughfare = mAddressChannel.getThoroughfare();

                    if (zoom >= 6 && zoom < 8) {          // 대한민국
                        mCurrentAddress.setText(countryName);
                        currentAddress = countryName + " 지역정보";
                    } else if (zoom == 8) {     // 도
                        mCurrentAddress.setText(countryName + " " + adminArea);
                        currentAddress = countryName + " " + adminArea + " 지역정보";
                    } else if (zoom > 8 && zoom < 11) {     // 서울시 , 시
                        mCurrentAddress.setText(adminArea + " " + localityName);
                        currentAddress = adminArea + " " + localityName + " 지역정보";
                    } else if (zoom > 10 && zoom < 14) {   // 구
                        mCurrentAddress.setText(adminArea + " " + localityName);
                        currentAddress = adminArea + " " + localityName + " 지역정보";
                    } else if (zoom > 13 && zoom <= 21) {  // 동
                        mCurrentAddress.setText(adminArea + " " + localityName + " " + thoroughfare);
                        currentAddress = adminArea + " " + localityName + " " + thoroughfare + " 지역정보";
                    } else {
                        mCurrentAddress.setText("준비중입니다");
                    }
                }
            });

        } catch (JSONException e) {
            Log.e(TAG, "error " + e.toString());
        }

    }

    protected void initTabAdapter(View view, ChannelData fetchChannel) {
        mViewPager = (ViewPager) view.findViewById(R.id.viewPaper);
        mTabLayout = (TabLayout) view.findViewById(R.id.tabs);
        mCommunityAdapter = new BaseTabAdapter(getActivity().getSupportFragmentManager());

        addFragmentToTabAdapter(mCommunityAdapter, fetchChannel);

        mViewPager.setAdapter(mCommunityAdapter);
        mTabLayout.setupWithViewPager(mViewPager);

//        setTabSelect();

        onTabSelected(mTabLayout);
    }

    protected void setTabSelect() {
        TabLayout.Tab tab;
        switch (mTabType) {
            case TAB_POSTS:
                tab = mTabLayout.getTabAt(0);
                break;
            case TAB_MEMBERS:
                tab = mTabLayout.getTabAt(1);
                break;
            case TAB_COMMUNITIES:
                tab = mTabLayout.getTabAt(2);
                break;
            case TAB_ABOUT:
                tab = mTabLayout.getTabAt(3);
                break;
            default:
                tab = mTabLayout.getTabAt(0);
                break;
        }

        tab.select();
    }

    protected void onTabSelected(TabLayout tabLayout) {
        tabLayout.setOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager) {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                super.onTabSelected(tab);
                mCurrentTapPosition = tab.getPosition();

                /*switch (mCurrentTapPosition) {
                    case 0:
                        mFab.setImageResource(R.drawable.ic_discuss);
                        if (AuthHelper.isLogin(mActivity)) {
                            mFab.setVisibility(View.VISIBLE);
                        }
                        break;
                    case 1: case 2: case 3: case 4:
                        mFab.setVisibility(View.GONE);
                        break;

                }*/

            }
        });
    }


    protected void addFragmentToTabAdapter(BaseTabAdapter adapter, ChannelData thisChannel) {

        Bundle bundle = new Bundle();
        bundle.putString("channel", thisChannel.getJsonObject().toString());
        adapter.addFragment(PostListKeywordFragment.newInstance(bundle), "정보광장");
        adapter.addFragment(CommunityListKeywordFragment.newInstance(bundle), "단체들");
        /*
        if (mChannel2.getJsonObject().toString()!= null){
            Bundle bundle2 = new Bundle();
            bundle2.putString("channel", mChannel2.getJsonObject().toString());
            adapter.addFragment(PostListKeywordFragment.newInstance(bundle), "정보광장");
            adapter.addFragment(CommunityListKeywordFragment.newInstance(bundle), "단체들");
        }

        if (mChannel3.getJsonObject().toString()!= null){
            Bundle bundle3 = new Bundle();
            bundle3.putString("channel", mChannel3.getJsonObject().toString());
            adapter.addFragment(PostListKeywordFragment.newInstance(bundle), "정보광장");
            adapter.addFragment(CommunityListKeywordFragment.newInstance(bundle), "단체들");
        }*/

    }

    private void updateCommunityBtn(final int zoom) {
        if (mCurrentMyPosition != null) {
//            mToCommunityBtn.setVisibility(View.VISIBLE);
//            mCommunityGoToPanel.setVisibility(View.VISIBLE);

            try {
                JSONObject params = new JSONObject();
                params.put("latitude", mCurrentMyPosition.latitude);
                params.put("longitude", mCurrentMyPosition.longitude);

                mApi.call(api_channels_getByPoint, params, new AjaxCallback<JSONObject>() {
                    @Override
                    public void callback(String url, JSONObject json, AjaxStatus status) {
                        mAddressChannel = new ChannelData(json);

                        mToCommunityBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                try {
                                    JSONObject params = new JSONObject();
                                    params.put("name", communityName);


                                    if (zoom < 8) {
                                        params.put("countryCode", mAddressChannel.getCountryCode());
                                        params.put("level", 2);
                                    } else if (zoom < 12) {
                                        params.put("countryCode", mAddressChannel.getCountryCode());
                                        params.put("adminArea", mAddressChannel.getAdminArea());

                                        params.put("level", 8);
                                    } else if (zoom < 14) {
                                        params.put("countryCode", mAddressChannel.getCountryCode());
                                        params.put("adminArea", mAddressChannel.getAdminArea());
                                        params.put("locality", mAddressChannel.getLocality());
                                        params.put("level", 12);
                                    } else {
                                        params.put("countryCode", mAddressChannel.getCountryCode());
                                        params.put("adminArea", mAddressChannel.getAdminArea());
                                        params.put("locality", mAddressChannel.getLocality());
                                        params.put("thoroughfare", mAddressChannel.getThoroughfare());
                                        params.put("level", 14);
                                    }

                                    mApi.call(api_findCommunity, params, new AjaxCallback<JSONObject>() {
                                        @Override
                                        public void callback(String url, JSONObject json, AjaxStatus status) {
                                            ChannelData channelData = new ChannelData(json);
                                            if (TextUtils.isEmpty(channelData.getId())) {
                                                Toast.makeText(mActivity, "지역내에 단체가 존재하지 않습니다.", Toast.LENGTH_SHORT).show();
                                            } else {
                                                Helper.startActivity(mActivity, channelData);
                                            }
                                        }
                                    });

                                } catch (JSONException e) {
                                    Log.e(TAG, "error " + e.toString());
                                }
                            }
                        });
                    }
                });


                String communityBtnName = communityName;

                if (zoom < 8) { // 대한민국
                    mToCommunityBtn.setText("국가단위 " + communityBtnName + " 단체");
                } else if (zoom < 12) { // 도시단위
                    mToCommunityBtn.setText("도시단위 " + communityBtnName + " 단체");
                } else if (zoom < 14) { // 구군단위
                    mToCommunityBtn.setText("구군단위 " + communityBtnName + " 단체");
                } else { //동단위
                    mToCommunityBtn.setText("동단위 " + communityBtnName + " 단체");
                }

            } catch (JSONException e) {
                Log.e(TAG, "error " + e.toString());
            }

        } else {
            mToCommunityBtn.setVisibility(View.GONE);
        }

    }


    private void getKeywordCommunityBtn(int zoom) {
        mLauncherLevel2.setVisibility(View.GONE);
        mLauncherLevel3.setVisibility(View.GONE);
        mLauncherLevel4.setVisibility(View.GONE);
        mLauncherLevel5.setVisibility(View.GONE);
        mLauncherLevel6.setVisibility(View.GONE);
        mLauncherLevel7.setVisibility(View.GONE);
        mLauncherLevel8.setVisibility(View.GONE);

        if (zoom == 2) {
            mLauncherLevel2.setVisibility(View.VISIBLE);

            try {
                JSONObject params1 = new JSONObject();
                params1.put("name", "환경");

                mApi.call(api_findCommunity, params1, new AjaxCallback<JSONObject>() {
                    @Override
                    public void callback(String url, JSONObject json, AjaxStatus status) {
                        mEnvironmentChannel = new ChannelData(json);
                    }
                });

                JSONObject params2 = new JSONObject();
                params2.put("name", "에너지");

                mApi.call(api_findCommunity, params2, new AjaxCallback<JSONObject>() {
                    @Override
                    public void callback(String url, JSONObject json, AjaxStatus status) {
                        mEnergyChannel = new ChannelData(json);
                    }
                });

            } catch (JSONException e) {
                Log.e(TAG, "error " + e.toString());
            }


        } else if (zoom == 3) {
            mLauncherLevel3.setVisibility(View.VISIBLE);

            try {
                JSONObject params1 = new JSONObject();
                params1.put("name", "철학");

                mApi.call(api_findCommunity, params1, new AjaxCallback<JSONObject>() {
                    @Override
                    public void callback(String url, JSONObject json, AjaxStatus status) {
                        mSpiritualChannel = new ChannelData(json);
                    }
                });

            } catch (JSONException e) {
                Log.e(TAG, "error " + e.toString());
            }

        } else if (zoom == 4) {
            mLauncherLevel4.setVisibility(View.VISIBLE);

            try {
                JSONObject params1 = new JSONObject();
                params1.put("name", "역사");

                mApi.call(api_findCommunity, params1, new AjaxCallback<JSONObject>() {
                    @Override
                    public void callback(String url, JSONObject json, AjaxStatus status) {
                        mHistoryChannel = new ChannelData(json);
                    }
                });

            } catch (JSONException e) {
                Log.e(TAG, "error " + e.toString());
            }

        } else if (zoom == 5) {
            mLauncherLevel5.setVisibility(View.VISIBLE);

            try {
                JSONObject params1 = new JSONObject();
                params1.put("name", "통일");

                mApi.call(api_findCommunity, params1, new AjaxCallback<JSONObject>() {
                    @Override
                    public void callback(String url, JSONObject json, AjaxStatus status) {
                        mUnityChannel = new ChannelData(json);
                    }
                });

            } catch (JSONException e) {
                Log.e(TAG, "error " + e.toString());
            }
        } else if (zoom == 6) {
            mLauncherLevel6.setVisibility(View.VISIBLE);

            try {
                JSONObject params1 = new JSONObject();
                params1.put("name", "건강");

                mApi.call(api_findCommunity, params1, new AjaxCallback<JSONObject>() {
                    @Override
                    public void callback(String url, JSONObject json, AjaxStatus status) {
                        mHealthChannel = new ChannelData(json);
                    }
                });

                JSONObject params2 = new JSONObject();
                params2.put("name", "정치");

                mApi.call(api_findCommunity, params2, new AjaxCallback<JSONObject>() {
                    @Override
                    public void callback(String url, JSONObject json, AjaxStatus status) {
                        mPoliticsChannel = new ChannelData(json);
                    }
                });

            } catch (JSONException e) {
                Log.e(TAG, "error " + e.toString());
            }
        } else if (zoom == 7) {
            mLauncherLevel7.setVisibility(View.VISIBLE);

            try {
                JSONObject params1 = new JSONObject();
                params1.put("name", "등산");

                mApi.call(api_findCommunity, params1, new AjaxCallback<JSONObject>() {
                    @Override
                    public void callback(String url, JSONObject json, AjaxStatus status) {
                        mClimbChannel = new ChannelData(json);
                    }
                });


                JSONObject params2 = new JSONObject();
                params2.put("name", "골프");

                mApi.call(api_findCommunity, params2, new AjaxCallback<JSONObject>() {
                    @Override
                    public void callback(String url, JSONObject json, AjaxStatus status) {
                        mGolfChannel = new ChannelData(json);
                    }
                });

            } catch (JSONException e) {
                Log.e(TAG, "error " + e.toString());
            }
        } else if (zoom == 8 || zoom >= 9) {
            mLauncherLevel8.setVisibility(View.VISIBLE);
        }
    }
/*

    private void loadMainPosts() {
        mAdapter.resetDocs();
        mAdapter.setCurrentPage(0);

        loadMoreMainPosts();
    }
*/

    //*******                광고로직 테스트

    private void loadMainAds() {

        try {
            JSONObject params = Helper.getZoomMinMaxLatLngParams(mMap);
            params.put("zoom", (int) mMap.getCameraPosition().zoom);  // level -> zoom

            // origin api :: api_main_findAds
            mApi.call(api_main_findAds2, params, new AjaxCallback<JSONObject>() {
                @Override
                public void callback(String url, JSONObject json, AjaxStatus status) {
                    if (json == null) {
                        Random rd = new Random();
                        int randomNum = rd.nextInt(3);
                        if (randomNum == 0) {
                            mAdsImage.setImageResource(R.drawable.ads_umanji_guide);
                        } else if (randomNum == 2) {
                            mAdsImage.setImageResource(R.drawable.ad_sample01);
                        } else if (randomNum == 1) {
                            mAdsImage.setImageResource(R.drawable.ad_sample);
                        }

                    } else {
                        addAdsToMap(json);
                    }
                }
            });
        } catch (JSONException e) {
            Log.e(TAG, "Error " + e.toString());
        }
        mProgress.hide();
    }


    private void addAdsToMap(JSONObject jsonObject) {
        try {
            mAds = jsonObject.getJSONArray("data");

            if (mAds.length() != 0) {

                Random rd = new Random();

                int randomNum = rd.nextInt(mAds.length());

                mAdChannel = new ChannelData(mAds.getJSONObject(randomNum));
                String photo = mAdChannel.getPhoto();
                if (!TextUtils.isEmpty(photo)) {
                    Glide.with(mActivity)
                            .load(photo)
                            .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                            .into(mAdsImage);

                    mAdsImage.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            try {
                                JSONObject params = new JSONObject();
                                params.put("id", mAdChannel.getParent().getId());
                                mApi.call(api_channels_get, params, new AjaxCallback<JSONObject>() {
                                    @Override
                                    public void callback(String url, JSONObject object, AjaxStatus status) {
                                        ChannelData channel = new ChannelData(object);
                                        Helper.startActivity(mActivity, channel, channel.getType());
                                    }
                                });
                            } catch (JSONException e) {
                                Log.e(TAG, "error " + e.toString());
                            }
                        }
                    });
                }
            } else {
                mAdsImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent webIntent = new Intent(mActivity, WebViewActivity.class);
                        mActivity.startActivity(webIntent);
                    }
                });
                Random rd = new Random();
                int randomNum = rd.nextInt(3);
                if (randomNum == 0) {
                    mAdsImage.setImageResource(R.drawable.ads_umanji_guide);
                } else if (randomNum == 2) {
                    mAdsImage.setImageResource(R.drawable.ad_sample01);
                } else if (randomNum == 1) {
                    mAdsImage.setImageResource(R.drawable.ad_sample);
                }
            }

        } catch (JSONException e) {
            Log.e(TAG, "error " + e.toString());
        }

    }

    /*  private void loadMoreMainPosts() {
          isLoading = true;
  //        mProgress.show();

          try {
              JSONObject params = Helper.getZoomMinMaxLatLngParams(mMap);
              params.put("page", mAdapter.getCurrentPage());
              params.put("limit", 5);
              //params.put("sort", "point DESC");

              mApi.call(api_main_findPosts, params, new AjaxCallback<JSONObject>() {
                  @Override
                  public void callback(String url, JSONObject object, AjaxStatus status) {
                      if (status.getCode() == 500) {
                          EventBus.getDefault().post(new ErrorData(TYPE_ERROR_AUTH, TYPE_ERROR_AUTH));
                      } else {
                          try {
                              JSONArray jsonArray = object.getJSONArray("data");
                              if (jsonArray.length() != 0) {
  //                                mlayout.setBackgroundResource(R.color.feed_bg);
  //                                mSlidingUpPanelLayout.setPanelHeight(Helper.dpToPixel(mActivity, 120));
                                  isTalkFlag= true;
                                  mTalk.setImageResource(R.drawable.button_kakao);
  //                                mTouchView.setEnabled(false);


                                  for (int idx = 0; idx < jsonArray.length(); idx++) {
                                      JSONObject jsonDoc = jsonArray.getJSONObject(idx);
                                      ChannelData doc = new ChannelData(jsonDoc);

                                      if (doc != null && doc.getOwner() != null && !TextUtils.isEmpty(doc.getOwner().getId())) {
                                          mAdapter.addBottom(doc);
                                      }
                                  }

                              } else {
  //                                mlayout.setBackgroundResource(R.drawable.empty_main_post);
                                  mSlidingUpPanelLayout.setPanelHeight(Helper.dpToPixel(mActivity, 0));
                                  isTalkFlag = false;
                                  mTalk.setImageResource(R.drawable.button_kakao_black);

                              }

                              isLoading = false;
                              mAdapter.notifyDataSetChanged();
                          } catch (JSONException e) {
                              Log.e(TAG, "Error " + e.toString());
                          }
                      }
                  }
              });
          } catch (JSONException e) {
              Log.e(TAG, "Error " + e.toString());
          }

          mAdapter.setCurrentPage(mAdapter.getCurrentPage() + 1);
          mProgress.hide();
      }
  */
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
        } catch (JSONException e) {
            Log.e(TAG, "error " + e.toString());
        }

    }

    private void login(AuthData auth) {
        if (checkPlayServices()) {
            Intent intent = new Intent(mActivity, GcmRegistrationIntentService.class);
            mActivity.startService(intent);
        }

        mUser = auth.getUser();
        AuthHelper.login(mActivity, auth);

        updateView();
    }

    private void logout() {
        mUser = null;
        AuthHelper.logout(mActivity);

        updateView();
    }

    private static boolean isKeywordTouchable(int zoom) {
        if (zoom >= 2 && zoom <= 7) {
            return true;
        } else {
            return false;
        }
    }

    private static boolean isTalkTouchable(int zoom) {
        if (zoom >= 8) {
            return true;
        } else {
            return false;
        }
    }

    private static boolean isPoliticTouchable(int zoom) {
        if (zoom >= 6 && zoom <= 7) {
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

    private void loadMainMarkers() {
        //mProgress.show();

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

            JSONObject params1 = new JSONObject();
            params1.put("name", "대한민국 정보센터");

            mApi.call(api_channels_findOne, params1, new AjaxCallback<JSONObject>() {
                @Override
                public void callback(String url, JSONObject json, AjaxStatus status) {
                    mHomeChannel = new ChannelData(json);
                    //mHomeChannel.setType("INFO_CENTER");
                }
            });

        } catch (JSONException e) {
            Log.e(TAG, "Error " + e.toString());
        }
        mProgress.hide();

    }

    private void addChannelsToMap(JSONObject jsonObject) {
        Double fetchLat;
        Double fetchLon;
        JSONObject params = Helper.getZoomMinMaxLatLngParams(mMap);

        try {
            mMap.clear();

            fetchLat = (Double) params.get("minLatitude") + 0.001;  // 세로
            fetchLon = (Double) params.get("minLongitude") + 0.002;  // 가로


            int zoom = (int) mMap.getCameraPosition().zoom;
            if (zoom >= 15) {
//                iconDrag(fetchLat, fetchLon);
            } else {

            }

            mMarkers = jsonObject.getJSONArray("data");

            int idx = 0;

            if (mCurrentChannel != null) {
                if (Helper.isInVisibleResion(mMap, new LatLng(mCurrentChannel.getLatitude(), mCurrentChannel.getLongitude()))) {
                    mFocusedMarker = Helper.addMarkerToMap(mMap, mCurrentChannel, MARKER_INDEX_BY_POST, mActivity);
                } else {
                    mCurrentChannel = null;
                }
            }

            if (mClickedChannel != null) {
                if (Helper.isInVisibleResion(mMap, new LatLng(mClickedChannel.getLatitude(), mClickedChannel.getLongitude()))) {
                    mFocusedMarker = Helper.addMarkerToMap(mMap, mClickedChannel, MARKER_INDEX_CLICKED, mActivity);
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
                        Helper.addMarkerToMap(mMap, channelData, idx, mActivity);
                    } else if (mSelectedChannel != null && !TextUtils.equals(mSelectedChannel.getId(), channelData.getId())) {
                        Helper.addMarkerToMap(mMap, channelData, idx, mActivity);
                    } else {
                        Helper.addMarkerToMap(mMap, channelData, idx, mActivity);
                    }
                }
            }

        } catch (JSONException e) {
            Log.e(TAG, "error " + e.toString());
        }

    }

    private void showJumpDialog(String division) {
        mProgress.hide();

        final Dialog dialog = new Dialog(mActivity);
        dialog.setContentView(R.layout.dialog_zoom_changed);
        TextView title = (TextView) dialog.findViewById(android.R.id.title);
        title.setText("아래로 이동합니다");
//        title.setBackgroundResource(R.drawable.gradient);
        title.setPadding(10, 10, 10, 10);
        title.setGravity(Gravity.CENTER); // this is required to bring it to center.
        title.setTextSize(22);

        TextView mMoveMessage = (TextView) dialog.findViewById(R.id.address);
        mMoveMessage.setText(Helper.getShortAddress(mChannelByPoint));

        Button okBtn = (Button) dialog.findViewById(R.id.moveDialogOK);
        Button cancelBtn = (Button) dialog.findViewById(R.id.moveDialogCancel);


        if (division.equals("levelFirst")) {

            okBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (TextUtils.isEmpty(mChannelByPoint.getId())) {

                        LatLng tmpPoint = Helper.getAdjustedPoint(mMap, mLatLngByPoint);
                        mMap.moveCamera(CameraUpdateFactory.newLatLng(tmpPoint));
                        mMap.animateCamera(CameraUpdateFactory.zoomTo(12), 2000, null);
                    } else {
                    }
                    dialog.cancel();

                }
            });

        } else {

            okBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (TextUtils.isEmpty(mChannelByPoint.getId())) {

                        LatLng tmpPoint = Helper.getAdjustedPoint(mMap, mLatLngByPoint);
                        mMap.moveCamera(CameraUpdateFactory.newLatLng(tmpPoint));
                        mMap.animateCamera(CameraUpdateFactory.zoomTo(15), 2000, null);
                    } else {
                    }
                    dialog.cancel();

                }
            });

        }

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });

        dialog.show();
    }

    private void showCreateComplexDialog() {
        mProgress.hide();

        final Dialog dialog = new Dialog(mActivity);
        dialog.setContentView(R.layout.dialog_create_complex);
        TextView title = (TextView) dialog.findViewById(android.R.id.title);
        title.setText("선택");
        title.setPadding(10, 10, 10, 10);
        title.setGravity(Gravity.CENTER);
        title.setTextSize(22);

        TextView text = (TextView) dialog.findViewById(R.id.address);
        text.setText(Helper.getShortAddress(mChannelByPoint));

        Button btnCreate = (Button) dialog.findViewById(R.id.create);
        Button btnFootPrint = (Button) dialog.findViewById(R.id.footPrint);
        Button btnCancel = (Button) dialog.findViewById(R.id.cancel);
        btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Helper.startCreateActivity(mActivity, mChannelByPoint, TYPE_COMPLEX);
                dialog.dismiss();
            }
        });

        btnFootPrint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mActivity, "Foot Print", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMarkerByPoint.remove();
                dialog.cancel();
            }
        });

        dialog.show();

    }

    private void showCreateSpotDialog() {
        mProgress.hide();

        final Dialog dialog = new Dialog(mActivity);
        dialog.setContentView(R.layout.dialog_create_spot);
        TextView title = (TextView) dialog.findViewById(android.R.id.title);
        title.setText("선택");
        title.setPadding(10, 10, 10, 10);
        title.setGravity(Gravity.CENTER);
        title.setTextSize(22);

        TextView text = (TextView) dialog.findViewById(R.id.address);
        text.setText(Helper.getFullAddress(mChannelByPoint));

        Button btnCreate = (Button) dialog.findViewById(R.id.create);
        Button btnFootPrint = (Button) dialog.findViewById(R.id.footPrint);
        Button btnCancel = (Button) dialog.findViewById(R.id.cancel);
        btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mProgress.show();
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
                dialog.dismiss();
            }
        });

        btnFootPrint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mActivity, "Foot Print", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMarkerByPoint.remove();
                dialog.cancel();
            }
        });

        dialog.show();

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

/*
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

                        if (mFocusedMarker != null) {
                            mFocusedMarker.remove();
                        }

                        mMap.animateCamera(CameraUpdateFactory.newLatLng(mPointByPost), 500, null);
                        mFocusedMarker = Helper.addMarkerToMap(mMap, mCurrentChannel, MARKER_INDEX_BY_POST, mActivity);
                    }

                    if (!isLoading) {
                        if (mPreFocusedItem == (totalItemCount - 2)) {
//                            loadMoreMainPosts();
                        }
                    }
                }
            }
        });

    }
*/

/*

    protected RecyclerView initMainListView(View view) {
        RecyclerView rView = (RecyclerView) view.findViewById(R.id.recyclerView);
        rView.setItemViewCacheSize(iItemViewCacheSize);
        mAdapter = new PostListAdapter(mActivity, this);
        rView.setAdapter(mAdapter);

        addOnScrollListener(rView);
        return rView;
    }
*/

    private void loadNewNoties() {
        try {
            JSONObject params = new JSONObject();
            params.put("read", false);
            mApi.call(api_noites_new_count, params, new AjaxCallback<JSONObject>() {
                @Override
                public void callback(String url, JSONObject object, AjaxStatus status) {
                    super.callback(url, object, status);
                    int notyCount = object.optInt("data");
                    if (notyCount > 0) {
                        mNotyCountBtn.setVisibility(View.VISIBLE);
                        mNotyCountBtn.setText(String.valueOf(notyCount));
                    } else {
                        mNotyCountBtn.setVisibility(View.GONE);
                        mNotyCountBtn.setText("0");
                    }
                }
            });
        } catch (JSONException e) {
            Log.e(TAG, "Error " + e.toString());
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
