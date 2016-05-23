package com.umanji.umanjiapp.ui.channel._fragment.keywords;

import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.bumptech.glide.Glide;
import com.umanji.umanjiapp.R;
import com.umanji.umanjiapp.helper.Helper;
import com.umanji.umanjiapp.model.ChannelData;
import com.umanji.umanjiapp.ui.BaseActivity;
import com.umanji.umanjiapp.ui.channel._fragment.BaseChannelListAdapter;

import org.json.JSONException;
import org.json.JSONObject;


public class KeywordListAdapter extends BaseChannelListAdapter {
    private static final String TAG = "KeywordListAdapter";

    public KeywordListAdapter(BaseActivity activity, Fragment fragment) {
        super(activity, fragment);
    }

    public KeywordListAdapter(BaseActivity activity, Fragment fragment, ChannelData channelData) {
        super(activity, fragment, channelData);
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_keyword, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final ChannelData channelData       = mChannels.get(position);

        setPoint(holder, channelData);
        setName(holder, channelData);
        setPhoto(holder, channelData);
        setUserPhoto(holder, channelData.getOwner());
        setStar(holder, channelData);
    }

    @Override
    protected void setParentName(final ViewHolder holder, final ChannelData parentChannelData) {
        String parentId = "";
        if(mChannel != null ) parentId = mChannel.getId();
        if(parentChannelData != null && !TextUtils.equals(parentChannelData.getId(), parentId)) {
            if(TextUtils.isEmpty(parentChannelData.getName())) {
                holder.parentName.setVisibility(View.GONE);
                holder.headerBorder.setVisibility(View.GONE);
            }else {
                holder.parentName.setVisibility(View.VISIBLE);
                holder.headerBorder.setVisibility(View.VISIBLE);
                holder.parentName.setText(Helper.getShortenString(parentChannelData.getName(), 6));
            }

            holder.parentName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        JSONObject params = new JSONObject();
                        params.put("id", parentChannelData.getId());

                        mApi.call(api_channels_get, params, new AjaxCallback<JSONObject>() {
                            @Override
                            public void callback(String url, JSONObject object, AjaxStatus status) {
                                ChannelData channelData = new ChannelData(object);
                                Helper.startActivity(mActivity, channelData);
                            }
                        });
                    }catch (JSONException e) {
                        Log.e(TAG, "Error " + e.toString());
                    }
                }
            });

        }else {
            holder.parentName.setVisibility(View.GONE);
        }
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
                    .load(R.drawable.community_background)
                    .into(holder.photo);
        }

    }
}
