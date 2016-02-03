package com.umanji.umanjiapp.ui.fragment.posts;

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
import com.umanji.umanjiapp.R;
import com.umanji.umanjiapp.helper.AuthHelper;
import com.umanji.umanjiapp.helper.CommonHelper;
import com.umanji.umanjiapp.helper.UiHelper;
import com.umanji.umanjiapp.model.ChannelData;
import com.umanji.umanjiapp.model.SubLinkData;
import com.umanji.umanjiapp.model.UserData;
import com.umanji.umanjiapp.ui.base.BaseChannelListAdapter;
import com.umanji.umanjiapp.ui.page.channel.community.CommunityActivity;
import com.umanji.umanjiapp.ui.page.channel.info.InfoActivity;
import com.umanji.umanjiapp.ui.page.channel.post.PostActivity;
import com.umanji.umanjiapp.ui.page.channel.post.create.PostCreateActivity;
import com.umanji.umanjiapp.ui.page.channel.spot.SpotActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.TimeZone;


public class PostListAdapter extends BaseChannelListAdapter {
    private static final String TAG = "PostListAdapter";

    public PostListAdapter(Activity activity, Fragment fragment) {
        super(activity, fragment);
    }




    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_post, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final ChannelData channelData       = mChannels.get(position);
        final ChannelData parentChannelData   = channelData.getParent();
        final ChannelData userData          = channelData.getOwner();

        holder.name.setText(channelData.getName());
        holder.userName.setText(channelData.getOwner().getUserName());

        ArrayList<SubLinkData> likeSubLinks = channelData.getSubLinks(TYPE_MEMBER);
        ArrayList<SubLinkData> replySubLinks = channelData.getSubLinks(TYPE_POST);

        if(likeSubLinks != null && likeSubLinks.size() > 0) {
            holder.point.setText("" + (likeSubLinks.size() * 100));
        }else {
            holder.point.setText("" + 0);
        }

        if(replySubLinks != null && replySubLinks.size() > 0) {
            holder.replyCount.setText("" + replySubLinks.size() + " 개");
        }else {
            holder.replyCount.setText("0 개");
        }

        holder.actionPanel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mActivity, PostActivity.class);

                Bundle bundle = new Bundle();
                bundle.putString("channel", channelData.getJsonObject().toString());
                intent.putExtra("bundle", bundle);

                mFragment.startActivity(intent);
            }
        });

        String [] photos = channelData.getPhotos();
        if(photos != null && photos[0] != null) {
            Glide.with(mActivity)
                    .load(photos[0])
                    .placeholder(R.drawable.empty)
                    .animate(R.anim.abc_fade_in)
                    .into(holder.photo);

            holder.photo.setVisibility(View.VISIBLE);
        }else {
            holder.photo.setVisibility(View.GONE);
        }



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

        String actionId = channelData.getActionId(TYPE_MEMBER, AuthHelper.getUserId(mActivity));
        holder.likeBtn.setEnabled(true);
        if(!TextUtils.isEmpty(actionId)) {
            holder.likeBtn.setTag(actionId);
            holder.likeBtn.setBackgroundResource( R.drawable.default2_btn_radius);
        }else {
            holder.likeBtn.setTag(null);
            holder.likeBtn.setBackgroundResource(R.drawable.default_btn_grey_radius);
        }




        holder.likeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    String actionId = (String)v.getTag();

                    if(!TextUtils.isEmpty(actionId)) {

                        JSONObject params = channelData.getAddressJSONObject();
                        params.put("parent", channelData.getId());
                        params.put("id", actionId);

                        mApiHelper.call(api_channels_unJoin, params);
                        holder.likeBtn.setEnabled(false);

                    }else {
                        JSONObject params = channelData.getAddressJSONObject();
                        params.put("parent", channelData.getId());
                        params.put("type", TYPE_MEMBER);
                        params.put("name", AuthHelper.getUserName(mActivity));

                        mApiHelper.call(api_channels_join, params);
                        holder.likeBtn.setEnabled(false);
                    }



                }catch(JSONException e) {
                    Log.e(TAG, "error " + e.toString());
                }
            }
        });

        holder.replyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!CommonHelper.isAuthError(mActivity)) {
                    Intent intent = new Intent(mActivity, PostCreateActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("channel", channelData.getJsonObject().toString());
                    bundle.putString("type", "POST");
                    intent.putExtra("bundle", bundle);
                    mFragment.startActivity(intent);
                }
            }
        });

        if(TYPE_MAIN.equals(mType) && parentChannelData != null) {
            if(TextUtils.isEmpty(parentChannelData.getName())) {
                holder.linkName.setText("이름없음");
            }else {
                holder.linkName.setText(parentChannelData.getName());
            }

            holder.linkName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent intent = null;
                    Bundle bundle = new Bundle();
                    bundle.putString("channel", parentChannelData.getJsonObject().toString());
                    switch (parentChannelData.getType()) {
                        case TYPE_POST:
                            intent = new Intent(mActivity, PostActivity.class);
                            intent.putExtra("bundle", bundle);
                            break;
                        case TYPE_SPOT:
                            intent = new Intent(mActivity, SpotActivity.class);
                            intent.putExtra("bundle", bundle);
                            break;
                        case TYPE_SPOT_INNER:
                            intent = new Intent(mActivity, SpotActivity.class);
                            intent.putExtra("bundle", bundle);
                            break;
                        case TYPE_INFO_CENTER:
                            intent = new Intent(mActivity, InfoActivity.class);
                            intent.putExtra("bundle", bundle);
                            break;

                        case TYPE_COMMUNITY:
                            intent = new Intent(mActivity, CommunityActivity.class);
                            intent.putExtra("bundle", bundle);
                            break;
                    }

                    mFragment.startActivityForResult(intent, UiHelper.CODE_POST_FRAGMENT);
                }
            });

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
    }
}
