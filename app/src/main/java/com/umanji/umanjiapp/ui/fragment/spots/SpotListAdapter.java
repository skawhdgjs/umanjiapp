package com.umanji.umanjiapp.ui.fragment.spots;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.umanji.umanjiapp.R;
import com.umanji.umanjiapp.helper.UiHelper;
import com.umanji.umanjiapp.model.ChannelData;
import com.umanji.umanjiapp.ui.base.BaseChannelListAdapter;
import com.umanji.umanjiapp.ui.page.channel.spot.SpotActivity;


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


        holder.name.setText(channelData.getName());
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
