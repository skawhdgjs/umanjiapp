package com.umanji.umanjiapp.ui.channelInterface;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

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
import com.umanji.umanjiapp.model.VoteData;
import com.umanji.umanjiapp.ui.BaseActivity;
import com.umanji.umanjiapp.ui.channel.post.reply.ReplyActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.TimeZone;

import de.greenrobot.event.EventBus;

import static com.umanji.umanjiapp.helper.FileHelper.extractUrls;


public abstract class BaseTalkListAdapter extends RecyclerView.Adapter<BaseTalkListAdapter.ViewHolder> implements AppConfig {
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


    public BaseTalkListAdapter(BaseActivity activity, Fragment fragment) {
        this.mActivity = activity;
        this.mFragment = fragment;
        this.mChannel = null;
        mChannels   = new ArrayList<ChannelData>();
        mApi        = new ApiHelper(activity);

        EventBus.getDefault().register(this);
    }

    public BaseTalkListAdapter(BaseActivity activity, Fragment fragment, ChannelData channelData) {
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
        if(userData != null){
            holder.userName.setText(userData.getUserName());
        } else {
            return;
        }

        holder.userName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Helper.startActivity(mActivity, userData);
            }
        });
    }

    protected void setUserPhoto(final ViewHolder holder, final ChannelData userData) {

        if(userData !=null && !TextUtils.isEmpty(userData.getPhoto())) {
            String userPhoto = userData.getPhoto();
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


    protected void setName(final ViewHolder holder, final ChannelData channelData, final ChannelData parentChannelData) {

        if(TextUtils.isEmpty(channelData.getName())) {
            holder.name.setText("내용 없음");
        } else {
            holder.name.setText(Helper.getShortenString(channelData.getName(), 200));
        }


        holder.name.setOnClickListener(new View.OnClickListener() {
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
    }

    protected void setKeywords(final ViewHolder holder, ChannelData channelData) {
        String [] keywords = channelData.getKeywords();


        if(keywords != null && keywords.length > 0) {
            String keyword = "";
            for(int idx=0; idx < keywords.length; idx++) {
                keyword = keyword + "#" + keywords[idx] + " ";
            }
            holder.keyword.setText(keyword);
        }else {
            holder.keyword.setText("키워드 없음");
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
                if(parentChannelData.getType().equals(TYPE_POST)){
                    holder.parentName.setTypeface(null,Typeface.NORMAL);;
                    holder.parentName.setText("댓글");                                                    // 댓글인 경우
                } else {
                    holder.parentName.setText(Helper.getShortenString(parentChannelData.getName()));    // 일반적인 장소 정보센터
                }

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




    protected  void setParentType (final ViewHolder holder, final ChannelData parentChannelData){
        if (parentChannelData == null){
            return;
        } else{
            if(parentChannelData.getType() != null) {
                String typeName="";
                switch(parentChannelData.getType()){
                    case TYPE_SPOT:
                        typeName = "빌딩";
                        break;
                    case TYPE_SPOT_INNER:
                        typeName = "장소";
                        break;
                    case TYPE_COMPLEX:
                        typeName = "복합건물";
                        break;
                    case TYPE_COMMUNITY:
                        typeName = "커뮤니티";
                        break;
                    case TYPE_KEYWORD_COMMUNITY:
                        typeName = "관심주제어";        // 연합단체 > 관심주제어
                        break;
                    case TYPE_INFO_CENTER:
                        typeName = "정보센터";
                        break;
                }
                holder.mParentType.setVisibility(View.VISIBLE);
                holder.mParentType.setText(typeName);
            }
        }

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
        public final TextView   headerBorder;
        public final TextView   parentName;

        public final TextView   desc;
        public final ImageView  userPhoto;
        public final TextView   userName;
        public final TextView   createdAt;
        public final TextView   mRole;


        public final LinearLayout metaPanel;
        public final ImageView metaPhoto;
        public final TextView metaTitle;
        public final TextView metaDesc;

        public final RelativeLayout actionPanel;
        public final TextView   point;
        public final TextView   memberCount;
        public final TextView   replyCount;
        public final LinearLayout     likeBtn;
        public final LinearLayout     replyBtn;


        public final TextView     keyword;
        public final TextView     floor;
        public final TextView     floorEmpty;

        public final ImageView    star;

        // for survey
        public final LinearLayout surveyPanel;

        public final TextView     surveyName;
        public final RelativeLayout     mRewardPanel;
        public TextView mParentType;





        public ViewHolder(View view) {
            super(view);
            mView           = view;

            photo           = (ImageView) view.findViewById(R.id.photo);
            name            = (TextView) view.findViewById(R.id.name);
            headerBorder    = (TextView) view.findViewById(R.id.headerBorder);
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
            memberCount     = (TextView) view.findViewById(R.id.memberCount);
            replyCount      = (TextView) view.findViewById(R.id.replyCount);
            likeBtn         = (LinearLayout) view.findViewById(R.id.likeBtn);
            replyBtn        = (LinearLayout) view.findViewById(R.id.replyBtn);


            keyword         = (TextView) view.findViewById(R.id.keyword);
            floor           = (TextView) view.findViewById(R.id.floor);
            floorEmpty      = (TextView) view.findViewById(R.id.emptyFloor);


            surveyPanel     = (LinearLayout) view.findViewById(R.id.surveyPanel);

            star            = (ImageView) view.findViewById(R.id.star);

            mRole           = (TextView) view.findViewById(R.id.roles);
            surveyName      = (TextView) view.findViewById(R.id.surveyName);

            mRewardPanel    = (RelativeLayout) view.findViewById(R.id.rewardPanel);

            mParentType = (TextView) view.findViewById(R.id.parentType);

        }
    }
}
