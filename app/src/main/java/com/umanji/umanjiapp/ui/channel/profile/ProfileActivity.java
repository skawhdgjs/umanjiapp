package com.umanji.umanjiapp.ui.channel.profile;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.umanji.umanjiapp.R;
import com.umanji.umanjiapp.ui.BaseActivity;


public class ProfileActivity extends BaseActivity {
    private static final String TAG = "ProfileActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.overridePendingTransition(R.anim.slide_in_left, R.anim.move_back);
    }

    protected Fragment createFragment() {
        return ProfileFragment.newInstance(getIntent().getBundleExtra("bundle"));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void finish() {
        super.finish();
        this.overridePendingTransition(R.anim.move_base, R.anim.slide_out_left);
    }
}
