package com.umanji.umanjiapp.ui.channel.spot;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.umanji.umanjiapp.R;
import com.umanji.umanjiapp.helper.Helper;
import com.umanji.umanjiapp.model.ChannelData;
import com.umanji.umanjiapp.model.SuccessData;
import com.umanji.umanjiapp.ui.channel.BaseChannelFragment;
import com.umanji.umanjiapp.ui.channel.BaseTabAdapter;
import com.umanji.umanjiapp.ui.channel._fragment.about.AboutFragment;
import com.umanji.umanjiapp.ui.channel._fragment.communities.CommunityListFragment;
import com.umanji.umanjiapp.ui.channel._fragment.members.MemberListFragment;
import com.umanji.umanjiapp.ui.channel._fragment.posts.PostListFragment;
import com.umanji.umanjiapp.ui.channel._fragment.spots.SpotListFragment;
import com.umanji.umanjiapp.ui.channel.post.create.PostCreateActivity;
import com.umanji.umanjiapp.ui.channel.spot.update.SpotUpdateActivity;

public class SpotFragment extends BaseChannelFragment {
    private static final String TAG = "SpotFragment";

    public static SpotFragment newInstance(Bundle bundle) {
        SpotFragment fragment = new SpotFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public View getView(LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.activity_channel, container, false);
    }

    @Override
    protected void addFragmentToTabAdapter(BaseTabAdapter adapter) {
        Bundle bundle = new Bundle();
        if(mChannel.getType().equals(TYPE_SPOT_INNER)){
            bundle.putString("channel", mChannel.getJsonObject().toString());
            adapter.addFragment(PostListFragment.newInstance(bundle), "POSTS");
            adapter.addFragment(MemberListFragment.newInstance(bundle), "MEMBERS");
            adapter.addFragment(CommunityListFragment.newInstance(bundle), "COMMUNITIES");
            adapter.addFragment(AboutFragment.newInstance(bundle), "ABOUT");
        } else {
            bundle.putString("channel", mChannel.getJsonObject().toString());
            adapter.addFragment(PostListFragment.newInstance(bundle), "POSTS");
            adapter.addFragment(MemberListFragment.newInstance(bundle), "MEMBERS");
            adapter.addFragment(SpotListFragment.newInstance(bundle), "SPOTS");
            adapter.addFragment(CommunityListFragment.newInstance(bundle), "COMMUNITIES");
            adapter.addFragment(AboutFragment.newInstance(bundle), "ABOUT");
        }

    }

    @Override
    public void updateView() {
        super.updateView();

        if(TextUtils.equals(mChannel.getType(), TYPE_SPOT)) {
            setName(mActivity, mChannel, "건물");
        } else {
            setName(mActivity, mChannel, "스팟");
        }

        setPhoto(mActivity, mChannel, R.drawable.multi_spot_background);
        setParentName(mActivity, mChannel.getParent());
        setUserPhoto(mActivity, mChannel.getOwner());
        setPoint(mActivity, mChannel);
        setMemberCount(mActivity, mChannel);
    }

    @Override
    protected void setName(Activity activity, ChannelData channelData, String label) {
        if(!TextUtils.isEmpty(channelData.getName())) {
            mName.setText(Helper.getShortenString(channelData.getName()));
        } else {
            mName.setText(label);
        }

        mName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent aboutIntent = new Intent(mActivity, SpotUpdateActivity.class);
                Bundle aboutBundle = new Bundle();
                aboutBundle.putString("channel", mChannel.getJsonObject().toString());
                aboutIntent.putExtra("bundle", aboutBundle);

                startActivity(aboutIntent);
            }
        });
    }

    @Override
    public void loadData() {
        super.loadData();
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
