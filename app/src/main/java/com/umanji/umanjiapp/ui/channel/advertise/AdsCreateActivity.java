package com.umanji.umanjiapp.ui.channel.advertise;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.umanji.umanjiapp.ui.BaseActivity;


public class AdsCreateActivity extends BaseActivity {
    private static final String TAG = "AdsCreateActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    protected Fragment createFragment() {
        return AdsCreateFragment.newInstance(getIntent().getBundleExtra("bundle"));
    }
}
