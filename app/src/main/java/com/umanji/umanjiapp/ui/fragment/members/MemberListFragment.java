package com.umanji.umanjiapp.ui.fragment.members;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import com.umanji.umanjiapp.R;
import com.umanji.umanjiapp.helper.ApiHelper;
import com.umanji.umanjiapp.helper.AuthHelper;
import com.umanji.umanjiapp.model.ChannelData;
import com.umanji.umanjiapp.model.SuccessData;
import com.umanji.umanjiapp.ui.base.BaseChannelListAdapter;
import com.umanji.umanjiapp.ui.base.BaseChannelListFragment;
import com.umanji.umanjiapp.ui.base.BaseFragment;
import com.umanji.umanjiapp.ui.fragment.posts.PostListAdapter;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MemberListFragment extends BaseChannelListFragment {
    private static final String TAG = "MemberListFragment";


    private Button mJoinBtn;
    private Button mUnJoinBtn;

    public static MemberListFragment newInstance(Bundle bundle) {
        MemberListFragment fragment = new MemberListFragment();
        fragment.setArguments(bundle);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mCreateApiName = api_channels_join;
        mListApiName = api_channels_members_find;
        mType   = TYPE_MEMBER;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        LinearLayout view = (LinearLayout) inflater.inflate(
                R.layout.fragment_members, container, false);

        RecyclerView rView = (RecyclerView)view.findViewById(R.id.recyclerView);
        super.onCreateView(rView);



        mJoinBtn = (Button)view.findViewById(R.id.joinBtn);
        mJoinBtn.setOnClickListener(this);

        mUnJoinBtn = (Button)view.findViewById(R.id.unJoinBtn);
        mUnJoinBtn.setOnClickListener(this);

        updateView();
        return view;
    }

    @Override
    public BaseChannelListAdapter getListAdapter() {
        return new MemberListAdapter(getActivity(), this);
    }

    @Override
    public void updateView() {
        super.updateView();

        switch (mLevel) {
            case LEVEL_LOCAL:
                mJoinBtn.setVisibility(View.VISIBLE);

                String actionId = mChannel.getActionId(TYPE_MEMBER, AuthHelper.getUserId(mContext));

                if(!TextUtils.isEmpty(actionId)) {
                    mJoinBtn.setVisibility(View.GONE);
                    mUnJoinBtn.setVisibility(View.VISIBLE);

                }else {
                    mJoinBtn.setVisibility(View.VISIBLE);
                    mUnJoinBtn.setVisibility(View.GONE);
                }

                break;
            default:
                mJoinBtn.setVisibility(View.GONE);
                break;
        }

        mJoinBtn.setEnabled(true);
        mUnJoinBtn.setEnabled(true);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.joinBtn:
                try {
                    JSONObject params = mChannel.getAddressJSONObject();
                    params.put("parent", mId);
                    params.put("type", mType);
                    params.put("name", AuthHelper.getUserName(mContext));

                    mApiHelper.call(api_channels_join, params);
                    mJoinBtn.setEnabled(false);
                    mUnJoinBtn.setEnabled(false);

                }catch(JSONException e) {
                    Log.e("BaseChannelCreate", "error " + e.toString());
                }

                break;
            case R.id.unJoinBtn:
                try {
                    String actionId = mChannel.getActionId(TYPE_MEMBER, AuthHelper.getUserId(mContext));

                    JSONObject params = new JSONObject();
                    params.put("parent", mChannel.getId());
                    params.put("id", actionId);

                    mApiHelper.call(api_channels_unJoin, params);
                    mJoinBtn.setEnabled(false);
                    mUnJoinBtn.setEnabled(false);

                }catch(JSONException e) {
                    Log.e("BaseChannelCreate", "error " + e.toString());
                }

                break;
        }
    }
}
