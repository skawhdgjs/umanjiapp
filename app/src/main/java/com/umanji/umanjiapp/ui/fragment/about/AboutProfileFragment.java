package com.umanji.umanjiapp.ui.fragment.about;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.umanji.umanjiapp.R;
import com.umanji.umanjiapp.helper.AuthHelper;
import com.umanji.umanjiapp.helper.UiHelper;
import com.umanji.umanjiapp.model.ChannelData;
import com.umanji.umanjiapp.model.SuccessData;
import com.umanji.umanjiapp.ui.base.BaseChannelListAdapter;
import com.umanji.umanjiapp.ui.base.BaseChannelListFragment;
import com.umanji.umanjiapp.ui.base.BaseFragment;
import com.umanji.umanjiapp.ui.page.channel.community.create.CommunityCreateActivity;
import com.umanji.umanjiapp.ui.page.channel.spot.home.HomeListActivity;
import com.umanji.umanjiapp.ui.page.map.MapActivity;

import org.json.JSONObject;

public class AboutProfileFragment extends BaseChannelListFragment {
    private static final String TAG = "AboutProfileFragment";

    /****************************************************
     *  View
     ****************************************************/
    protected Button mLogoutBtn;
    protected TextView mAddress;

    private Button addHomeBtn;

    public static AboutProfileFragment newInstance(Bundle bundle) {
        AboutProfileFragment fragment = new AboutProfileFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mCreateApiName = api_links_createCommunity;
        mListApiName = api_channels_keywords_find;
        mType   = TYPE_KEYWORD;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        LinearLayout view = (LinearLayout) inflater.inflate(R.layout.fragment_about_profile, container, false);
        RecyclerView rView = (RecyclerView)view.findViewById(R.id.recyclerView);
        super.onCreateView(rView);


        mLogoutBtn = (Button) view.findViewById(R.id.logoutBtn);
        mLogoutBtn.setOnClickListener(this);
        mAddress = (TextView) view.findViewById(R.id.address);

        addHomeBtn = (Button)view.findViewById(R.id.addHomeBtn);
        addHomeBtn.setOnClickListener(this);

        return view;
    }

    @Override
    public BaseChannelListAdapter getListAdapter() {
        return new KeywordListAdapter(getActivity(), this);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);

        switch (v.getId()) {
            case R.id.logoutBtn:
                JSONObject params = new JSONObject();
                mApiHelper.call(api_logout, params);
                mActivity.finish();
                break;
            case R.id.addHomeBtn:

                Intent homeIntent = new Intent(mContext, HomeListActivity.class);

                Bundle homeBundle = new Bundle();
                homeBundle.putString("channel", mChannel.getJsonObject().toString());
                homeIntent.putExtra("bundle", homeBundle);

                startActivity(homeIntent);

                break;
        }
    }

    @Override
    public void updateView() {
        super.updateView();
        mAddress.setText(mChannel.getCountryName() + " " + mChannel.getAdminArea() + " " + mChannel.getLocality() + " " + mChannel.getThoroughfare() + " " + mChannel.getFeatureName());
    }

    @Override
    public void onEvent(SuccessData event) {
        super.onEvent(event);

        switch (event.type) {
            case api_channels_id_update:
                mChannel = new ChannelData(event.response);
                updateView();
                break;
        }
    }
}
