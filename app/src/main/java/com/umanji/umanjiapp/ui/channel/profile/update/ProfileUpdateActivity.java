package com.umanji.umanjiapp.ui.channel.profile.update;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.umanji.umanjiapp.ui.BaseActivity;
import com.umanji.umanjiapp.ui.channel.community.update.CommunityUpdateFragment;


public class ProfileUpdateActivity extends BaseActivity {
    private static final String TAG = "ProfileUpdateActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    protected Fragment createFragment() {
        return ProfileUpdateFragment.newInstance(getIntent().getBundleExtra("bundle"));
    }
}