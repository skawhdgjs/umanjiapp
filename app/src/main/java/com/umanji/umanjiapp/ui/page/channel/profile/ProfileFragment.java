package com.umanji.umanjiapp.ui.page.channel.profile;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.umanji.umanjiapp.R;
import com.umanji.umanjiapp.helper.AuthHelper;
import com.umanji.umanjiapp.ui.base.BaseChannelFragment;
import com.umanji.umanjiapp.ui.base.BaseTabAdapter;
import com.umanji.umanjiapp.ui.fragment.about.AboutProfileFragment;
import com.umanji.umanjiapp.ui.fragment.community.CommunityListFragment;
import com.umanji.umanjiapp.ui.fragment.noties.NotyListFragment;
import com.umanji.umanjiapp.ui.fragment.posts.PostListFragment;
import com.umanji.umanjiapp.ui.fragment.spots.SpotListFragment;

import org.json.JSONException;
import org.json.JSONObject;

public class ProfileFragment extends BaseChannelFragment {
    private static final String TAG = "ProfileFragment";


    public static ProfileFragment newInstance(Bundle bundle) {
        ProfileFragment fragment = new ProfileFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected BaseTabAdapter initTabAdapter() {
        Bundle bundle = new Bundle();
        bundle.putString("channel", mChannel.getJsonObject().toString());
        mAdapter.addFragment(NotyListFragment.newInstance(bundle), "NOTIES");
        mAdapter.addFragment(PostListFragment.newInstance(bundle), "POSTS");
        mAdapter.addFragment(SpotListFragment.newInstance(bundle), "SPOTS");
        mAdapter.addFragment(CommunityListFragment.newInstance(bundle), "COMMUNITIES");
        mAdapter.addFragment(AboutProfileFragment.newInstance(bundle), "ABOUT");
        return mAdapter;
    }

    protected void initTabs() {
        if (mViewPager != null) {
            mViewPager.setAdapter(initTabAdapter());
            mTabLayout.setupWithViewPager(mViewPager);
        }
    }

    @Override
    public void loadData() {
        if(!TextUtils.isEmpty(mId)) {
            try {
                JSONObject params = new JSONObject();
                params.put("id", mId);
                mApiHelper.call(api_users_get, params);
            } catch(JSONException e) {
                Log.e(TAG, "error " + e.toString());
            }
        }
    }

    @Override
    public void updateView() {
        super.updateView();
        if(!TextUtils.isEmpty(mChannel.getUserName())) {
            mName.setText(mChannel.getUserName() + " 프로필");
        }
        else {
            mName.setText("프로필");
        }

        mPoint.setText(mChannel.getPoint() + " p");

        String userPhoto = mChannel.getPhoto();
        if(userPhoto != null) {
            Glide.with(mContext)
                    .load(userPhoto)
                    .animate(R.anim.abc_fade_in)
                    .into(mPhoto);

            Glide.with(mContext)
                    .load(userPhoto)
                    .animate(R.anim.abc_fade_in)
                    .into(mUserPhoto);
        }else {
            Glide.with(mContext)
                    .load(R.drawable.profile_background)
                    .animate(R.anim.abc_fade_in)
                    .into(mPhoto);

            Glide.with(mContext)
                    .load(R.drawable.avatar_default_0)
                    .animate(R.anim.abc_fade_in)
                    .into(mUserPhoto);
        }

        mFab.setVisibility(View.GONE);
    }

}
