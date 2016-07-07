package com.umanji.umanjiapp.ui.channelInterface;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.umanji.umanjiapp.R;
import com.umanji.umanjiapp.ui.BaseActivity;


public class ChannelInterfaceActivity extends BaseActivity {
    private static final String TAG = "ChannelInterfaceActivity";

    int mEnterAnim = 0;
    int mExitAnim = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.overridePendingTransition( R.anim.slide_in_up, R.anim.slide_out_up);
    }

    @Override
    protected void onResume() {
        super.onResume();
        overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up);
    }

    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(R.anim.slide_in_down, R.anim.slide_out_down);
    }

    protected Fragment createFragment() {
        return ChannelInterfaceFragment.newInstance(getIntent().getBundleExtra("bundle"));
    }

    @Override
    public void finish() {
        super.finish();
        this.overridePendingTransition( R.anim.slide_in_up, R.anim.slide_out_up);
    }
}
