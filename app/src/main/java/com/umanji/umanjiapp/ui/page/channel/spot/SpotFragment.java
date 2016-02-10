package com.umanji.umanjiapp.ui.page.channel.spot;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.text.TextUtils;
import android.view.View;

import com.bumptech.glide.Glide;
import com.umanji.umanjiapp.R;
import com.umanji.umanjiapp.helper.AuthHelper;
import com.umanji.umanjiapp.helper.CommonHelper;
import com.umanji.umanjiapp.helper.UiHelper;
import com.umanji.umanjiapp.model.ChannelData;
import com.umanji.umanjiapp.ui.base.BaseChannelFragment;
import com.umanji.umanjiapp.ui.base.BaseTabAdapter;
import com.umanji.umanjiapp.ui.fragment.about.AboutFragment;
import com.umanji.umanjiapp.ui.fragment.community.CommunityListFragment;
import com.umanji.umanjiapp.ui.fragment.members.MemberListFragment;
import com.umanji.umanjiapp.ui.fragment.posts.PostListFragment;
import com.umanji.umanjiapp.ui.fragment.spots.SpotListFragment;
import com.umanji.umanjiapp.ui.page.channel.spot.create.SpotCreateActivity;

public class SpotFragment extends BaseChannelFragment {
    private static final String TAG = "SpotFragment";

    public static SpotFragment newInstance(Bundle bundle) {
        SpotFragment fragment = new SpotFragment();
        fragment.setArguments(bundle);
        return fragment;
    }


    @Override
    protected BaseTabAdapter initTabAdapter() {
        Bundle bundle = new Bundle();
        bundle.putString("channel", mChannel.getJsonObject().toString());
        mAdapter.addFragment(PostListFragment.newInstance(bundle), "POSTS");
        mAdapter.addFragment(MemberListFragment.newInstance(bundle), "MEMBERS");

        if(mType.equals("SPOT")) {
            mAdapter.addFragment(SpotListFragment.newInstance(bundle), "SPOTS");
        }

        mAdapter.addFragment(CommunityListFragment.newInstance(bundle), "COMMUNITIES");
        mAdapter.addFragment(AboutFragment.newInstance(bundle), "ABOUT");
        return mAdapter;
    }

    @Override
    public void updateView() {
        super.updateView();

        if(mType.equals(TYPE_SPOT)) {
            if(!TextUtils.isEmpty(mChannel.getName())) {
                mName.setText(CommonHelper.getShortenString(mChannel.getName()) + " 건물");
            } else {
                mName.setText("이름없는 건물");
            }
        }else {
            if(!TextUtils.isEmpty(mChannel.getName())) {
                mName.setText(CommonHelper.getShortenString(mChannel.getName()) + " 스팟");
            } else {
                mName.setText("스팟");
            }
        }


        String photoUrl = mChannel.getPhoto();
        if(photoUrl != null) {
            Glide.with(mContext)
                    .load(photoUrl)
                    .animate(R.anim.abc_fade_in)
                    .into(mPhoto);
        }else {
            Glide.with(mContext)
                    .load(R.drawable.multi_spot_background)
                    .animate(R.anim.abc_fade_in)
                    .into(mPhoto);
        }




    }

}
