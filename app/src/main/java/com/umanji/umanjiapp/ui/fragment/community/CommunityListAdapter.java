package com.umanji.umanjiapp.ui.fragment.community;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.umanji.umanjiapp.R;
import com.umanji.umanjiapp.helper.CommonHelper;
import com.umanji.umanjiapp.helper.UiHelper;
import com.umanji.umanjiapp.model.ChannelData;
import com.umanji.umanjiapp.ui.base.BaseChannelListAdapter;
import com.umanji.umanjiapp.ui.page.channel.community.CommunityActivity;
import com.umanji.umanjiapp.ui.page.channel.spot.SpotActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class CommunityListAdapter extends BaseChannelListAdapter {
    private static final String TAG = "CommunityListAdapter";

    public CommunityListAdapter(Activity activity, Fragment fragment, ChannelData channelData) {
        super(activity, fragment, channelData);
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

        holder.name.setText(CommonHelper.getShortenString(channelData.getName()));
        holder.name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mActivity, CommunityActivity.class);

                Bundle bundle = new Bundle();
                bundle.putString("channel", channelData.getJsonObject().toString());
                intent.putExtra("bundle", bundle);

                mFragment.startActivityForResult(intent, UiHelper.CODE_CHANNEL_ACTIVITY);
            }
        });

        holder.point.setText(channelData.getPoint() + " p");

        String [] photos = channelData.getPhotos();
        if(photos != null && photos[0] != null) {
            Glide.with(mActivity)
                    .load(photos[0])
                    .placeholder(R.drawable.empty)
                    .animate(R.anim.abc_fade_in)
                    .into(holder.photo);
        }

    }

}
