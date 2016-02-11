package com.umanji.umanjiapp.ui.fragment.noties;

import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.umanji.umanjiapp.model.ChannelData;
import com.umanji.umanjiapp.model.NotyData;
import com.umanji.umanjiapp.model.SuccessData;
import com.umanji.umanjiapp.ui.base.BaseChannelListAdapter;
import com.umanji.umanjiapp.ui.base.BaseChannelListFragment;
import com.umanji.umanjiapp.ui.fragment.community.CommunityListAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class NotyListFragment extends BaseChannelListFragment {
    private static final String TAG = "NotyListFragment";

    public static NotyListFragment newInstance(Bundle bundle) {
        NotyListFragment fragment = new NotyListFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mListApiName = api_noites_find;
        mType   = TYPE_NOTY;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        RecyclerView rView = getRView(inflater, container);
        super.onCreateView(rView);

        return rView;
    }

    @Override
    public BaseChannelListAdapter getListAdapter() {
        return new NotyListAdapter(getActivity(), this, mChannel);
    }


    @Override
    public void loadData() {
        try {
            JSONObject params = new JSONObject();
            params.put("page", mAdapter.getCurrentPage());
            mApiHelper.call(api_noites_find, params);
        } catch(JSONException e) {
            Log.e(TAG, "error " + e.toString());
        }
    }

    @Override
    public void onEvent(SuccessData event) {
        if (event.response == null) return;

        switch (event.type) {
            case api_noites_find:
                try {
                    boolean hasNewNoty = false;
                    JSONArray jsonArray = event.response.getJSONArray("data");
                    for(int idx = 0; idx < jsonArray.length(); idx++) {
                        JSONObject jsonDoc = jsonArray.getJSONObject(idx);
                        NotyData notyData = new NotyData(jsonDoc);

                        if(!hasNewNoty) {
                            hasNewNoty = !notyData.isRead();
                        }
                        ((NotyListAdapter)mAdapter).addBottom(notyData);
                    }

                    if(hasNewNoty) {
                        mApiHelper.call(api_noites_read);
                    }

                    updateView();

                } catch(JSONException e) {
                    Log.e(TAG, "error " + e.toString());
                }
                break;
        }
    }
}
