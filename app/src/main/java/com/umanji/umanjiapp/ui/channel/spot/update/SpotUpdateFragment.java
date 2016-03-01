package com.umanji.umanjiapp.ui.channel.spot.update;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.umanji.umanjiapp.R;
import com.umanji.umanjiapp.model.ChannelData;
import com.umanji.umanjiapp.model.SuccessData;
import com.umanji.umanjiapp.ui.channel.BaseChannelUpdateFragment;

import org.json.JSONException;
import org.json.JSONObject;

import de.greenrobot.event.EventBus;


public class SpotUpdateFragment extends BaseChannelUpdateFragment {
    private static final String TAG = "SpotUpdateFragment";

    private AutoCompleteTextView mFloor;
    private CheckBox mBasementCheckBox;
    private boolean isBasement = false;

    public static SpotUpdateFragment newInstance(Bundle bundle) {
        SpotUpdateFragment fragment = new SpotUpdateFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);

        mFloor = (AutoCompleteTextView) view.findViewById(R.id.floor);
        mBasementCheckBox = (CheckBox) view.findViewById(R.id.basementCheckBox);
        mBasementCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {
                if (buttonView.getId() == R.id.basementCheckBox) {
                    if (isChecked) {
                        isBasement = true;
                    } else {
                        isBasement = false;
                    }
                }
            }
        });

        updateView();
        return view;
    }

    @Override
    public View getView(LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.activity_spot_update, container, false);
    }


    @Override
    protected void request() {
        try {
            JSONObject params = new JSONObject();
            setChannelParams(params);
            setSpotDesc(params);
            mApi.call(api_channels_id_update, params);

        }catch(JSONException e) {
            Log.e("BaseChannelUpdate", "error " + e.toString());
        }
    }

    protected void setSpotDesc(JSONObject params) throws JSONException {
        String floor = mFloor.getText().toString();
        if(TextUtils.isEmpty(mFloor.getText().toString())){
            floor = "1";
            //return;
        }

        int floorNum = Integer.parseInt(floor);

        if(isBasement) {
            floorNum = floorNum * -1;
        }


        JSONObject descParams = new JSONObject();
        descParams.put("floor", floorNum);
        params.put("desc", descParams);
    }

    @Override
    public void updateView() {
        super.updateView();

        setName(mActivity, mChannel);
        setAddress(mActivity, mChannel);
        setPhoto(mActivity, mChannel);
        setFloor(mActivity, mChannel);
    }

    protected void setFloor(Activity activity, final ChannelData channelData) {
        JSONObject descJson = channelData.getDesc();
        if(descJson != null) {
            int floor = descJson.optInt("floor");

            if(floor > 0) {
                mFloor.setText(String.valueOf(floor));
                mBasementCheckBox.setChecked(false);
                isBasement = false;
            }else {
                mFloor.setText(String.valueOf(floor * -1));
                mBasementCheckBox.setChecked(true);

                isBasement = true;
            }
        }
    }

    @Override
    public void onEvent(SuccessData event) {
        super.onEvent(event);

        switch (event.type) {
            case api_channels_id_update:
                mActivity.finish();
                EventBus.getDefault().post(new SuccessData(EVENT_UPDATEVIEW, null));
                break;
        }
    }


}
