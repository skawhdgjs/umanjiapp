package com.umanji.umanjiapp.ui.page.channel.spot.home;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.umanji.umanjiapp.R;
import com.umanji.umanjiapp.helper.AuthHelper;
import com.umanji.umanjiapp.helper.UiHelper;
import com.umanji.umanjiapp.model.ChannelData;
import com.umanji.umanjiapp.model.ErrorData;
import com.umanji.umanjiapp.model.SuccessData;
import com.umanji.umanjiapp.ui.base.BaseChannelListAdapter;
import com.umanji.umanjiapp.ui.base.BaseChannelListFragment;
import com.umanji.umanjiapp.ui.fragment.spots.SpotListAdapter;
import com.umanji.umanjiapp.ui.page.channel.spot.create.SpotCreateActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import de.greenrobot.event.EventBus;

public class HomeListFragment extends BaseChannelListFragment {
    private static final String TAG = "HomeListFragment";

    public static HomeListFragment newInstance(Bundle bundle) {
        HomeListFragment fragment = new HomeListFragment();
        fragment.setArguments(bundle);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mListApiName = api_channels_spots_find;
        mType   = TYPE_SPOT_INNER;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        LinearLayout view = (LinearLayout) inflater.inflate(
                R.layout.activity_spot_home, container, false);

        RecyclerView rView = (RecyclerView)view.findViewById(R.id.recyclerView);
        super.onCreateView(rView);

        return view;
    }

    @Override
    public void updateView() {
        super.updateView();
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void loadMoreData() {
        try {
            JSONObject params = new JSONObject();
            params.put("page", mAdapter.getCurrentPage());

            if(mType.equals(TYPE_SPOT_INNER)) {
                params.put("type", TYPE_SPOTS);
            } else if(mType.equals(TYPE_COMMUNITY)) {
                params.put("type", mType);
                params.put("level", LEVEL_LOCAL);
            } else {
                params.put("type", mType);
            }
            params.put("owner", mChannel.getId());
            params.put("sort", "point DESC");

            mApiHelper.call(mListApiName, params, new AjaxCallback<JSONObject>() {
                @Override
                public void callback(String url, JSONObject json, AjaxStatus status) {
                    if (status.getCode() == 500) {
                        EventBus.getDefault().post(new ErrorData(TYPE_ERROR_AUTH, TYPE_ERROR_AUTH));
                    } else {
                        try {
                            isLoading = true;
                            JSONArray jsonArray = json.getJSONArray("data");
                            for(int idx = 0; idx < jsonArray.length(); idx++) {
                                JSONObject jsonDoc = jsonArray.getJSONObject(idx);
                                ChannelData doc = new ChannelData(jsonDoc);

                                mAdapter.addBottom(doc);
                            }
                            updateView();
                        } catch(JSONException e) {
                            Log.e(TAG, "error " + e.toString());
                        }
                    }
                }
            });

        } catch(JSONException e) {
            Log.e(TAG, "error " + e.toString());
        }
    }

    @Override
    public BaseChannelListAdapter getListAdapter() {
        return new HomeListAdapter(getActivity(), this);
    }

    @Override
    public void onEvent(SuccessData event) {
        if (event.response == null) return;

        switch (event.type) {
            case api_channels_id_update:
                mActivity.finish();
                break;
        }
    }
}
