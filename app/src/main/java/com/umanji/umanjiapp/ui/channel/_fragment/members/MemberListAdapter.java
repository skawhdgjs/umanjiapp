package com.umanji.umanjiapp.ui.channel._fragment.members;

import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.umanji.umanjiapp.R;
import com.umanji.umanjiapp.model.ChannelData;
import com.umanji.umanjiapp.ui.BaseActivity;
import com.umanji.umanjiapp.ui.channel._fragment.BaseChannelListAdapter;


public class MemberListAdapter extends BaseChannelListAdapter {
    private static final String TAG = "MemberListAdapter";

    public MemberListAdapter(BaseActivity activity, Fragment fragment) {
        super(activity, fragment);
    }

    public MemberListAdapter(BaseActivity activity, Fragment fragment, ChannelData channelData) {
        super(activity, fragment, channelData);
    }



    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_member, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        ChannelData channelData       = mChannels.get(position);
        ChannelData userData = null;

        if(TextUtils.equals(channelData.getType(), TYPE_USER)) {
            userData = channelData;
        } else {
            userData = channelData.getOwner();
        }

        setUserName(holder, userData);
        setPoint(holder, userData);
        setUserPhoto(holder, userData);
    }
}
