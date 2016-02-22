package com.umanji.umanjiapp.ui.channel._fragment;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;

import com.umanji.umanjiapp.R;
import com.umanji.umanjiapp.model.ChannelData;
import com.umanji.umanjiapp.model.SuccessData;
import com.umanji.umanjiapp.ui.BaseFragment;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public abstract class BaseChannelListFragment extends BaseFragment {
    private static final String TAG = "BaseChannelListFragment";

    /****************************************************
     *  Intent
     ****************************************************/
    protected ChannelData   mChannel;

    /****************************************************
     *  View
     ****************************************************/
    protected BaseChannelListAdapter mAdapter;


    /****************************************************
     *  For Etc.
     ****************************************************/
    protected boolean isLoading = false;
    protected int mPreFocusedItem = 0;
    protected int mLoadCount = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(getArguments() != null) {
            String jsonString = getArguments().getString("channel");
            if(jsonString != null) {
                mChannel = new ChannelData(jsonString);
            }
        }


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        RecyclerView rView = (RecyclerView)view.findViewById(R.id.recyclerView);

        rView.setItemViewCacheSize(iItemViewCacheSize);

        addOnScrollListener(rView);

        mAdapter = getListAdapter();
        rView.setAdapter(mAdapter);

        loadData();

        return view;
    }

    protected void addOnScrollListener(RecyclerView rView) {
        final LinearLayoutManager mLayoutManager = new LinearLayoutManager(rView.getContext());
        rView.setLayoutManager(mLayoutManager);

        rView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0) {
                    int visibleItemCount = mLayoutManager.getChildCount();
                    int totalItemCount = mLayoutManager.getItemCount();
                    int pastVisiblesItems = mLayoutManager.findFirstVisibleItemPosition();

                    ArrayList<ChannelData> channels = mAdapter.getDocs();

                    int currentFocusedIndex = visibleItemCount + pastVisiblesItems;
                    if (currentFocusedIndex == mPreFocusedItem) return;
                    mPreFocusedItem = currentFocusedIndex;
                    if (channels.size() <= mPreFocusedItem) return;

                    if (!isLoading) {
                        if (mPreFocusedItem == (totalItemCount - 3)) {
                            loadMoreData();
                        }
                    }
                }
            }
        });
    }

    @Override
    public void loadData() {
        mAdapter.resetDocs();
        mAdapter.setCurrentPage(0);

        loadMoreData();
    }

    @Override
    public View getView(LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.fragment_links, container, false);
    }

    @Override
    public void onEvent(SuccessData event) {
        super.onEvent(event);
        ChannelData channelData = new ChannelData(event.response);

        switch (event.type) {
            case api_channels_create:
                String parentId = event.response.optString("parent");
                if(TextUtils.equals(mChannel.getId(), parentId)) {
                    mChannel = channelData.getParent();

                    loadData();
                }
                break;
            case api_channels_id_update:
                if(TextUtils.equals(mChannel.getId(), channelData.getId())) {
                    mChannel = channelData;

                    loadData();
                }
                break;
            case EVENT_UPDATEVIEW:
                loadData();
                break;
        }
    }

    protected void setAddressParams(JSONObject params, ChannelData channelData) {
        try {
            switch (mChannel.getLevel()) {
                case LEVEL_LOCAL:
                    params.put("parent", channelData.getId());
                    params.put("featureName", channelData.getFeatureName());
                case LEVEL_DONG:
                    params.put("thoroughfare", channelData.getThoroughfare());
                case LEVEL_GUGUN:
                    params.put("locality", channelData.getLocality());
                case LEVEL_DOSI:
                    params.put("countryName", channelData.getCountryName());
                    params.put("adminArea", channelData.getAdminArea());
                    break;
            }

        } catch (JSONException e) {
            Log.e(TAG, "Error " + e.toString());
        }

    }

    public abstract BaseChannelListAdapter getListAdapter();
    public abstract void loadMoreData();

}
