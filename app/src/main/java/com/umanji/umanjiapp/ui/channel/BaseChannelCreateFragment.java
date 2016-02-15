package com.umanji.umanjiapp.ui.channel;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
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
import com.umanji.umanjiapp.helper.FileHelper;
import com.umanji.umanjiapp.helper.Helper;
import com.umanji.umanjiapp.helper.UiHelper;
import com.umanji.umanjiapp.model.ChannelData;
import com.umanji.umanjiapp.model.SuccessData;
import com.umanji.umanjiapp.ui.BaseFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;


public abstract class BaseChannelCreateFragment extends BaseFragment {
    private static final String TAG = "BaseChannelCreate";

    /****************************************************
     *  Intent
     ****************************************************/
    protected ChannelData mChannel;

    /****************************************************
     *  View
     ****************************************************/

    protected AutoCompleteTextView mName;
    protected Button mPhotoBtn;
    protected Button mGallaryBtn;

    protected Button mSubmitBtn;
    protected ImageView mPhoto;

    protected LinearLayout mMetaPanel;
    protected ImageView mMetaPhoto;
    protected TextView mMetaTitle;
    protected TextView mMetaDesc;
    protected boolean isPreview = false;

    /****************************************************
     *  Etc.
     ****************************************************/
    TextCrawler textCrawler;

    protected String mPhotoUri;
    protected String mMetaPhotoUrl;
    protected File mResizedFile;

    // 카메라 찍은 후 저장될 파일 경로
    protected String mFilePath;


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
        mName = (AutoCompleteTextView) view.findViewById(R.id.name);
        mName.setOnKeyListener(new View.OnKeyListener() {

            @Override
            public boolean onKey(View arg0, int arg1, KeyEvent arg2) {
                String name = mName.getText().toString().replace("\n", " ");
                ArrayList<String> urls = SearchUrls.matches(name);

                if (arg1 == 66 && urls.size() > 0) {

                    textCrawler
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

                                        if(sourceContent.getImages().size() > 0) {
                                            mMetaPhotoUrl = sourceContent.getImages().get(0);
                                            mMetaPhoto.setVisibility(View.VISIBLE);
                                            Glide.with(mActivity).load(mMetaPhotoUrl).into(mMetaPhoto);
                                        }else {
                                            mMetaPhoto.setVisibility(View.GONE);
                                        }

                                        if(TextUtils.isEmpty(sourceContent.getTitle())) {
                                            mMetaTitle.setVisibility(View.GONE);
                                        }else {
                                            mMetaTitle.setVisibility(View.VISIBLE);
                                            mMetaTitle.setText(sourceContent.getTitle());
                                        }

                                        if(TextUtils.isEmpty(sourceContent.getDescription())) {
                                            mMetaDesc.setVisibility(View.GONE);
                                        }else {
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

        mPhoto = (ImageView) view.findViewById(R.id.photo);

        mSubmitBtn = (Button) view.findViewById(R.id.submitBtn);
        mSubmitBtn.setOnClickListener(this);

        mPhotoBtn = (Button) view.findViewById(R.id.photoBtn);
        mPhotoBtn.setOnClickListener(this);

        mGallaryBtn = (Button) view.findViewById(R.id.gallaryBtn);
        mGallaryBtn.setOnClickListener(this);

        mMetaPanel = (LinearLayout) view.findViewById(R.id.metaPanel);
        mMetaPhoto = (ImageView) view.findViewById(R.id.metaPhoto);
        mMetaTitle = (TextView) view.findViewById(R.id.metaTitle);
        mMetaDesc = (TextView) view.findViewById(R.id.metaDesc);

        textCrawler = new TextCrawler();
    }

    public View getView(LayoutInflater inflater, ViewGroup container) {
        View view = inflater.inflate(R.layout.activity_channel_create, container, false);
        return view;
    }

    @Override
    public void loadData() {

    }

    @Override
    public void updateView() {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.submitBtn:
                submit();
                break;
            case R.id.photoBtn:
                mFilePath = UiHelper.callCamera(this);
                break;
            case R.id.gallaryBtn:
                UiHelper.callGallery(this);
                break;
        }
    }

    @Override
    public void onEvent(SuccessData event) {
        switch (event.type) {
            case api_photo:

                try{
                    mResizedFile.delete();
                    mResizedFile = null;
                    mPhotoUri = null;

                    JSONObject data = event.response.getJSONObject("data");
                    mPhotoUri = REST_S3_URL + data.optString("photo");
                }catch(JSONException e) {
                    Log.e("BaseChannelCreate", "error " + e.toString());
                }
                break;
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        Log.d("BaseChannelCreate", "onActivityResult");

        File file = null;
        switch (requestCode) {
            case UiHelper.CODE_CAMERA_ACTIVITY:
                file = new File(mFilePath);
                mResizedFile = UiHelper.imageUploadAndDisplay(mActivity, mApi, file, mResizedFile, mPhoto, false);
                break;
            case UiHelper.CODE_GALLERY_ACTIVITY:
                file = FileHelper.getFileFromUri(mActivity, intent.getData());
                mResizedFile = UiHelper.imageUploadAndDisplay(mActivity, mApi, file, mResizedFile, mPhoto, false);
                break;
        }

    }


    /****************************************************
     *  Methods
     ****************************************************/

    protected void submit() {

        String name = mName.getText().toString().replace("\n", " ");
        ArrayList<String> urls = SearchUrls.matches(name);

        if (isPreview == false && urls.size() > 0) {
            textCrawler
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

                                if(sourceContent.getImages().size() > 0) {
                                    mMetaPhotoUrl = sourceContent.getImages().get(0);
                                    mMetaPhoto.setVisibility(View.VISIBLE);
                                    Glide.with(mActivity).load(mMetaPhotoUrl).into(mMetaPhoto);
                                }else {
                                    mMetaPhoto.setVisibility(View.GONE);
                                }

                                if(TextUtils.isEmpty(sourceContent.getTitle())) {
                                    mMetaTitle.setVisibility(View.GONE);
                                }else {
                                    mMetaTitle.setVisibility(View.VISIBLE);
                                    mMetaTitle.setText(sourceContent.getTitle());
                                }

                                if(TextUtils.isEmpty(sourceContent.getDescription())) {
                                    mMetaDesc.setVisibility(View.GONE);
                                }else {
                                    mMetaDesc.setVisibility(View.VISIBLE);
                                    mMetaDesc.setText(sourceContent.getDescription());
                                }
                            }

                            mProgress.hide();
                            request();
                        }
                    }, urls.get(0));
        } else {
            request();
        }

    }

    protected abstract void request();
}
