package com.umanji.umanjiapp.ui.fragment.spots;

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
import com.umanji.umanjiapp.helper.AuthHelper;
import com.umanji.umanjiapp.helper.UiHelper;
import com.umanji.umanjiapp.model.ChannelData;
import com.umanji.umanjiapp.model.SuccessData;
import com.umanji.umanjiapp.ui.base.BaseChannelListAdapter;
import com.umanji.umanjiapp.ui.base.BaseChannelListFragment;
import com.umanji.umanjiapp.ui.base.BaseFragment;
import com.umanji.umanjiapp.ui.fragment.community.CommunityListAdapter;
import com.umanji.umanjiapp.ui.fragment.posts.PostListAdapter;
import com.umanji.umanjiapp.ui.page.channel.community.create.CommunityCreateActivity;
import com.umanji.umanjiapp.ui.page.channel.spot.create.SpotCreateActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class SpotListFragment extends BaseChannelListFragment {
    private static final String TAG = "SpotListFragment";

    private Button mAddBtn;

    public static SpotListFragment newInstance(Bundle bundle) {
        SpotListFragment fragment = new SpotListFragment();
        fragment.setArguments(bundle);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mCreateApiName = api_links_createSpot;
        mListApiName = api_channels_spots_find;
        mType   = TYPE_SPOT_INNER;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        LinearLayout view = (LinearLayout) inflater.inflate(
                R.layout.fragment_spots, container, false);

        RecyclerView rView = (RecyclerView)view.findViewById(R.id.recyclerView);
        super.onCreateView(rView);

        mAddBtn = (Button)view.findViewById(R.id.addSpotBtn);
        mAddBtn.setOnClickListener(this);

        return view;
    }

    @Override
    public void updateView() {
        super.updateView();

        if(mChannel != null) {
            switch (mChannel.getType()) {
                case TYPE_USER:
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
            case R.id.addSpotBtn:
                Intent intent = new Intent(mContext, SpotCreateActivity.class);

                Bundle bundle = new Bundle();
                bundle.putString("channel", mChannel.getJsonObject().toString());
                bundle.putString("type", mType);
                intent.putExtra("bundle", bundle);

                startActivityForResult(intent, UiHelper.CODE_SPOT_ACTIVITY);

                break;
        }
    }

    @Override
    public BaseChannelListAdapter getListAdapter() {
        return new SpotListAdapter(getActivity(), this);
    }
}
