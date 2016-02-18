package com.umanji.umanjiapp.ui.channel._fragment.communities;

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
import com.umanji.umanjiapp.ui.channel.community.create.CommunityCreateActivity;
import com.umanji.umanjiapp.ui.channel.spot.create.SpotCreateActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import de.greenrobot.event.EventBus;

public class CommunityListFragment extends BaseChannelListFragment {
    private static final String TAG = "CommunityListFragment";

    private Button mAddBtn;

    public static CommunityListFragment newInstance(Bundle bundle) {
        CommunityListFragment fragment = new CommunityListFragment();
        fragment.setArguments(bundle);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public View getView(LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.fragment_communities, container, false);
    }

    @Override
    public BaseChannelListAdapter getListAdapter() {
        return new CommunityListAdapter(mActivity, this, mChannel);
    }

    @Override
    public void initWidgets(View view) {
        mAddBtn = (Button)view.findViewById(R.id.addChannelBtn);
        mAddBtn.setOnClickListener(this);

        switch (mChannel.getType()) {
            case TYPE_USER:
                mAddBtn.setVisibility(View.GONE);
                break;
        }
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
            params.put("type", TYPE_COMMUNITY);
            params.put("level", mChannel.getLevel());

            if(mChannel.getLevel() != LEVEL_LOCAL) {
                params.put("sort", "point DESC");
            }

            switch (mChannel.getType()) {
                case TYPE_USER:
                    params.put("owner", mChannel.getId());
                    break;
                case TYPE_INFO_CENTER:
                case TYPE_COMMUNITY:
                    setAddressParams(params, mChannel);
                    break;
                default:
                    params.put("parent", mChannel.getId());
                    break;
            }

            mApi.call(api_channels_communities_find, params, new AjaxCallback<JSONObject>() {
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

        if(mChannel != null) {
            switch (mChannel.getType()) {
                case TYPE_USER:
                case TYPE_INFO_CENTER:
                    mAddBtn.setVisibility(View.GONE);
                    break;
                default:
                    mAddBtn.setVisibility(View.VISIBLE);
                    break;
            }
        }

        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onEvent(SuccessData event) {
        super.onEvent(event);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);

        switch (v.getId()) {
            case R.id.addChannelBtn:
                Intent intent = new Intent(mActivity, CommunityCreateActivity.class);

                Bundle bundle = new Bundle();
                bundle.putString("channel", mChannel.getJsonObject().toString());
                intent.putExtra("bundle", bundle);

                startActivity(intent);

                break;
        }
    }
}
