package com.umanji.umanjiapp.ui.channel._fragment.communities;

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


public class CommunityListAdapter extends BaseChannelListAdapter {
    private static final String TAG = "CommunityListAdapter";

    public CommunityListAdapter(BaseActivity activity, Fragment fragment) {
        super(activity, fragment);
    }

    public CommunityListAdapter(BaseActivity activity, Fragment fragment, ChannelData channelData) {
        super(activity, fragment, channelData);
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_community, parent, false);

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
        setStar(holder, channelData);
    }

    public void setStar(final ViewHolder holder, final ChannelData channelData) {

        holder.star.setVisibility(View.GONE);

        switch (channelData.getLevel()) {
            case LEVEL_LOCAL:
                if(channelData.getPoint() >= POINT_STAR_LOCAL) {
                    holder.star.setVisibility(View.VISIBLE);
                }
                break;
            case LEVEL_COMPLEX:
                if(channelData.getPoint() >= POINT_STAR_COMPLEX) {
                    holder.star.setVisibility(View.VISIBLE);
                }
                break;
            case LEVEL_DONG:
                if(channelData.getPoint() >= POINT_STAR_DONG) {
                    holder.star.setVisibility(View.VISIBLE);
                }
                break;
            case LEVEL_GUGUN:
                if(channelData.getPoint() >= POINT_STAR_GUGUN) {
                    holder.star.setVisibility(View.VISIBLE);
                }
                break;
            case LEVEL_COUNTRY:
                if(channelData.getPoint() >= POINT_STAR_COUNTRY) {
                    holder.star.setVisibility(View.VISIBLE);
                }
                break;

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
                    .load(R.drawable.community_default)
                    .into(holder.photo);
        }

    }
}
