package com.umanji.umanjiapp.ui.main;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.widget.Toast;

import com.umanji.umanjiapp.R;
import com.umanji.umanjiapp.ui.BaseActivity;


public class MainActivity extends BaseActivity {

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    protected Fragment createFragment() {
        return MainFragment.newInstance(getIntent().getBundleExtra("bundle"));
    }

    @Override
    protected void onResume() {
        super.onResume();
        overridePendingTransition(R.anim.move_base, R.anim.slide_out_down);
    }

    @Override
    public void finish() {
        super.finish();
    }

    boolean doubleBackToExitPressedOnce = false;

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "한 번 더 누르시면 우만지 앱이 종료됩니다", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
            }
        }, 2000);
    }
}
