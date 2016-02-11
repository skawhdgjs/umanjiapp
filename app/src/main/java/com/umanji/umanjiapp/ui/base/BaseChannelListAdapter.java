package com.umanji.umanjiapp.ui.base;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.umanji.umanjiapp.AppConfig;
import com.umanji.umanjiapp.R;
import com.umanji.umanjiapp.helper.ApiHelper;
import com.umanji.umanjiapp.model.ChannelData;
import com.umanji.umanjiapp.model.SubLinkData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;


public abstract class BaseChannelListAdapter extends RecyclerView.Adapter<BaseChannelListAdapter.ViewHolder> implements AppConfig{
    private static final String TAG = "BaseChannelListAdapter";

    /****************************************************
     *  Context & Fragment instance
     ****************************************************/
    protected Activity mActivity;
    protected Fragment mFragment;
    protected ApiHelper mApiHelper;

    protected String mType;

    protected ChannelData mChannel;
    /****************************************************
     *  ArrayList
     ****************************************************/
    protected ArrayList<ChannelData> mChannels;
    protected int mCurrentPage = 0;

    public BaseChannelListAdapter(Activity activity, Fragment fragment, ChannelData channelData) {
        this.mActivity = activity;
        this.mFragment = fragment;
        this.mChannel = channelData;
        mApiHelper  = new ApiHelper(activity, null);
        mChannels   = new ArrayList<ChannelData>();
    }


    /****************************************************
     *  methods
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
        public final TextView   linkName;
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
            linkName        = (TextView) view.findViewById(R.id.linkName);
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
