package com.umanji.umanjiapp.ui.fragment.community;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import com.umanji.umanjiapp.R;
import com.umanji.umanjiapp.helper.ApiHelper;
import com.umanji.umanjiapp.helper.UiHelper;
import com.umanji.umanjiapp.model.ChannelData;
import com.umanji.umanjiapp.model.SuccessData;
import com.umanji.umanjiapp.ui.base.BaseChannelListAdapter;
import com.umanji.umanjiapp.ui.base.BaseChannelListFragment;
import com.umanji.umanjiapp.ui.base.BaseFragment;
import com.umanji.umanjiapp.ui.fragment.posts.PostListAdapter;
import com.umanji.umanjiapp.ui.page.channel.community.create.CommunityCreateActivity;
import com.umanji.umanjiapp.ui.page.channel.spot.create.SpotCreateActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class CommunityListFragment extends BaseChannelListFragment {
    private static final String TAG = "CommunityListFragment";

    private Button mAddBtn;

    public static CommunityListFragment newInstance(Bundle bundle) {
        CommunityListFragment fragment = new CommunityListFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mCreateApiName = api_links_createCommunity;
        mListApiName = api_channels_communities_find;
        mType   = TYPE_COMMUNITY;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        LinearLayout view = (LinearLayout) inflater.inflate(
                R.layout.fragment_communities, container, false);

        RecyclerView rView = (RecyclerView)view.findViewById(R.id.recyclerView);
        super.onCreateView(rView);

        mAddBtn = (Button)view.findViewById(R.id.addCommunityBtn);
        mAddBtn.setOnClickListener(this);

        return view;
    }

    @Override
    public BaseChannelListAdapter getListAdapter() {
        return new CommunityListAdapter(getActivity(), this);
    }

    @Override
    public void updateView() {
        super.updateView();

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
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.addCommunityBtn:
                Intent intent = new Intent(mContext, CommunityCreateActivity.class);

                Bundle bundle = new Bundle();
                bundle.putString("channel", mChannel.getJsonObject().toString());
                bundle.putString("type", mType);
                intent.putExtra("bundle", bundle);

                startActivity(intent);

                break;
        }
    }

}
