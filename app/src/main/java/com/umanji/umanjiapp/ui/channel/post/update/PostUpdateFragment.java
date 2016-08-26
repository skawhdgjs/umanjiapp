package com.umanji.umanjiapp.ui.channel.post.update;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.leocardz.link.preview.library.LinkPreviewCallback;
import com.leocardz.link.preview.library.SearchUrls;
import com.leocardz.link.preview.library.SourceContent;
import com.leocardz.link.preview.library.TextCrawler;
import com.umanji.umanjiapp.R;
import com.umanji.umanjiapp.helper.AuthHelper;
import com.umanji.umanjiapp.helper.Helper;
import com.umanji.umanjiapp.model.ChannelData;
import com.umanji.umanjiapp.model.ErrorData;
import com.umanji.umanjiapp.model.SubLinkData;
import com.umanji.umanjiapp.model.SuccessData;
import com.umanji.umanjiapp.model.VoteData;
import com.umanji.umanjiapp.ui.channel.BaseChannelUpdateFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;

import static com.umanji.umanjiapp.helper.FileHelper.extractUrls;


public class PostUpdateFragment extends BaseChannelUpdateFragment {
    private static final String TAG = "BaseChannelCreateFragment";

    // for site preview info.
    TextCrawler mTextCrawler;
    protected LinearLayout mMetaPanel;
    protected ImageView mMetaPhoto;
    protected TextView mMetaTitle;
    protected TextView mMetaDesc;
    protected boolean isPreview = false;

    public Button mApplyBtn;
    public int mPreBoundLevel;
    public int mBoundLevel = 0;


    // for voting
    protected boolean hasVote = false;
    protected LinearLayout mVotePanel;
    protected LinearLayout mVoteOptionPanel;
    protected LinearLayout mVoteBtn;
    protected Button mVoteRemoveBtn;
    protected Button mAddVoteOptionBtn;

    protected CheckBox mPush;
    protected boolean isPushChecked = false;
    protected Button mBoundSet;
    protected Button mCancelButton;

    protected boolean isReady = false;
    protected boolean mClicked = false;

    public static PostUpdateFragment newInstance(Bundle bundle) {
        PostUpdateFragment fragment = new PostUpdateFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(getArguments() != null) {
            String jsonString = getArguments().getString("channel");
            if(jsonString != null) {
                mChannel = new ChannelData(jsonString);
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);



        updateView();
        return view;
    }

    @Override
    public void initWidgets(View view) {
        super.initWidgets(view);

        mMetaPanel = (LinearLayout) view.findViewById(R.id.metaPanel);
        mMetaPhoto = (ImageView) view.findViewById(R.id.metaPhoto);
        mMetaTitle = (TextView) view.findViewById(R.id.metaTitle);
        mMetaDesc = (TextView) view.findViewById(R.id.metaDesc);

        mTextCrawler = new TextCrawler();
        setKeyListnerForSitePreview();

        mVotePanel = (LinearLayout) view.findViewById(R.id.votePanel);
        mVoteOptionPanel = (LinearLayout) view.findViewById(R.id.voteOptionPanel);
        mVoteBtn = (LinearLayout) view.findViewById(R.id.voteBtn);
        mVoteBtn.setOnClickListener(this);
        mVoteRemoveBtn = (Button) view.findViewById(R.id.voteRemoveBtn);
        mVoteRemoveBtn.setOnClickListener(this);
        mAddVoteOptionBtn = (Button) view.findViewById(R.id.addVoteOptionBtn);
        mAddVoteOptionBtn.setOnClickListener(this);

        mSubmitBtn.setEnabled(isReady);
        mSubmitBtn.setTextColor(Color.parseColor("#5c5cd6"));

        mBoundSet = (Button) view.findViewById(R.id.boundSet);
        mBoundSet.setOnClickListener(this);

        mName.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable arg0) {
                isReady = true;
                if (mName.getText().toString().length() == 0) {
                    mSubmitBtn.setTextColor(Color.parseColor("#5c5cd6"));
                    mBoundSet.setVisibility(View.GONE);
                } else {
                    mSubmitBtn.setTextColor(Color.parseColor("#ffffff"));
                    mBoundSet.setVisibility(View.VISIBLE);
                }
                enableSubmitIfReady();
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });


        mPush = (CheckBox) view.findViewById(R.id.push);
        mPush.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {
                if (buttonView.getId() == R.id.push) {
                    if (isChecked) {
                        isPushChecked = true;
                    } else {
                        isPushChecked = false;
                    }
                }
            }
        });

        setName(mActivity, mChannel);
        setPhoto(mActivity, mChannel);
        setMetaPanel(mActivity, mChannel);
        setSurvey(mActivity, mChannel);

        mPreBoundLevel = mChannel.getLevel();
    }

    @Override
    public void updateView() {
        super.updateView();

        setName(mActivity, mChannel);
        setPhoto(mActivity, mChannel);
    }

    protected void setMetaPanel(Activity activity, final ChannelData channelData) {
        JSONObject descJson = channelData.getDesc();
        if(descJson != null) {
            String strMetaTitle = descJson.optString("metaTitle");
            String strMetaDesc = descJson.optString("metaDesc");
            String strMetaPhoto = descJson.optString("metaPhoto");

            if(!TextUtils.isEmpty(strMetaTitle) || !TextUtils.isEmpty(strMetaDesc) || !TextUtils.isEmpty(strMetaPhoto)) {

                mMetaPanel.setVisibility(View.VISIBLE);
                mMetaPanel.setOnClickListener(new View.OnClickListener() {
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
                if(!TextUtils.isEmpty(strMetaTitle)) {
                    mMetaTitle.setVisibility(View.VISIBLE);
                    mMetaTitle.setText(strMetaTitle);
                }else {
                    mMetaTitle.setVisibility(View.GONE);
                }

                if(!TextUtils.isEmpty(strMetaDesc)) {
                    mMetaDesc.setVisibility(View.VISIBLE);
                    mMetaDesc.setText(strMetaDesc);
                }else {
                    mMetaDesc.setVisibility(View.GONE);
                }

                if(!TextUtils.isEmpty(strMetaPhoto)) {
                    mMetaPhoto.setVisibility(View.VISIBLE);
                    Glide.with(mActivity)
                            .load(mMetaPhoto)
                            .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                            .into(mMetaPhoto);
                }else {
                    mMetaPhoto.setVisibility(View.GONE);
                }

            }else {
                mMetaPanel.setVisibility(View.GONE);
            }
        }else {
            mMetaPanel.setVisibility(View.GONE);
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
                        mVotePanel.setVisibility(View.GONE);
                        return;
                    }

                    mVotePanel.setVisibility(View.VISIBLE);

                    LinearLayout surveyContentPanel = (LinearLayout)mVotePanel.findViewById(R.id.surveyContentPanel);
                    surveyContentPanel.removeAllViews();


                    String actionName = channelData.getActionName(TYPE_SURVEY, AuthHelper.getUserId(mActivity));
                    if(TextUtils.isEmpty(actionName)) {
                        mVotePanel.setTag(null);
                    }else {
                        mVotePanel.setTag(actionName);
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
                                String actionName = (String)mVotePanel.getTag();
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
                                                mVotePanel.setTag(actionName);
                                                surveyOptionPanel.setBackgroundResource(R.drawable.feed_new);

                                                EventBus.getDefault().post(new SuccessData(api_channels_id_vote, object));
                                            }

                                        }
                                    });

                                }catch (JSONException e) {
//                                    Log.e(TAG, "Error " + e.toString());
                                }
                            }
                        });

                        surveyContentPanel.addView(view);
                    }

                } catch (JSONException e) {
//                    Log.e(TAG, "Error " + e.toString());
                }


            }else {
                mVotePanel.setVisibility(View.GONE);
            }
        }
    }



    public void enableSubmitIfReady() {

        boolean isReady =mName.getText().toString().length()>1;
        mSubmitBtn.setEnabled(isReady);
    }

    @Override
    public View getView(LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.activity_post_update, container, false);
    }


    @Override
    protected void submit() {

        ArrayList<String> urls = getUrlsFrom();
        if (isPreview == false && urls.size() > 0) {
            requestWithMeta(urls);

        } else {
            request();
        }
    }

    @Override
    protected void request() {
        mProgress.show();
        if(mClicked == true){
            Toast.makeText(mActivity,"이미 요청했습니다.", Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            JSONObject params = new JSONObject();
            setChannelParams(params);
            if(mBoundLevel > 0 && mPreBoundLevel != mBoundLevel){       // Level value changed.
                params.put("level", mBoundLevel);
            }

            JSONObject descParams = new JSONObject();
            if(isPreview) {
                descParams.put("metaTitle", mMetaTitle.getText().toString());
                descParams.put("metaDesc", Helper.getShortenString(mMetaDesc.getText().toString(), 50));
                descParams.put("metaPhoto", mMetaPhotoUrl);
            }

            if(hasVote) {
                JSONObject voteParams = new JSONObject();
                JSONArray options = new JSONArray();
                for(int idx=0; idx < mVoteOptionPanel.getChildCount(); idx++) {
                    TextView text = (TextView)mVoteOptionPanel.getChildAt(idx);

                    JSONObject voteOptionParams = new JSONObject();
                    voteOptionParams.put("name", text.getText().toString());
                    voteOptionParams.put("count", 0);
                    voteOptionParams.put("voters", null);

                    options.put(voteOptionParams);
                }

                voteParams.put("type", TYPE_POST_SURVEY);
                voteParams.put("options", (Object)options);

                descParams.put("vote", voteParams);
            }

            params.put("desc", descParams);
            mApi.call(api_channels_id_update, params);

        }catch(JSONException e) {
            Log.e("BaseChannelUpdate", "error " + e.toString());
        }
    }

    @Override
    public void onEvent(SuccessData event) {
        super.onEvent(event);

        switch (event.type) {
            case api_channels_id_update:
                mActivity.finish();
                mClicked = false;
                EventBus.getDefault().post(new SuccessData(EVENT_UPDATEVIEW));
                break;
        }
    }

    private void setKeyListnerForSitePreview() {
        mName.setOnKeyListener(new View.OnKeyListener() {

            @Override
            public boolean onKey(View arg0, int arg1, KeyEvent arg2) {
                String name = mName.getText().toString().replace("\n", " ");
                ArrayList<String> urls = SearchUrls.matches(name);

                if (arg1 == 66 && urls.size() > 0) {

                    mTextCrawler
                            .makePreview(new LinkPreviewCallback() {
                                @Override
                                public void onPre() {
                                    mProgress.show();
                                }

                                @Override
                                public void onPos(SourceContent sourceContent, boolean isNull) {
                                    if (isNull || sourceContent.getFinalUrl().equals("")) {
                                        isPreview = false;
                                        mMetaPanel.setVisibility(View.GONE);

                                    } else {
                                        isPreview = true;
                                        mMetaPanel.setVisibility(View.VISIBLE);

                                        if (sourceContent.getImages().size() > 0) {
                                            mMetaPhotoUrl = sourceContent.getImages().get(0);
                                            mMetaPhoto.setVisibility(View.VISIBLE);
                                            Glide.with(mActivity).load(mMetaPhotoUrl).into(mMetaPhoto);
                                        } else {
                                            mMetaPhoto.setVisibility(View.GONE);
                                        }

                                        if (TextUtils.isEmpty(sourceContent.getTitle())) {
                                            mMetaTitle.setVisibility(View.GONE);
                                        } else {
                                            mMetaTitle.setVisibility(View.VISIBLE);
                                            mMetaTitle.setText(sourceContent.getTitle());
                                        }

                                        if (TextUtils.isEmpty(sourceContent.getDescription())) {
                                            mMetaDesc.setVisibility(View.GONE);
                                        } else {
                                            mMetaDesc.setVisibility(View.VISIBLE);
                                            mMetaDesc.setText(sourceContent.getDescription());
                                        }
                                    }

                                    mProgress.hide();
                                }
                            }, urls.get(0));
                }
                return false;
            }
        });
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);

        switch (v.getId()) {
            case R.id.voteBtn:
                hasVote = true;
                mVotePanel.setVisibility(View.VISIBLE);

                mVoteBtn.setVisibility(View.GONE);
                mVoteRemoveBtn.setVisibility(View.VISIBLE);
                break;
            case R.id.voteRemoveBtn:
                hasVote = false;
                mVotePanel.setVisibility(View.GONE);

                mVoteBtn.setVisibility(View.VISIBLE);
                mVoteRemoveBtn.setVisibility(View.GONE);
                break;
            case R.id.addVoteOptionBtn:
                AutoCompleteTextView option = new AutoCompleteTextView(mActivity);
                mVoteOptionPanel.addView(option);
                break;

            case R.id.boundSet:
                levelPickerShow();
                break;
        }
    }

    public void levelPickerShow() {

        final Dialog d = new Dialog(getActivity());
        d.setContentView(R.layout.dialog_level_picker);

        TextView title = (TextView) d.findViewById(android.R.id.title);
        title.setText("알리고자 하는 범위");
        title.setGravity(Gravity.CENTER);
        title.setTextSize(22);
        title.setPadding(10, 10, 10, 10);

        final NumberPicker np = (NumberPicker) d.findViewById(R.id.level_picker);
        np.setMinValue(6);   // min value 0
        np.setMaxValue(18); // max value 100
        np.setValue(18);
        np.setDisplayedValues( Helper.levelBounds );
        np.setWrapSelectorWheel(false);
        mApplyBtn = (Button) d.findViewById(R.id.apply_button);
        mCancelButton = (Button) d.findViewById(R.id.cancel_button);

        mCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                d.cancel();
            }
        });

        mApplyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBoundLevel = np.getValue();
                Toast.makeText(mActivity, String.valueOf(np.getValue()), Toast.LENGTH_LONG).show();
                d.cancel();
            }
        });

        d.show();
    }

    private ArrayList<String> getUrlsFrom() {
        String name = mName.getText().toString().replace("\n", " ");
        return SearchUrls.matches(name);
    }

    private void requestWithMeta(ArrayList<String> urls) {
        mTextCrawler
                .makePreview(new LinkPreviewCallback() {
                    @Override
                    public void onPre() {
                        mProgress.show();
                    }

                    @Override
                    public void onPos(SourceContent sourceContent, boolean isNull) {

                        if (isNull || sourceContent.getFinalUrl().equals("")) {
                            isPreview = false;
                            mMetaPanel.setVisibility(View.GONE);

                        } else {
                            isPreview = true;
                            mMetaPanel.setVisibility(View.VISIBLE);

                            if (sourceContent.getImages().size() > 0) {
                                mMetaPhotoUrl = sourceContent.getImages().get(0);
                                mMetaPhoto.setVisibility(View.VISIBLE);
                                Glide.with(mActivity).load(mMetaPhotoUrl).into(mMetaPhoto);
                            } else {
                                mMetaPhoto.setVisibility(View.GONE);
                            }

                            if (TextUtils.isEmpty(sourceContent.getTitle())) {
                                mMetaTitle.setVisibility(View.GONE);
                            } else {
                                mMetaTitle.setVisibility(View.VISIBLE);
                                mMetaTitle.setText(sourceContent.getTitle());
                            }

                            if (TextUtils.isEmpty(sourceContent.getDescription())) {
                                mMetaDesc.setVisibility(View.GONE);
                            } else {
                                mMetaDesc.setVisibility(View.VISIBLE);
                                mMetaDesc.setText(sourceContent.getDescription());
                            }
                        }

                        mProgress.hide();
                        request();
                    }
                }, urls.get(0));
    }


}
