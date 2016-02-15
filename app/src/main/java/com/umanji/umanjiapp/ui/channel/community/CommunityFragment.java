package com.umanji.umanjiapp.ui.channel.community;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.umanji.umanjiapp.R;
import com.umanji.umanjiapp.model.SuccessData;
import com.umanji.umanjiapp.ui.BaseTabAdapter;
import com.umanji.umanjiapp.ui.channel.BaseChannelFragment;
import com.umanji.umanjiapp.ui.channel._fragment.about.AboutFragment;
import com.umanji.umanjiapp.ui.channel._fragment.communities.CommunityListFragment;
import com.umanji.umanjiapp.ui.channel._fragment.members.MemberListFragment;
import com.umanji.umanjiapp.ui.channel._fragment.posts.PostListFragment;
import com.umanji.umanjiapp.ui.channel.post.create.PostCreateActivity;

public class CommunityFragment extends BaseChannelFragment {
    private static final String TAG = "SpotFragment";

    public static CommunityFragment newInstance(Bundle bundle) {
        CommunityFragment fragment = new CommunityFragment();
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
        bundle.putString("channel", mChannel.getJsonObject().toString());
        adapter.addFragment(PostListFragment.newInstance(bundle), "POSTS");
        adapter.addFragment(MemberListFragment.newInstance(bundle), "MEMBERS");
        adapter.addFragment(CommunityListFragment.newInstance(bundle), "COMMUNITIES");
        adapter.addFragment(AboutFragment.newInstance(bundle), "ABOUT");
    }


    @Override
    public void updateView() {
        super.updateView();

        setName(mActivity, mChannel, "커뮤니티");
        setPhoto(mActivity, mChannel, R.drawable.multi_spot_background);
        setParentName(mActivity, mChannel.getParent());
        setUserPhoto(mActivity, mChannel.getOwner());
        setPoint(mActivity, mChannel);
        setMemberCount(mActivity, mChannel);
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

        switch (v.getId()) {
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


}
