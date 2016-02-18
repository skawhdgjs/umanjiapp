package com.umanji.umanjiapp.ui.channel;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.leocardz.link.preview.library.TextCrawler;
import com.umanji.umanjiapp.R;
import com.umanji.umanjiapp.helper.FileHelper;
import com.umanji.umanjiapp.helper.Helper;
import com.umanji.umanjiapp.model.ChannelData;
import com.umanji.umanjiapp.model.SuccessData;
import com.umanji.umanjiapp.ui.BaseFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;


public abstract class BaseChannelUpdateFragment extends BaseFragment {
    private static final String TAG = "BaseChannelUpdateFragment";

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

    protected Spinner mFloorSpinner;
    protected TextView mAddress;
    protected Button mChangeAddressBtn;
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
        mPhoto = (ImageView) view.findViewById(R.id.photo);

        mSubmitBtn = (Button) view.findViewById(R.id.submitBtn);
        mSubmitBtn.setOnClickListener(this);

        mPhotoBtn = (Button) view.findViewById(R.id.photoBtn);
        mPhotoBtn.setOnClickListener(this);

        mGallaryBtn = (Button) view.findViewById(R.id.gallaryBtn);
        mGallaryBtn.setOnClickListener(this);

        mAddress = (TextView) view.findViewById(R.id.address);
        mChangeAddressBtn = (Button) view.findViewById(R.id.changeAddressBtn);
        mChangeAddressBtn.setOnClickListener(this);

        mFloorSpinner = (Spinner) view.findViewById(R.id.floorSpinner);

        String[] floorList = mActivity.getResources().getStringArray(R.array.floorList);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(mActivity,
                R.layout.widget_spinner_item, floorList);

        mFloorSpinner.setAdapter(adapter);
    }


    public View getView(LayoutInflater inflater, ViewGroup container) {
        View view = inflater.inflate(R.layout.activity_channel_update, container, false);
        return view;
    }

    @Override
    public void loadData() {

    }

    @Override
    public void updateView() {

    }

    protected void setName(Activity activity, ChannelData channelData) {
        mName.setText(mChannel.getName());
    }

    protected void setAddress(Activity activity, ChannelData channelData) {
        mAddress.setText(Helper.getFullAddress(mChannel));
    }

    protected void setPhoto(Activity activity, ChannelData channelData) {
        String photoUrl = channelData.getPhoto();
        if(photoUrl != null) {
            Glide.with(activity)
                    .load(photoUrl)
                    .into(mPhoto);
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.submitBtn:
                submit();
                break;
            case R.id.photoBtn:
                mFilePath = Helper.callCamera(this);
                break;
            case R.id.gallaryBtn:
                Helper.callGallery(this);
                break;
        }
    }

    @Override
    public void onEvent(SuccessData event) {
        switch (event.type) {
            case api_photo:
                mProgress.hide();

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
        if(resultCode == 0) return;

        File file = null;
        switch (requestCode) {
            case CODE_CAMERA_ACTIVITY:
                mProgress.show();
                file = new File(mFilePath);
                mResizedFile = Helper.imageUploadAndDisplay(mActivity, mApi, file, mResizedFile, mPhoto, false);
                break;
            case CODE_GALLERY_ACTIVITY:
                mProgress.show();
                file = FileHelper.getFileFromUri(mActivity, intent.getData());
                mResizedFile = Helper.imageUploadAndDisplay(mActivity, mApi, file, mResizedFile, mPhoto, false);
                break;
        }

    }


    /****************************************************
     *  Methods
     ****************************************************/

    protected void submit() {
        request();
    }

    protected abstract void request();



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
}
