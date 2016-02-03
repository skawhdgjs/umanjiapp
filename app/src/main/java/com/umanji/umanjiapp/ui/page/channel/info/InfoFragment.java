package com.umanji.umanjiapp.ui.page.channel.info;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.umanji.umanjiapp.R;
import com.umanji.umanjiapp.ui.base.BaseChannelFragment;
import com.umanji.umanjiapp.ui.base.BaseTabAdapter;
import com.umanji.umanjiapp.ui.fragment.about.AboutFragment;
import com.umanji.umanjiapp.ui.fragment.community.CommunityListFragment;
import com.umanji.umanjiapp.ui.fragment.members.MemberListFragment;
import com.umanji.umanjiapp.ui.fragment.posts.PostListFragment;
import com.umanji.umanjiapp.ui.fragment.spots.SpotListFragment;

public class InfoFragment extends BaseChannelFragment {
    private static final String TAG = "InfoFragment";

    public static InfoFragment newInstance(Bundle bundle) {
        InfoFragment fragment = new InfoFragment();
        fragment.setArguments(bundle);
        return fragment;
    }


    @Override
    protected BaseTabAdapter initTabAdapter() {
        Bundle bundle = new Bundle();
        bundle.putString("channel", mChannel.getJsonObject().toString());
        mAdapter.addFragment(PostListFragment.newInstance(bundle), "POSTS");
        mAdapter.addFragment(MemberListFragment.newInstance(bundle), "MEMBERS");
        mAdapter.addFragment(CommunityListFragment.newInstance(bundle), "COMMUNITIES");
        mAdapter.addFragment(AboutFragment.newInstance(bundle), "ABOUT");
        return mAdapter;
    }

    @Override
    public void updateView() {
        super.updateView();
        mFab.setVisibility(View.GONE);

        if(!TextUtils.isEmpty(mChannel.getName())) {
            mName.setText(mChannel.getName());
        }
        else {
            mName.setText("정보센터");
        }

        mPoint.setText(mChannel.getPoint() + " p");

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
