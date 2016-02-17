package com.umanji.umanjiapp.ui.channel.spot.update;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.umanji.umanjiapp.ui.BaseActivity;


public class SpotUpdateActivity extends BaseActivity {
    private static final String TAG = "SpotUpdateActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    protected Fragment createFragment() {
        return SpotUpdateFragment.newInstance(getIntent().getBundleExtra("bundle"));
    }
}
