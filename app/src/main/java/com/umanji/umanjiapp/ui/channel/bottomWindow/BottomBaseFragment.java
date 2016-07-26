package com.umanji.umanjiapp.ui.channel.bottomWindow;


import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.umanji.umanjiapp.AppConfig;
import com.umanji.umanjiapp.R;
import com.umanji.umanjiapp.helper.ApiHelper;
import com.umanji.umanjiapp.model.SuccessData;
import com.umanji.umanjiapp.ui.BaseActivity;

import de.greenrobot.event.EventBus;

public abstract class BottomBaseFragment extends Fragment implements View.OnClickListener, AppConfig {

    /****************************************************
     *  View
     ****************************************************/
    protected BaseActivity      mActivity;
    protected ProgressDialog    mProgress;

    /****************************************************
     *  Api
     ****************************************************/
    public ApiHelper mApi;

    public AlertDialog.Builder mAlert;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
    }


    @Override
     public void onDestroy() {
        super.onDestroy();

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBus.getDefault().unregister(this);
    }

    public abstract void loadData();
    public abstract void updateView();

    public void onEvent(SuccessData event) {
        switch (event.type) {
            case EVENT_UPDATEVIEW:
                updateView();
                break;
        }
    };

    @Override
    public void onClick(View v) {}
}
