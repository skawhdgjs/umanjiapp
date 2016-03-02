package com.umanji.umanjiapp.ui.channel.info;

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
import com.bumptech.glide.Glide;
import com.umanji.umanjiapp.R;
import com.umanji.umanjiapp.helper.AuthHelper;
import com.umanji.umanjiapp.helper.Helper;
import com.umanji.umanjiapp.model.ChannelData;
import com.umanji.umanjiapp.model.SuccessData;
import com.umanji.umanjiapp.ui.channel.BaseTabAdapter;
import com.umanji.umanjiapp.ui.channel.BaseChannelFragment;
import com.umanji.umanjiapp.ui.channel._fragment.about.AboutFragment;
import com.umanji.umanjiapp.ui.channel._fragment.communities.CommunityListFragment;
import com.umanji.umanjiapp.ui.channel._fragment.members.MemberListFragment;
import com.umanji.umanjiapp.ui.channel._fragment.posts.PostListFragment;
import com.umanji.umanjiapp.ui.channel.post.create.PostCreateActivity;

import org.json.JSONException;
import org.json.JSONObject;

public class InfoFragment extends BaseChannelFragment {
    private static final String TAG = "InfoFragment";

    public static InfoFragment newInstance(Bundle bundle) {
        InfoFragment fragment = new InfoFragment();
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
    public void initWidgets(View view) {
        super.initWidgets(view);
    }

    @Override
    public View getView(LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.activity_channel, container, false);
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
    protected void onTabSelected(TabLayout tabLayout) {
        tabLayout.setOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager) {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                super.onTabSelected(tab);
                mCurrentTapPosition = tab.getPosition();

                switch (mCurrentTapPosition) {
                    case 0:
                        if(AuthHelper.isLogin(mActivity)) {
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


        setName(mActivity, mChannel, "");
        setPhoto(mActivity, mChannel, R.drawable.multi_spot_background);
        setParentName(mActivity, mChannel.getParent());
        setUserPhoto(mActivity, mChannel.getOwner());
        setPoint(mActivity, mChannel);
        setLevel(mActivity, mChannel);
        setMemberCount(mActivity, mChannel);
        setKeywords(mActivity, mChannel);
    }

    private boolean hasAuthority() {
        if(TextUtils.isEmpty(AuthHelper.getLevel(mActivity))) return false;

        int loginUserLevel = Integer.parseInt(AuthHelper.getLevel(mActivity));
        if(loginUserLevel <= mChannel.getLevel()) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    protected void setPhoto(Activity activity, final ChannelData channelData, int defaultImage) {
        String photoUrl = channelData.getPhoto();
        if(photoUrl != null) {
            Glide.with(activity)
                    .load(photoUrl)
                    .into(mPhoto);

            mPhoto.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Helper.startImageViewActivity(mActivity, channelData);
                }
            });
        }else {
            Glide.with(activity)
                    .load(defaultImage)
                    .into(mPhoto);
        }
    }

    @Override
    public void loadData() {

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
