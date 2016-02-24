package com.umanji.umanjiapp.ui.channel.info;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.umanji.umanjiapp.R;
import com.umanji.umanjiapp.helper.Helper;
import com.umanji.umanjiapp.model.ChannelData;
import com.umanji.umanjiapp.model.SuccessData;
import com.umanji.umanjiapp.ui.channel.BaseTabAdapter;
import com.umanji.umanjiapp.ui.channel.BaseChannelFragment;
import com.umanji.umanjiapp.ui.channel._fragment.about.AboutFragment;
import com.umanji.umanjiapp.ui.channel._fragment.communities.CommunityListFragment;
import com.umanji.umanjiapp.ui.channel._fragment.members.MemberListFragment;
import com.umanji.umanjiapp.ui.channel._fragment.posts.PostListFragment;
import com.umanji.umanjiapp.ui.channel.post.create.PostCreateActivity;

public class InfoFragment extends BaseChannelFragment {
    private static final String TAG = "SpotFragment";

    public static InfoFragment newInstance(Bundle bundle) {
        InfoFragment fragment = new InfoFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        return view;
    }

    @Override
    public View getView(LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.activity_channel, container, false);
    }

    @Override
    protected void addFragmentToTabAdapter(BaseTabAdapter adapter) {
        Bundle bundle = new Bundle();
        bundle.putString("channel", mChannel.getJsonObject().toString());
        adapter.addFragment(PostListFragment.newInstance(bundle), "POSTS");
        adapter.addFragment(MemberListFragment.newInstance(bundle), "MEMBERS");
        adapter.addFragment(CommunityListFragment.newInstance(bundle), "COMMUNITIES");
        adapter.addFragment(AboutFragment.newInstance(bundle), "ABOUT");
    }

    @Override
    public void updateView() {
        super.updateView();

        mFab.setVisibility(View.GONE);

        setName(mActivity, mChannel, "");
        setPhoto(mActivity, mChannel, R.drawable.multi_spot_background);
        setParentName(mActivity, mChannel.getParent());
        setUserPhoto(mActivity, mChannel.getOwner());
        setPoint(mActivity, mChannel);
        setMemberCount(mActivity, mChannel);
        setKeywords(mActivity, mChannel);
    }

    @Override
    protected void setPhoto(Activity activity, final ChannelData channelData, int defaultImage) {
        String photoUrl = channelData.getPhoto();
        if(photoUrl != null) {
            Glide.with(activity)
                    .load(photoUrl)
                    .into(mPhoto);

            mPhoto.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Helper.startImageViewActivity(mActivity, channelData);
                }
            });
        }else {
            Glide.with(activity)
                    .load(defaultImage)
                    .into(mPhoto);
        }
    }

    @Override
    public void loadData() {

    }

    @Override
    public void onEvent(SuccessData event) {
        super.onEvent(event);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
    }


}
