package com.umanji.umanjiapp.ui.fragment.members;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.umanji.umanjiapp.AppConfig;
import com.umanji.umanjiapp.R;
import com.umanji.umanjiapp.helper.UiHelper;
import com.umanji.umanjiapp.model.ChannelData;
import com.umanji.umanjiapp.model.UserData;
import com.umanji.umanjiapp.ui.base.BaseChannelListAdapter;
import com.umanji.umanjiapp.ui.page.channel.community.CommunityActivity;
import com.umanji.umanjiapp.ui.page.channel.profile.ProfileActivity;
import com.umanji.umanjiapp.ui.page.channel.spot.SpotActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class MemberListAdapter extends BaseChannelListAdapter {
    private static final String TAG = "MemberListAdapter";


    public MemberListAdapter(Activity activity, Fragment fragment) {
        super(activity, fragment);
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_channel, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final ChannelData channelData = mChannels.get(position);
        final ChannelData userData;
        if(channelData.getOwner() != null) {
            userData = channelData.getOwner();
        } else {
            userData = channelData;
        }

        holder.name.setText(userData.getUserName());
        holder.name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mActivity, ProfileActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("channel", userData.getJsonObject().toString());
                intent.putExtra("bundle", bundle);

                mFragment.startActivityForResult(intent, UiHelper.CODE_CHANNEL_ACTIVITY);

            }
        });

        holder.point.setText(userData.getPoint() + " p");

        String photo = userData.getPhoto();
        if(!TextUtils.isEmpty(photo)) {
            Glide.with(mActivity)
                    .load(photo)
                    .into(holder.photo);
        }else {
            Glide.with(mActivity)
                    .load(R.drawable.avatar_default_0)
                    .into(holder.photo);
        }
    }

}
