package com.umanji.umanjiapp.ui.mainHome.localCommunity;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.umanji.umanjiapp.ui.BaseActivity;


public class StepTwoActivity extends BaseActivity {
    private static final String TAG = "StepTwoActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    protected Fragment createFragment() {
        return StepTwoFragment.newInstance(getIntent().getBundleExtra("bundle"));
    }
}
