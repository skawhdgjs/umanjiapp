package com.umanji.umanjiapp.ui.page.channel.spot;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.umanji.umanjiapp.R;
import com.umanji.umanjiapp.ui.base.BaseActivity;


public class SpotActivity extends BaseActivity {
    private static final String TAG = "SpotActivity";

    int mEnterAnim = 0;
    int mExitAnim = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mEnterAnim = getIntent().getIntExtra("enterAnim", R.anim.slide_in_right);
        mExitAnim = getIntent().getIntExtra("exitAnim", R.anim.move_back);

        this.overridePendingTransition(mEnterAnim, R.anim.move_back);


    }

    protected Fragment createFragment() {
        return SpotFragment.newInstance(getIntent().getBundleExtra("bundle"));
    }

    @Override
    public void finish() {
        super.finish();
        this.overridePendingTransition(R.anim.move_base, mExitAnim);
    }
}
