package com.umanji.umanjiapp.ui.channel.complex.update;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.umanji.umanjiapp.ui.BaseActivity;
import com.umanji.umanjiapp.ui.channel.community.update.CommunityUpdateFragment;


public class ComplexUpdateActivity extends BaseActivity {
    private static final String TAG = "ComplexUpdateActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    protected Fragment createFragment() {
        return ComplexUpdateFragment.newInstance(getIntent().getBundleExtra("bundle"));
    }
}
