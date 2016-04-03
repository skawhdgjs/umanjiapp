package com.umanji.umanjiapp.ui.distribution;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.umanji.umanjiapp.ui.BaseActivity;


public class CommunityDistributionActivity extends BaseActivity {

    private static final String TAG = "CommunityDistributionActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    protected Fragment createFragment() {
        return CommunityDistributionFragment.newInstance(getIntent().getBundleExtra("bundle"));
    }

    @Override
    public void finish() {
        super.finish();
    }
}
