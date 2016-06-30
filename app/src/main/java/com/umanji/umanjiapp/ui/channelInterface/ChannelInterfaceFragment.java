package com.umanji.umanjiapp.ui.channelInterface;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.umanji.umanjiapp.R;
import com.umanji.umanjiapp.helper.AuthHelper;
import com.umanji.umanjiapp.helper.Helper;
import com.umanji.umanjiapp.model.ChannelData;
import com.umanji.umanjiapp.model.SuccessData;
import com.umanji.umanjiapp.ui.channel.BaseTabAdapter;
import com.umanji.umanjiapp.ui.channel._fragment.about.AboutFragment;
import com.umanji.umanjiapp.ui.channel._fragment.keywords.KeywordListFragment;
import com.umanji.umanjiapp.ui.channel._fragment.members.MemberListFragment;
import com.umanji.umanjiapp.ui.channel._fragment.posts.PostListFragment;

import org.json.JSONException;
import org.json.JSONObject;

public class ChannelInterfaceFragment extends InterfaceBaseFragment {
    private static final String TAG = "ChannelInterfaceFragment";

    public static ChannelInterfaceFragment newInstance(Bundle bundle) {
        ChannelInterfaceFragment fragment = new ChannelInterfaceFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        return view;
    }

    @Override
    public View getView(LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.activity_channel_interface, container, false);
    }

    @Override
    protected void addFragmentToTabAdapter(BaseTabAdapter adapter) {
//        mChannel.setType(TYPE_INFO_CENTER);
//        mChannel.setLevel(2);
        Bundle bundle = new Bundle();

        String data = getArguments().getString("data");
        bundle.putString("channel", mChannel.getJsonObject().toString());
        bundle.putString("type", "channelInterface");
        bundle.putString("data", data);
        adapter.addFragment(PostListFragment.newInstance(bundle), "광장");
        adapter.addFragment(MemberListFragment.newInstance(bundle), "시민");
        adapter.addFragment(KeywordListFragment.newInstance(bundle), "단체 : 커뮤니티");
        adapter.addFragment(AboutFragment.newInstance(bundle), "세부정보 및 수정");
    }

    @Override
    protected void onTabSelected(TabLayout tabLayout) {
        tabLayout.setOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager) {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                super.onTabSelected(tab);
                mCurrentTapPosition = tab.getPosition();

                switch (mCurrentTapPosition) {
                    case 0:
                        if (AuthHelper.isLogin(mActivity)) {
                            mFab.setVisibility(View.VISIBLE);
                        }
                        break;
                    case 1:
                    case 2:
                    case 3:
                    case 4:
                        mFab.setVisibility(View.GONE);
                        break;

                }
            }
        });
    }


    @Override
    protected void setTabSelect() {
        if(TextUtils.isEmpty(mTabType)) return;

        TabLayout.Tab tab;
        switch (mTabType) {
            case TAB_POSTS:
                tab = mTabLayout.getTabAt(0);
                break;
            case TAB_MEMBERS:
                tab = mTabLayout.getTabAt(1);
                break;
            case TAB_COMMUNITIES:
                tab = mTabLayout.getTabAt(2);
                break;
            case TAB_ABOUT:
                tab = mTabLayout.getTabAt(3);
                break;
            default:
                tab = mTabLayout.getTabAt(0);
                break;
        }

        tab.select();
    }

    @Override
    public void updateView() {
        super.updateView();

        if(AuthHelper.isLogin(mActivity)) {
            mFab.setVisibility(View.VISIBLE);
        } else {
            mFab.setVisibility(View.GONE);
        }
    }

    @Override
    public void loadData() {
        super.loadData();
    }

    @Override
    public void onEvent(SuccessData event) {
        super.onEvent(event);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);

        switch (v.getId()) {
            case R.id.parentInfoCenter:

                try {
                    JSONObject params = new JSONObject();
                    params.put("type", TYPE_INFO_CENTER);
                    switch (mChannel.getLevel()) {
                        case LEVEL_COMPLEX:
                        case LEVEL_LOCAL:
                            params.put("level", LEVEL_DONG);
                            params.put("thoroughfare", mChannel.getThoroughfare());
                            params.put("locality", mChannel.getLocality());
                            params.put("adminArea", mChannel.getAdminArea());
                            params.put("countryCode", mChannel.getCountryCode());
                            break;
                        case LEVEL_DONG:
                            params.put("level", LEVEL_GUGUN);
                            params.put("locality", mChannel.getLocality());
                            params.put("adminArea", mChannel.getAdminArea());
                            params.put("countryCode", mChannel.getCountryCode());
                            break;
                        case LEVEL_GUGUN:
                            params.put("level", LEVEL_DOSI);
                            params.put("adminArea", mChannel.getAdminArea());
                            params.put("countryCode", mChannel.getCountryCode());
                            break;
                        case LEVEL_DOSI:
                            params.put("level", LEVEL_COUNTRY);
                            params.put("countryCode", mChannel.getCountryCode());
                            break;
                    }

                    mApi.call(api_channels_findOne, params, new AjaxCallback<JSONObject>() {
                        @Override
                        public void callback(String url, JSONObject object, AjaxStatus status) {
                            ChannelData channelData = new ChannelData(object);
                            Helper.startActivity(mActivity, channelData);
                        }
                    });

                } catch(JSONException e) {
                }
                break;

        }
    }


}
