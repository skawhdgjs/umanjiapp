package com.umanji.umanjiapp.ui.mainHome.localCommunity;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.umanji.umanjiapp.R;
import com.umanji.umanjiapp.helper.AuthHelper;
import com.umanji.umanjiapp.model.ChannelData;
import com.umanji.umanjiapp.model.SuccessData;
import com.umanji.umanjiapp.ui.BaseFragment;
import com.umanji.umanjiapp.ui.main.MainActivity;
import com.umanji.umanjiapp.ui.modal.WebViewActivity;


public class CreateLocalCommunityFragment extends BaseFragment {
    private static final String TAG = "CreateLocalCommunityFragment";

    protected TextView mGoOut;

    public static CreateLocalCommunityFragment newInstance(Bundle bundle) {
        CreateLocalCommunityFragment fragment = new CreateLocalCommunityFragment();
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

        mGoOut = (TextView) view.findViewById(R.id.goout);
        mGoOut.setOnClickListener(this);

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

            case R.id.goout:
                mGoOut.startAnimation(buttonClick);
                buttonClick.setDuration(500);
                mActivity.finish();
                break;


        }
    }
}
