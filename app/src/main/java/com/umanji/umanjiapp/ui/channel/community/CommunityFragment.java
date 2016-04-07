package com.umanji.umanjiapp.ui.channel.community;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.umanji.umanjiapp.R;
import com.umanji.umanjiapp.helper.Helper;
import com.umanji.umanjiapp.model.ChannelData;
import com.umanji.umanjiapp.model.SuccessData;
import com.umanji.umanjiapp.ui.channel.BaseChannelFragment;
import com.umanji.umanjiapp.ui.channel.BaseTabAdapter;
import com.umanji.umanjiapp.ui.channel._fragment.about.AboutFragment;
import com.umanji.umanjiapp.ui.channel._fragment.communities.CommunityListFragment;
import com.umanji.umanjiapp.ui.channel._fragment.members.MemberListFragment;
import com.umanji.umanjiapp.ui.channel._fragment.posts.PostListFragment;
import com.umanji.umanjiapp.ui.distribution.CommunityDistributionActivity;

import org.json.JSONException;
import org.json.JSONObject;

import de.greenrobot.event.EventBus;

public class CommunityFragment extends BaseChannelFragment {
    private static final String TAG = "SpotFragment";

    private ChannelData mParentChannel;

    public static CommunityFragment newInstance(Bundle bundle) {
        CommunityFragment fragment = new CommunityFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);

        setParentChannel();

        return view;
    }

    @Override
    public View getView(LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.activity_community, container, false);
    }

    @Override
    protected void addFragmentToTabAdapter(BaseTabAdapter adapter) {
        Bundle bundle = new Bundle();
        bundle.putString("channel", mChannel.getJsonObject().toString());
        adapter.addFragment(PostListFragment.newInstance(bundle), "POSTS");
        adapter.addFragment(MemberListFragment.newInstance(bundle), "MEMBERS");
        adapter.addFragment(CommunityListFragment.newInstance(bundle), "COMMUNITIES");
        adapter.addFragment(AboutFragment.newInstance(bundle), "ABOUT");
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

        setName(mActivity, mChannel, "커뮤니티");
        setPhoto(mActivity, mChannel, R.drawable.community_background);

        mLookLink.setVisibility(View.VISIBLE);
        mLookLink.setOnClickListener(this);

        if(mParentChannel != null) {
            setParentName(mActivity, mParentChannel);
        }

        setUserPhoto(mActivity, mChannel.getOwner());
        setPoint(mActivity, mChannel);
        setLevel(mActivity, mChannel);
        setMemberCount(mActivity, mChannel);
        setKeywords(mActivity, mChannel);
    }

    @Override
    protected void setName(Activity activity, final ChannelData channelData, String label) {
        if(!TextUtils.isEmpty(channelData.getName())) {
            mName.setText(Helper.getShortenString(channelData.getName()));
        } else {
            mName.setText(label);
        }

        mName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Helper.startUpdateActivity(mActivity, channelData);
            }
        });
    }

    @Override
    public void loadData() {
        super.loadData();
    }

    @Override
    public void onEvent(SuccessData event) {
        super.onEvent(event);

        switch (event.type) {
            case EVENT_LOOK_AROUND:
                //mActivity.finish();
                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.parentInfoCenter:
                Log.d(TAG, "parentInfoCenter");

                try {
                    JSONObject params = new JSONObject();
                    params.put("type", TYPE_COMMUNITY);
                    params.put("name", mChannel.getName());
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
                    Log.e(TAG, "error " + e.toString());
                }
                break;

            case R.id.lookAround:
                EventBus.getDefault().post(new SuccessData(EVENT_LOOK_AROUND, mChannel.getJsonObject()));
                break;

            case R.id.lookLink:
                Intent intent = new Intent(mActivity, CommunityDistributionActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("channel", mChannel.getJsonObject().toString());
                intent.putExtra("bundle", bundle);
                startActivity(intent);
                break;
        }
    }



    private void setParentChannel() {
        if(mChannel.getLevel() >= LEVEL_COMPLEX) {
            mParentChannel = mChannel.getParent();
        }else {
            try {
                JSONObject params = new JSONObject();
                params.put("type", TYPE_INFO_CENTER);
                params.put("level", mChannel.getLevel());
                switch (mChannel.getLevel()) {
                    case LEVEL_DONG:
                        params.put("thoroughfare", mChannel.getThoroughfare());
                        params.put("locality", mChannel.getLocality());
                        params.put("adminArea", mChannel.getAdminArea());
                        params.put("countryCode", mChannel.getCountryCode());
                        break;
                    case LEVEL_GUGUN:
                        params.put("locality", mChannel.getLocality());
                        params.put("adminArea", mChannel.getAdminArea());
                        params.put("countryCode", mChannel.getCountryCode());
                        break;
                    case LEVEL_DOSI:
                        params.put("adminArea", mChannel.getAdminArea());
                        params.put("countryCode", mChannel.getCountryCode());
                        break;
                    case LEVEL_COUNTRY:
                        params.put("countryCode", mChannel.getCountryCode());
                        break;
                }

                mApi.call(api_channels_findOne, params, new AjaxCallback<JSONObject>() {
                    @Override
                    public void callback(String url, JSONObject object, AjaxStatus status) {
                        mParentChannel = new ChannelData(object);
                        updateView();
                    }
                });

            } catch(JSONException e) {
                Log.e(TAG, "error " + e.toString());
            }
        }
    }
}
