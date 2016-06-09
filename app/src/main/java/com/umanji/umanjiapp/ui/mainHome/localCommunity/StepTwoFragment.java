package com.umanji.umanjiapp.ui.mainHome.localCommunity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.Image;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.umanji.umanjiapp.R;
import com.umanji.umanjiapp.helper.AuthHelper;
import com.umanji.umanjiapp.helper.FileHelper;
import com.umanji.umanjiapp.helper.Helper;
import com.umanji.umanjiapp.model.ChannelData;
import com.umanji.umanjiapp.model.SuccessData;
import com.umanji.umanjiapp.ui.BaseFragment;
import com.umanji.umanjiapp.ui.channel.BaseChannelCreateFragment;
import com.umanji.umanjiapp.ui.channel.BaseChannelUpdateFragment;
import com.umanji.umanjiapp.ui.channel.community.CommunityActivity;
import com.umanji.umanjiapp.ui.channel.complex.ComplexActivity;
import com.umanji.umanjiapp.ui.channel.spot.SpotActivity;
import com.umanji.umanjiapp.ui.main.MainActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;


public class StepTwoFragment extends BaseChannelCreateFragment {
    private static final String TAG = "StepTwoFragment";

    protected ChannelData mChannel;

    protected AutoCompleteTextView mName;

    private ImageView mGoBackBtn;
    private TextView mSubmitBtn2;
    private TextView mConsole;

    protected String mTabType = "";

    protected AutoCompleteTextView mKeywordName;
    protected Button mAddKeywordBtn;

    protected TextView mKeyword1;
    protected TextView mKeyword2;

    ArrayList<String> mKeywords = new ArrayList<>();

    protected boolean isReady = false;

    public static StepTwoFragment newInstance(Bundle bundle) {
        StepTwoFragment fragment = new StepTwoFragment();
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

            mTabType = getArguments().getString("tabType");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public View getView(LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.activity_step_two, container, false);
    }

    @Override
    public void initWidgets(View view) {
        super.initWidgets(view);

        mName = (AutoCompleteTextView) view.findViewById(R.id.name);

        mGoBackBtn = (ImageView) view.findViewById(R.id.goBackBtn);
        mGoBackBtn.setOnClickListener(this);

        mSubmitBtn2 = (TextView) view.findViewById(R.id.submitBtn2);
        mSubmitBtn2.setOnClickListener(this);

        mConsole = (TextView) view.findViewById(R.id.console);

        mKeywordName = (AutoCompleteTextView) view.findViewById(R.id.keywordName);
        mAddKeywordBtn = (Button) view.findViewById(R.id.addKeywordBtn);
        mAddKeywordBtn.setOnClickListener(this);

        mKeyword1 = (TextView) view.findViewById(R.id.keyword1);
        mKeyword1.setOnClickListener(this);

        mKeyword2 = (TextView) view.findViewById(R.id.keyword2);
        mKeyword2.setOnClickListener(this);

        mConsole.setText(mChannel.getType()+" :: this is console answer...");

        mName.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable arg0) {
                isReady = true;
                if (mName.getText().toString().length() == 0) {
                    mSubmitBtn2.setVisibility(View.GONE);
                } else {
                    mSubmitBtn2.setVisibility(View.VISIBLE);
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
    public void loadData() {

    }

    @Override
    public void updateView() {
        if(AuthHelper.isLogin(mActivity)) {
        }else {
        }

    }

    @Override
    protected void request() {
        try {
            JSONObject params = mChannel.getAddressJSONObject();
            params.put("parent", mChannel.getId());
//            params.put("parentType", mChannel.getParent().getType());
            params.put("level", 13);
            params.put("name", mName.getText().toString());
            params.put("type", TYPE_COMMUNITY);

            if(mKeywords.size() > 0) {
                params.put("keywords", new JSONArray(mKeywords));
            }

            if(mPhotoUri != null) {
                ArrayList<String> photos = new ArrayList<>();
                photos.add(mPhotoUri);
                params.put("photos", new JSONArray(photos));
                mPhotoUri = null;
            }

            mApi.call(api_channels_createCommunity, params);
            mActivity.finish();
            Helper.startActivity(mActivity, mChannel, TAB_COMMUNITIES);

        }catch(JSONException e) {
            Log.e("BaseChannelCreate", "error " + e.toString());
        }

    }

    private void startActivity(ChannelData channel) {

        Intent intent = null;
        intent = new Intent(getActivity(), CommunityActivity.class);

        Bundle bundle = new Bundle();
        bundle.putString("channel", channel.getJsonObject().toString());
        intent.putExtra("bundle", bundle);
        intent.putExtra("enterAnim", R.anim.zoom_out);
        intent.putExtra("exitAnim", R.anim.zoom_in);

        startActivity(intent);
    }

    private AlphaAnimation buttonClick = new AlphaAnimation(0F, 1F);

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.addKeywordBtn:
                if(TextUtils.isEmpty(mKeyword1.getText())) {
                    mKeyword1.setText(mKeywordName.getText() + " [X]");
                    mKeywords.add(mKeywordName.getText().toString());
                } else if(TextUtils.isEmpty(mKeyword2.getText())){
                    mKeyword2.setText(mKeywordName.getText() + " [X]");
                    mKeywords.add(mKeywordName.getText().toString());
                } else {
                    Toast.makeText(mActivity, "키워드는 2개까지 입력 가능합니다.", Toast.LENGTH_SHORT).show();
                }

                mKeywordName.setText(null);
                break;

            case R.id.keyword1:
                if(!TextUtils.isEmpty(mKeyword1.getText())) {
                    if(!TextUtils.isEmpty(mKeyword2.getText())) {
                        mKeyword1.setText(mKeyword2.getText());
                        mKeywords.remove(0);
                        mKeywords.remove(0);

                        mKeywords.add(mKeyword2.getText().toString());
                        mKeyword2.setText(null);
                    }else {
                        mKeyword1.setText(null);
                        mKeywords.remove(0);
                    }
                }
                break;
            case R.id.keyword2:
                if(!TextUtils.isEmpty(mKeyword2.getText())) {
                    mKeyword2.setText(null);
                    mKeywords.remove(1);
                }
                break;

            case R.id.goBackBtn:
                mGoBackBtn.startAnimation(buttonClick);
                buttonClick.setDuration(500);

                Intent mInt = new Intent(mActivity, StepOneActivity.class);
                startActivity(mInt);
                mActivity.finish();
                break;

            case R.id.photoBtn:
                mFilePath = Helper.callCamera(this);
                FileHelper.setString(mActivity, "tmpFilePath", mFilePath);
                break;
            case R.id.gallaryBtn:
                Helper.callGallery(this);
                break;

            case R.id.submitBtn2:
                mSubmitBtn2.startAnimation(buttonClick);
                buttonClick.setDuration(500);

                request();
                Toast.makeText(mActivity, "submit", Toast.LENGTH_SHORT).show();
                break;


        }
    }

}
