package com.umanji.umanjiapp.ui.mainHome;

import android.content.Intent;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.umanji.umanjiapp.R;
import com.umanji.umanjiapp.helper.AuthHelper;
import com.umanji.umanjiapp.model.ChannelData;
import com.umanji.umanjiapp.model.ErrorData;
import com.umanji.umanjiapp.model.SuccessData;
import com.umanji.umanjiapp.ui.BaseFragment;
import com.umanji.umanjiapp.ui.main.MainActivity;
import com.umanji.umanjiapp.ui.mainHome.localCommunity.CreateLocalCommunityActivity;
import com.umanji.umanjiapp.ui.modal.WebViewActivity;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import java.text.NumberFormat;
import java.util.Locale;

import de.greenrobot.event.EventBus;

public class MainHomeFragment extends BaseFragment {
    private static final String TAG = "MainHomeFragment";

    protected TextView mUmanji;
    protected TextView mName;
    protected ImageView mUserPhoto;
    protected LinearLayout mLookAround;
    protected LinearLayout mCreateCommunity;

    protected TextView mCommunityCount;

    protected RelativeLayout mMyCommunityOne;
    protected RelativeLayout mMyCommunityTwo;
    protected RelativeLayout mMyCommunityThree;

    JSONArray jsonArray;
    int num=0;


    public static MainHomeFragment newInstance(Bundle bundle) {
        MainHomeFragment fragment = new MainHomeFragment();
        fragment.setArguments(bundle);
        return fragment;
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

        mUmanji = (TextView) view.findViewById(R.id.umanji);
        mUmanji.setOnClickListener(this);

        mUserPhoto = (ImageView) view.findViewById(R.id.userPhoto);
        mUserPhoto.setOnClickListener(this);

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
        loadData();

    }

    @Override
    public void loadData() {

        try {
            JSONObject params = new JSONObject();
            params.put("level", 2);
            params.put("type", TYPE_COMMUNITY);
            params.put("sort", "point DESC");

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
        if (AuthHelper.isLogin(mActivity)) {
        } else {
        }

        String strNumber = NumberFormat.getNumberInstance().format(num);
        mCommunityCount.setText(strNumber);
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

                Toast.makeText(mActivity, "로그인해주세요", Toast.LENGTH_SHORT).show();
                break;

            case R.id.create_community:
                mCreateCommunity.startAnimation(buttonClick);
                buttonClick.setDuration(500);

                Intent createInt = new Intent(mActivity, CreateLocalCommunityActivity.class);
                startActivity(createInt);
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

                Toast.makeText(mActivity, "만든 커뮤니티가 없습니다", Toast.LENGTH_SHORT).show();
                break;

            case R.id.myCommunityTwo:
                mMyCommunityTwo.startAnimation(buttonClick);
                buttonClick.setDuration(500);

                Toast.makeText(mActivity, "나의 커뮤니티가 없습니다", Toast.LENGTH_SHORT).show();
                break;

            case R.id.myCommunityThree:
                mMyCommunityThree.startAnimation(buttonClick);
                buttonClick.setDuration(500);

                Toast.makeText(mActivity, "커뮤니티를 만들어 주세요", Toast.LENGTH_SHORT).show();
                break;


        }
    }
}
