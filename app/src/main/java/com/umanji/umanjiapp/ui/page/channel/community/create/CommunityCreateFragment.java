package com.umanji.umanjiapp.ui.page.channel.community.create;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.umanji.umanjiapp.model.ChannelData;
import com.umanji.umanjiapp.model.SuccessData;
import com.umanji.umanjiapp.ui.base.BaseChannelCreateFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class CommunityCreateFragment extends BaseChannelCreateFragment {
    private static final String TAG = "CommunityCreateFragment";

    public static CommunityCreateFragment newInstance(Bundle bundle) {
        CommunityCreateFragment fragment = new CommunityCreateFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mCreateApiName = api_links_createCommunity;
        mType = TYPE_COMMUNITY;
    }
}
