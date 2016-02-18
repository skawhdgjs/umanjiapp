package com.umanji.umanjiapp.ui.channel;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.bumptech.glide.Glide;
import com.umanji.umanjiapp.R;
import com.umanji.umanjiapp.helper.AuthHelper;
import com.umanji.umanjiapp.helper.Helper;
import com.umanji.umanjiapp.model.ChannelData;
import com.umanji.umanjiapp.model.SubLinkData;
import com.umanji.umanjiapp.model.SuccessData;
import com.umanji.umanjiapp.ui.BaseFragment;
import com.umanji.umanjiapp.ui.channel._fragment.posts.PostListFragment;
import com.umanji.umanjiapp.ui.channel.post.create.PostCreateActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public abstract class BaseChannelFragment extends BaseFragment {
    private static final String TAG = "BaseChannelFragment";

    /****************************************************
     *  Intent
     ****************************************************/
    protected ChannelData   mChannel;

    /****************************************************
     *  View
     ****************************************************/
    private View mNoticePanel;

    protected BaseTabAdapter mAdapter;

    protected FloatingActionButton mFab;

    protected TextView mName;
    protected TextView mParentName;
    protected TextView mHeaderBorder;
    protected ImageView mPhoto;

    protected ImageView mUserPhoto;
    protected ImageView mLookAround;
    protected TextView mMemberCount;
    protected TextView mPoint;
    protected TextView mKeywords;

    protected ViewPager mViewPager;
    protected TabLayout mTabLayout;


    /****************************************************
     *  For Etc.
     ****************************************************/
    protected int mCurrentTapPosition = 0;




    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(getArguments() != null) {
            String jsonString = getArguments().getString("channel");
            if(jsonString != null) {
                mChannel = new ChannelData(jsonString);
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        initTabAdapter(view);
        updateView();

        loadData();
        return view;
    }

    @Override
    public void updateView() {
        if(AuthHelper.isLogin(mActivity)) {
            mFab.setVisibility(View.VISIBLE);
        }else {
            mFab.setVisibility(View.GONE);
        }
    }

    @Override
    public View getView(LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.activity_channel, container, false);
    }

    @Override
    public void initWidgets(View view) {
        mNoticePanel = view.findViewById(R.id.noticePanel);

        mFab = (FloatingActionButton) view.findViewById(R.id.fab);
        mFab.setOnClickListener(this);

        mName= (TextView) view.findViewById(R.id.name);
        mName.setVisibility(View.VISIBLE);
        mName.setOnClickListener(this);

        mParentName= (TextView) view.findViewById(R.id.parentName);
        mParentName.setOnClickListener(this);

        mHeaderBorder = (TextView) view.findViewById(R.id.headerBorder);


        mPhoto = (ImageView) view.findViewById(R.id.photo);
        mUserPhoto = (ImageView) view.findViewById(R.id.userPhoto);
        mUserPhoto.setOnClickListener(this);

        mLookAround = (ImageView) view.findViewById(R.id.lookAround);
        mLookAround.setOnClickListener(this);

        mMemberCount = (TextView) view.findViewById(R.id.memberCount);
        mPoint = (TextView) view.findViewById(R.id.point);
        mKeywords = (TextView) view.findViewById(R.id.keywords);
    }

    protected void initTabAdapter(View view) {
        mViewPager = (ViewPager) view.findViewById(R.id.viewPaper);
        mTabLayout = (TabLayout) view.findViewById(R.id.tabs);
        mAdapter = new BaseTabAdapter(getActivity().getSupportFragmentManager());

        addFragmentToTabAdapter(mAdapter);

        mViewPager.setAdapter(mAdapter);
        mTabLayout.setupWithViewPager(mViewPager);


        onTabSelected(mTabLayout);
    }

    protected abstract void addFragmentToTabAdapter(BaseTabAdapter adapter);

    protected void onTabSelected(TabLayout tabLayout) {
        tabLayout.setOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager) {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                super.onTabSelected(tab);
                mCurrentTapPosition = tab.getPosition();

                switch (mCurrentTapPosition) {
                    case 0:
                        mFab.setImageResource(R.drawable.ic_discuss);
                        if (AuthHelper.isLogin(mActivity)) {
                            mFab.setVisibility(View.VISIBLE);
                        }
                        break;
                    case 1: case 2: case 3: case 4:
                        mFab.setVisibility(View.GONE);
                        break;

                }

            }
        });
    }

    @Override
    public void loadData() {
        if(!TextUtils.isEmpty(mChannel.getId())) {
            try {
                JSONObject params = new JSONObject();
                params.put("id", mChannel.getId());
                mApi.call(api_channels_get, params, new AjaxCallback<JSONObject>() {
                    @Override
                    public void callback(String url, JSONObject object, AjaxStatus status) {
                        mChannel = new ChannelData(object);
                    }
                });
                updateView();
            } catch(JSONException e) {
                Log.e(TAG, "error " + e.toString());
            }
        }
    }

    @Override
    public void onEvent(SuccessData event) {
        switch (event.type) {
            case api_channels_id_join:
            case api_channels_id_like:
            case api_channels_create:
                String parentId = event.response.optString("parent");
                if(TextUtils.equals(mChannel.getId(), parentId)) {
                    ChannelData channelData = new ChannelData(event.response);
                    mChannel = channelData.getParent();
                }
                updateView();
                Helper.showNoticePanel(mActivity, mNoticePanel, POINT_DEFAULT + " 포인트 증가");
                break;
            case api_channels_id_unJoin:
            case api_channels_id_unLike:
                Helper.showNoticePanel(mActivity, mNoticePanel, POINT_DEFAULT + " 포인트 감소");
            case api_channels_id_update:
                ChannelData channelData = new ChannelData(event.response);
                if(TextUtils.equals(mChannel.getId(), channelData.getId())) {
                    mChannel = channelData;
                }
                updateView();
                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.parentName:
                Helper.startActivity(mActivity, mChannel.getParent());
                break;
            case R.id.fab:
                if (mCurrentTapPosition == 0) {
                    Intent intent = new Intent(mActivity, PostCreateActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("channel", mChannel.getJsonObject().toString());
                    intent.putExtra("bundle", bundle);
                    startActivity(intent);
                }
                break;
        }
    }



    /****************************************************
     *  methods for Channel
     ****************************************************/

    protected void setName(Activity activity, ChannelData channelData, String label) {
        if(!TextUtils.isEmpty(channelData.getName())) {
            mName.setText(Helper.getShortenString(channelData.getName()) + " " + label);
        } else {
            mName.setText(label);
        }
    }

    protected void setUserName(Activity activity, ChannelData channelData, String label) {
        if(!TextUtils.isEmpty(channelData.getUserName())) {
            mName.setText(Helper.getShortenString(channelData.getUserName()) + " " + label);
        } else {
            mName.setText(label);
        }
    }

    protected void setPhoto(Activity activity, ChannelData channelData, int defaultImage) {
        String photoUrl = channelData.getPhoto();
        if(photoUrl != null) {
            Glide.with(activity)
                    .load(photoUrl)
                    .into(mPhoto);
        }else {
            Glide.with(activity)
                    .load(defaultImage)
                    .into(mPhoto);
        }
    }

    protected void setUserPhoto(Activity activity, ChannelData userData) {
        if(userData != null) {
            String userPhoto = userData.getPhoto();
            if(userPhoto != null) {
                Glide.with(mActivity)
                        .load(userPhoto)
                        .placeholder(R.drawable.empty)
                        .animate(R.anim.abc_fade_in)
                        .override(40, 40)
                        .into(mUserPhoto);
            }
        } else {
            Glide.with(mActivity)
                    .load(R.drawable.avatar_default_0)
                    .animate(R.anim.abc_fade_in)
                    .into(mUserPhoto);
        }
    }

    protected void setParentName(Activity activity, ChannelData parentData) {
        if(parentData == null) {
            mHeaderBorder.setVisibility(View.GONE);
            mParentName.setVisibility(View.GONE);
        } else {
            mHeaderBorder.setVisibility(View.VISIBLE);
            mParentName.setVisibility(View.VISIBLE);

            if(TextUtils.isEmpty(parentData.getName())) {
                mParentName.setText("이름없음");
            } else {
                mParentName.setText(parentData.getName());
            }
        }
    }

    protected void setPoint(Activity activity, ChannelData channelData) {
        mPoint.setText(channelData.getPoint() + " p");
    }

    protected void setMemberCount(Activity activity, ChannelData channelData) {
        ArrayList<SubLinkData> memberSubLinks = channelData.getSubLinks(TYPE_MEMBER);
        if(mMemberCount!=null && memberSubLinks != null) {
            mMemberCount.setText(memberSubLinks.size() + " 명");
        }
    }

}
