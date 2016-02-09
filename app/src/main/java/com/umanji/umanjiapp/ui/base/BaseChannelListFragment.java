package com.umanji.umanjiapp.ui.base;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.umanji.umanjiapp.R;
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

    protected boolean isLoading = true;
    protected int mPreFocusedItem = 0;


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

                    ArrayList<ChannelData> channels = mAdapter.getDocs();

                    int currentFocusedIndex = visibleItemCount + pastVisiblesItems;
                    if (currentFocusedIndex == mPreFocusedItem) return;
                    mPreFocusedItem = currentFocusedIndex;
                    if (channels.size() <= mPreFocusedItem) return;

                    if (!isLoading) {
                        if (mPreFocusedItem == (totalItemCount - 4)) {
                            Log.d(TAG, "isLoading = true");
                            isLoading = true;
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
                    } else if(mType.equals(TYPE_COMMUNITY)) {
                        params.put("type", mType);
                        params.put("level", LEVEL_LOCAL);
                    } else {
                        params.put("type", mType);
                    }
                    params.put("owner", mChannel.getId());
                    params.put("sort", "point DESC");
                    break;
                case TYPE_INFO_CENTER:
                    params.put("parentType", TYPE_INFO_CENTER);
                default:
                    if(mChannel.getLevel() >= LEVEL_LOCAL) {
                        params.put("parent", mChannel.getId());
                        params.put("level", mChannel.getLevel());
                        params.put("type", mType);
                    } else {
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
                                break;
                        }

                        params.put("parent", mChannel.getId());
                        params.put("level", mChannel.getLevel());

                        if(mType.equals(TYPE_MEMBER)) {
                            params.put("type", TYPE_USER);
                        }else {
                            params.put("type", mType);
                        }


                        params.put("sort", "point DESC");

                    }
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
                    }
                    updateView();
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
                } else {
                    mAdapter.updateDoc(channelData);
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
