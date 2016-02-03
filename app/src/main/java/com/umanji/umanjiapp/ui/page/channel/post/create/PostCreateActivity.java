package com.umanji.umanjiapp.ui.page.channel.post.create;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.umanji.umanjiapp.ui.base.BaseActivity;


public class PostCreateActivity extends BaseActivity {
    private static final String TAG = "PostCreateActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    protected Fragment createFragment() {
        return PostCreateFragment.newInstance(getIntent().getBundleExtra("bundle"));
    }
}
