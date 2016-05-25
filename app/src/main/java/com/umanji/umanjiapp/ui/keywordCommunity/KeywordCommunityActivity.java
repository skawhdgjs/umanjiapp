package com.umanji.umanjiapp.ui.keywordCommunity;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.umanji.umanjiapp.R;
import com.umanji.umanjiapp.ui.BaseActivity;
import com.umanji.umanjiapp.ui.main.MainFragment;


public class KeywordCommunityActivity extends BaseActivity {

    private static final String TAG = "KeywordCommunityActivity";

    int mEnterAnim = 0;
    int mExitAnim = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mEnterAnim = getIntent().getIntExtra("enterAnim", R.anim.slide_in_right);
        mExitAnim = getIntent().getIntExtra("exitAnim", R.anim.slide_out_right);

        this.overridePendingTransition(mEnterAnim, R.anim.move_back);
    }

    protected Fragment createFragment() {
        return KeywordCommunityFragment.newInstance(getIntent().getBundleExtra("bundle"));
    }

    @Override
    public void finish() {
        super.finish();

        this.overridePendingTransition(R.anim.move_base, mExitAnim);
    }
}
