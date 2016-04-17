package com.umanji.umanjiapp.ui.channel.post.create;

import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.leocardz.link.preview.library.LinkPreviewCallback;
import com.leocardz.link.preview.library.SearchUrls;
import com.leocardz.link.preview.library.SourceContent;
import com.leocardz.link.preview.library.TextCrawler;
import com.umanji.umanjiapp.R;
import com.umanji.umanjiapp.helper.Helper;
import com.umanji.umanjiapp.model.SuccessData;
import com.umanji.umanjiapp.ui.channel.BaseChannelCreateFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class PostCreateFragment extends BaseChannelCreateFragment {
    private static final String TAG = "BaseChannelCreateFragment";

    // for site preview info.
    TextCrawler mTextCrawler;
    protected LinearLayout mMetaPanel;
    protected ImageView mMetaPhoto;
    protected TextView mMetaTitle;
    protected TextView mMetaDesc;
    protected boolean isPreview = false;


    // for voting
    protected boolean hasVote = false;
    protected LinearLayout mVotePanel;
    protected LinearLayout mVoteOptionPanel;
    protected Button mVoteBtn;
    protected Button mVoteRemoveBtn;
    protected Button mAddVoteOptionBtn;

    protected boolean isReady = false;

    public static PostCreateFragment newInstance(Bundle bundle) {
        PostCreateFragment fragment = new PostCreateFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
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
        mVoteBtn = (Button) view.findViewById(R.id.voteBtn);
        mVoteBtn.setOnClickListener(this);
        mVoteRemoveBtn = (Button) view.findViewById(R.id.voteRemoveBtn);
        mVoteRemoveBtn.setOnClickListener(this);
        mAddVoteOptionBtn = (Button) view.findViewById(R.id.addVoteOptionBtn);
        mAddVoteOptionBtn.setOnClickListener(this);

        mSubmitBtn2.setEnabled(isReady);
        mSubmitBtn2.setTextColor(Color.parseColor("#5c5cd6"));

        mName.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable arg0) {
                isReady = true;
                if(mName.getText().toString().length()==0){
                    mSubmitBtn2.setTextColor(Color.parseColor("#5c5cd6"));
                } else{
                    mSubmitBtn2.setTextColor(Color.parseColor("#ffffff"));
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
    }

    public void enableSubmitIfReady() {

        boolean isReady =mName.getText().toString().length()>1;
        mSubmitBtn2.setEnabled(isReady);
    }

    @Override
    public View getView(LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.activity_post_create, container, false);
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
        try {
            JSONObject params = mChannel.getAddressJSONObject();
            params.put("parent", mChannel.getId());
            params.put("level", mChannel.getLevel());
            params.put("name", mName.getText().toString());
            params.put("type", TYPE_POST);


            if(mPhotoUri != null) {
                ArrayList<String> photos = new ArrayList<>();
                photos.add(mPhotoUri);
                params.put("photos", new JSONArray(photos));
                mPhotoUri = null;
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
            mApi.call(api_channels_create, params);

        }catch(JSONException e) {
            Log.e("BaseChannelCreate", "error " + e.toString());
        }
    }

    @Override
    public void onEvent(SuccessData event) {
        super.onEvent(event);

        switch (event.type) {
            case api_channels_create:
                mProgress.hide();
                mActivity.finish();
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
        }
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
