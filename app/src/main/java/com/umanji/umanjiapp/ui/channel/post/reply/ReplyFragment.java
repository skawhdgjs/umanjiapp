package com.umanji.umanjiapp.ui.channel.post.reply;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
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
import com.umanji.umanjiapp.ui.channel._fragment.BaseChannelListAdapter;
import com.umanji.umanjiapp.ui.channel._fragment.BaseChannelListFragment;
import com.umanji.umanjiapp.ui.channel.advertise.AdsCreateActivity;
import com.umanji.umanjiapp.ui.channel.post.create.PostCreateActivity;
import com.umanji.umanjiapp.ui.channel.post.update.PostUpdateActivity;
import com.umanji.umanjiapp.ui.modal.WebViewActivity;

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


public class ReplyFragment extends BaseChannelListFragment {
    private static final String TAG = "ReplyFragment";

    protected boolean mClicked = false;

    protected ImageView mUserPhoto;
    protected ImageView mLookAround;
    protected TextView mName;
    protected TextView mUserName;
    protected TextView mParentName;

    protected TextView mCreatedAt;
    protected ImageView mPhoto;

    protected LinearLayout metaPanel;
    protected ImageView metaPhoto;
    protected TextView metaTitle;
    protected TextView metaDesc;
    protected TextView mPostAd;
    protected ImageView mOptionBtn;

    protected LinearLayout mSurveyPanel;

    protected RelativeLayout mFab;

    protected LinearLayout mGotoSpot;
    protected LinearLayout mAdvertise;
    protected LinearLayout mAbuse;
    protected LinearLayout mEdit;
    protected Button mCancel;

    protected LinearLayout mLikeBtn;
    protected LinearLayout mDislikeBtn;
    protected LinearLayout mReplyBtn;

    public static ReplyFragment newInstance(Bundle bundle) {
        ReplyFragment fragment = new ReplyFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public View getView(LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.activity_reply, container, false);
    }

    @Override
    public BaseChannelListAdapter getListAdapter() {
        return new ReplyListAdapter(mActivity, this, mChannel);
    }

    @Override
    public void initWidgets(View view) {

//        mEmptyStates = (ImageView) view.findViewById(R.id.empty_states);


        mUserPhoto = (ImageView) view.findViewById(R.id.userPhoto);
        mLookAround = (ImageView) view.findViewById(R.id.lookAround);
        mLookAround.setOnClickListener(this);

        mUserName = (TextView) view.findViewById(R.id.userName);
        mParentName = (TextView) view.findViewById(R.id.parentName);

        mCreatedAt = (TextView) view.findViewById(R.id.createdAt);

        mPhoto = (ImageView) view.findViewById(R.id.photo);
        if(mChannel.getPhoto() != null){
            mPhoto.setVisibility(View.VISIBLE);
        }

        mUserPhoto = (ImageView) view.findViewById(R.id.userPhoto);
        mUserPhoto.setOnClickListener(this);

        mName = (TextView) view.findViewById(R.id.name);
        mFab = (RelativeLayout) view.findViewById(R.id.fab);

        metaPanel       = (LinearLayout) view.findViewById(R.id.metaPanel);
        metaPhoto       = (ImageView) view.findViewById(R.id.metaPhoto);
        metaTitle       = (TextView) view.findViewById(R.id.metaTitle);
        metaDesc        = (TextView) view.findViewById(R.id.metaDesc);

        mSurveyPanel    = (LinearLayout) view.findViewById(R.id.surveyPanel);

        mFab = (RelativeLayout) view.findViewById(R.id.fab);
        mFab.setOnClickListener(this);

        mPostAd = (TextView) view.findViewById(R.id.postAd);
        mPostAd.setOnClickListener(this);

        mOptionBtn = (ImageView) view.findViewById(R.id.optionAlert);
        mOptionBtn.setOnClickListener(this);

        mLikeBtn = (LinearLayout) view.findViewById(R.id.likeBtn);
        mLikeBtn.setOnClickListener(this);
        mDislikeBtn = (LinearLayout) view.findViewById(R.id.dislikeBtn);
        mDislikeBtn.setOnClickListener(this);
        mReplyBtn = (LinearLayout) view.findViewById(R.id.replyBtn);
        mReplyBtn.setOnClickListener(this);

        setName(mActivity, mChannel, "내용없음");
        setUserPhoto(mActivity, mChannel.getOwner());
        setPhoto(mActivity, mChannel);
        setMetaPanel(mActivity, mChannel);
        setSurvey(mActivity, mChannel);
        setUserName(mActivity, mChannel.getOwner());
        setParentName(mActivity, mChannel.getParent());
        setCreatedAt(mChannel);
    }

    protected void setParentName(Activity activity, final ChannelData parentChannelData) {
        String parentId = "";
        if(mChannel != null ) parentId = mChannel.getId();
        if(parentChannelData != null && !TextUtils.equals(parentChannelData.getId(), parentId)) {
            mParentName.setVisibility(View.VISIBLE);

            if(TextUtils.isEmpty(parentChannelData.getName())) {
                mParentName.setText("이름없음");
            }else {
                mParentName.setText(Helper.getShortenString(parentChannelData.getName()));
            }

            mParentName.setOnClickListener(new View.OnClickListener() {
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
            mParentName.setVisibility(View.GONE);
        }

    }

    protected void setPhoto(Activity activity, final ChannelData channelData) {
        String photo = channelData.getPhoto();
        if(!TextUtils.isEmpty(photo)) {
            Glide.with(mActivity)
                    .load(photo)
                    .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                    .into(mPhoto);

            mPhoto.setVisibility(View.VISIBLE);

            mPhoto.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Helper.startImageViewActivity(mActivity, channelData);
                }
            });
        }else {
            mPhoto.setVisibility(View.GONE);
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
//                        .override(40, 40)
                        .thumbnail(1f)
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

    protected void setUserName(Activity activity, final ChannelData channelData) {
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

        mUserName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Helper.startActivity(mActivity, channelData);
            }
        });

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
    public void loadMoreData() {
        isLoading = true;
        mLoadCount = mLoadCount + 1;

        try {
            JSONObject params = new JSONObject();
            params.put("page", mAdapter.getCurrentPage()); // for paging
            params.put("limit", 5);
            params.put("type", TYPE_POST);

            switch (mChannel.getType()) {
                case TYPE_USER:
                    params.put("owner", mChannel.getId());
                    break;
                default:
                    params.put("parent", mChannel.getId());
                    break;
            }


            mApi.call(api_channels_posts_find, params, new AjaxCallback<JSONObject>() {
                @Override
                public void callback(String url, JSONObject object, AjaxStatus status) {
                    if(status.getCode() == 500) {
                        EventBus.getDefault().post(new ErrorData(TYPE_ERROR_AUTH, TYPE_ERROR_AUTH));
                    }else {
                        try {
                            JSONArray jsonArray = object.getJSONArray("data");

                            if(jsonArray.length() == 0) {

//                                mEmptyStates.setVisibility(View.VISIBLE);
                                updateView();

                            } else {
                                for(int idx = 0; idx < jsonArray.length(); idx++) {
                                    JSONObject jsonDoc = jsonArray.getJSONObject(idx);
                                    ChannelData doc = new ChannelData(jsonDoc);

                                    if(doc != null && doc.getOwner() != null && !TextUtils.isEmpty(doc.getOwner().getId())) {
                                        mAdapter.addBottom(doc);
                                    }
                                }
                                updateView();
                            }
                        } catch (JSONException e) {
                            Log.e(TAG, "Error " + e.toString());
                        }

                        isLoading = false;
                    }
                }
            });
            mAdapter.setCurrentPage(mAdapter.getCurrentPage() + 1);
        } catch(JSONException e) {
            Log.e(TAG, "error " + e.toString());
        }

    }

    private AlphaAnimation buttonClick = new AlphaAnimation(0F, 1F);

    private void showOptionAlert() {
        final Dialog dialog = new Dialog(getActivity());
//        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_post_option);

        TextView title = (TextView) dialog.findViewById(android.R.id.title);
        title.setText("선택");
//        title.setBackgroundResource(R.drawable.gradient);
        title.setPadding(10, 10, 10, 10);
        title.setGravity(Gravity.CENTER); // this is required to bring it to center.
        title.setTextSize(22);

        mGotoSpot = (LinearLayout) dialog.findViewById(R.id.gotoSpot);
        mGotoSpot.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                EventBus.getDefault().post(new SuccessData(EVENT_LOOK_AROUND, mChannel.getJsonObject()));
//                mActivity.finish();
                dialog.dismiss();
            }
        });
        mGotoSpot.startAnimation(buttonClick);
        buttonClick.setDuration(500);

        mAdvertise = (LinearLayout) dialog.findViewById(R.id.advertise);
        mAbuse = (LinearLayout) dialog.findViewById(R.id.abuse);
        mEdit = (LinearLayout) dialog.findViewById(R.id.edit);
        mCancel = (Button) dialog.findViewById(R.id.cancelButton);

        mAdvertise.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle adsBundle = new Bundle();
                adsBundle.putString("channel", mChannel.getJsonObject().toString());
                adsBundle.putString("whichAction", "nothing");
                Intent adsIntent = new Intent(mActivity, AdsCreateActivity.class);
                adsIntent.putExtra("bundle", adsBundle);
                startActivity(adsIntent);
                dialog.dismiss();
            }
        });
        mAdvertise.startAnimation(buttonClick);
        buttonClick.setDuration(500);


        mAbuse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mActivity, "준비중입니다...", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });
        mAbuse.startAnimation(buttonClick);
        buttonClick.setDuration(500);


        mEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent editIntent = new Intent(mActivity, PostUpdateActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("channel", mChannel.getJsonObject().toString());
                editIntent.putExtra("bundle", bundle);
                startActivity(editIntent);
                Toast.makeText(mActivity, "수정하세요.", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });
        mEdit.startAnimation(buttonClick);
        buttonClick.setDuration(500);


        mCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                EventBus.getDefault().post(new SuccessData(EVENT_LOOK_AROUND, mChannel.getJsonObject()));
                dialog.dismiss();
            }
        });
        mCancel.startAnimation(buttonClick);
        buttonClick.setDuration(500);

        dialog.show();

    }

    @Override
    public void updateView() {
        mAdapter.notifyDataSetChanged();

        setName(mActivity, mChannel, "내용없음");
        setUserPhoto(mActivity, mChannel.getOwner());
        setPhoto(mActivity, mChannel);
        setMetaPanel(mActivity, mChannel);
        setSurvey(mActivity, mChannel);
        setUserName(mActivity, mChannel.getOwner());
        setParentName(mActivity, mChannel.getParent());
        setCreatedAt(mChannel);
/*
        if(AuthHelper.isLogin(mActivity)) {
            mFab.setVisibility(View.VISIBLE);
        }else {
            mFab.setVisibility(View.GONE);
        }
        */
    }

    @Override
    public void onEvent(SuccessData event) {
        super.onEvent(event);

        ChannelData channelData = new ChannelData(event.response);

        switch (event.type) {
            case api_channels_create:
                String parentId = event.response.optString("parent");
                if(TextUtils.equals(mChannel.getId(), parentId)) {
                    mChannel = channelData.getParent();
                    mAdapter.addTop(channelData);
                    mAdapter.notifyDataSetChanged();
                }
                break;

            case EVENT_LOOK_AROUND:
                mActivity.finish();
                break;
        }
    }

    protected void levelRequest(String type) {
        mProgress.show();
        if(mClicked == true){
            Toast.makeText(mActivity,"이미 요청했습니다.", Toast.LENGTH_SHORT).show();
            return;
        }
        int thisLevel = mChannel.getLevel();
        try {
            JSONObject params = new JSONObject();
            params.put("id", mChannel.getId());
            switch(type){
                case "like":
                    params.put("level", thisLevel-1);
                    break;
                case "dislike":
                    params.put("level", thisLevel+1);
                    break;
            }

            mApi.call(api_channels_id_update, params);

            mClicked = true;
            mProgress.cancel();

        }catch(JSONException e) {
            Log.e("BaseChannelUpdate", "error " + e.toString());
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fab:
                    Intent intent = new Intent(mActivity, PostCreateActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("channel", mChannel.getJsonObject().toString());
                    intent.putExtra("bundle", bundle);
                    startActivity(intent);
                break;

            case R.id.lookAround:
                EventBus.getDefault().post(new SuccessData(EVENT_LOOK_AROUND, mChannel.getJsonObject()));
                break;

            case R.id.postAd:
                Bundle adsBundle = new Bundle();
                adsBundle.putString("channel", mChannel.getJsonObject().toString());
                Intent adsIntent = new Intent(mActivity, AdsCreateActivity.class);
                adsIntent.putExtra("bundle", adsBundle);
                startActivity(adsIntent);
                break;

            case R.id.optionAlert:
                mOptionBtn.startAnimation(buttonClick);
                buttonClick.setDuration(500);

                showOptionAlert();
                break;
            case R.id.likeBtn:
                levelRequest("like");
                Toast.makeText(mActivity, "한 단계 상위에 알려졌습니다", Toast.LENGTH_LONG).show();
                mActivity.finish();
                break;
            case R.id.dislikeBtn:
                levelRequest("dislike");
                Toast.makeText(mActivity, "본 글이 한단계 강등됐습니다.", Toast.LENGTH_LONG).show();
                mActivity.finish();
                break;
            case R.id.replyBtn:
                Toast.makeText(mActivity, "write reply", Toast.LENGTH_LONG).show();
                if (AuthHelper.isLogin(mActivity)) {
                    Helper.startPostCreateActivity(mActivity, mChannel);
                } else {
                    Helper.callAuthErrorEvent();
                }
                break;
        }
    }
}
