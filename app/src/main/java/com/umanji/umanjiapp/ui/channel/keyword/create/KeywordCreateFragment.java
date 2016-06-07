package com.umanji.umanjiapp.ui.channel.keyword.create;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.umanji.umanjiapp.R;
import com.umanji.umanjiapp.model.SubLinkData;
import com.umanji.umanjiapp.model.SuccessData;
import com.umanji.umanjiapp.ui.channel.BaseChannelCreateFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class KeywordCreateFragment extends BaseChannelCreateFragment {
    private static final String TAG = "KeywordCreateFragment";

    public static KeywordCreateFragment newInstance(Bundle bundle) {
        KeywordCreateFragment fragment = new KeywordCreateFragment();
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

        mHeaderTitle = (TextView) view.findViewById(R.id.headerTitle);
        mHeaderTitle.setText("키워드 설정");

    }

    @Override
    public View getView(LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.activity_channel_create, container, false);
    }

    @Override
    protected void request() {

        if(mClicked == true){
            Toast.makeText(mActivity,"이미 요청했습니다.", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            ArrayList<SubLinkData> subLinks = mChannel.getSubLinks(TYPE_KEYWORD);
            if(subLinks != null) {
                for(int idx=0; idx < subLinks.size(); idx++) {
                    if(TextUtils.equals(mName.getText().toString(), subLinks.get(idx).getName())) {
                        Toast.makeText(mActivity, "이미 존재하는 키워드 입니다.", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
            }

            JSONObject params = mChannel.getAddressJSONObject();
            params.put("id", mChannel.getId());
            params.put("name", mName.getText().toString());
            params.put("type", TYPE_KEYWORD);

            if(mPhotoUri != null) {
                ArrayList<String> photos = new ArrayList<>();
                photos.add(mPhotoUri);
                params.put("photos", new JSONArray(photos));
                mPhotoUri = null;
            }

            mApi.call(api_channels_createKeyword, params);
            mClicked = true;

        }catch(JSONException e) {
            Log.e("BaseChannelCreate", "error " + e.toString());
        }
    }

    @Override
    public void onEvent(SuccessData event) {
        super.onEvent(event);

        switch (event.type) {
            case api_channels_createKeyword:
                mClicked = false;
                mActivity.finish();
                break;
        }
    }


}
