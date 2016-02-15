package com.umanji.umanjiapp.ui.channel.community.create;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.umanji.umanjiapp.ui.BaseActivity;
import com.umanji.umanjiapp.ui.channel.spot.create.SpotCreateFragment;


public class CommunityCreateActivity extends BaseActivity {
    private static final String TAG = "CommunityCreateActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    protected Fragment createFragment() {
        return CommunityCreateFragment.newInstance(getIntent().getBundleExtra("bundle"));
    }
}
