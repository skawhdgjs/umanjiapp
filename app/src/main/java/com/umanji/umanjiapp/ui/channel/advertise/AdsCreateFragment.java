package com.umanji.umanjiapp.ui.channel.advertise;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RadioGroup;

import com.umanji.umanjiapp.R;
import com.umanji.umanjiapp.model.SuccessData;
import com.umanji.umanjiapp.ui.channel.BaseChannelCreateFragment;
import com.umanji.umanjiapp.ui.modal.calendar.AdsCalendarActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class AdsCreateFragment extends BaseChannelCreateFragment {
    private static final String TAG = "AdsCreateFragment";


    protected EditText mStartDay;
    protected EditText mEndDay;
    protected RadioGroup mRadio;
    protected int mAdLevel = 18;


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

        mStartDay = (EditText) view.findViewById(R.id.startDay);
        mEndDay = (EditText) view.findViewById(R.id.endDay);
        mStartDay.setOnClickListener(this);

        mRadio = (RadioGroup) view.findViewById(R.id.radioGroup);
        mRadio.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // checkedId is the RadioButton selected

                switch (checkedId) {
                    case R.id.radioButton1:
                        mAdLevel = 18;
                        break;
                    case R.id.radioButton2:
                        mAdLevel = 15;
                        break;
                    case R.id.radioButton3:
                        mAdLevel = 12;
                        break;
                    case R.id.radioButton4:
                        mAdLevel = 8;
                        break;
                }
            }
        });
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
            // params.put("level", mChannel.getLevel());
            params.put("level", mAdLevel);
            params.put("name", mName.getText().toString());
            params.put("startDay", mStartDay.getText().toString());
            params.put("endDay", mEndDay.getText().toString());
            params.put("type", TYPE_ADS);
            params.put("userName", mChannel.getUserName());


            if (mPhotoUri != null) {
                ArrayList<String> photos = new ArrayList<>();
                photos.add(mPhotoUri);
                params.put("photos", new JSONArray(photos));
                mPhotoUri = null;
            }

            mApi.call(api_channels_create, params);

        } catch (JSONException e) {
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
            case R.id.startDay:
                Intent i = new Intent(mActivity, AdsCalendarActivity.class);
                startActivity(i);


                break;

        }
    }


}
