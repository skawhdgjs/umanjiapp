package com.umanji.umanjiapp.ui;


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
import android.view.animation.AlphaAnimation;

import com.umanji.umanjiapp.AppConfig;
import com.umanji.umanjiapp.R;
import com.umanji.umanjiapp.helper.ApiHelper;
import com.umanji.umanjiapp.model.SuccessData;

import de.greenrobot.event.EventBus;

public abstract class BaseFragment extends Fragment implements View.OnClickListener, AppConfig {

    /****************************************************
     *  View
     ****************************************************/
    protected BaseActivity      mActivity;
    protected ProgressDialog    mProgress;

    /****************************************************
     *  Api
     ****************************************************/
    public ApiHelper mApi;

    /****************************************************
     *  action
     ****************************************************/
    public AlphaAnimation buttonClick = new AlphaAnimation(0F, 1F);
    public AlertDialog.Builder mAlert;
    public boolean mClicked = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = (BaseActivity)getActivity();
        EventBus.getDefault().register(this);
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = getView(inflater, container);

        final Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        if (toolbar != null) {
            mActivity.setSupportActionBar(toolbar);
            mActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            mActivity.getSupportActionBar().setDisplayShowHomeEnabled(true);
            mActivity.getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        CollapsingToolbarLayout collapsingToolbar =
                (CollapsingToolbarLayout) view.findViewById(R.id.collapsingToolbar);

        if (collapsingToolbar != null) {
            collapsingToolbar.setTitleEnabled(false);
        }


        mProgress = new ProgressDialog(getActivity());
        mProgress.setMessage("잠시만 기다려주세요...");
//        mProgress.setTitle("Connecting server");
        mProgress.setCancelable(true);

        mAlert = new AlertDialog.Builder(mActivity);

        mApi = new ApiHelper(mActivity);

        initWidgets(view);
        return view;
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

    public abstract View getView(LayoutInflater inflater, ViewGroup container);

    public abstract void initWidgets(View view);
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
