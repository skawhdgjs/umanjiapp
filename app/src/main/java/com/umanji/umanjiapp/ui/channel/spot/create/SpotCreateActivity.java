package com.umanji.umanjiapp.ui.channel.spot.create;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.umanji.umanjiapp.ui.BaseActivity;
import com.umanji.umanjiapp.ui.channel.post.create.PostCreateFragment;


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
