package com.umanji.umanjiapp.ui.setting.home;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.umanji.umanjiapp.R;
import com.umanji.umanjiapp.model.ChannelData;
import com.umanji.umanjiapp.model.ErrorData;
import com.umanji.umanjiapp.model.SuccessData;
import com.umanji.umanjiapp.ui.channel._fragment.BaseChannelListAdapter;
import com.umanji.umanjiapp.ui.channel._fragment.BaseChannelListFragment;
import com.umanji.umanjiapp.ui.channel._fragment.spots.SpotListAdapter;
import com.umanji.umanjiapp.ui.channel.spot.create.SpotCreateActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import de.greenrobot.event.EventBus;

public class HomeFragment extends BaseChannelListFragment {
    private static final String TAG = "PostListFragment";

    public static HomeFragment newInstance(Bundle bundle) {
        HomeFragment fragment = new HomeFragment();
        fragment.setArguments(bundle);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public View getView(LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.activity_spot_home, container, false);
    }

    @Override
    public BaseChannelListAdapter getListAdapter() {
        return new HomeAdapter(mActivity, this, mChannel);
    }

    @Override
    public void initWidgets(View view) {
    }

    @Override
    public void loadData() {
        mAdapter.resetDocs();
        mAdapter.setCurrentPage(0);

        loadMoreData();
    }

    @Override
    public void loadMoreData() {
        isLoading = true;

        try {
            JSONObject params = new JSONObject();
            params.put("page", mAdapter.getCurrentPage()); // for paging
            params.put("type", TYPE_SPOTS);
            params.put("owner", mChannel.getId());

            mApi.call(api_channels_spots_find, params, new AjaxCallback<JSONObject>() {
                @Override
                public void callback(String url, JSONObject object, AjaxStatus status) {
                    if(status.getCode() == 500) {
                        EventBus.getDefault().post(new ErrorData(TYPE_ERROR_AUTH, TYPE_ERROR_AUTH));
                    }else {
                        try {
                            JSONArray jsonArray = object.getJSONArray("data");
                            for(int idx = 0; idx < jsonArray.length(); idx++) {
                                JSONObject jsonDoc = jsonArray.getJSONObject(idx);
                                ChannelData doc = new ChannelData(jsonDoc);
                                mAdapter.addBottom(doc);
                            }

                            updateView();
                        } catch (JSONException e) {
                            Log.e(TAG, "Error " + e.toString());
                        }

                        isLoading = false;
                    }
                }
            });

            mAdapter.setCurrentPage(mAdapter.getCurrentPage() + 1);
        } catch(JSONException e) {
            Log.e(TAG, "error " + e.toString());
        }

    }

    @Override
    public void updateView() {
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onEvent(SuccessData event) {
        super.onEvent(event);
        if (event.response == null) return;
        switch (event.type) {
            case api_channels_id_update:
                mActivity.finish();
                break;
        }
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
    }
}
