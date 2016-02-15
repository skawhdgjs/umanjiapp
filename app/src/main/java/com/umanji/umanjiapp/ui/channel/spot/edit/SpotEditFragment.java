package com.umanji.umanjiapp.ui.channel.spot.edit;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.umanji.umanjiapp.R;
import com.umanji.umanjiapp.helper.Helper;
import com.umanji.umanjiapp.model.SuccessData;
import com.umanji.umanjiapp.ui.channel.BaseChannelUpdateFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class SpotEditFragment extends BaseChannelUpdateFragment {
    private static final String TAG = "SpotEditFragment";

    public static SpotEditFragment newInstance(Bundle bundle) {
        SpotEditFragment fragment = new SpotEditFragment();
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
    public View getView(LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.activity_spot_update, container, false);
    }

    @Override
    protected void request() {
        final String fName = mName.getText().toString();
        String floor = mFloorSpinner.getSelectedItem().toString();
        final int fFloor = Integer.parseInt(floor.substring(0, floor.indexOf("F") - 1));

        try {

            JSONObject params = mChannel.getAddressJSONObject();
            params.put("id", mChannel.getId());
            params.put("name", fName);

            JSONObject descParams = new JSONObject();
            descParams.put("floor", fFloor);

            params.put("desc", descParams);

            if(mPhotoUri != null) {
                ArrayList<String> photos = new ArrayList<>();
                photos.add(mPhotoUri);
                params.put("photos", new JSONArray(photos));
                mPhotoUri = null;
            }

            mApi.call(api_channels_id_update, params);

        }catch(JSONException e) {
            Log.e("BaseChannelCreate", "error " + e.toString());
        }
    }

    @Override
    public void updateView() {
        super.updateView();

        setName(mActivity, mChannel);
        setAddress(mActivity, mChannel);
    }

    @Override
    public void onEvent(SuccessData event) {
        super.onEvent(event);

        switch (event.type) {
            case api_channels_id_update:
                mActivity.finish();
                break;
        }
    }


}
