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
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.bumptech.glide.Glide;
import com.leocardz.link.preview.library.LinkPreviewCallback;
import com.leocardz.link.preview.library.SearchUrls;
import com.leocardz.link.preview.library.SourceContent;
import com.leocardz.link.preview.library.TextCrawler;
import com.umanji.umanjiapp.R;
import com.umanji.umanjiapp.helper.AuthHelper;
import com.umanji.umanjiapp.helper.Helper;
import com.umanji.umanjiapp.model.AuthData;
import com.umanji.umanjiapp.model.ChannelData;
import com.umanji.umanjiapp.model.SubLinkData;
import com.umanji.umanjiapp.model.SuccessData;
import com.umanji.umanjiapp.ui.channel.BaseChannelCreateFragment;
import com.umanji.umanjiapp.ui.main.MainFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;
import de.greenrobot.event.EventBus;


public class PostCreateFragment extends BaseChannelCreateFragment {
    private static final String TAG = "BaseChannelCreateFragment";

    // for site preview info.
    TextCrawler mTextCrawler;
    protected LinearLayout mMetaPanel;
    //    protected ChannelData mUser;
    protected ImageView mMetaPhoto;
    protected TextView mMetaTitle;
    protected TextView mMetaDesc;
    protected boolean isPreview = false;


    // for voting
    protected boolean hasVote = false;
    protected LinearLayout mVotePanel;
    protected LinearLayout mVoteOptionPanel;
    protected LinearLayout mVoteBtn;
    protected Button mVoteRemoveBtn;
    protected Button mAddVoteOptionBtn;

    protected CheckBox mPush;
    protected boolean isPushChecked = false;


    protected boolean isReady = false;
    protected ArrayList<SubLinkData> mExperts;
    protected ChannelData mUser;
    protected ArrayList<String> mExpertsArr;

    protected String sendPointMessage = "0";

    public static PostCreateFragment newInstance(Bundle bundle) {
        PostCreateFragment fragment = new PostCreateFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mExpertsArr = new ArrayList<String>();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }
/*

    @Override
    public void onPause() {
        super.onPause();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        EventBus.getDefault().register(this);
//        EventBus.getDefault().hasSubscriberForEvent(MainFragment.class);
    }
*/

    @Override
    public void initWidgets(View view) {
        super.initWidgets(view);

        getUserData();

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

        mName.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable arg0) {
                isReady = true;
                if (mName.getText().toString().length() == 0) {
                    mSubmitBtn.setTextColor(Color.parseColor("#5c5cd6"));
                } else {
                    mSubmitBtn.setTextColor(Color.parseColor("#ffffff"));
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
    }

    public void enableSubmitIfReady() {

        boolean isReady = mName.getText().toString().length() > 1;
        mSubmitBtn.setEnabled(isReady);
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

    public void getUserData() {
        try {
            JSONObject params = new JSONObject();
            params.put("access_token", AuthHelper.getToken(mActivity));
            mApi.call(api_token_check, params, new AjaxCallback<JSONObject>() {
                @Override
                public void callback(String url, JSONObject object, AjaxStatus status) {
                    AuthData auth = new AuthData(object);
                    if (auth != null && auth.getToken() != null) {
                        mUser = auth.getUser();
                        mExperts = mUser.getSubLinks();
                        SubLinkData element;

                        for (int idx = 0; mExperts.size() > idx; idx++) {    // sublinks 배열 갯수
                            element = mExperts.get(idx);
                            String name = element.getName().toString();

                            mExpertsArr.add(name);

                        }

                        Log.d("Paul", "auth :: " + mUser);

                    }

                }
            });
        } catch (JSONException e) {
            Log.e(TAG, "error " + e.toString());
        }

    }

    protected void subRequest(JSONObject params) {
        mApi.call(api_channels_expert, params);
    }

    protected int getBiggestPoint(String expert) {
        mExperts = mUser.getSubLinks();
        SubLinkData element;

        int tempPoint = 0;

        int totalPoint;

        if (mExperts != null) {
            for (int idx = 0; mExperts.size() > idx; idx++) {    // sublinks 배열 갯수
                element = mExperts.get(idx);
                String name;
                if (element.getName().toString() != null) {
                    name = element.getName().toString();

                    if (name.equals(expert)) {
                        int expertPoint = 0;
                        if (element.getPoint().toString() != null) {
                            String expertPointStr = element.getPoint().toString();   // point 가져온다
                            if (expertPointStr.equals("")) {
                                expertPointStr = "0";
                            }
                            expertPoint = Integer.parseInt(expertPointStr);          // to Int
                        }

                        if (expertPoint > tempPoint) {                               // 임시로 가장 큰 놈을 넣는다
                            tempPoint = expertPoint;
                        }
                    }
                }
            }

        }

        totalPoint = tempPoint + 200;                               // 결과적으로 가장 큰 값에 가산점을 더함

        Log.d("Paul", "auth :: " + mUser);

        return totalPoint;
    }

    @Override
    protected void request() {
        if (mClicked == true) {
            Toast.makeText(mActivity, "이미 요청했습니다.", Toast.LENGTH_SHORT).show();
            return;
        }
        try {

            JSONObject params = mChannel.getAddressJSONObject();
            params.put("parent", mChannel.getId());
            params.put("level", mChannel.getLevel());
            params.put("name", mName.getText().toString());
            params.put("type", TYPE_POST);

            String positionType = mChannel.getType();
            String keyword = "";
            if (positionType != null && positionType.equals(TYPE_INFO_CENTER)) {                                 // Info_center  TYPE_INFO_CENTER
                if (mExpertsArr != null && mExpertsArr.contains(INTEREST_ADMINISTRATION)) { // 행정전문가
                    int expertPoint = getBiggestPoint(INTEREST_ADMINISTRATION);
                    params.put("sub_type", TYPE_EXPERT);
                    params.put("sub_name", INTEREST_ADMINISTRATION);
                    params.put("sub_point", expertPoint);
                    sendPointMessage = String.valueOf(expertPoint);
                } else {                                                               // 일반시민 // 돈있냐
                    int expertPoint = 200;
                    if (mExpertsArr != null && mExpertsArr.contains(INTEREST_ADMINISTRATION)) {
                        expertPoint = getBiggestPoint(INTEREST_ADMINISTRATION);
                    }
                    params.put("sub_type", TYPE_EXPERT);
                    params.put("sub_name", INTEREST_ADMINISTRATION);
                    params.put("sub_point", expertPoint);
                    sendPointMessage = String.valueOf(expertPoint);
                }
            } else {                                                                                                             // 일반 장소
                if (getArguments().getString("keyword") != null && getArguments().getString("keyword").length() != 0) {  //키워드 장소
                    keyword = getArguments().getString("keyword");
                    int expertPoint = getBiggestPoint(keyword);
                    if (mExpertsArr.size() > 0) {                                  // 내키워드와 장소 키워드 일치 : 전문가
                        if (mExpertsArr.contains(keyword)) {       // 내 전문분야에 이 키워드가 있으면 update
                            params.put("sub_name", keyword);
                            params.put("sub_type", TYPE_EXPERT);
                            params.put("sub_point", expertPoint);
                            sendPointMessage = String.valueOf(expertPoint);
                        } else {                                    // mExpertsArr != null
                            params.put("sub_name", keyword);
                            params.put("sub_type", TYPE_EXPERT);
                            params.put("sub_point", 200);
                            sendPointMessage = String.valueOf(expertPoint);
                        }
                    } else {                                       // 처음 방문하는 키워드 장소 : 전문가로 될 수 있음 포인트
                        params.put("sub_name", keyword);
                        params.put("sub_type", TYPE_EXPERT);
                        params.put("sub_point", 200);
                        sendPointMessage = String.valueOf(expertPoint);
                    }
                } else {                                                            // 이름없는 장소 : 그냥 글쓰면 올라감 포인트 없음
//                    params.put("sub_name", keyword);
//                    params.put("sub_type", TYPE_EXPERT);
//                    params.put("sub_point", "200");
                    sendPointMessage = "0";
                }
            }
//            ***************** ******************************************************************** end of division

            String[] keywords = mChannel.getKeywords();
            if (keywords != null && keywords.length > 0) {
                ArrayList<String> keywordArray = new ArrayList<>();
                for (int idx = 0; idx < keywords.length; idx++) {
                    keywordArray.add(keywords[idx]);
                }
                params.put("keywords", new JSONArray(keywordArray));
            }

            params.put("push", isPushChecked);

            if (mPhotoUri != null) {
                ArrayList<String> photos = new ArrayList<>();
                photos.add(mPhotoUri);
                params.put("photos", new JSONArray(photos));
                mPhotoUri = null;
            }

            JSONObject descParams = new JSONObject();
            if (isPreview) {
                descParams.put("metaTitle", mMetaTitle.getText().toString());
                descParams.put("metaDesc", Helper.getShortenString(mMetaDesc.getText().toString(), 50));
                descParams.put("metaPhoto", mMetaPhotoUrl);
            }

            if (hasVote) {
                JSONObject voteParams = new JSONObject();
                JSONArray options = new JSONArray();
                for (int idx = 0; idx < mVoteOptionPanel.getChildCount(); idx++) {
                    TextView text = (TextView) mVoteOptionPanel.getChildAt(idx);

                    JSONObject voteOptionParams = new JSONObject();
                    voteOptionParams.put("name", text.getText().toString());
                    voteOptionParams.put("count", 0);
                    voteOptionParams.put("voters", null);

                    options.put(voteOptionParams);
                }

                voteParams.put("type", TYPE_POST_SURVEY);
                voteParams.put("options", (Object) options);

                descParams.put("vote", voteParams);
            }

            params.put("desc", descParams);
            mApi.call(api_channels_create, params);

                Toast.makeText(mActivity, keyword + " 전문가 점수가 '" + sendPointMessage + "'이 되셨습니다.", Toast.LENGTH_LONG).show();

            mClicked = true;

        } catch (JSONException e) {
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
                mClicked = false;
                break;
            case DATA_EXPERT:
                mExperts = event.arrayData;
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
