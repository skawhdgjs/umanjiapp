package com.umanji.umanjiapp.ui.setting.home;

import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.umanji.umanjiapp.R;
import com.umanji.umanjiapp.helper.AuthHelper;
import com.umanji.umanjiapp.model.ChannelData;
import com.umanji.umanjiapp.model.SubLinkData;
import com.umanji.umanjiapp.ui.BaseActivity;
import com.umanji.umanjiapp.ui.channel._fragment.BaseChannelListAdapter;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class HomeAdapter extends BaseChannelListAdapter {
    private static final String TAG = "PostListAdapter";

    public HomeAdapter(BaseActivity activity, Fragment fragment) {
        super(activity, fragment);
    }

    public HomeAdapter(BaseActivity activity, Fragment fragment, ChannelData channelData) {
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
        setKeywords(holder, channelData);
        setSpotPhoto(holder, channelData);
        setUserPhoto(holder, channelData);

        setHomeSelectEvent(holder, channelData);
    }

    protected void setHomeSelectEvent(final ViewHolder holder, final ChannelData channelData) {
        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    JSONObject params = channelData.getAddressJSONObject();
                    params.put("id", AuthHelper.getUserId(mActivity));
                    mApi.call(api_channels_id_update, params);
                } catch (JSONException e) {
                    Log.e(TAG, "Error " + e.toString());
                }
            }
        });
    }

    protected void setSpotPhoto(final ViewHolder holder, ChannelData channelData) {
        String photo = channelData.getPhoto();
        if(!TextUtils.isEmpty(photo)) {
            Glide.with(mActivity)
                    .load(photo)
                    .placeholder(R.drawable.spot_dark_bg)
                    .into(holder.photo);
        } else {
            Glide.with(mActivity)
                    .load(R.drawable.spot_default)
                    .into(holder.photo);
        }
    }
}
