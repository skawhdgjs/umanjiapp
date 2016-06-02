package com.umanji.umanjiapp.ui.mainHome;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.umanji.umanjiapp.ui.BaseActivity;
import com.umanji.umanjiapp.ui.channel.post.reply.ReplyFragment;


public class MainHomeActivity extends BaseActivity {
    private static final String TAG = "MainHomeActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    protected Fragment createFragment() {
        return MainHomeFragment.newInstance(getIntent().getBundleExtra("bundle"));
    }
}
