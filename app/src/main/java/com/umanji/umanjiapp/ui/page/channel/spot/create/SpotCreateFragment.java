package com.umanji.umanjiapp.ui.page.channel.spot.create;

import android.os.Bundle;
import android.util.Log;

import com.umanji.umanjiapp.model.SuccessData;
import com.umanji.umanjiapp.ui.base.BaseChannelCreateFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class SpotCreateFragment extends BaseChannelCreateFragment {
    private static final String TAG = "SpotCreateFragment";

    public static SpotCreateFragment newInstance(Bundle bundle) {
        SpotCreateFragment fragment = new SpotCreateFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mCreateApiName = api_links_createSpot;
        mType = TYPE_SPOT_INNER;
    }
}
