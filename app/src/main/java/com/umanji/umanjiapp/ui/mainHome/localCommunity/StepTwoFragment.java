package com.umanji.umanjiapp.ui.mainHome.localCommunity;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.umanji.umanjiapp.R;
import com.umanji.umanjiapp.helper.AuthHelper;
import com.umanji.umanjiapp.model.ChannelData;
import com.umanji.umanjiapp.model.SuccessData;
import com.umanji.umanjiapp.ui.BaseFragment;


public class StepTwoFragment extends BaseFragment {
    private static final String TAG = "StepTwoFragment";

    protected ChannelData mChannel;

    private ImageView mGoBackBtn;
    private TextView mSubmit;
    private TextView mConsole;

    protected String mTabType = "";

    public static StepTwoFragment newInstance(Bundle bundle) {
        StepTwoFragment fragment = new StepTwoFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            String jsonString = getArguments().getString("channel");
            if (jsonString != null) {
                mChannel = new ChannelData(jsonString);
            }

            mTabType = getArguments().getString("tabType");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public View getView(LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.activity_step_two, container, false);
    }

    @Override
    public void initWidgets(View view) {

        mGoBackBtn = (ImageView) view.findViewById(R.id.goBackBtn);
        mGoBackBtn.setOnClickListener(this);

        mSubmit = (TextView) view.findViewById(R.id.submit);
        mSubmit.setOnClickListener(this);

        mConsole = (TextView) view.findViewById(R.id.console);

        updateView();

    }

    @Override
    public void loadData() {

    }


    @Override
    public void updateView() {
        if(AuthHelper.isLogin(mActivity)) {
        }else {
        }

        mConsole.setText(mChannel.getType()+" :: this is console answer...");
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

            case R.id.goBackBtn:
                mGoBackBtn.startAnimation(buttonClick);
                buttonClick.setDuration(500);

                Intent mInt = new Intent(mActivity, StepOneActivity.class);
                startActivity(mInt);
                mActivity.finish();

            case R.id.submit:
                mSubmit.startAnimation(buttonClick);
                buttonClick.setDuration(500);
                /*Intent nextInt = new Intent(mActivity, StepTwoActivity.class);
                startActivity(nextInt);*/
                Toast.makeText(mActivity, "submit", Toast.LENGTH_SHORT).show();
//                mActivity.finish();

        }
    }
}
