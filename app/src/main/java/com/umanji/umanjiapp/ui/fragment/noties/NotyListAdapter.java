package com.umanji.umanjiapp.ui.fragment.noties;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.umanji.umanjiapp.AppConfig;
import com.umanji.umanjiapp.R;
import com.umanji.umanjiapp.helper.ApiHelper;
import com.umanji.umanjiapp.helper.UiHelper;
import com.umanji.umanjiapp.model.ChannelData;
import com.umanji.umanjiapp.model.NotyData;
import com.umanji.umanjiapp.ui.base.BaseChannelListAdapter;
import com.umanji.umanjiapp.ui.page.channel.community.CommunityActivity;
import com.umanji.umanjiapp.ui.page.channel.post.PostActivity;
import com.umanji.umanjiapp.ui.page.channel.spot.SpotActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;


public class NotyListAdapter extends BaseChannelListAdapter {
    private static final String TAG = "BaseChannelListAdapter";

    /****************************************************
     *  ArrayList
     ****************************************************/
    protected ArrayList<NotyData> mNoties;

    public NotyListAdapter(Activity activity, Fragment fragment) {
        super(activity, fragment);
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
        final ChannelData userData  = notyData.getFrom();
        final boolean isRead = notyData.isRead();

        switch (channelData.getType()) {
            case TYPE_POST:
                holder.name.setText("글을 작성하였습니다.");
                break;
            case TYPE_MEMBER:
                holder.name.setText("참여하였습니다.");
                break;
        }

        holder.userName.setText(userData.getUserName());

        String userPhoto = userData.getPhoto();
        if(userPhoto != null) {
            Glide.with(mActivity)
                    .load(userPhoto)
                    .placeholder(R.drawable.empty)
                    .animate(R.anim.abc_fade_in)
                    .override(40, 40)
                    .into(holder.userPhoto);

        }else {
            Glide.with(mActivity)
                    .load(R.drawable.avatar_default_0)
                    .placeholder(R.drawable.empty)
                    .animate(R.anim.abc_fade_in)
                    .override(40, 40)
                    .into(holder.userPhoto);
        }

        String dateString = channelData.getCreatedAt();

        try{
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
            dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
            Date parsedDate = dateFormat.parse(dateString);
            Timestamp timestamp = new Timestamp(parsedDate.getTime());
            holder.createdAt.setText(UiHelper.toPrettyDate(timestamp.getTime()));
        }catch(Exception e){
            Log.e(TAG, "error " + e.toString());
        }


        if(!isRead) {
            holder.mView.setBackgroundResource(R.drawable.feed_new);
        }

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "clicked");

                mType = channelData.getType();

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
