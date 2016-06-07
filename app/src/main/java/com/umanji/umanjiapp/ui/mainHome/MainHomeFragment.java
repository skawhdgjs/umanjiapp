package com.umanji.umanjiapp.ui.mainHome;

import android.content.Intent;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.bumptech.glide.Glide;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.umanji.umanjiapp.R;
import com.umanji.umanjiapp.analytics.ApplicationController;
import com.umanji.umanjiapp.gcm.GcmRegistrationIntentService;
import com.umanji.umanjiapp.helper.AuthHelper;
import com.umanji.umanjiapp.helper.Helper;
import com.umanji.umanjiapp.model.AuthData;
import com.umanji.umanjiapp.model.ChannelData;
import com.umanji.umanjiapp.model.ErrorData;
import com.umanji.umanjiapp.model.SuccessData;
import com.umanji.umanjiapp.ui.BaseFragment;
import com.umanji.umanjiapp.ui.channel.profile.ProfileActivity;
import com.umanji.umanjiapp.ui.main.MainActivity;
import com.umanji.umanjiapp.ui.mainHome.localCommunity.CreateLocalCommunityActivity;
import com.umanji.umanjiapp.ui.modal.WebViewActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;


import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

import de.greenrobot.event.EventBus;

public class MainHomeFragment extends BaseFragment {
    private static final String TAG = "MainHomeFragment";

    protected TextView mUmanji;
    protected TextView mName;
    protected ImageView mUserPhoto;
    private ChannelData mUser;
    protected LinearLayout mLookAround;
    protected LinearLayout mCreateCommunity;

    protected TextView mCommunityCount;

    protected RelativeLayout mMyCommunityOne;
    protected RelativeLayout mMyCommunityTwo;
    protected RelativeLayout mMyCommunityThree;

    protected ChannelData mMyCommunityChannelOne;
    protected ChannelData mMyCommunityChannelTwo;
    protected ChannelData mMyCommunityChannelThree;

    protected TextView mEmpty1;
    protected TextView mEmpty2;
    protected TextView mEmpty3;


    private Button mNotyCountBtn;

    JSONArray jsonArray;

    private ChannelData mChannelByPoint;
    LatLng mCurrentMyPosition;
    int num = 0;


    public static MainHomeFragment newInstance(Bundle bundle) {
        MainHomeFragment fragment = new MainHomeFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        double latitude = 37.642443934398;
        double longitude = 126.977429352700;

        mCurrentMyPosition = new LatLng(latitude, longitude);

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


        return super.onCreateView(inflater, container, savedInstanceState);


    }

    @Override
    public View getView(LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.activity_main_home, container, false);
    }

    @Override
    public void initWidgets(View view) {

        if (AuthHelper.isLogin(mActivity)) {
            loginByToken();
        } else {
            //updateView();
        }

        mUmanji = (TextView) view.findViewById(R.id.umanji);
        mUmanji.setOnClickListener(this);

        mUserPhoto = (ImageView) view.findViewById(R.id.userPhoto);
        mUserPhoto.setOnClickListener(this);

        mNotyCountBtn = (Button) view.findViewById(R.id.mNotyCount);
        mNotyCountBtn.setOnClickListener(this);
        mNotyCountBtn.setText("0");

        mLookAround = (LinearLayout) view.findViewById(R.id.community_lookaround);
        mLookAround.setOnClickListener(this);

        mCreateCommunity = (LinearLayout) view.findViewById(R.id.create_community);
        mCreateCommunity.setOnClickListener(this);

        mMyCommunityOne = (RelativeLayout) view.findViewById(R.id.myCommunityOne);
        mMyCommunityOne.setOnClickListener(this);

        mMyCommunityTwo = (RelativeLayout) view.findViewById(R.id.myCommunityTwo);
        mMyCommunityTwo.setOnClickListener(this);

        mMyCommunityThree = (RelativeLayout) view.findViewById(R.id.myCommunityThree);
        mMyCommunityThree.setOnClickListener(this);

        mCommunityCount = (TextView) view.findViewById(R.id.communityCount);

        mEmpty1 = (TextView) view.findViewById(R.id.empty1);
        mEmpty2 = (TextView) view.findViewById(R.id.empty2);
        mEmpty3 = (TextView) view.findViewById(R.id.empty3);

        loadData();


    }

    private void loadCommunity() {

        try {
            JSONObject params = new JSONObject();
            params.put("level", 2);
            params.put("sort", "point DESC");
            params.put("owner", mUser.getOwner());
            params.put("type", TYPE_COMMUNITY);

            mApi.call(api_channels_communities_find, params, new AjaxCallback<JSONObject>() {
                @Override
                public void callback(String url, JSONObject object, AjaxStatus status) {

                    if(status.getCode() == 500) {
                        EventBus.getDefault().post(new ErrorData(TYPE_ERROR_AUTH, TYPE_ERROR_AUTH));
                    }else {
                        try {
                            ArrayList<ChannelData> channels;
                            JSONArray jsonArray = object.getJSONArray("data");
                            for(int idx = 0; idx < 3; idx++) {
                                JSONObject jsonDoc = jsonArray.getJSONObject(idx);
                                ChannelData doc = new ChannelData(jsonDoc);
                                switch(idx){
                                    case 0:
                                        mMyCommunityChannelOne = doc;
                                        mEmpty1.setVisibility(View.GONE);
                                        break;
                                    case 1:
                                        mMyCommunityChannelTwo = doc;
                                        mEmpty2.setVisibility(View.GONE);
                                        break;
                                    case 2:
                                        mMyCommunityChannelThree = doc;
                                        mEmpty3.setVisibility(View.GONE);
                                        break;
                                }

                            }
                            Toast.makeText(mActivity, "called Community", Toast.LENGTH_SHORT).show();

                            updateView();

                        } catch (JSONException e) {
                            Log.e(TAG, "Error " + e.toString());
                        }

                    }
                }
            });
        } catch(JSONException e) {
            Log.e(TAG, "error " + e.toString());
        }

        return;
    }

    @Override
    public void loadData() {

        try {

            JSONObject paramsD = new JSONObject();
            paramsD.put("latitude", mCurrentMyPosition.latitude);
            paramsD.put("longitude", mCurrentMyPosition.longitude);

            mApi.call(api_channels_getByPoint, paramsD, new AjaxCallback<JSONObject>() {
                @Override
                public void callback(String url, JSONObject object, AjaxStatus status) {
                    mChannelByPoint = new ChannelData(object);

                    if (TextUtils.isEmpty(mChannelByPoint.getId())) {
                        if (AuthHelper.isLogin(mActivity)) {

                            loadCommunity();
                        } else {
                            //Helper.startSigninActivity(mActivity, mCurrentMyPosition);
                        }

                    } else {

                    }
                }
            });

            JSONObject params = new JSONObject();
            params.put("level", 2);
            params.put("type", TYPE_COMMUNITY);
            params.put("limit", 1000);
//            params.put("sort", "point DESC");

//            api_channels_communities_find
//            api_channels_communities_num
            mApi.call(api_channels_communities_find, params, new AjaxCallback<JSONObject>() {
                @Override
                public void callback(String url, JSONObject object, AjaxStatus status) {

                    if (status.getCode() == 500) {
                        EventBus.getDefault().post(new ErrorData(TYPE_ERROR_AUTH, TYPE_ERROR_AUTH));
                    } else {
                        try {
                            jsonArray = object.getJSONArray("data");
                            num = jsonArray.length();

                            updateView();

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


    @Override
    public void updateView() {

        String strNumber = NumberFormat.getNumberInstance().format(num);
        mCommunityCount.setText(strNumber);

        if (AuthHelper.isLogin(mActivity)) {
            mUserPhoto.setVisibility(View.VISIBLE);
            String userPhoto = mUser.getPhoto();
            if (!TextUtils.isEmpty(userPhoto)) {
                Glide.with(mActivity)
                        .load(userPhoto)
                        .placeholder(R.drawable.empty)
                        .animate(R.anim.abc_fade_in)
                        .override(40, 40)
                        .into(mUserPhoto);
            } else {
                Glide.with(mActivity)
                        .load(R.drawable.avatar_default_0)
                        .placeholder(R.drawable.empty)
                        .override(40, 40)
                        .into(mUserPhoto);
            }
        } else {
            /*Glide.with(mActivity)
                    .load(R.drawable.login)
                    .animate(R.anim.abc_fade_in)
                    .override(40, 40)
                    .into(mUserPhoto);*/
            mUserPhoto.setImageResource(R.drawable.icon_user_person);
        }
    }

    public void loginupdateView() {

    }

    @Override
    public void onEvent(SuccessData event) {
        super.onEvent(event);

        ChannelData channelData = new ChannelData(event.response);

        switch (event.type) {
            case api_channels_create:

                break;

            case api_channels_communities_find:
                break;

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
        }
    }

    public void onEvent(ErrorData event) {

        switch (event.type) {
            case TYPE_ERROR_AUTH:
                Helper.startSigninActivity(mActivity, mCurrentMyPosition);
                break;
        }
    }

    private AlphaAnimation buttonClick = new AlphaAnimation(0F, 1F);

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.umanji:
                mUmanji.startAnimation(buttonClick);
                buttonClick.setDuration(500);

                Intent webInt = new Intent(mActivity, WebViewActivity.class);
                webInt.putExtra("url", "http://blog.naver.com/mothcar/220720111996");
                mActivity.startActivity(webInt);
                break;

            case R.id.userPhoto:
                mUserPhoto.startAnimation(buttonClick);
                buttonClick.setDuration(500);

                if (AuthHelper.isLogin(mActivity) && mUser != null) {
                    Intent intent = new Intent(mActivity, ProfileActivity.class);
                    Bundle bundle = new Bundle();
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

            case R.id.create_community:
                mCreateCommunity.startAnimation(buttonClick);
                buttonClick.setDuration(500);

                if (AuthHelper.isLogin(mActivity) && mUser != null) {
                    Intent intent = new Intent(mActivity, CreateLocalCommunityActivity.class);
                    Bundle bundle = new Bundle();
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

            case R.id.community_lookaround:
                mLookAround.startAnimation(buttonClick);
                buttonClick.setDuration(500);

                Intent lookInt = new Intent(mActivity, MainActivity.class);
                startActivity(lookInt);
                break;

            case R.id.myCommunityOne:
                mMyCommunityOne.startAnimation(buttonClick);
                buttonClick.setDuration(500);

                if(mMyCommunityChannelOne != null){
                    Helper.startActivity(mActivity, mMyCommunityChannelOne);
                } else {
                    Toast.makeText(mActivity, "만든 커뮤니티가 없습니다", Toast.LENGTH_SHORT).show();
                }

                break;

            case R.id.myCommunityTwo:
                mMyCommunityTwo.startAnimation(buttonClick);
                buttonClick.setDuration(500);

                if(mMyCommunityChannelTwo != null){
                    Helper.startActivity(mActivity, mMyCommunityChannelTwo);
                } else {
                    Toast.makeText(mActivity, "나의 커뮤니티가 없습니다", Toast.LENGTH_SHORT).show();
                }

                break;

            case R.id.myCommunityThree:
                mMyCommunityThree.startAnimation(buttonClick);
                buttonClick.setDuration(500);

                if(mMyCommunityChannelThree != null){
                    Helper.startActivity(mActivity, mMyCommunityChannelThree);
                } else {
                    Toast.makeText(mActivity, "커뮤니티를 만들어 주세요", Toast.LENGTH_SHORT).show();
                }

                break;

            case R.id.mAvatarImageBtn:
                if (AuthHelper.isLogin(mActivity) && mUser != null) {
                    Intent intent = new Intent(mActivity, ProfileActivity.class);
                    Bundle bundle = new Bundle();
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


        }
    }

    private int getNewNoticeCount() {
        return Integer.parseInt(mNotyCountBtn.getText().toString());
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
