package com.umanji.umanjiapp.ui.channel.post;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.umanji.umanjiapp.R;
import com.umanji.umanjiapp.helper.Helper;
import com.umanji.umanjiapp.model.ChannelData;
import com.umanji.umanjiapp.model.SuccessData;
import com.umanji.umanjiapp.ui.BaseFragment;

import org.json.JSONObject;

import java.util.List;

import de.greenrobot.event.EventBus;

import static com.umanji.umanjiapp.helper.FileHelper.extractUrls;

public class PostFragment extends BaseFragment {
    private static final String TAG = "PostFragment";

    protected ChannelData mChannel;

    protected ImageView mUserPhoto;
    protected ImageView mLookAround;
    protected TextView mName;
    protected ImageView mPhoto;

    protected LinearLayout metaPanel;
    protected ImageView metaPhoto;
    protected TextView metaTitle;
    protected TextView metaDesc;

    protected FloatingActionButton mFab;

    public static PostFragment newInstance(Bundle bundle) {
        PostFragment fragment = new PostFragment();
        fragment.setArguments(bundle);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            String jsonString = getArguments().getString("channel");
            if (jsonString != null) {
                mChannel = new ChannelData(jsonString);
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public View getView(LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.activity_post, container, false);
    }

    @Override
    public void initWidgets(View view) {
        mUserPhoto = (ImageView) view.findViewById(R.id.userPhoto);
        mLookAround = (ImageView) view.findViewById(R.id.lookAround);
        mLookAround.setOnClickListener(this);

        if(mChannel.getPhoto() != null){
            mPhoto = (ImageView) view.findViewById(R.id.photo);
            mPhoto.setVisibility(View.VISIBLE);
        }

        mUserPhoto = (ImageView) view.findViewById(R.id.userPhoto);
        mUserPhoto.setOnClickListener(this);

        mName = (TextView) view.findViewById(R.id.name);
        mFab = (FloatingActionButton) view.findViewById(R.id.fab);

        metaPanel       = (LinearLayout) view.findViewById(R.id.metaPanel);
        metaPhoto       = (ImageView) view.findViewById(R.id.metaPhoto);
        metaTitle       = (TextView) view.findViewById(R.id.metaTitle);
        metaDesc        = (TextView) view.findViewById(R.id.metaDesc);

        setName(mActivity, mChannel, "없어용~");
        setUserPhoto(mActivity, mChannel.getOwner());
        setMetaPanel(mActivity, mChannel);



    }

    protected void setUserPhoto(Activity activity, final ChannelData userData) {
        if(userData != null) {
            String userPhoto = userData.getPhoto();
            if(userPhoto != null) {
                Glide.with(mActivity)
                        .load(userPhoto)
                        .placeholder(R.drawable.empty)
                        .animate(R.anim.abc_fade_in)
                        .override(40, 40)
                        .into(mUserPhoto);
            }

            mUserPhoto.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Helper.startActivity(mActivity, userData);
                }
            });

        } else {
            Glide.with(mActivity)
                    .load(R.drawable.avatar_default_0)
                    .animate(R.anim.abc_fade_in)
                    .into(mUserPhoto);
        }
    }

    protected void setName(Activity activity, ChannelData channelData, String label) {
        if (!TextUtils.isEmpty(mChannel.getName())) {
            mName.setText(mChannel.getName());
        } else {
            mName.setText(label);
        }
    }

    protected void setPhoto(Activity activity, final ChannelData channelData, int defaultImage) {
        String photoUrl = channelData.getPhoto();
        if(photoUrl != null) {
            Glide.with(activity)
                    .load(photoUrl)
                    .into(mPhoto);

            mPhoto.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Helper.startImageViewActivity(mActivity, channelData);
                }
            });
        }else {
            Glide.with(activity)
                    .load(defaultImage)
                    .into(mPhoto);

            mPhoto.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Helper.startUpdateActivity(mActivity, channelData);
                }
            });
        }
    }

    protected void setMetaPanel(Activity activity, final ChannelData channelData) {
        JSONObject descJson = channelData.getDesc();
        if(descJson != null) {
            String mMetaTitle = descJson.optString("metaTitle");
            String mMetaDesc = descJson.optString("metaDesc");
            String mMetaPhoto = descJson.optString("metaPhoto");

            if(!TextUtils.isEmpty(mMetaTitle) || !TextUtils.isEmpty(mMetaDesc) || !TextUtils.isEmpty(mMetaPhoto)) {

                metaPanel.setVisibility(View.VISIBLE);
                metaPanel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //Toast.makeText(mActivity, "Clicked", Toast.LENGTH_LONG).show();

                        String myLink = channelData.getName();

                        List<String> extractedUrls = extractUrls(myLink);

                        for (String url : extractedUrls){
                            Intent link=new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                            ((Activity)v.getContext()).startActivity(link);
                        }
                    }
                });
                if(!TextUtils.isEmpty(mMetaTitle)) {
                    metaTitle.setVisibility(View.VISIBLE);
                    metaTitle.setText(mMetaTitle);
                }else {
                    metaTitle.setVisibility(View.GONE);
                }

                if(!TextUtils.isEmpty(mMetaDesc)) {
                    metaDesc.setVisibility(View.VISIBLE);
                    metaDesc.setText(mMetaDesc);
                }else {
                    metaDesc.setVisibility(View.GONE);
                }

                if(!TextUtils.isEmpty(mMetaPhoto)) {
                    metaPhoto.setVisibility(View.VISIBLE);
                    Glide.with(mActivity)
                            .load(mMetaPhoto)
                            .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                            .into(metaPhoto);
                }else {
                    metaPhoto.setVisibility(View.GONE);
                }

            }else {
                metaPanel.setVisibility(View.GONE);
            }
        }else {
            metaPanel.setVisibility(View.GONE);
        }
    }

    @Override
    public void loadData() {

    }

    @Override
    public void updateView() {
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch(v.getId()){
            case R.id.lookAround:
                EventBus.getDefault().post(new SuccessData(EVENT_LOOK_AROUND, mChannel.getJsonObject()));
                break;
        }
    }

    @Override
    public void onEvent(SuccessData event) {
        super.onEvent(event);
        switch(event.type){
            case EVENT_LOOK_AROUND:
                mActivity.finish();
                break;
        }
    }
}
