package com.umanji.umanjiapp.ui.fragment.posts;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.umanji.umanjiapp.R;
import com.umanji.umanjiapp.helper.ApiHelper;
import com.umanji.umanjiapp.model.ChannelData;
import com.umanji.umanjiapp.model.SuccessData;
import com.umanji.umanjiapp.ui.base.BaseChannelListAdapter;
import com.umanji.umanjiapp.ui.base.BaseChannelListFragment;
import com.umanji.umanjiapp.ui.base.BaseFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class PostListFragment extends BaseChannelListFragment {
    private static final String TAG = "PostListFragment";

    private TextView mEmptyPost;

    public static PostListFragment newInstance(Bundle bundle) {
        PostListFragment fragment = new PostListFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mCreateApiName = api_links_createPost;
        mListApiName = api_channels_posts_find;
        mType   = TYPE_POST;

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        LinearLayout view = (LinearLayout) inflater.inflate(
                R.layout.fragment_posts, container, false);

        RecyclerView rView = (RecyclerView)view.findViewById(R.id.recyclerView);
        super.onCreateView(rView);

        mEmptyPost = (TextView) view.findViewById(R.id.emptyPost);

        return view;
    }

    @Override
    public BaseChannelListAdapter getListAdapter() {
        return new PostListAdapter(getActivity(), this);
    }

    @Override
    public void onEvent(SuccessData event) {
        super.onEvent(event);


        if(mListApiName != null && mListApiName.equals(event.type)) {
            try {
                String id = event.response.optString("parent");
                if(mId.equals(id)) {
                    JSONArray jsonArray = event.response.getJSONArray("data");
                    if(jsonArray.length() > 0) {
                        mEmptyPost.setVisibility(View.GONE);
                    } else {
                        mEmptyPost.setVisibility(View.VISIBLE);
                    }
                }


            } catch(JSONException e) {
                Log.e(TAG, "error " + e.toString());
            }
            return;
        }

        ChannelData channelData = new ChannelData(event.response);

        if(mCreateApiName != null && mCreateApiName.equals(event.type)) {
            if(TextUtils.equals(mId, channelData.getParent().getId())){
                mEmptyPost.setVisibility(View.GONE);
            }
        }
    }
}
