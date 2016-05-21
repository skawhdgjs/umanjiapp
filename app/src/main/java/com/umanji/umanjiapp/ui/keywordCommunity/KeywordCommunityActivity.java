package com.umanji.umanjiapp.ui.keywordCommunity;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.umanji.umanjiapp.ui.BaseActivity;
import com.umanji.umanjiapp.ui.main.MainFragment;


public class KeywordCommunityActivity extends BaseActivity {

    private static final String TAG = "KeywordCommunityActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    protected Fragment createFragment() {
        return KeywordCommunityFragment.newInstance(getIntent().getBundleExtra("bundle"));
    }

    @Override
    public void finish() {
        super.finish();
    }
}
