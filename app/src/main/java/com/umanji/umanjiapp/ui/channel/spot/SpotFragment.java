package com.umanji.umanjiapp.ui.channel.spot;

import android.app.Activity;
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
import com.umanji.umanjiapp.ui.channel._fragment.spots.SpotListFragment;

import org.json.JSONException;
import org.json.JSONObject;

public class SpotFragment extends BaseChannelFragment {
    private static final String TAG = "SpotFragment";

    public static SpotFragment newInstance(Bundle bundle) {
        SpotFragment fragment = new SpotFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);

        if(TextUtils.isEmpty(mChannel.getName())) {
            TabLayout.Tab tab = mTabLayout.getTabAt(2);
            tab.select();
        }


        if(TextUtils.equals(mChannel.getType(), TYPE_SPOT_INNER)) {
            if(mChannel.getSubLinks(TYPE_MEMBER) != null) {
                TabLayout.Tab tabMember = mTabLayout.getTabAt(1);
                tabMember.setText("멤버 (" + mChannel.getSubLinks(TYPE_MEMBER).size() + ")");
            }

            if(mChannel.getSubLinks(TYPE_COMMUNITY) != null) {
                TabLayout.Tab tabSpot = mTabLayout.getTabAt(2);
                tabSpot.setText("커뮤니티 (" + mChannel.getSubLinks(TYPE_COMMUNITY).size() + ")");
            }

        } else {
            if(mChannel.getSubLinks(TYPE_MEMBER) != null) {
                TabLayout.Tab tabMember = mTabLayout.getTabAt(1);
                tabMember.setText("멤버 (" + mChannel.getSubLinks(TYPE_MEMBER).size() + ")");
            }

            if(mChannel.getSubLinks(TYPE_SPOT_INNER) != null) {
                TabLayout.Tab tabSpot = mTabLayout.getTabAt(2);
                tabSpot.setText("장소 (" + mChannel.getSubLinks(TYPE_SPOT_INNER).size() + ")");
            }

            if(mChannel.getSubLinks(TYPE_COMMUNITY) != null) {
                TabLayout.Tab tabCommunity = mTabLayout.getTabAt(3);
                tabCommunity.setText("커뮤니티 (" + mChannel.getSubLinks(TYPE_COMMUNITY).size() + ")");
            }
        }
        return view;
    }

    @Override
    public View getView(LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.activity_channel, container, false);
    }

    @Override
    protected void addFragmentToTabAdapter(BaseTabAdapter adapter) {
        Bundle bundle = new Bundle();
        if(mChannel.getType().equals(TYPE_SPOT_INNER)){
            bundle.putString("channel", mChannel.getJsonObject().toString());
            adapter.addFragment(PostListFragment.newInstance(bundle), "광장");
            adapter.addFragment(MemberListFragment.newInstance(bundle), "MEMBERS");
            adapter.addFragment(CommunityListFragment.newInstance(bundle), "COMMUNITIES");
            adapter.addFragment(AboutFragment.newInstance(bundle), "ABOUT");
        } else {
            bundle.putString("channel", mChannel.getJsonObject().toString());
            adapter.addFragment(PostListFragment.newInstance(bundle), "광장");
            adapter.addFragment(MemberListFragment.newInstance(bundle), "MEMBERS");
            adapter.addFragment(SpotListFragment.newInstance(bundle), "SPOTS");
            adapter.addFragment(CommunityListFragment.newInstance(bundle), "COMMUNITIES");
            adapter.addFragment(AboutFragment.newInstance(bundle), "ABOUT");
        }

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
            case TAB_SPOTS:
                tab = mTabLayout.getTabAt(2);
                break;
            case TAB_COMMUNITIES:
                tab = mTabLayout.getTabAt(3);
                break;
            case TAB_ABOUT:
                tab = mTabLayout.getTabAt(4);
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

        if(TextUtils.equals(mChannel.getType(), TYPE_SPOT)) {
            setName(mActivity, mChannel, "스팟");
        } else {
            setName(mActivity, mChannel, "내부스팟");
        }

        setPhoto(mActivity, mChannel, R.drawable.multi_spot_background);
        if(mChannel.getType().equals(TYPE_SPOT_INNER)){
            setPhoto(mActivity, mChannel, R.drawable.spot_background);
        }
        setParentName(mActivity, mChannel.getParent());

        setUserPhoto(mActivity, mChannel.getOwner());
        setPoint(mActivity, mChannel);
//        setLevel(mActivity, mChannel);
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
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);

        switch (v.getId()) {
            case R.id.parentInfoCenter:
                Log.d(TAG, "parentInfoCenter");

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
                    Log.e(TAG, "error " + e.toString());
                }
                break;

        }
    }


}
