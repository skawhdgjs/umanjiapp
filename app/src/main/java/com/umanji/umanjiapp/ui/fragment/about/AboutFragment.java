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
import com.umanji.umanjiapp.helper.UiHelper;
import com.umanji.umanjiapp.ui.base.BaseChannelListAdapter;
import com.umanji.umanjiapp.ui.base.BaseChannelListFragment;
import com.umanji.umanjiapp.ui.fragment.community.CommunityListAdapter;
import com.umanji.umanjiapp.ui.page.channel.community.CommunityActivity;
import com.umanji.umanjiapp.ui.page.channel.community.create.CommunityCreateActivity;
import com.umanji.umanjiapp.ui.page.channel.keyword.create.KeywordCreateActivity;

public class AboutFragment extends BaseChannelListFragment {
    private static final String TAG = "AboutFragment";

    /****************************************************
     *  View
     ****************************************************/
    protected Button mCreateKeywordBtn;
    protected Button mDongBtn;
    protected Button mGuBtn;
    protected Button mCityBtn;

    protected TextView mAddress;

    public static AboutFragment newInstance(Bundle bundle) {
        AboutFragment fragment = new AboutFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mCreateApiName = api_links_createKeyword;
        mListApiName = api_channels_keywords_find;
        mType   = TYPE_KEYWORD;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        LinearLayout view = (LinearLayout) inflater.inflate(R.layout.fragment_about, container, false);
        RecyclerView rView = (RecyclerView)view.findViewById(R.id.recyclerView);
        super.onCreateView(rView);

        mCreateKeywordBtn = (Button) view.findViewById(R.id.createKeywordBtn);
        mCreateKeywordBtn.setOnClickListener(this);

        mCityBtn = (Button) view.findViewById(R.id.cityBtn);
        mCityBtn.setOnClickListener(this);
        mGuBtn = (Button) view.findViewById(R.id.guBtn);
        mGuBtn.setOnClickListener(this);
        mDongBtn = (Button) view.findViewById(R.id.dongBtn);
        mDongBtn.setOnClickListener(this);

        mAddress = (TextView) view.findViewById(R.id.address);

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
            case R.id.createKeywordBtn:
                Intent intent = new Intent(mContext, KeywordCreateActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("channel", mChannel.getJsonObject().toString());
                bundle.putString("type", mType);
                intent.putExtra("bundle", bundle);

                startActivityForResult(intent, UiHelper.CODE_ABOUT_FRAGMENT);
                break;
        }
    }

    @Override
    public void updateView() {
        super.updateView();

        mAddress.setText(mChannel.getCountryName() + " " + mChannel.getAdminArea() + " " + mChannel.getLocality() + " " + mChannel.getThoroughfare() + " " + mChannel.getFeatureName());

        switch (mLevel) {
            case LEVEL_LOCAL:
                mCreateKeywordBtn.setVisibility(View.VISIBLE);
                break;
            default:
                mCreateKeywordBtn.setVisibility(View.GONE);
                break;
        }

    }

}
