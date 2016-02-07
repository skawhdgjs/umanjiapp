package com.umanji.umanjiapp.ui.base;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.umanji.umanjiapp.R;
import com.umanji.umanjiapp.helper.ApiHelper;
import com.umanji.umanjiapp.helper.AuthHelper;
import com.umanji.umanjiapp.helper.CommonHelper;
import com.umanji.umanjiapp.helper.FileHelper;
import com.umanji.umanjiapp.helper.UiHelper;
import com.umanji.umanjiapp.model.ChannelData;
import com.umanji.umanjiapp.model.SuccessData;
import com.umanji.umanjiapp.ui.fragment.about.AboutFragment;
import com.umanji.umanjiapp.ui.fragment.community.CommunityListFragment;
import com.umanji.umanjiapp.ui.fragment.members.MemberListFragment;
import com.umanji.umanjiapp.ui.fragment.posts.PostListFragment;
import com.umanji.umanjiapp.ui.fragment.spots.SpotListFragment;
import com.umanji.umanjiapp.ui.page.channel.post.create.PostCreateActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;

public class BaseChannelFragment extends BaseFragment {
    private static final String TAG = "ProfileFragment";

    /****************************************************
     *  View
     ****************************************************/
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

    File mResizedFile;
    String mPhotoUri;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = getLayoutView(inflater, container, savedInstanceState);
        super.onCreateView(view);

        final Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar);

        if (toolbar != null) {
            ((BaseActivity)mActivity).setSupportActionBar(toolbar);
            ((BaseActivity)mActivity).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            ((BaseActivity)mActivity).getSupportActionBar().setDisplayShowHomeEnabled(true);
            ((BaseActivity)mActivity).getSupportActionBar().setDisplayShowTitleEnabled(false);
        }


        CollapsingToolbarLayout collapsingToolbar =
                (CollapsingToolbarLayout) view.findViewById(R.id.collapsingToolbar);

        if (collapsingToolbar != null) {
            collapsingToolbar.setTitleEnabled(false);
        }


        mFab = (FloatingActionButton) view.findViewById(R.id.fab);
        mFab.setOnClickListener(this);

        if(AuthHelper.isLogin(mActivity)) {
            mFab.setVisibility(View.VISIBLE);
        }else {
            mFab.setVisibility(View.GONE);
        }

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


        // tabs
        mViewPager = (ViewPager) view.findViewById(R.id.viewPaper);
        mTabLayout = (TabLayout) view.findViewById(R.id.tabs);

        mAdapter = new BaseTabAdapter(getActivity().getSupportFragmentManager());

        initTabs();

        updateView();

        return view;
    }

    protected View getLayoutView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_channel, container, false);
        return view;
    }

    protected BaseTabAdapter initTabAdapter() {
        Bundle bundle = new Bundle();
        bundle.putString("channel", mChannel.getJsonObject().toString());
        mAdapter.addFragment(PostListFragment.newInstance(bundle), "POSTS");
        mAdapter.addFragment(MemberListFragment.newInstance(bundle), "MEMBERS");
        mAdapter.addFragment(SpotListFragment.newInstance(bundle), "SPOTS");
        mAdapter.addFragment(CommunityListFragment.newInstance(bundle), "COMMUNITIES");
        mAdapter.addFragment(AboutFragment.newInstance(bundle), "ABOUT");
        return mAdapter;
    }

    protected void initTabs() {
        if (mViewPager != null) {
            mViewPager.setAdapter(initTabAdapter());

            mTabLayout.setupWithViewPager(mViewPager);
            mTabLayout.setOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager) {
                @Override
                public void onTabSelected(TabLayout.Tab tab) {
                    super.onTabSelected(tab);
                    mCurrentTapPosition = tab.getPosition();

                    switch(mCurrentTapPosition) {
                        case 0:
                            mFab.setImageResource(R.drawable.ic_discuss);
                            if(AuthHelper.isLogin(mActivity)) {
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
    }


    @Override
    public void loadData() {
        if(!TextUtils.isEmpty(mId)) {
            try {
                JSONObject params = new JSONObject();
                params.put("id", mId);
                mApiHelper.call(api_channels_get, params);
            } catch(JSONException e) {
                Log.e(TAG, "error " + e.toString());
            }
        }
    }

    @Override
    public void updateView() {

        ChannelData parentData = mChannel.getParent();

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

        if(mUser != null) {
            String userPhoto = mUser.getPhoto();
            if(userPhoto != null) {
                Glide.with(mContext)
                        .load(userPhoto)
                        .placeholder(R.drawable.empty)
                        .animate(R.anim.abc_fade_in)
                        .override(40, 40)
                        .into(mUserPhoto);
            }
        }
    }

    @Override
    public void onEvent(SuccessData event) {
        ChannelData channelData = null;
        if(event.response == null) return;


        switch (event.type) {
            case api_channels_get:
                channelData = new ChannelData(event.response);
                if(TextUtils.equals(mId, channelData.getId())) {
                    mChannel = channelData;
                    mUser = mChannel.getOwner();
                    updateView();
                }

                break;
            case api_channels_members_find:
                try {
                    String id = event.response.optString("id");
                    if(mId.equals(id)) {
                        ArrayList<ChannelData> channels = ApiHelper.getChannelsFromJSONArray(event.response.getJSONArray("data"));
                        mMemberCount.setText(channels.size() + "명");
                        updateView();
                    }

                } catch(JSONException e) {
                    Log.e(TAG, "error " + e.toString());
                }
                break;

            case api_photo:

                try{
                    mResizedFile.delete();
                    mResizedFile = null;
                    mPhotoUri = null;

                    JSONObject data = event.response.getJSONObject("data");
                    mPhotoUri = REST_S3_URL + data.optString("photo");


                    JSONObject params = new JSONObject();
                    params.put("id", mChannel.getId());

                    ArrayList<String> photos = new ArrayList<>();
                    photos.add(mPhotoUri);
                    params.put("photos", new JSONArray(photos));

                    mApiHelper.call(api_profile_id_update, params);
                    mPhotoUri = null;
                }catch(JSONException e) {
                    Log.e("BaseChannelCreate", "error " + e.toString());
                }
                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.parentName:
                CommonHelper.startActivity(mActivity, mChannel.getParent());
                break;
            case R.id.userPhoto:
                if(AuthHelper.getUserId(mContext).equals(mChannel.getId())) {
                    UiHelper.callGallery(this);
                }else {
                    //TODO: 사진 확대보기 기능추가
                }

                break;
            case R.id.fab:
                if(mCurrentTapPosition == 0) {
                    Intent intent = new Intent(mContext, PostCreateActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("channel", mChannel.getJsonObject().toString());
                    bundle.putString("type", "POST");
                    intent.putExtra("bundle", bundle);

                    startActivityForResult(intent, UiHelper.CODE_SPOT_ACTIVITY);
                } else if(mCurrentTapPosition == 1) {

                } else if(mCurrentTapPosition == 2){

                } else if(mCurrentTapPosition == 3){

                }

                break;
        }
    };


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        switch (requestCode) {
            case UiHelper.CODE_GALLERY_ACTIVITY:

                File file = FileHelper.getFileFromUri(mContext, intent.getData());
                mResizedFile = UiHelper.imageUploadAndDisplay(mActivity, mApiHelper, file, mResizedFile, mUserPhoto, true);
                break;
        }

    }
}
