package com.umanji.umanjiapp.ui.channel.post.reply;

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
import com.umanji.umanjiapp.model.SuccessData;
import com.umanji.umanjiapp.ui.channel.BaseChannelCreateFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class ReplyFragment extends BaseChannelCreateFragment {
    private static final String TAG = "ReplyFragment";

    private AutoCompleteTextView mFloor;
    private CheckBox mBasementCheckBox;
    private boolean isBasement = false;

    public static ReplyFragment newInstance(Bundle bundle) {
        ReplyFragment fragment = new ReplyFragment();
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
        updateView();
        return view;
    }

    @Override
    public View getView(LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.activity_spot_create, container, false);
    }

    @Override
    public void initWidgets(View view) {
        super.initWidgets(view);

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

        mSubmitBtn.setText("스팟 생성");
    }

    @Override
    protected void request() {
        try {
            JSONObject params = mChannel.getAddressJSONObject();
            params.put("parent", mChannel.getId());
            params.put("name", mName.getText().toString());
            params.put("type", TYPE_SPOT_INNER);

            setSpotDesc(params);

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
    public void onEvent(SuccessData event) {
        super.onEvent(event);

        switch (event.type) {
            case api_channels_create:
                mActivity.finish();
                break;
        }
    }


}
