package com.umanji.umanjiapp.ui.page.channel.community;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import com.bumptech.glide.Glide;
import com.umanji.umanjiapp.R;
import com.umanji.umanjiapp.helper.CommonHelper;
import com.umanji.umanjiapp.helper.UiHelper;
import com.umanji.umanjiapp.ui.base.BaseChannelFragment;
import com.umanji.umanjiapp.ui.base.BaseTabAdapter;
import com.umanji.umanjiapp.ui.fragment.about.AboutFragment;
import com.umanji.umanjiapp.ui.fragment.community.CommunityListFragment;
import com.umanji.umanjiapp.ui.fragment.members.MemberListFragment;
import com.umanji.umanjiapp.ui.fragment.posts.PostListFragment;
import com.umanji.umanjiapp.ui.fragment.spots.SpotListFragment;
import com.umanji.umanjiapp.ui.page.main.MainActivity;

public class CommunityFragment extends BaseChannelFragment {
    private static final String TAG = "SpotFragment";

    public static CommunityFragment newInstance(Bundle bundle) {
        CommunityFragment fragment = new CommunityFragment();
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
        mLookAround.setVisibility(View.VISIBLE);

        if(!TextUtils.isEmpty(mChannel.getName())) {
            mName.setText(CommonHelper.getShortenString(mChannel.getName()) + " 커뮤니티");
        }
        else {
            mName.setText("커뮤니티");
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
                    .load(R.drawable.community_background)
                    .animate(R.anim.abc_fade_in)
                    .into(mPhoto);
        }

    }

    @Override
    public void onClick(View v) {
        super.onClick(v);

        switch (v.getId()) {
            case R.id.lookAround:
                Bundle bundle = new Bundle();
                bundle.putString("name", mChannel.getName());
                bundle.putString("type", mChannel.getType());
                Intent intent = new Intent(mContext, MainActivity.class);
                intent.putExtra("bundle", bundle);
                startActivityForResult(intent, UiHelper.CODE_COMMUNITY_ACTIVITY);
                break;
        }
    }
}
