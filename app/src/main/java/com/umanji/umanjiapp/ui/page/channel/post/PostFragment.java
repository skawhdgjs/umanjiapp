package com.umanji.umanjiapp.ui.page.channel.post;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.umanji.umanjiapp.R;
import com.umanji.umanjiapp.helper.CommonHelper;
import com.umanji.umanjiapp.helper.UiHelper;
import com.umanji.umanjiapp.ui.base.BaseChannelFragment;
import com.umanji.umanjiapp.ui.base.BaseTabAdapter;
import com.umanji.umanjiapp.ui.fragment.likes.LikeListFragment;
import com.umanji.umanjiapp.ui.fragment.members.MemberListFragment;
import com.umanji.umanjiapp.ui.fragment.posts.PostListFragment;
import com.umanji.umanjiapp.ui.page.channel.spot.create.SpotCreateActivity;

public class PostFragment extends BaseChannelFragment {
    private static final String TAG = "PostFragment";

    protected TextView mReplyTitle;

    public static PostFragment newInstance(Bundle bundle) {
        PostFragment fragment = new PostFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected View getLayoutView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_post, container, false);
        return view;
    }

    @Override
    protected BaseTabAdapter initTabAdapter() {
        Bundle bundle = new Bundle();
        bundle.putString("channel", mChannel.getJsonObject().toString());
        mAdapter.addFragment(PostListFragment.newInstance(bundle), "POSTS");
        mAdapter.addFragment(LikeListFragment.newInstance(bundle), "LIKES");
        mAdapter.addFragment(MemberListFragment.newInstance(bundle), "MEMBER");
        return mAdapter;
    }

    @Override
    public void updateView() {
        super.updateView();

        if(!TextUtils.isEmpty(mChannel.getName())) {
            mName.setText(CommonHelper.getShortenString(mChannel.getName()));
        } else {
            mName.setText("내용없음");
        }

        String photoUrl = mChannel.getPhoto();
        if(photoUrl != null) {
            Glide.with(mContext)
                    .load(photoUrl)
                    .animate(R.anim.abc_fade_in)
                    .into(mPhoto);
        }else {
            Glide.with(mContext)
                    .load(R.drawable.reply_background)
                    .animate(R.anim.abc_fade_in)
                    .into(mPhoto);
        }
    }

}
