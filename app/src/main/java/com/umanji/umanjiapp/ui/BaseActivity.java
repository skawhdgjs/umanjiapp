package com.umanji.umanjiapp.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;

import com.umanji.umanjiapp.AppConfig;
import com.umanji.umanjiapp.R;


public abstract class BaseActivity extends AppCompatActivity implements AppConfig {
    protected abstract Fragment createFragment();

    protected int getLayoutResourceId() {
        return R.layout.activity_base;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutResourceId());
        initFragment();

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    /****************************************************
     *  Private Methods
     ****************************************************/

    private void initFragment() {
        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.abContainer);

        if (fragment == null) {
            fragment = createFragment();
            fm.beginTransaction().add(R.id.abContainer, fragment).commit();
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
