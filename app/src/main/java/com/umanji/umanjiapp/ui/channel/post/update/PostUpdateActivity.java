package com.umanji.umanjiapp.ui.channel.post.update;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.umanji.umanjiapp.ui.BaseActivity;
import com.umanji.umanjiapp.ui.channel.spot.update.SpotUpdateFragment;


public class PostUpdateActivity extends BaseActivity {
    private static final String TAG = "PostUpdateActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    protected Fragment createFragment() {
        return PostUpdateFragment.newInstance(getIntent().getBundleExtra("bundle"));
    }
}
