package com.umanji.umanjiapp.ui.mainHome;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.umanji.umanjiapp.R;
import com.umanji.umanjiapp.helper.AuthHelper;
import com.umanji.umanjiapp.helper.Helper;
import com.umanji.umanjiapp.model.ChannelData;
import com.umanji.umanjiapp.model.ErrorData;
import com.umanji.umanjiapp.model.SubLinkData;
import com.umanji.umanjiapp.model.SuccessData;
import com.umanji.umanjiapp.model.VoteData;
import com.umanji.umanjiapp.ui.BaseFragment;
import com.umanji.umanjiapp.ui.channel._fragment.BaseChannelListAdapter;
import com.umanji.umanjiapp.ui.channel._fragment.BaseChannelListFragment;
import com.umanji.umanjiapp.ui.channel.advertise.AdsCreateActivity;
import com.umanji.umanjiapp.ui.channel.post.create.PostCreateActivity;
import com.umanji.umanjiapp.ui.channel.post.reply.ReplyListAdapter;
import com.umanji.umanjiapp.ui.channel.post.update.PostUpdateActivity;
import com.umanji.umanjiapp.ui.main.MainActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import de.greenrobot.event.EventBus;

import static com.umanji.umanjiapp.helper.FileHelper.extractUrls;


public class MainHomeFragment extends BaseFragment {
    private static final String TAG = "ReplyFragment";

    protected TextView mName;
    protected LinearLayout mLookAround;
    protected LinearLayout mCreateCommunity;


    public static MainHomeFragment newInstance(Bundle bundle) {
        MainHomeFragment fragment = new MainHomeFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public View getView(LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.activity_main_home, container, false);
    }

    @Override
    public void initWidgets(View view) {
        mLookAround = (LinearLayout) view.findViewById(R.id.community_lookaround);
        mLookAround.setOnClickListener(this);

        mCreateCommunity = (LinearLayout) view.findViewById(R.id.create_community);
        mCreateCommunity.setOnClickListener(this);

    }

    @Override
    public void loadData() {

    }


    @Override
    public void updateView() {
        if(AuthHelper.isLogin(mActivity)) {
        }else {
        }
    }

    @Override
    public void onEvent(SuccessData event) {
        super.onEvent(event);

        ChannelData channelData = new ChannelData(event.response);

        switch (event.type) {
            case api_channels_create:

                break;

            case EVENT_LOOK_AROUND:
                mActivity.finish();
                break;
        }
    }

    private AlphaAnimation buttonClick = new AlphaAnimation(0F, 1F);

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.create_community:
                Toast.makeText(mActivity, "준비중입니다 ", Toast.LENGTH_SHORT).show();
                /*Intent createInt = new Intent(mActivity, MainActivity.class);
                startActivity(createInt);*/
                break;

            case R.id.community_lookaround:
                Intent lookInt = new Intent(mActivity, MainActivity.class);
                startActivity(lookInt);
                break;

        }
    }
}
