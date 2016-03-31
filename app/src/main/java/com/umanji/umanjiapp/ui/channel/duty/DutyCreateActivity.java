package com.umanji.umanjiapp.ui.channel.duty;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.umanji.umanjiapp.ui.BaseActivity;


public class DutyCreateActivity extends BaseActivity {
    private static final String TAG = "DutyCreateActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    protected Fragment createFragment() {
        return DutyCreateFragment.newInstance(getIntent().getBundleExtra("bundle"));
    }
}
