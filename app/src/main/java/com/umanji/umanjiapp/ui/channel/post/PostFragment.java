package com.umanji.umanjiapp.ui.channel.post;

import android.app.Activity;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.umanji.umanjiapp.R;
import com.umanji.umanjiapp.helper.Helper;
import com.umanji.umanjiapp.model.ChannelData;
import com.umanji.umanjiapp.ui.BaseFragment;

public class PostFragment extends BaseFragment {
    private static final String TAG = "PostFragment";

    protected ChannelData   mChannel;

    protected ImageView mUserPhoto;
    protected ImageView mLookAround;
    protected TextView mName;

    protected FloatingActionButton mFab;

    public static PostFragment newInstance(Bundle bundle) {
        PostFragment fragment = new PostFragment();
        fragment.setArguments(bundle);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(getArguments() != null) {
            String jsonString = getArguments().getString("channel");
            if(jsonString != null) {
                mChannel = new ChannelData(jsonString);
            }

        }
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
    public void initWidgets(View view) {
        mUserPhoto = (ImageView) view.findViewById(R.id.userPhoto);
        mLookAround =(ImageView) view.findViewById(R.id.lookAround);
        mLookAround.setOnClickListener(this);
        mName = (TextView) view.findViewById(R.id.name);
        mFab = (FloatingActionButton) view.findViewById(R.id.fab);


    }

    @Override
    public void loadData() {

    }

    @Override
    public void updateView() {
        setName(mActivity, mChannel, "내용없음");
        setUserPhoto(mActivity, mChannel.getOwner());
    }

    protected void setName(Activity activity, ChannelData channelData, String label) {
        if(!TextUtils.isEmpty(mChannel.getName())) {
            mName.setText(Helper.getShortenString(mChannel.getName(), 20));
        } else {
            mName.setText(label);
        }
    }

    protected void setUserPhoto(Activity activity, final ChannelData userData) {
        if(userData != null) {
            String userPhoto = userData.getPhoto();
            if(userPhoto != null) {
                Glide.with(mActivity)
                        .load(userPhoto)
                        .placeholder(R.drawable.empty)
                        .animate(R.anim.abc_fade_in)
                        .override(40, 40)
                        .into(mUserPhoto);
            }

            mUserPhoto.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Helper.startActivity(mActivity, userData);
                }
            });

        } else {
            Glide.with(mActivity)
                    .load(R.drawable.avatar_default_0)
                    .animate(R.anim.abc_fade_in)
                    .into(mUserPhoto);
        }
    }


}
