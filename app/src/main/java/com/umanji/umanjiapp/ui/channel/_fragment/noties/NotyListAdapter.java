package com.umanji.umanjiapp.ui.channel._fragment.noties;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.text.util.Linkify;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.bumptech.glide.Glide;
import com.umanji.umanjiapp.R;
import com.umanji.umanjiapp.helper.Helper;
import com.umanji.umanjiapp.helper.UiHelper;
import com.umanji.umanjiapp.model.ChannelData;
import com.umanji.umanjiapp.model.ErrorData;
import com.umanji.umanjiapp.model.NotyData;
import com.umanji.umanjiapp.ui.BaseActivity;
import com.umanji.umanjiapp.ui.channel._fragment.BaseChannelListAdapter;
import com.umanji.umanjiapp.ui.channel.community.CommunityActivity;
import com.umanji.umanjiapp.ui.channel.post.PostActivity;
import com.umanji.umanjiapp.ui.channel.spot.SpotActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;

import de.greenrobot.event.EventBus;


public class NotyListAdapter extends BaseChannelListAdapter {
    private static final String TAG = "BaseChannelListAdapter";

    /****************************************************
     *  ArrayList
     ****************************************************/
    protected ArrayList<NotyData> mNoties;

    public NotyListAdapter(BaseActivity activity, Fragment fragment, ChannelData channelData) {
        super(activity, fragment, channelData);
        mNoties = new ArrayList<NotyData>();
    }



    @Override
    public NotyListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_noty, parent, false);

        return new NotyListAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final NotyListAdapter.ViewHolder holder, int position) {

        final NotyData notyData     = mNoties.get(position);
        final ChannelData channelData = notyData.getChannel();
        final ChannelData userData = notyData.getFrom();
        final ChannelData parentData = notyData.getParent();
        final boolean isRead = notyData.isRead();

        if(channelData == null && channelData.getType() == null) return;

        if(!isRead) {
            holder.mView.setBackgroundResource(R.drawable.feed_new);
        }

        switch (channelData.getType()) {
            case TYPE_POST:
                holder.desc.setText("글작성");
                holder.name.setVisibility(View.VISIBLE);
                holder.name.setText(channelData.getName());
                break;
            case TYPE_MEMBER:
                holder.desc.setText("참여");
                holder.name.setVisibility(View.GONE);
                break;
            case TYPE_LIKE:
                holder.desc.setText("도움");
                holder.name.setVisibility(View.GONE);
                break;
        }

        setUserName(holder, userData);
        setParentName(holder, parentData);
        setUserPhoto(holder, userData);
        setCreatedAt(holder, channelData);

        setNotyClickEvent(holder, channelData);
    }


    protected void setNotyClickEvent(final ViewHolder holder, final ChannelData channelData) {
        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mType = channelData.getType();

                ChannelData channel = null;
                if(mType.equals(TYPE_MEMBER)) {
                    try {
                        JSONObject params = new JSONObject();
                        params.put("id", channelData.getParentId());
                        mApi.call(api_channels_get, params, new AjaxCallback<JSONObject>() {
                            @Override
                            public void callback(String url, JSONObject json, AjaxStatus status) {
                                if (status.getCode() == 500) {
                                    EventBus.getDefault().post(new ErrorData(TYPE_ERROR_AUTH, TYPE_ERROR_AUTH));
                                } else {
                                    Intent intent = null;
                                    Bundle bundle = new Bundle();
                                    ChannelData parentData = new ChannelData(json);
                                    bundle.putString("channel", parentData.getJsonObject().toString());
                                    String type = parentData.getType();
                                    if(type.equals(TYPE_SPOT)) {
                                        intent = new Intent(mActivity, SpotActivity.class);
                                    } else if(type.equals(TYPE_COMMUNITY)) {
                                        intent = new Intent(mActivity, CommunityActivity.class);
                                    } else if(type.equals(TYPE_POST)) {
                                        intent = new Intent(mActivity, PostActivity.class);
                                    } else {
                                        intent = new Intent(mActivity, SpotActivity.class);
                                    }

                                    intent.putExtra("bundle", bundle);
                                    mActivity.startActivity(intent);
                                }
                            }
                        });

                    }catch (JSONException e) {
                        Log.e(TAG, "Error " + e.toString());
                    }


                } else {
                    Intent intent = null;
                    Bundle bundle = new Bundle();

                    bundle.putString("channel", channelData.getJsonObject().toString());

                    if(mType.equals(TYPE_SPOT)) {
                        intent = new Intent(mActivity, SpotActivity.class);
                    } else if(mType.equals(TYPE_COMMUNITY)) {
                        intent = new Intent(mActivity, CommunityActivity.class);
                    } else if(mType.equals(TYPE_POST)) {
                        intent = new Intent(mActivity, PostActivity.class);
                    } else {
                        intent = new Intent(mActivity, SpotActivity.class);
                    }

                    intent.putExtra("bundle", bundle);
                    mActivity.startActivity(intent);
                }

            }
        });
    }

    /****************************************************
     *  methods
     ****************************************************/


    public void addBottom(NotyData doc) {
        mNoties.add(doc);
        mChannels.add(doc.getChannel());
    }

    public void add(NotyData doc) {
        mNoties.add(0, doc);
        mChannels.add(0, doc.getChannel());
    }
}
