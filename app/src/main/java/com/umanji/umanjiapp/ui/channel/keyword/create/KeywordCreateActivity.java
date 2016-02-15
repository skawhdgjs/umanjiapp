package com.umanji.umanjiapp.ui.channel.keyword.create;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.umanji.umanjiapp.ui.BaseActivity;
import com.umanji.umanjiapp.ui.channel.community.create.CommunityCreateFragment;


public class KeywordCreateActivity extends BaseActivity {
    private static final String TAG = "KeywordCreateActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    protected Fragment createFragment() {
        return KeywordCreateFragment.newInstance(getIntent().getBundleExtra("bundle"));
    }
}
