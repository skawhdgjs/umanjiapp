package com.umanji.umanjiapp.ui.mainHome.localCommunity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;


public class StepTwoFragment extends BaseChannelUpdateFragment {
    private static final String TAG = "StepTwoFragment";

    protected ChannelData mChannel;

    protected AutoCompleteTextView mName;

    private ImageView mGoBackBtn;
    private TextView mSubmitBtn2;
    private TextView mConsole;

    protected String mTabType = "";

    protected ImageView mPhotoBtn;
    protected ImageView mGallaryBtn;

    protected AutoCompleteTextView mKeywordName;
    protected Button mAddKeywordBtn;

    protected TextView mKeyword1;
    protected TextView mKeyword2;

    ArrayList<String> mKeywords = new ArrayList<>();

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

        String channelType = getArguments().getString("whichAction");

        if (channelType == null){
            mPhotoBtn = (ImageView) view.findViewById(R.id.photoBtn);
            mPhotoBtn.setOnClickListener(this);
            mGallaryBtn = (ImageView) view.findViewById(R.id.gallaryBtn);
            mGallaryBtn.setOnClickListener(this);
        }

        updateView();

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
//            setChannelParams(params);
            params.put("parent", mChannel.getId());
            params.put("parentType", mChannel.getType());
            params.put("level", mChannel.getLevel());
            params.put("name", mName.getText().toString());
            params.put("type", TYPE_COMMUNITY);


            if(mKeywords.size() > 0) {
                params.put("keywords", new JSONArray(mKeywords));
            }

            mApi.call(api_channels_createCommunity, params);

        }catch(JSONException e) {
            Log.e("BaseChannelCreate", "error " + e.toString());
        }

    }

    protected void setChannelParams(JSONObject params) throws JSONException {
        mChannel.setAddressJSONObject(params);
        params.put("id", mChannel.getId());
        params.put("name", mName.getText().toString());

        if(mPhotoUri != null) {
            ArrayList<String> photos = new ArrayList<>();
            photos.add(mPhotoUri);
            params.put("photos", new JSONArray(photos));
            mPhotoUri = null;
        }
    }

    private AlphaAnimation buttonClick = new AlphaAnimation(0F, 1F);

    @Override
    public void onClick(View v) {
//        super.onClick(v);

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

    @Override
    public void onEvent(SuccessData event) {
        super.onEvent(event);

//        ChannelData channelData = new ChannelData(event.response);

        switch (event.type) {
            case api_channels_createCommunity:
                mActivity.finish();
                break;

            case api_photo:
                mProgress.hide();

                try{
                    if(mResizedFile != null) {
                        mResizedFile.delete();
                        mResizedFile = null;
                    }
                    if(!TextUtils.isEmpty(mPhotoUri)) {
                        mPhotoUri = null;
                    }

                    JSONObject data = event.response.getJSONObject("data");
                    mPhotoUri = REST_S3_URL + data.optString("photo");

                    // Galaxy S4 에서만 나타나는 현상 임시처리.
                    if(mPhoto.getTag() == null) {
                        Glide.with(mActivity)
                                .load(mPhotoUri)
                                .into(mPhoto);
                    }
                }catch(JSONException e) {
                    Log.e("BaseChannelCreate", "error " + e.toString());
                }
                break;

        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if(resultCode == 0) return;

        File file = null;
        switch (requestCode) {
            case CODE_CAMERA_ACTIVITY:
                mProgress.show();

                mFilePath = FileHelper.getString(mActivity, "tmpFilePath");
                if(!TextUtils.isEmpty(mFilePath)) {
                    file = new File(mFilePath);
                    mResizedFile = Helper.imageUploadAndDisplay(mActivity, mApi, file, mResizedFile, mPhoto, false);
                    FileHelper.setString(mActivity, "tmpFilePath", null);
                } else {
                    Toast.makeText(mActivity, "사진정보를 가져오지 못했습니다. 다시한번 시도해 주세요.", Toast.LENGTH_SHORT).show();
                }
                break;
            case CODE_GALLERY_ACTIVITY:
                mProgress.show();

                file = FileHelper.getFileFromUri(mActivity, intent.getData());
                mResizedFile = Helper.imageUploadAndDisplay(mActivity, mApi, file, mResizedFile, mPhoto, false);
                break;
        }

    }




}
