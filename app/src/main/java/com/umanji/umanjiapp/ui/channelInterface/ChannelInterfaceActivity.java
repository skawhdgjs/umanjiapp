package com.umanji.umanjiapp.ui.channelInterface;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.umanji.umanjiapp.R;
import com.umanji.umanjiapp.ui.BaseActivity;
import com.umanji.umanjiapp.ui.channel.spot.SpotFragment;


public class ChannelInterfaceActivity extends BaseActivity {
    private static final String TAG = "ChannelInterfaceActivity";

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
        return ChannelInterfaceFragment.newInstance(getIntent().getBundleExtra("bundle"));
    }

    @Override
    public void finish() {
        super.finish();
        this.overridePendingTransition(R.anim.move_base, mExitAnim);
    }
}
