package com.umanji.umanjiapp.ui.channel.post.reply;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.umanji.umanjiapp.ui.BaseActivity;
import com.umanji.umanjiapp.ui.channel.spot.create.SpotCreateFragment;


public class ReplyActivity extends BaseActivity {
    private static final String TAG = "ReplyActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    protected Fragment createFragment() {
        return ReplyFragment.newInstance(getIntent().getBundleExtra("bundle"));
    }
}
