package com.umanji.umanjiapp.ui.channel.info;

import android.app.Activity;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.bumptech.glide.Glide;
import com.umanji.umanjiapp.R;
import com.umanji.umanjiapp.helper.AuthHelper;
import com.umanji.umanjiapp.helper.Helper;
import com.umanji.umanjiapp.model.ChannelData;
import com.umanji.umanjiapp.model.SuccessData;
import com.umanji.umanjiapp.ui.channel.BaseChannelFragment;
import com.umanji.umanjiapp.ui.channel.BaseTabAdapter;
import com.umanji.umanjiapp.ui.channel._fragment.about.AboutFragment;
import com.umanji.umanjiapp.ui.channel._fragment.keywords.KeywordListFragment;
import com.umanji.umanjiapp.ui.channel._fragment.members.MemberListFragment;
import com.umanji.umanjiapp.ui.channel._fragment.posts.PostListFragment;

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
    public View getView(LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.activity_channel, container, false);
    }

    @Override
    protected void addFragmentToTabAdapter(BaseTabAdapter adapter) {
        Bundle bundle = new Bundle();
        bundle.putString("channel", mChannel.getJsonObject().toString());
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
        if (TextUtils.isEmpty(mTabType)) return;

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

        if (getArguments() != null) {
            String check = getArguments().getString("extraData");
            if (check != null) {
                if (getArguments().getString("extraData").equals("BottomList")) {
                    setUserPhoto(mActivity, mOwner);
                } else {
                    setUserPhoto(mActivity, mChannel.getOwner());
                }
            } else {
                setUserPhoto(mActivity, mChannel.getOwner());
            }
        }

        if (AuthHelper.isLogin(mActivity)) {
            mFab.setVisibility(View.VISIBLE);
        } else {
            mFab.setVisibility(View.GONE);
        }

        setName(mActivity, mChannel, "");
        setPhoto(mActivity, mChannel, R.drawable.multi_spot_background);
        setParentName(mActivity, mChannel.getParent());
        setPoint(mActivity, mChannel);
//        setLevel(mActivity, mChannel);
        setMemberCount(mActivity, mChannel);


        mKeywordPanel.setVisibility(View.GONE);
//        setKeywords(mActivity, mChannel);
    }

    @Override
    protected void setName(Activity activity, final ChannelData channelData, String label) {
        if (!TextUtils.isEmpty(channelData.getName())) {
            mName.setText(Helper.getShortenString(channelData.getName()));
        } else {
            mName.setText(label);
        }

        try {
            String userId = AuthHelper.getUserId(mActivity);

            JSONObject params = new JSONObject();
            params.put("id", userId);
            mApi.call(api_channels_get, params, new AjaxCallback<JSONObject>() {
                @Override
                public void callback(String url, JSONObject object, AjaxStatus status) {
                    final ChannelData userData = new ChannelData(object);

                    mName.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (userData.getPoint() >= 10000) {
                                Helper.startUpdateActivity(mActivity, channelData);
                            } else {
                                Toast.makeText(mActivity, "활동 포인트 10000 이상에서 수정가능합니다.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            });
        } catch (JSONException e) {
            Log.e(TAG, "error " + e.toString());
        }
    }

    private boolean hasAuthority() {
        if (TextUtils.isEmpty(AuthHelper.getLevel(mActivity))) return false;

        int loginUserLevel = Integer.parseInt(AuthHelper.getLevel(mActivity));
        if (loginUserLevel <= mChannel.getLevel()) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    protected void setPhoto(Activity activity, final ChannelData channelData, int defaultImage) {
        String photoUrl = channelData.getPhoto();
        if (photoUrl != null) {
            Glide.with(activity)
                    .load(photoUrl)
                    .into(mPhoto);

            mPhoto.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Helper.startImageViewActivity(mActivity, channelData);
                }
            });
        } else {
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

                } catch (JSONException e) {
                    Log.e(TAG, "error " + e.toString());
                }
                break;

        }
    }


}
