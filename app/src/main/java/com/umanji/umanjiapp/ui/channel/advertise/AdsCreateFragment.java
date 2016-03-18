package com.umanji.umanjiapp.ui.channel.advertise;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.umanji.umanjiapp.R;
import com.umanji.umanjiapp.model.SuccessData;
import com.umanji.umanjiapp.ui.channel.BaseChannelCreateFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class AdsCreateFragment extends BaseChannelCreateFragment {
    private static final String TAG = "AdsCreateFragment";


    protected EditText mStartDay;
    protected EditText mEndDay;

    public static AdsCreateFragment newInstance(Bundle bundle) {
        AdsCreateFragment fragment = new AdsCreateFragment();
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

        mStartDay   = (EditText) view.findViewById(R.id.startDay);
        mEndDay     = (EditText) view.findViewById(R.id.endDay);

    }

    @Override
    public View getView(LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.activity_ads_create, container, false);

    }


    @Override
    protected void submit() {

        request();

    }

    @Override
    protected void request() {
        try {
            JSONObject params = mChannel.getAddressJSONObject();
            params.put("parent", mChannel.getId());
            params.put("level", mChannel.getLevel());
            params.put("name", mName.getText().toString());
            params.put("startDay", mStartDay.getText().toString());
            params.put("endDay", mEndDay.getText().toString());
            params.put("type", TYPE_ADS);


            if(mPhotoUri != null) {
                ArrayList<String> photos = new ArrayList<>();
                photos.add(mPhotoUri);
                params.put("photos", new JSONArray(photos));
                mPhotoUri = null;
            }

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
                mActivity.finish();
                break;
        }
    }



    @Override
    public void onClick(View v) {
        super.onClick(v);

        switch (v.getId()) {

        }
    }



}
