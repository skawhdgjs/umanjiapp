package com.umanji.umanjiapp.ui.page.channel.keyword.create;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.umanji.umanjiapp.ui.base.BaseActivity;
import com.umanji.umanjiapp.ui.page.channel.community.create.CommunityCreateFragment;


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
