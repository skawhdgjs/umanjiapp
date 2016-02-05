package com.umanji.umanjiapp.ui.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.umanji.umanjiapp.R;
import com.umanji.umanjiapp.helper.ApiHelper;
import com.umanji.umanjiapp.model.ChannelData;
import com.umanji.umanjiapp.model.SuccessData;
import com.umanji.umanjiapp.ui.fragment.posts.PostListAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public abstract class BaseChannelListFragment extends BaseFragment {
    private static final String TAG = "BaseChannelListFragment";

    /****************************************************
     *  View
     ****************************************************/
    protected BaseChannelListAdapter mAdapter;


    /****************************************************
     *  For Etc.
     ****************************************************/
    protected String mCreateApiName;
    protected String mListApiName;

    private boolean isLoading = true;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(getArguments() != null) {
            String jsonString = getArguments().getString("channel");
            mChannel = new ChannelData(jsonString);
            if(mChannel != null) {
                mId     = mChannel.getId();
                mLevel  = mChannel.getLevel();
            }
        }
    }

    protected void onCreateView(RecyclerView view) {
        view.setItemViewCacheSize(iItemViewCacheSize);
        super.onCreateView(view);

        final LinearLayoutManager mLayoutManager = new LinearLayoutManager(view.getContext());
        view.setLayoutManager(new LinearLayoutManager(view.getContext()));

        view.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy)
            {
                if(dy > 0) {
                    int visibleItemCount = mLayoutManager.getChildCount();
                    int totalItemCount = mLayoutManager.getItemCount();
                    int pastVisiblesItems = mLayoutManager.findFirstVisibleItemPosition();

                    if(isLoading) {
                        if( (visibleItemCount + pastVisiblesItems) >= ( totalItemCount - 5 )) {
                            isLoading = false;
                            mAdapter.setCurrentPage(mAdapter.getCurrentPage() + 1);
                            loadMoreData();
                        }
                    }
                }
            }
        });



        mAdapter = getListAdapter();
        view.setAdapter(mAdapter);

        loadData();
    }


    public RecyclerView getRView(LayoutInflater inflater, ViewGroup container) {
        RecyclerView rView = (RecyclerView)inflater.inflate(R.layout.fragment_links, container, false);
        return rView;
    }

    public BaseChannelListAdapter getListAdapter() {
        return new PostListAdapter(getActivity(), this);
    }

    public void loadMoreData() {
        try {
            JSONObject params = new JSONObject();
            params.put("page", mAdapter.getCurrentPage());

            switch (mChannel.getType()) {
                case TYPE_USER:
                    if(mType.equals(TYPE_SPOT_INNER)) {
                        params.put("type", TYPE_SPOTS);
                    }else {
                        params.put("type", mType);
                    }
                    params.put("owner", mChannel.getId());
                    break;
                case TYPE_COMMUNITY:
                    params.put("level", mChannel.getLevel());
                default:
                    switch (mChannel.getLevel()) {
                        case LEVEL_DONG:
                            params.put("thoroughfare", mChannel.getThoroughfare());
                        case LEVEL_GUGUN:
                            params.put("locality", mChannel.getLocality());
                        case LEVEL_DOSI:
                            params.put("countryName", mChannel.getCountryName());
                            params.put("adminArea", mChannel.getAdminArea());
                            break;
                        case LEVEL_LOCAL:
                            params.put("parent", mChannel.getId());
                            break;
                    }

                    params.put("parent", mChannel.getId());
                    params.put("level", mChannel.getLevel());
                    params.put("type", mType);
                    break;
            }


            mApiHelper.call(mListApiName, params);
        } catch(JSONException e) {
            Log.e(TAG, "error " + e.toString());
        }
    }

    @Override
    public void loadData() {
        JSONObject params = new JSONObject();
        mAdapter.resetDocs();
        mAdapter.setCurrentPage(0);

        loadMoreData();
    }

    @Override
    public void onEvent(SuccessData event) {
        if(event.response == null) return;

        ChannelData channelData = new ChannelData(event.response);
        if(mListApiName != null && mListApiName.equals(event.type)) {
            try {
                String id = event.response.optString("parent");
                if(mId.equals(id)) {
                    isLoading = true;
                    JSONArray jsonArray = event.response.getJSONArray("data");
                    for(int idx = 0; idx < jsonArray.length(); idx++) {
                        JSONObject jsonDoc = jsonArray.getJSONObject(idx);
                        ChannelData doc = new ChannelData(jsonDoc);

                        mAdapter.addBottom(doc);

                        updateView();
                    }
                }


            } catch(JSONException e) {
                Log.e(TAG, "error " + e.toString());
            }
            return;
        }

        if(mCreateApiName != null && mCreateApiName.equals(event.type)) {
            if(TextUtils.equals(mId, channelData.getParent().getId())){
                mAdapter.addTop(channelData);
                mAdapter.notifyDataSetChanged();
            }else if(TextUtils.equals(mId, channelData.getParent().getParentId())) {
                try {
                    String parent = event.response.getString("parent");
                    JSONObject params = new JSONObject();
                    params.put("id", parent);
                    mApiHelper.call(api_channels_get, params);
                } catch(JSONException e) {
                    Log.e(TAG, "error " + e.toString());
                }
            }

            return;
        }

        switch (event.type) {
            case api_links_create:
            case api_links_createCommunity:
                if(channelData.getType().equals(mType)) {
                    mAdapter.addTop(channelData);
                }
                break;

            case api_channels_get:
                if(TextUtils.equals(mId, channelData.getId())) {
                    mChannel = channelData;
                    mUser = mChannel.getOwner();
                    updateView();
                } else if(TextUtils.equals(mId, channelData.getParent().getId())){
                    mAdapter.updateDoc(channelData);
                }

                break;
            case api_channels_unJoin:
                mAdapter.removeDoc(channelData);
            case api_channels_join:
                try {
                    String parentId = event.response.getString("parent");
                    JSONObject params = new JSONObject();
                    params.put("id", parentId);
                    mApiHelper.call(api_channels_get, params);
                } catch(JSONException e) {
                    Log.e(TAG, "error " + e.toString());
                }
                break;
        }
    }

    @Override
    public void onClick(View v) {

    };

    @Override
    public void updateView() {
        mAdapter.notifyDataSetChanged();
    }


}
