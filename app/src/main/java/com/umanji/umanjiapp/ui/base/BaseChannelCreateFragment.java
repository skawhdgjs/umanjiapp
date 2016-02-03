package com.umanji.umanjiapp.ui.base;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;

import com.umanji.umanjiapp.R;
import com.umanji.umanjiapp.helper.FileHelper;
import com.umanji.umanjiapp.helper.UiHelper;
import com.umanji.umanjiapp.model.ChannelData;
import com.umanji.umanjiapp.model.SuccessData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;


public class BaseChannelCreateFragment extends BaseFragment {
    private static final String TAG = "BaseChannelCreateFragment";

    /****************************************************
     *  View
     ****************************************************/

    protected AutoCompleteTextView mName;
    protected Button mPhotoBtn;
    protected Button mCreateBtn;
    protected ImageView mPhoto;

    /****************************************************
     *  Etc.
     ****************************************************/
    protected String mPhotoUri;
    protected File mResizedFile;

    protected String mCreateApiName;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = getView(inflater, container);

        mName = (AutoCompleteTextView) view.findViewById(R.id.name);
        mPhoto = (ImageView) view.findViewById(R.id.photo);

        mCreateBtn = (Button) view.findViewById(R.id.createBtn);
        mCreateBtn.setOnClickListener(this);

        mPhotoBtn = (Button) view.findViewById(R.id.photoBtn);
        mPhotoBtn.setOnClickListener(this);

        super.onCreateView(view);
        return view;
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
            case R.id.createBtn:
                create();
                break;
            case R.id.photoBtn:
                UiHelper.callGallery(this);
                break;
        }
    }

    @Override
    public void onEvent(SuccessData event) {
        if(mCreateApiName != null && mCreateApiName.equals(event.type)) {
            mActivity.finish();
            return;
        }

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

        switch (requestCode) {
            case UiHelper.CODE_GALLERY_ACTIVITY:

                File file = FileHelper.getFileFromUri(mContext, intent.getData());
                mResizedFile = UiHelper.imageUploadAndDisplay(mActivity, mApiHelper, file, mResizedFile, mPhoto, false);
                break;
        }

    }


    /****************************************************
     *  Methods
     ****************************************************/

    protected void create() {
        final String fName = mName.getText().toString();

        try {

            JSONObject params = mChannel.getAddressJSONObject();
            params.put("parent", mChannel.getId());
            params.put("type", mType);
            params.put("name", fName);


            if(mPhotoUri != null) {
                ArrayList<String> photos = new ArrayList<>();
                photos.add(mPhotoUri);
                params.put("photos", new JSONArray(photos));
                mPhotoUri = null;
            }

            mApiHelper.call(mCreateApiName, params);

        }catch(JSONException e) {
            Log.e("BaseChannelCreate", "error " + e.toString());
        }
    }


}
