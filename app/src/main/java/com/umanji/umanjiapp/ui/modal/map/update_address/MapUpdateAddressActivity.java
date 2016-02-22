package com.umanji.umanjiapp.ui.modal.map.update_address;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.umanji.umanjiapp.ui.BaseActivity;
import com.umanji.umanjiapp.ui.modal.map.MapFragment;


public class MapUpdateAddressActivity extends BaseActivity {
    private static final String TAG = "MapUpdateAddressActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    protected Fragment createFragment() {
        return MapUpdateAddressFragment.newInstance(getIntent().getBundleExtra("bundle"));
    }

    @Override
    public void finish() {
        super.finish();
    }
}
