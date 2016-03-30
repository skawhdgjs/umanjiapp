package com.umanji.umanjiapp.ui.channel.appointment;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.umanji.umanjiapp.ui.BaseActivity;
import com.umanji.umanjiapp.ui.channel.keyword.create.KeywordCreateFragment;


public class AppointCreateActivity extends BaseActivity {
    private static final String TAG = "AppointCreateActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    protected Fragment createFragment() {
        return AppointCreateFragment.newInstance(getIntent().getBundleExtra("bundle"));
    }
}
