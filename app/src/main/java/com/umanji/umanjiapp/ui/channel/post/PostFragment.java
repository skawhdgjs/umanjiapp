package com.umanji.umanjiapp.ui.channel.post;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.umanji.umanjiapp.R;
import com.umanji.umanjiapp.helper.AuthHelper;
import com.umanji.umanjiapp.helper.Helper;
import com.umanji.umanjiapp.model.ChannelData;
import com.umanji.umanjiapp.model.ErrorData;
import com.umanji.umanjiapp.model.SubLinkData;
import com.umanji.umanjiapp.model.SuccessData;
import com.umanji.umanjiapp.model.VoteData;
import com.umanji.umanjiapp.ui.BaseFragment;
import com.umanji.umanjiapp.ui.channel.advertise.AdsCreateActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import de.greenrobot.event.EventBus;

import static com.umanji.umanjiapp.helper.FileHelper.extractUrls;

public class PostFragment extends BaseFragment {
    private static final String TAG = "PostFragment";

    protected ChannelData mChannel;

    protected ImageView mUserPhoto;
    protected ImageView mLookAround;
    protected TextView mName;
    protected TextView mUserName;
    protected TextView mCreatedAt;
    protected ImageView mPhoto;

    protected LinearLayout metaPanel;
    protected ImageView metaPhoto;
    protected TextView metaTitle;
    protected TextView metaDesc;
    protected TextView mPostAd;

    protected LinearLayout mSurveyPanel;

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

        mUserName = (TextView) view.findViewById(R.id.userName);
        mCreatedAt = (TextView) view.findViewById(R.id.createdAt);

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

        mSurveyPanel    = (LinearLayout) view.findViewById(R.id.surveyPanel);

        setName(mActivity, mChannel, "내용없음");
        setUserPhoto(mActivity, mChannel.getOwner());
        setMetaPanel(mActivity, mChannel);
        setSurvey(mActivity, mChannel);
        setUserName(mActivity, mChannel.getOwner());
        setCreatedAt(mChannel);

        mPostAd = (TextView) view.findViewById(R.id.postAd);
        mPostAd.setOnClickListener(this);
    }

    protected void setSurvey(Activity activity, final ChannelData channelData) {
        JSONObject descJson = channelData.getDesc();
        if(descJson != null) {
            JSONObject survey = descJson.optJSONObject("vote");

            if(survey != null) {
                try {
                    String postType = survey.optString("type");
                    JSONArray options = survey.getJSONArray("options");

                    if(options == null && options.length() == 0) {
                        mSurveyPanel.setVisibility(View.GONE);
                        return;
                    }

                    mSurveyPanel.setVisibility(View.VISIBLE);

                    LinearLayout surveyContentPanel = (LinearLayout)mSurveyPanel.findViewById(R.id.surveyContentPanel);
                    surveyContentPanel.removeAllViews();


                    String actionName = channelData.getActionName(TYPE_SURVEY, AuthHelper.getUserId(mActivity));
                    if(TextUtils.isEmpty(actionName)) {
                        mSurveyPanel.setTag(null);
                    }else {
                        mSurveyPanel.setTag(actionName);
                    }

                    for(int idx = 0; idx < options.length(); idx++) {
                        View view = LayoutInflater.from(mActivity).inflate(R.layout.include_survey_card, null);
                        final LinearLayout surveyOptionPanel = (LinearLayout) view.findViewById(R.id.surveyOptionPanel);
                        final TextView surverOptionName = (TextView) view.findViewById(R.id.surveyOptionName);
                        final TextView voteCount = (TextView) view.findViewById(R.id.voteCount);

                        JSONObject jsonDoc = options.getJSONObject(idx);
                        VoteData voteData = new VoteData(jsonDoc);

                        surverOptionName.setText(voteData.getName());
                        if(TextUtils.equals(actionName, String.valueOf(idx))) {
                            surveyOptionPanel.setBackgroundResource(R.drawable.feed_new);
                        };

                        ArrayList<SubLinkData> surveySubLinks = channelData.getSubLinks(TYPE_SURVEY, String.valueOf(idx));

                        if(surveySubLinks == null) {
                            voteCount.setText("0명");
                        } else {
                            voteCount.setText(surveySubLinks.size() + "명");
                        }


                        final int fIdx = idx;
                        surveyOptionPanel.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                String actionName = (String)mSurveyPanel.getTag();
                                if(!TextUtils.isEmpty(actionName)) {
                                    Toast.makeText(mActivity, "이미 투표에 참여하였습니다.", Toast.LENGTH_SHORT).show();
                                    return;
                                }

                                try {

                                    JSONObject params = channelData.getAddressJSONObject();
                                    params.put("parent", channelData.getId());
                                    params.put("type", TYPE_SURVEY);
                                    params.put("name", String.valueOf(fIdx));

                                    mApi.call(api_channels_id_vote, params, new AjaxCallback<JSONObject>() {
                                        @Override
                                        public void callback(String url, JSONObject object, AjaxStatus status) {
                                            super.callback(url, object, status);

                                            if (status.getCode() == 500) {
                                                EventBus.getDefault().post(new ErrorData(TYPE_ERROR_AUTH, TYPE_ERROR_AUTH));
                                            } else {
                                                ChannelData channelData = new ChannelData(object);
                                                ChannelData parentData = channelData.getParent();
                                                ArrayList<SubLinkData> subLinkDatas = parentData.getSubLinks(TYPE_SURVEY, String.valueOf(fIdx));

                                                voteCount.setText(subLinkDatas.size() + "명");

                                                String actionName = parentData.getActionName(TYPE_SURVEY, AuthHelper.getUserId(mActivity));
                                                mSurveyPanel.setTag(actionName);
                                                surveyOptionPanel.setBackgroundResource(R.drawable.feed_new);

                                                EventBus.getDefault().post(new SuccessData(api_channels_id_vote, object));
                                            }

                                        }
                                    });

                                }catch (JSONException e) {
                                    Log.e(TAG, "Error " + e.toString());
                                }
                            }
                        });

                        surveyContentPanel.addView(view);
                    }

                } catch (JSONException e) {
                    Log.e(TAG, "Error " + e.toString());
                }


            }else {
                mSurveyPanel.setVisibility(View.GONE);
            }
        }
    }

    protected void setUserName(Activity activity, ChannelData channelData) {
        if(channelData == null){
            if(!TextUtils.isEmpty(mChannel.getUserName())) {
                mUserName.setText(mChannel.getUserName());
            } else {
                mUserName.setText("아무개");
            }
        } else {
            if(!TextUtils.isEmpty(channelData.getUserName())) {
                mUserName.setText(channelData.getUserName());
            } else {
                mUserName.setText("아무개");
            }
        }

    }

    protected void setCreatedAt( ChannelData channelData) {
        String dateString = channelData.getCreatedAt();
        try{
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
            dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
            Date parsedDate = dateFormat.parse(dateString);
            Timestamp timestamp = new Timestamp(parsedDate.getTime());
            mCreatedAt.setText(Helper.toPrettyDate(timestamp.getTime()));
        }catch(Exception e){
            Log.e(TAG, "error " + e.toString());
        }
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

            // postAd
            case R.id.postAd:
                Bundle adsBundle = new Bundle();
                adsBundle.putString("channel", mChannel.getJsonObject().toString());
                Intent adsIntent = new Intent(mActivity, AdsCreateActivity.class);
                adsIntent.putExtra("bundle", adsBundle);
                startActivity(adsIntent);
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
