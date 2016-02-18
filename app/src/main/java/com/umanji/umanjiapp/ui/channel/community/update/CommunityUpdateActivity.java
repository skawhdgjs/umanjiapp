package com.umanji.umanjiapp.ui.channel.community.update;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.umanji.umanjiapp.ui.BaseActivity;
import com.umanji.umanjiapp.ui.channel.spot.update.SpotUpdateFragment;


public class CommunityUpdateActivity extends BaseActivity {
    private static final String TAG = "CommunityUpdateActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    protected Fragment createFragment() {
        return CommunityUpdateFragment.newInstance(getIntent().getBundleExtra("bundle"));
    }
}
