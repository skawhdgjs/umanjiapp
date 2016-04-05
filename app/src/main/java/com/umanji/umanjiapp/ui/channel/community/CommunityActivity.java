package com.umanji.umanjiapp.ui.channel.community;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.umanji.umanjiapp.ui.BaseActivity;

public class CommunityActivity extends BaseActivity {
    private static final String TAG = "CommunityActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //this.overridePendingTransition(R.anim.slide_in_right, R.anim.move_back);
    }

    protected Fragment createFragment() {
        return CommunityFragment.newInstance(getIntent().getBundleExtra("bundle"));
    }

    @Override
    public void finish() {
        super.finish();
        //this.overridePendingTransition(R.anim.move_base, R.anim.slide_out_right);
    }
}
