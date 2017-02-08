package com.umanji.umanjiapp.helper;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;

import com.umanji.umanjiapp.R;
import com.umanji.umanjiapp.model.ChannelData;
import com.umanji.umanjiapp.model.SuccessData;
import com.umanji.umanjiapp.ui.BaseFragment;


public class DefaultFragment extends BaseFragment {
    private static final String TAG = "DefaultFragment";

    public static DefaultFragment newInstance(Bundle bundle) {
        DefaultFragment fragment = new DefaultFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public View getView(LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.activity_create_local_home, container, false);
    }

    @Override
    public void initWidgets(View view) {

    }

    @Override
    public void loadData() {

    }


    @Override
    public void updateView() {
        if(AuthHelper.isLogin(mActivity)) {
        }else {
        }
    }

    @Override
    public void onEvent(SuccessData event) {
        super.onEvent(event);

        ChannelData channelData = new ChannelData(event.response);

        switch (event.type) {
            case api_channels_create:

                break;

            case EVENT_LOOK_AROUND:
                mActivity.finish();
                break;
        }
    }

    private AlphaAnimation buttonClick = new AlphaAnimation(0F, 1F);

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

        }
    }
}
