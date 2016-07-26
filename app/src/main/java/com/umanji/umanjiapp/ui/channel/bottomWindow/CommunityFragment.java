package com.umanji.umanjiapp.ui.channel.bottomWindow;


import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.umanji.umanjiapp.AppConfig;
import com.umanji.umanjiapp.R;
import com.umanji.umanjiapp.helper.ApiHelper;
import com.umanji.umanjiapp.model.ChannelData;
import com.umanji.umanjiapp.model.SubLinkData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by paul on 7/6/16.
 */
public class CommunityFragment extends BottomBaseFragment{
    private static final String TAG = "CommunityFragment";
    private static final String KEY_LAYOUT_MANAGER = "layoutManager";
    private static final int SPAN_COUNT = 2;

    public CommunityFragment() {
        // Required empty public constructor
    }

    public ApiHelper mApi;
    private JSONObject mParams;
    private String thisType;
    private String keywordName;

    public static CommunityFragment newInstance(Bundle bundle) {
        CommunityFragment fragment = new CommunityFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    private enum LayoutManagerType {
        GRID_LAYOUT_MANAGER,
        LINEAR_LAYOUT_MANAGER
    }

    protected LayoutManagerType mCurrentLayoutManagerType;
    protected RecyclerView mRecyclerView;
    protected CommunityAdapter mAdapter;
    protected RecyclerView.LayoutManager mLayoutManager;
    protected ArrayList<ChannelData> mChannels;

    protected boolean isLoading = false;
    protected int mPreFocusedItem = 0;
    protected int mLoadCount = 0;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize dataset, this data would usually come from a local content provider or
        // remote server.

        mApi = new ApiHelper(getContext());

        String getData = getArguments().getString("params");
        thisType = getArguments().getString("thisType");
        keywordName = getArguments().getString("keywordName");

        try {
            mParams = new JSONObject(getData);
        } catch (JSONException e) {
            e.printStackTrace();
        }

//        getTalkData(mParams);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.recycler_view_frag, container, false);
        rootView.setTag(TAG);

        mProgress = new ProgressDialog(getActivity());
        mProgress.setMessage("잠시만 기다려주세요...");
//        mProgress.setTitle("Connecting server");
        mProgress.setCancelable(true);
        mProgress.show();

        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerView);

        addOnScrollListener(mRecyclerView);

        mAdapter = new CommunityAdapter(getActivity(), getActivity().getApplicationContext(), mChannels);

        // Set TalkAdapter as the adapter for RecyclerView.
        mRecyclerView.setAdapter(mAdapter);
//        mAdapter.notifyDataSetChanged();

        // LinearLayoutManager is used here, this will layout the elements in a similar fashion
        // to the way ListView would layout elements. The RecyclerView.LayoutManager defines how
        // elements are laid out.
//        mLayoutManager = new LinearLayoutManager(getActivity());

        loadData();

//        setRecyclerViewLayoutManager(LayoutManagerType.LINEAR_LAYOUT_MANAGER);


        return rootView;
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
                        if (mPreFocusedItem != (totalItemCount - 1)) {  // -1
                            loadMoreData(mParams);
                        }
                    }
                }
            }
        });
    }

    public void loadData() {
        mAdapter.resetDocs();
        mAdapter.setCurrentPage(0);

        if(thisType.equals("talk")){
            loadMoreData(mParams);
        } else {
            loadMoreKeywordData(mParams);
        }

    }

    public void updateView() {
        mAdapter.notifyDataSetChanged();
        mProgress.hide();
    }

    /**
     * Set RecyclerView's LayoutManager to the one given.
     *
     * @param layoutManagerType Type of layout manager to switch to.
     */
    public void setRecyclerViewLayoutManager(LayoutManagerType layoutManagerType) {
        int scrollPosition = 0;

        // If a layout manager has already been set, get current scroll position.

        mLayoutManager = new LinearLayoutManager(getActivity());
        mCurrentLayoutManagerType = LayoutManagerType.LINEAR_LAYOUT_MANAGER;

        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.scrollToPosition(scrollPosition);
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        // Save currently selected layout manager.
        savedInstanceState.putSerializable(KEY_LAYOUT_MANAGER, mCurrentLayoutManagerType);
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    private void loadMoreData(JSONObject params) {

        isLoading = true;
        mLoadCount = mLoadCount + 1;

        final JSONArray extractArr = new JSONArray();

        try {
            params.put("page", mAdapter.getCurrentPage());
            params.put("type", TYPE_COMMUNITY);
            params.put("level", 18);
//            params.put("keywords", communityName);
            params.put("limit", 8);
            params.put("sort", "point DESC");
        } catch (JSONException e) {
            e.printStackTrace();
        }
// original :: api_channels_community_find
// test     :: api_channels_communities_find

        mApi.call(api_bottom_communities_find, params, new AjaxCallback<JSONObject>() {
            @Override
            public void callback(String url, JSONObject json, AjaxStatus status) {
                ArrayList<ChannelData> communityArr = new ArrayList<ChannelData>();
                if (status.getCode() == 500) {
//                    EventBus.getDefault().post(new ErrorData(TYPE_ERROR_AUTH, TYPE_ERROR_AUTH));
                } else {
                    try {

                        JSONArray jsonArray = json.getJSONArray("data");

                        for (int idx = 0; idx < jsonArray.length(); idx++) {
                            JSONObject jsonDoc = jsonArray.getJSONObject(idx);
                            ChannelData doc = new ChannelData(jsonDoc);
                            communityArr.add(doc);

                            ChannelData channelDoc = communityArr.get(idx);
                            mAdapter.addBottom(channelDoc);

                            updateView();

                        }
                        /*
                        for (int idx = 0; idx < jsonArray.length(); idx++) {
                            JSONObject jsonDoc = jsonArray.getJSONObject(idx);
                            ChannelData doc = new ChannelData(jsonDoc);
                            if(doc.getParent() != null){
                                String type = doc.getParent().getType();
                                if (type != null && type.equals(TYPE_INFO_CENTER)) {

                                } else {
                                    communityArr.add(doc);
                                }
                            }

                        }
                        *//*

                        for (int idx2 = 0; idx2 < communityArr.size(); idx2++) {
                            ChannelData channelDoc = communityArr.get(idx2);
//                            ChannelData doc = new ChannelData(jsonDoc);
                            mAdapter.addBottom(channelDoc);

                            updateView();

                        }
*/
                        isLoading = false;
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }
        });
        mAdapter.setCurrentPage(mAdapter.getCurrentPage() + 1);
    }

    //************************************************************************************************** KeywordCommunityMode
    private void loadMoreKeywordData(JSONObject params){

        isLoading = true;
        mLoadCount = mLoadCount + 1;

        try {
            params.put("page", mAdapter.getCurrentPage());
            params.put("keywords", keywordName);
            params.put("type", TYPE_COMMUNITY);
            params.put("limit", 8);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        mApi.call(api_main_findDistributions, params, new AjaxCallback<JSONObject>() {
            @Override
            public void callback(String url, JSONObject json, AjaxStatus status) {
                if (status.getCode() == 500) {
//                    EventBus.getDefault().post(new ErrorData(TYPE_ERROR_AUTH, TYPE_ERROR_AUTH));
                } else {
                    try {
                        JSONArray jsonArray = json.getJSONArray("data");

                        if (jsonArray.length() != 0) {

                            String mData = jsonArray.toString();

                            mChannels = new ArrayList<ChannelData>();
                            String getData = mData;

                            for (int idx = 0; idx < jsonArray.length(); idx++) {
                                JSONObject jsonDoc = null;
                                try {
                                    jsonDoc = jsonArray.getJSONObject(idx);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                ChannelData doc = new ChannelData(jsonDoc);
                                mAdapter.addBottom(doc);

                                updateView();
                            }

                        } else {

                        }
                        //mTalkAdapter.notifyDataSetChanged();
                        isLoading = false;
                    } catch (JSONException e) {
                        Log.e(TAG, "Error " + e.toString());
                    }

                }
            }
        });
        mAdapter.setCurrentPage(mAdapter.getCurrentPage() + 1);
    }
}

