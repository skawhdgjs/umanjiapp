package com.umanji.umanjiapp.ui.fragment.spots;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.umanji.umanjiapp.R;
import com.umanji.umanjiapp.helper.UiHelper;
import com.umanji.umanjiapp.model.ChannelData;
import com.umanji.umanjiapp.ui.base.BaseChannelListAdapter;
import com.umanji.umanjiapp.ui.page.channel.spot.SpotActivity;

import org.json.JSONObject;


public class SpotListAdapter extends BaseChannelListAdapter {
    private static final String TAG = "SpotListAdapter";


    public SpotListAdapter(Activity activity, Fragment fragment) {
        super(activity, fragment);
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_spot, parent, false);

        return new ViewHolder(view);
    }

    // test for push

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final ChannelData channelData = mChannels.get(position);
        final ChannelData userData = channelData.getOwner();
        final ChannelData parentData = channelData.getParent();

        holder.name.setText(channelData.getName());
        holder.point.setText(channelData.getPoint() + " p");

        JSONObject descJson = channelData.getDesc();
        if(descJson != null) {
            int floor = descJson.optInt("floor");
            holder.floor.setText(floor + "F");
            holder.floor.setVisibility(View.VISIBLE);
            holder.floorEmpty.setVisibility(View.GONE);
        }else {
            holder.floor.setVisibility(View.GONE);
            holder.floorEmpty.setVisibility(View.VISIBLE);
        }


        //holder.keyword.setText(channelData.getKeywords());
        String [] keywords = channelData.getKeywords();
        if(keywords != null && keywords[0] != null) {
            holder.keyword.setText(keywords[0]);
        }



        holder.name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mActivity, SpotActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("channel", channelData.getJsonObject().toString());
                intent.putExtra("bundle", bundle);

                mFragment.startActivityForResult(intent, UiHelper.CODE_CHANNEL_ACTIVITY);
            }
        });

        String photo = channelData.getPhoto();
        if(!TextUtils.isEmpty(photo)) {
            Glide.with(mActivity)
                    .load(photo)
                    .placeholder(R.drawable.spot_dark_bg)
                    .animate(R.anim.abc_fade_in)
                    .into(holder.photo);
        } else {
            Glide.with(mActivity)
                    .load(R.drawable.spot_default)
                    .animate(R.anim.abc_fade_in)
                    .into(holder.photo);
        }

        String userPhoto = userData.getPhoto();
        if(!TextUtils.isEmpty(userPhoto)) {
            Glide.with(mActivity)
                    .load(userPhoto)
                    .placeholder(R.drawable.empty)
                    .animate(R.anim.abc_fade_in)
                    .into(holder.userPhoto);
        } else {
            Glide.with(mActivity)
                    .load(R.drawable.avatar_default_0)
                    .animate(R.anim.abc_fade_in)
                    .into(holder.userPhoto);
        }
    }
}
