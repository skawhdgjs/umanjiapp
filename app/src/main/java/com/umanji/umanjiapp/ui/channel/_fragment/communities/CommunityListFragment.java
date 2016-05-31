package com.umanji.umanjiapp.ui.channel._fragment.communities;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.umanji.umanjiapp.R;
import com.umanji.umanjiapp.helper.Helper;
import com.umanji.umanjiapp.model.ChannelData;
import com.umanji.umanjiapp.model.ErrorData;
import com.umanji.umanjiapp.model.SuccessData;
import com.umanji.umanjiapp.ui.channel._fragment.BaseChannelListAdapter;
import com.umanji.umanjiapp.ui.channel._fragment.BaseChannelListFragment;

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
            case TYPE_INFO_CENTER:
                mAddBtn.setVisibility(View.GONE);
                break;
        }
    }

    @Override
    public void loadMoreData() {
        isLoading = true;
        mLoadCount = mLoadCount + 1;
        try {
            JSONObject params = new JSONObject();
            params.put("page", mAdapter.getCurrentPage()); // for paging
            params.put("level", mChannel.getLevel());
            params.put("sort", "point DESC");

            switch (mChannel.getType()) {
                case TYPE_USER:
                    params.put("owner", mChannel.getId());
                    params.put("type", TYPE_PROFILE_COMMUNITIES);
                    break;
                case TYPE_INFO_CENTER:
                    params.put("type", TYPE_KEYWORD_COMMUNITY);
                    setAddressParams(params, mChannel);
                    break;
                default:
                    params.put("parent", mChannel.getId());
                    params.put("type", TYPE_COMMUNITY);
                    break;
            }

            String communityType = mChannel.getType();
            String apiType;

            switch(communityType){
                case TYPE_COMMUNITY:
                    apiType = api_channels_community_find;
                    break;
                case TYPE_INFO_CENTER:
                    apiType = api_channels_communities_find;
                    break;
                default:
                    apiType = api_channels_community_find;
                    break;
//          일단 헷갈려서 여기까지... 필요없을 수도 있지만 2중 체크 차원에서 해놓음
            }

//            api_channels_communities_find
            mApi.call(apiType, params, new AjaxCallback<JSONObject>() {
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
        ChannelData channelData = new ChannelData(event.response);

        switch (event.type) {
            case api_channels_createCommunity:
                String parentId = event.response.optString("parent");
                if(TextUtils.equals(mChannel.getId(), parentId)) {
                    try {
                        JSONObject params = new JSONObject();
                        params.put("id", mChannel.getId());
                        mApi.call(api_channels_get, params, new AjaxCallback<JSONObject>() {
                            @Override
                            public void callback(String url, JSONObject object, AjaxStatus status) {
                                mChannel = new ChannelData(object);
                            }
                        });
                        updateView();
                    } catch(JSONException e) {
                        Log.e(TAG, "error " + e.toString());
                    }

                    if(TextUtils.isEmpty(channelData.getId())) {
                        Toast.makeText(mActivity, "이미 존재하는 커뮤니티 입니다.", Toast.LENGTH_SHORT).show();
                    } else {
                        mAdapter.addTop(channelData);
                        mAdapter.notifyDataSetChanged();
                    }
                }
                break;
        }
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);

        switch (v.getId()) {
            case R.id.addChannelBtn:
                Helper.startCreateActivity(mActivity, mChannel, TYPE_COMMUNITY);
                break;
        }
    }
}
