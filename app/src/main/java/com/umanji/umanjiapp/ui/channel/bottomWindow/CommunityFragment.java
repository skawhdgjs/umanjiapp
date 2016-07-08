package com.umanji.umanjiapp.ui.channel.bottomWindow;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.umanji.umanjiapp.R;
import com.umanji.umanjiapp.model.ChannelData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by paul on 7/6/16.
 */
public class CommunityFragment extends Fragment {

    public CommunityFragment() {
        // Required empty public constructor
    }

    public static CommunityFragment newInstance(Bundle bundle) {
        CommunityFragment fragment = new CommunityFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    private static final String TAG = "RecyclerViewFragment";
    private static final String KEY_LAYOUT_MANAGER = "layoutManager";
    private static final int SPAN_COUNT = 2;

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
        initDataset();
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

        mAdapter = new CommunityAdapter(getActivity(), getActivity().getApplicationContext(), mChannels);
        // Set TalkAdapter as the adapter for RecyclerView.
        mRecyclerView.setAdapter(mAdapter);

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
    private void initDataset() {
        mDataset = new String[60];
        mChannels   = new ArrayList<ChannelData>();
        String getData = getArguments().getString("channels");
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
    }

}