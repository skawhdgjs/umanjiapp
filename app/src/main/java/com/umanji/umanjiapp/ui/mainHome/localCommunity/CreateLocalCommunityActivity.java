package com.umanji.umanjiapp.ui.mainHome.localCommunity;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.umanji.umanjiapp.ui.BaseActivity;
import com.umanji.umanjiapp.ui.mainHome.MainHomeFragment;


public class CreateLocalCommunityActivity extends BaseActivity {
    private static final String TAG = "CreateLocalCommunityActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    protected Fragment createFragment() {
        return CreateLocalCommunityFragment.newInstance(getIntent().getBundleExtra("bundle"));
    }
}
