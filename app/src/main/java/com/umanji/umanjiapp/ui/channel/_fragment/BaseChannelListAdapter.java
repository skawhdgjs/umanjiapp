package com.umanji.umanjiapp.ui.channel._fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.umanji.umanjiapp.AppConfig;
import com.umanji.umanjiapp.R;
import com.umanji.umanjiapp.helper.ApiHelper;
import com.umanji.umanjiapp.helper.AuthHelper;
import com.umanji.umanjiapp.helper.Helper;
import com.umanji.umanjiapp.model.ChannelData;
import com.umanji.umanjiapp.model.ErrorData;
import com.umanji.umanjiapp.model.SubLinkData;
import com.umanji.umanjiapp.model.SuccessData;
import com.umanji.umanjiapp.ui.BaseActivity;
import com.umanji.umanjiapp.ui.channel.post.PostActivity;
import com.umanji.umanjiapp.ui.modal.imageview.ImageViewActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.TimeZone;

import de.greenrobot.event.EventBus;


public abstract class BaseChannelListAdapter extends RecyclerView.Adapter<BaseChannelListAdapter.ViewHolder> implements AppConfig {
    private static final String TAG = "BaseChannelListAdapter";

    /****************************************************
     *  Context & Fragment instance
     ****************************************************/
    protected Activity mActivity;
    protected Fragment mFragment;
    protected ApiHelper mApi;

    protected String mType;

    protected ChannelData mChannel;
    /****************************************************
     *  ArrayList
     ****************************************************/
    protected ArrayList<ChannelData> mChannels;
    protected int mCurrentPage = 0;

    public BaseChannelListAdapter(BaseActivity activity, Fragment fragment) {
        this.mActivity = activity;
        this.mFragment = fragment;
        this.mChannel = null;
        mChannels   = new ArrayList<ChannelData>();
        mApi        = new ApiHelper(activity);

        EventBus.getDefault().register(this);
    }

    public BaseChannelListAdapter(BaseActivity activity, Fragment fragment, ChannelData channelData) {
        this.mActivity = activity;
        this.mFragment = fragment;
        this.mChannel = channelData;
        mChannels   = new ArrayList<ChannelData>();
        mApi        = new ApiHelper(activity);

        EventBus.getDefault().register(this);
    }


    public void onEvent(SuccessData event) {
        switch (event.type) {
            case api_channels_create:
                ChannelData channelData = new ChannelData(event.response);
                ChannelData parentData = channelData.getParent();
                if(parentData != null) {
                    this.updateDoc(parentData);
                }
                break;
        }
    }

    /****************************************************
     * private methods for channel
     ****************************************************/

    protected void setUserName(final ViewHolder holder, final ChannelData userData) {
        holder.userName.setText(userData.getUserName());
        holder.userName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Helper.startActivity(mActivity, userData);
            }
        });
    }

    protected void setUserPhoto(final ViewHolder holder, final ChannelData userData) {
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

        holder.userPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Helper.startActivity(mActivity, userData);
            }
        });
    }

    protected void setPoint(final ViewHolder holder, ChannelData channelData) {
        holder.point.setText("" + channelData.getPoint());
    }

    protected void setName(final ViewHolder holder, final ChannelData channelData) {

        if(TextUtils.isEmpty(channelData.getName())) {
            holder.name.setText("이름없음");
        } else {
            holder.name.setText(channelData.getName());
        }


        holder.name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Helper.startActivity(mActivity, channelData);
            }
        });
    }

    protected void setKeywords(final ViewHolder holder, ChannelData channelData) {
        ArrayList<SubLinkData> spotSubLinks = channelData.getSubLinks(TYPE_KEYWORD);
        if(spotSubLinks != null && spotSubLinks.size() > 0) {
            holder.keyword.setText(spotSubLinks.get(0).getName());
        }else {
            holder.keyword.setText("키워드를 설정해 주세요");
        }
    }

    protected void setCreatedAt(final ViewHolder holder, ChannelData channelData) {
        String dateString = channelData.getCreatedAt();
        try{
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
            dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
            Date parsedDate = dateFormat.parse(dateString);
            Timestamp timestamp = new Timestamp(parsedDate.getTime());
            holder.createdAt.setText(Helper.toPrettyDate(timestamp.getTime()));
        }catch(Exception e){
            Log.e(TAG, "error " + e.toString());
        }
    }

    protected void setParentName(final ViewHolder holder, final ChannelData parentChannelData) {
        String parentId = "";
        if(mChannel != null ) parentId = mChannel.getId();
        if(parentChannelData != null && !TextUtils.equals(parentChannelData.getId(), parentId)) {
            holder.parentName.setVisibility(View.VISIBLE);

            if(TextUtils.isEmpty(parentChannelData.getName())) {
                holder.parentName.setText("이름없음");
            }else {
                holder.parentName.setText(Helper.getShortenString(parentChannelData.getName()));
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

    protected void setPreview(final ViewHolder holder, ChannelData channelData) {
        JSONObject descJson = channelData.getDesc();
        if(descJson != null) {
            String metaTitle = descJson.optString("metaTitle");
            String metaDesc = descJson.optString("metaTitle");
            String metaPhoto = descJson.optString("metaPhoto");

            if(!TextUtils.isEmpty(metaTitle) || !TextUtils.isEmpty(metaDesc) || !TextUtils.isEmpty(metaPhoto)) {
                holder.metaPanel.setVisibility(View.VISIBLE);
                if(!TextUtils.isEmpty(metaTitle)) {
                    holder.metaTitle.setVisibility(View.VISIBLE);
                    holder.metaTitle.setText(metaTitle);
                }else {
                    holder.metaTitle.setVisibility(View.GONE);
                }

                if(!TextUtils.isEmpty(metaDesc)) {
                    holder.metaDesc.setVisibility(View.VISIBLE);
                    holder.metaDesc.setText(metaDesc);
                }else {
                    holder.metaDesc.setVisibility(View.GONE);
                }

                if(!TextUtils.isEmpty(metaPhoto)) {
                    holder.metaPhoto.setVisibility(View.VISIBLE);
                    Glide.with(mActivity)
                            .load(metaPhoto)
                            .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                            .into(holder.metaPhoto);
                }else {
                    holder.metaPhoto.setVisibility(View.GONE);
                }

            }else {
                holder.metaPanel.setVisibility(View.GONE);
            }
        }else {
            holder.metaPanel.setVisibility(View.GONE);
        }
    }

    protected void setPhoto(final ViewHolder holder, final ChannelData channelData) {
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
    }

    protected void setActionPanel(final ViewHolder holder, final ChannelData channelData) {
        ArrayList<SubLinkData> replySubLinks = channelData.getSubLinks(TYPE_POST);
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


        String actionId = channelData.getActionId(TYPE_LIKE, AuthHelper.getUserId(mActivity));
        if(!TextUtils.isEmpty(actionId)) {
            holder.likeBtn.setTag(actionId);
            holder.likeBtn.setBackgroundResource(R.drawable.default2_btn_radius);
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

                        mApi.call(api_channels_id_unLike, params, new AjaxCallback<JSONObject>() {
                            @Override
                            public void callback(String url, JSONObject json, AjaxStatus status) {
                                if (status.getCode() == 500) {
                                    EventBus.getDefault().post(new ErrorData(TYPE_ERROR_AUTH, TYPE_ERROR_AUTH));
                                } else {
                                    ChannelData parentData = new ChannelData(json);
                                    holder.point.setText("" + parentData.getPoint());
                                    holder.likeBtn.setTag(null);
                                    holder.likeBtn.setBackgroundResource(R.drawable.default_btn_grey_radius);

                                    EventBus.getDefault().post(new SuccessData(api_channels_id_unLike, json));
                                }
                            }
                        });

                    }else {
                        JSONObject params = channelData.getAddressJSONObject();
                        params.put("parent", channelData.getId());
                        params.put("type", TYPE_LIKE);
                        params.put("name", AuthHelper.getUserName(mActivity));
                        mApi.call(api_channels_id_like, params, new AjaxCallback<JSONObject>() {
                            @Override
                            public void callback(String url, JSONObject json, AjaxStatus status) {
                                if (status.getCode() == 500) {
                                    EventBus.getDefault().post(new ErrorData(TYPE_ERROR_AUTH, TYPE_ERROR_AUTH));
                                } else {
                                    ChannelData channelData = new ChannelData(json);
                                    ChannelData parentData = channelData.getParent();
                                    holder.point.setText("" + parentData.getPoint());
                                    String actionId = parentData.getActionId(TYPE_LIKE, AuthHelper.getUserId(mActivity));
                                    holder.likeBtn.setTag(actionId);
                                    holder.likeBtn.setBackgroundResource(R.drawable.default2_btn_radius);

                                    EventBus.getDefault().post(new SuccessData(api_channels_id_like, json));
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
                if (AuthHelper.isLogin(mActivity)) {
                    Helper.startPostCreateActivity(mActivity, channelData);
                } else {
                    Helper.callAuthErrorEvent();
                }
            }
        });

    }


    /****************************************************
     *  util methods
     ****************************************************/
    @Override
    public int getItemCount() {
        if(mChannels == null) {
            return 0;
        }else {
            return mChannels.size();
        }
    }

    public String getIdByUserId(String userId) {
        if(mChannels != null && mChannels.size() > 0) {
            Iterator<ChannelData> iterator = mChannels.iterator();

            while(iterator.hasNext()) {
                ChannelData data = iterator.next();
                if(data.getOwner().getId().equals(userId)) {
                    return data.getId();
                }
            }
        }
        return "";
    }

    public int getCurrentPage() {
        return mCurrentPage;
    }

    public void setCurrentPage(int mCurrentPage) {
        this.mCurrentPage = mCurrentPage;
    }

    public String getType() {
        return mType;
    }

    public void setType(String type) {
        this.mType = type;
    }

    public void add(ChannelData doc) {
        mChannels.add(0, doc);
    }

    public void addTop(ChannelData doc) {
        mChannels.add(0, doc);
    }

    public void addBottom(ChannelData doc) {
        mChannels.add(doc);
    }

    public void add(JSONObject jsonObject) {
        ChannelData doc = new ChannelData(jsonObject);
        mChannels.add(0, doc);
    }

    public ArrayList<ChannelData> getDocs() {
        return mChannels;
    }

    public void setDocs(ArrayList<ChannelData> docs) {
        this.mChannels = null;
        this.mChannels = docs;
    }

    public void setDocs(JSONArray jsonArray) {

        try {
            if (jsonArray != null) {
                for (int idx=0; idx<jsonArray.length(); idx++){
                    ChannelData doc = new ChannelData(jsonArray.getJSONObject(idx));
                    mChannels.add(doc);
                }
            }
        } catch(JSONException e) {
            Log.e(TAG, "error " + e.toString());
        }
    }

    public void updateDoc(ChannelData channelData) {

        if(mChannels != null && mChannels.size() > 0) {
            for (int idx = 0; mChannels.size() > idx; idx++) {
                ChannelData doc = mChannels.get(idx);

                if(doc.getId().equals(channelData.getId())){
                    mChannels.set(idx, channelData);
                    notifyItemChanged(idx);
                    return;
                }
            }
        }
    }

    public void resetDocs() {
        mChannels = new ArrayList<ChannelData>();
    }

    public void removeDoc(ChannelData channelData) {
        if(mChannels != null && mChannels.size() > 0) {
            for (int idx = 0; mChannels.size() > idx; idx++) {
                ChannelData doc = mChannels.get(idx);

                if(doc.getId().equals(channelData.getId())){
                    mChannels.remove(idx);
                    return;
                }
            }
        }
    }

    /****************************************************
     *  ViewHolder
     ****************************************************/

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public final View       mView;

        public final ImageView  photo;
        public final TextView   name;
        public final TextView   parentName;

        public final TextView   desc;
        public final ImageView  userPhoto;
        public final TextView   userName;
        public final TextView   createdAt;


        public final LinearLayout metaPanel;
        public final ImageView metaPhoto;
        public final TextView metaTitle;
        public final TextView metaDesc;

        public final RelativeLayout actionPanel;
        public final TextView   point;
        public final TextView   replyCount;
        public final Button     likeBtn;
        public final Button     replyBtn;


        public final TextView     keyword;
        public final TextView     floor;
        public final TextView     floorEmpty;





        public ViewHolder(View view) {
            super(view);
            mView           = view;

            photo           = (ImageView) view.findViewById(R.id.photo);
            name            = (TextView) view.findViewById(R.id.name);
            parentName      = (TextView) view.findViewById(R.id.parentName);

            desc            = (TextView) view.findViewById(R.id.desc);
            userPhoto       = (ImageView) view.findViewById(R.id.userPhoto);
            userName        = (TextView) view.findViewById(R.id.userName);
            createdAt       = (TextView) view.findViewById(R.id.createdAt);

            metaPanel       = (LinearLayout) view.findViewById(R.id.metaPanel);
            metaPhoto       = (ImageView) view.findViewById(R.id.metaPhoto);
            metaTitle       = (TextView) view.findViewById(R.id.metaTitle);
            metaDesc        = (TextView) view.findViewById(R.id.metaDesc);

            actionPanel     = (RelativeLayout) view.findViewById(R.id.actionPanel);
            point           = (TextView) view.findViewById(R.id.point);
            replyCount      = (TextView) view.findViewById(R.id.replyCount);
            likeBtn         = (Button) view.findViewById(R.id.likeBtn);
            replyBtn        = (Button) view.findViewById(R.id.replyBtn);


            keyword         = (TextView) view.findViewById(R.id.spotKeyword);
            floor           = (TextView) view.findViewById(R.id.floor);
            floorEmpty      = (TextView) view.findViewById(R.id.emptyFloor);


        }
    }
}
