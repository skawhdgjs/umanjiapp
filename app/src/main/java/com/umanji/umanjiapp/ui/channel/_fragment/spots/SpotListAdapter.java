package com.umanji.umanjiapp.ui.channel._fragment.spots;

import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.umanji.umanjiapp.R;
import com.umanji.umanjiapp.helper.Helper;
import com.umanji.umanjiapp.model.ChannelData;
import com.umanji.umanjiapp.ui.BaseActivity;
import com.umanji.umanjiapp.ui.channel._fragment.BaseChannelListAdapter;

import org.json.JSONObject;


public class SpotListAdapter extends BaseChannelListAdapter {
    private static final String TAG = "PostListAdapter";

    public SpotListAdapter(BaseActivity activity, Fragment fragment) {
        super(activity, fragment);
    }

    public SpotListAdapter(BaseActivity activity, Fragment fragment, ChannelData channelData) {
        super(activity, fragment, channelData);
    }



    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_spot, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final ChannelData channelData       = mChannels.get(position);

        setPoint(holder, channelData);
        setName(holder, channelData);
        setMemberCount(holder, channelData);
        setPhoto(holder, channelData);
        setUserPhoto(holder, channelData.getOwner());

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
    }

    @Override
    protected void setPhoto(final ViewHolder holder, final ChannelData channelData) {
        String photo = channelData.getPhoto();
        if(!TextUtils.isEmpty(photo)) {
            Glide.with(mActivity)
                    .load(photo)
                    .placeholder(R.drawable.spot_dark_bg)
                    .into(holder.photo);

            holder.photo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Helper.startImageViewActivity(mActivity, channelData);
                }
            });
        } else {
            Glide.with(mActivity)
                    .load(R.drawable.spot_default)
                    .into(holder.photo);
        }
    }
}
