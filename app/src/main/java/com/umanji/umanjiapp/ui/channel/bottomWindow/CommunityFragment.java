package com.umanji.umanjiapp.ui.channel.bottomWindow;


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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by paul on 7/6/16.
 */
public class CommunityFragment extends Fragment  implements AppConfig {
    private static final String TAG = "CommunityFragment";
    private static final String KEY_LAYOUT_MANAGER = "layoutManager";
    private static final int SPAN_COUNT = 2;

    public CommunityFragment() {
        // Required empty public constructor
    }

    public ApiHelper mApi;
    private JSONObject mParams;

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
    protected String[] mDataset;
    protected ArrayList<ChannelData> mChannels;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize dataset, this data would usually come from a local content provider or
        // remote server.

        mApi = new ApiHelper(getContext());

        String getData = getArguments().getString("params");

        try {
            mParams = new JSONObject(getData);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        getTalkData(mParams);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.recycler_view_frag, container, false);
        rootView.setTag(TAG);

        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerView);

        // LinearLayoutManager is used here, this will layout the elements in a similar fashion
        // to the way ListView would layout elements. The RecyclerView.LayoutManager defines how
        // elements are laid out.
        mLayoutManager = new LinearLayoutManager(getActivity());

        setRecyclerViewLayoutManager(LayoutManagerType.LINEAR_LAYOUT_MANAGER);

        return rootView;
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

    /**
     * Generates Strings for RecyclerView's adapter. This data would usually come
     * from a local content provider or remote server.
     */
    private void initDataset(String mData) {
        mChannels = new ArrayList<ChannelData>();
        String getData = mData;
        JSONArray jsonArray = null;
        try {
            jsonArray = new JSONArray(getData);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        for (int idx = 0; idx < jsonArray.length(); idx++) {
            JSONObject jsonDoc = null;
            try {
                jsonDoc = jsonArray.getJSONObject(idx);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            ChannelData doc = new ChannelData(jsonDoc);
            mChannels.add(doc);
        }
        mAdapter = new CommunityAdapter(getActivity(), getActivity().getApplicationContext(), mChannels);
        // Set TalkAdapter as the adapter for RecyclerView.
        mRecyclerView.setAdapter(mAdapter);
//        mAdapter.notifyDataSetChanged();
    }

    private void getTalkData(JSONObject params){

        try {
            params.put("type", TYPE_COMMUNITY);
//            params.put("keywords", communityName);
            params.put("limit", 30);
            params.put("sort", "point DESC");
        } catch (JSONException e) {
            e.printStackTrace();
        }
//        api_channels_communities_num
//        api_main_findPosts

//        api_main_findDistributions
//        api_channels_bottomCommunity
        mApi.call(api_main_findDistributions, params, new AjaxCallback<JSONObject>() {
            @Override
            public void callback(String url, JSONObject json, AjaxStatus status) {
                if (status.getCode() == 500) {
//                    EventBus.getDefault().post(new ErrorData(TYPE_ERROR_AUTH, TYPE_ERROR_AUTH));
                } else {
                    try {
                        JSONArray jsonArray = json.getJSONArray("data");

                        if (jsonArray.length() != 0) {

                            String mdata = jsonArray.toString();

                            initDataset(mdata);

                        } else {

                        }
                        //mTalkAdapter.notifyDataSetChanged();
                    } catch (JSONException e) {
                        Log.e(TAG, "Error " + e.toString());
                    }
                }
            }
        });

    }

}