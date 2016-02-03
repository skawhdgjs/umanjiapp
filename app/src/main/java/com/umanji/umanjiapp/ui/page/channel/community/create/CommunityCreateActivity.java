package com.umanji.umanjiapp.ui.page.channel.community.create;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.umanji.umanjiapp.ui.base.BaseActivity;
import com.umanji.umanjiapp.ui.page.channel.post.create.PostCreateFragment;


public class CommunityCreateActivity extends BaseActivity {
    private static final String TAG = "CommunityCreateActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    protected Fragment createFragment() {
        return CommunityCreateFragment.newInstance(getIntent().getBundleExtra("bundle"));
    }
}
