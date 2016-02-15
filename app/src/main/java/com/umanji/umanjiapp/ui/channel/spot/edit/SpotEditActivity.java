package com.umanji.umanjiapp.ui.channel.spot.edit;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.umanji.umanjiapp.ui.BaseActivity;
import com.umanji.umanjiapp.ui.channel.spot.create.SpotCreateFragment;


public class SpotEditActivity extends BaseActivity {
    private static final String TAG = "SpotEditActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    protected Fragment createFragment() {
        return SpotEditFragment.newInstance(getIntent().getBundleExtra("bundle"));
    }
}
