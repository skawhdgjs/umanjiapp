package com.umanji.umanjiapp.ui.channel.post;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.umanji.umanjiapp.R;
import com.umanji.umanjiapp.helper.AuthHelper;
import com.umanji.umanjiapp.helper.FileHelper;
import com.umanji.umanjiapp.helper.Helper;
import com.umanji.umanjiapp.helper.UiHelper;
import com.umanji.umanjiapp.model.ChannelData;
import com.umanji.umanjiapp.model.SuccessData;
import com.umanji.umanjiapp.ui.BaseTabAdapter;
import com.umanji.umanjiapp.ui.channel.BaseChannelFragment;
import com.umanji.umanjiapp.ui.channel._fragment.likes.LikeListFragment;
import com.umanji.umanjiapp.ui.channel._fragment.posts.PostListFragment;
import com.umanji.umanjiapp.ui.channel._fragment.spots.SpotListFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;

public class PostFragment extends BaseChannelFragment {
    private static final String TAG = "ProfileFragment";

    public static PostFragment newInstance(Bundle bundle) {
        PostFragment fragment = new PostFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public View getView(LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.activity_post, container, false);
    }

    @Override
    protected void addFragmentToTabAdapter(BaseTabAdapter adapter) {
        Bundle bundle = new Bundle();
        bundle.putString("channel", mChannel.getJsonObject().toString());
        adapter.addFragment(PostListFragment.newInstance(bundle), "POSTS");
        adapter.addFragment(LikeListFragment.newInstance(bundle), "LIKES");


    }

    @Override
    public void loadData() {
        super.loadData();
    }

    @Override
    public void updateView() {
        super.updateView();

        setName(mActivity, mChannel, "내용없음");
        setPhoto(mActivity, mChannel, R.drawable.reply_background);
    }

    @Override
    protected void setName(Activity activity, ChannelData channelData, String label) {
        if(!TextUtils.isEmpty(mChannel.getName())) {
            mName.setText(Helper.getShortenString(mChannel.getName()));
        } else {
            mName.setText(label);
        }
    }



}
