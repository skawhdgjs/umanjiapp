package com.umanji.umanjiapp.ui.auth;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.umanji.umanjiapp.R;
import com.umanji.umanjiapp.model.ChannelData;
import com.umanji.umanjiapp.ui.BaseFragment;
import com.umanji.umanjiapp.ui.modal.WebViewActivity;

public class SignInFragment extends BaseFragment {
    private static final String TAG = "PostFragment";

    protected ChannelData mChannel;

    protected ImageView mUserPhoto;
    protected ImageView mLookAround;
    protected TextView mName;
    protected CheckBox mCheckbox;
    protected TextView mLocationConfirm;
    protected TextView mPrivacyConfirm;

    protected FloatingActionButton mFab;

    public static SignInFragment newInstance(Bundle bundle) {
        SignInFragment fragment = new SignInFragment();
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

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public View getView(LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.activity_sign_in, container, false);
    }

    @Override
    public void initWidgets(View view) {
        mCheckbox = (CheckBox) view.findViewById(R.id.checkbox1);
        mLocationConfirm = (TextView) view.findViewById(R.id.locationConfirm);
        mLocationConfirm.setOnClickListener(this);

        mPrivacyConfirm = (TextView) view.findViewById(R.id.privacyConfirm);
        mPrivacyConfirm.setOnClickListener(this);

        // mPrivacyConfirm

        mCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {
                if (buttonView.getId() == R.id.checkbox) {
                    if (isChecked) {
                        Toast.makeText(mActivity, "눌림", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(mActivity, "안눌림", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);

        switch (v.getId()) {
            case R.id.locationConfirm:
                Intent webIntent = new Intent(mActivity, WebViewActivity.class);
                webIntent.putExtra("url", "http://blog.naver.com/mothcar/220673352101");
                mActivity.startActivity(webIntent);
                break;

            case R.id.privacyConfirm:
                Intent privacyIntent = new Intent(mActivity, WebViewActivity.class);
                privacyIntent.putExtra("url", "http://blog.naver.com/mothcar/220673367007");
                mActivity.startActivity(privacyIntent);
                break;


        }
    }

    @Override
    public void loadData() {

    }

    @Override
    public void updateView() {


    }


    protected void setName(Activity activity, ChannelData channelData, String label) {
        if (!TextUtils.isEmpty(mChannel.getName())) {
            mName.setText(mChannel.getName());
        } else {
            mName.setText(label);
        }
    }


}
