package com.umanji.umanjiapp.ui.base;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;

import com.androidquery.AQuery;
import com.umanji.umanjiapp.AppConfig;
import com.umanji.umanjiapp.helper.ApiHelper;
import com.umanji.umanjiapp.model.ChannelData;
import com.umanji.umanjiapp.model.SuccessData;
import com.umanji.umanjiapp.model.UserData;

import de.greenrobot.event.EventBus;

public abstract class BaseFragment extends Fragment implements View.OnClickListener, AppConfig {
    private static final String TAG = "BaseFragment";


    /****************************************************
     *  Intent
     ****************************************************/
    protected String mId;
    protected String mType;
    protected int mLevel;


    /****************************************************
     *  View
     ****************************************************/
    protected Activity      mActivity;
    protected Context       mContext;
    protected AQuery        aQuery;
    protected ProgressDialog mProgress;

    protected ChannelData   mChannel;
    protected ChannelData   mUser;

    /****************************************************
     *  Api
     ****************************************************/
    public ApiHelper mApiHelper;


    public static BaseFragment newInstance(Bundle bundle) {
        BaseChannelFragment fragment = new BaseChannelFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(getArguments() != null) {
            String jsonString = getArguments().getString("channel");
            if(jsonString != null) {
                mChannel = new ChannelData(jsonString);
                mType = mChannel.getType();
                mId = mChannel.getId();
                mLevel = mChannel.getLevel();
            }
        }

        mActivity = getActivity();
        mContext = getActivity().getBaseContext();
    }

    protected void onCreateView(View view) {
        mProgress = new ProgressDialog(getActivity());
        mProgress.setMessage("Loading, please wait");
        mProgress.setTitle("Connecting server");
        mProgress.setCancelable(false);

        mApiHelper  = new ApiHelper(getActivity(), mProgress);
        aQuery      = new AQuery(getActivity(), view);

        EventBus.getDefault().register(this);
    }


    @Override
     public void onDestroy() {
        super.onDestroy();

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBus.getDefault().unregister(this);
    }

    public abstract void loadData();
    public abstract void updateView();

    public abstract void onEvent(SuccessData event);

    @Override
    public void onClick(View v) {}
}
