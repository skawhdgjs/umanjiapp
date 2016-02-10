package com.umanji.umanjiapp.ui.fragment.posts;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.umanji.umanjiapp.R;
import com.umanji.umanjiapp.helper.AuthHelper;
import com.umanji.umanjiapp.helper.CommonHelper;
import com.umanji.umanjiapp.helper.UiHelper;
import com.umanji.umanjiapp.model.ChannelData;
import com.umanji.umanjiapp.model.ErrorData;
import com.umanji.umanjiapp.model.SubLinkData;
import com.umanji.umanjiapp.ui.base.BaseChannelListAdapter;
import com.umanji.umanjiapp.ui.page.channel.community.CommunityActivity;
import com.umanji.umanjiapp.ui.page.channel.info.InfoActivity;
import com.umanji.umanjiapp.ui.page.channel.post.PostActivity;
import com.umanji.umanjiapp.ui.page.channel.post.create.PostCreateActivity;
import com.umanji.umanjiapp.ui.page.channel.spot.SpotActivity;
import com.umanji.umanjiapp.ui.page.util.image.ImageViewActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;

import de.greenrobot.event.EventBus;


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
        Linkify.addLinks(holder.name, Linkify.WEB_URLS);
        holder.name.setMovementMethod(LinkMovementMethod.getInstance());

        holder.userName.setText(channelData.getOwner().getUserName());

        ArrayList<SubLinkData> replySubLinks = channelData.getSubLinks(TYPE_POST);

        holder.point.setText("" + channelData.getPoint());

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

        String photo = channelData.getPhoto();
        if(!TextUtils.isEmpty(photo)) {
            Glide.with(mActivity)
                    .load(photo)
                    .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                    .into(holder.photo);

            holder.photo.setVisibility(View.VISIBLE);
        }else {
            holder.photo.setVisibility(View.GONE);
        }

        holder.photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mActivity, ImageViewActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("channel", channelData.getJsonObject().toString());
                intent.putExtra("bundle", bundle);
                mFragment.startActivity(intent);
            }
        });



        String userPhoto = userData.getPhoto();
        if(!TextUtils.isEmpty(userPhoto)) {
            Glide.with(mActivity)
                    .load(userPhoto)
                    .override(40, 40)
                    .into(holder.userPhoto);

        } else {
            Glide.with(mActivity)
                    .load(R.drawable.avatar_default_0)
                    .override(40, 40)
                    .into(holder.userPhoto);
        }

        String actionId = channelData.getActionId(TYPE_MEMBER, AuthHelper.getUserId(mActivity));
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

                        mApiHelper.call(api_channels_unJoin, params, new AjaxCallback<JSONObject>() {
                            @Override
                            public void callback(String url, JSONObject json, AjaxStatus status) {
                                if (status.getCode() == 500) {
                                    EventBus.getDefault().post(new ErrorData(TYPE_ERROR_AUTH, TYPE_ERROR_AUTH));
                                } else {
                                    ChannelData parentData = new ChannelData(json);
                                    holder.point.setText("" + parentData.getPoint());
                                    holder.likeBtn.setTag(null);
                                    holder.likeBtn.setBackgroundResource(R.drawable.default_btn_grey_radius);
                                }
                            }
                        });

                    }else {
                        JSONObject params = channelData.getAddressJSONObject();
                        params.put("parent", channelData.getId());
                        params.put("type", TYPE_MEMBER);
                        params.put("name", AuthHelper.getUserName(mActivity));
                        mApiHelper.call(api_channels_join, params, new AjaxCallback<JSONObject>() {
                            @Override
                            public void callback(String url, JSONObject json, AjaxStatus status) {
                                if (status.getCode() == 500) {
                                    EventBus.getDefault().post(new ErrorData(TYPE_ERROR_AUTH, TYPE_ERROR_AUTH));
                                } else {
                                    ChannelData channelData = new ChannelData(json);
                                    ChannelData parentData = channelData.getParent();
                                    holder.point.setText("" + parentData.getPoint());
                                    String actionId = parentData.getActionId(TYPE_MEMBER, AuthHelper.getUserId(mActivity));
                                    holder.likeBtn.setTag(actionId);
                                    holder.likeBtn.setBackgroundResource( R.drawable.default2_btn_radius);
                                }
                            }
                        });

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
                holder.linkName.setText(CommonHelper.getShortenString(parentChannelData.getName()));
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
