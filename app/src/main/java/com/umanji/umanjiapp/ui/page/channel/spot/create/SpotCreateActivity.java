package com.umanji.umanjiapp.ui.page.channel.spot.create;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.umanji.umanjiapp.ui.base.BaseActivity;
import com.umanji.umanjiapp.ui.page.channel.community.create.CommunityCreateFragment;


public class SpotCreateActivity extends BaseActivity {
    private static final String TAG = "SpotCreateActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    protected Fragment createFragment() {
        return SpotCreateFragment.newInstance(getIntent().getBundleExtra("bundle"));
    }
}
