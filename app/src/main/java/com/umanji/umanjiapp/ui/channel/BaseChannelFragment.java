package com.umanji.umanjiapp.ui.channel;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.bumptech.glide.Glide;
import com.umanji.umanjiapp.R;
import com.umanji.umanjiapp.helper.AuthHelper;
import com.umanji.umanjiapp.helper.Helper;
import com.umanji.umanjiapp.model.ChannelData;
import com.umanji.umanjiapp.model.SubLinkData;
import com.umanji.umanjiapp.model.SuccessData;
import com.umanji.umanjiapp.ui.BaseFragment;
import com.umanji.umanjiapp.ui.channel.post.create.PostCreateActivity;
import com.umanji.umanjiapp.ui.modal.WebViewActivity;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.text.NumberFormat;
import java.util.ArrayList;

import de.greenrobot.event.EventBus;


public abstract class BaseChannelFragment extends BaseFragment {
    private static final String TAG = "BaseChannelFragment";

    /****************************************************
     * Intent
     ****************************************************/
    protected ChannelData mChannel;

    /****************************************************
     * View
     ****************************************************/
    private View mNoticePanel;

    protected BaseTabAdapter mAdapter;

    protected RelativeLayout mFab;

    protected TextView mName;
    protected TextView mParentName;
    protected ImageView mParentInfoCenter;
    protected TextView mHeaderBorder;
    protected ImageView mPhoto;

    protected ImageView mUserPhoto;
    protected ImageView mLookAround;
    protected ImageView mLookLink;
    protected TextView mMemberCount;
    protected TextView mPoint;
    protected TextView mLevel;
    protected LinearLayout mKeywordPanel;

    protected ViewPager mViewPager;
    protected TabLayout mTabLayout;

    protected TextView mNameType;
    protected TextView mCommentHint;

    protected ImageView mInfoBtn;


    /****************************************************
     * For Etc.
     ****************************************************/
    protected int mCurrentTapPosition = 0;
    protected String mTabType = "";


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            String jsonString = getArguments().getString("channel");
            if (jsonString != null) {
                mChannel = new ChannelData(jsonString);
            }

            mTabType = getArguments().getString("tabType");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        initTabAdapter(view);
        updateView();
        loadData();
        return view;
    }

    @Override
    public void updateView() {
        if (mChannel.getType().equals(TYPE_USER)) {
            return;
        } else {


            if (AuthHelper.isLogin(mActivity)) {
                switch (mChannel.getType()) {
                    case TYPE_SPOT:
                        mCommentHint.setText("이 장소에서 일어난 일들을 공유해 보아요.");
                        break;
                    case TYPE_COMMUNITY:
                        mCommentHint.setText("우리 단체에게 알리고 싶은 글을 공유해 주세요.");
                        break;
                    case TYPE_KEYWORD_COMMUNITY:
                        mCommentHint.setText("우리 단체에게 알리고 싶은 정보를 공유해 주세요.");
                        break;
                    case TYPE_INFO_CENTER:
                        mCommentHint.setText("요즘 우리 지역은 어떤가요?");
                        break;
                    case TYPE_SPOT_INNER:
                        mCommentHint.setText("이 장소에서 여러 사람과 공유해 보아요. ");
                        break;
                }
                mFab.setVisibility(View.VISIBLE);
            } else {
                mFab.setVisibility(View.GONE);
            }

        }


        switch (mChannel.getLevel()) {
            case LEVEL_COMPLEX:
            case LEVEL_LOCAL:
                mParentInfoCenter.setImageResource(R.drawable.elevator_up);
                break;
            case LEVEL_DONG:
                mParentInfoCenter.setImageResource(R.drawable.elevator_up);
                break;
            case LEVEL_GUGUN:
                mParentInfoCenter.setImageResource(R.drawable.elevator_up);
                break;
            case LEVEL_DOSI:
                mParentInfoCenter.setImageResource(R.drawable.elevator_up);
                break;
            default:
                mParentInfoCenter.setVisibility(View.GONE);
                break;
        }
        mProgress.hide();
    }


    @Override
    public View getView(LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.activity_channel, container, false);
    }

    @Override
    public void initWidgets(View view) {
        mNoticePanel = view.findViewById(R.id.noticePanel);

        if (mChannel.getType().equals(TYPE_USER)) {

        } else {
            mFab = (RelativeLayout) view.findViewById(R.id.fab);
            mFab.setOnClickListener(this);
        }


        mName = (TextView) view.findViewById(R.id.name);
        mName.setVisibility(View.VISIBLE);
        mName.setOnClickListener(this);

        mParentInfoCenter = (ImageView) view.findViewById(R.id.parentInfoCenter);
        mParentInfoCenter.setOnClickListener(this);

        mParentName = (TextView) view.findViewById(R.id.parentName);
        mParentName.setOnClickListener(this);

        mHeaderBorder = (TextView) view.findViewById(R.id.headerBorder);


        mPhoto = (ImageView) view.findViewById(R.id.photo);
        mUserPhoto = (ImageView) view.findViewById(R.id.userPhoto);
        mUserPhoto.setOnClickListener(this);

        mLookAround = (ImageView) view.findViewById(R.id.lookAround);
        if (mLookAround != null) {
            mLookAround.setOnClickListener(this);
        }

        mMemberCount = (TextView) view.findViewById(R.id.memberCount);
        mPoint = (TextView) view.findViewById(R.id.point);

        mKeywordPanel = (LinearLayout) view.findViewById(R.id.keywordPanel);

        mNameType = (TextView) view.findViewById(R.id.nameType);
        switch (mChannel.getType()) {
            case TYPE_COMMUNITY:
                mNameType.setText("커뮤니티 : ");
                break;
            case TYPE_KEYWORD_COMMUNITY:
                mNameType.setText("단체들 : ");
                break;
            case TYPE_INFO_CENTER:
                mNameType.setText("정보센터 : ");
                break;
            case TYPE_SPOT:
                mNameType.setText("빌딩 : ");
                break;
            case TYPE_SPOT_INNER:
                mNameType.setText("내부공간 : ");
                break;
        }

        mCommentHint = (TextView) view.findViewById(R.id.comment_hint);

        mInfoBtn = (ImageView) view.findViewById(R.id.infoButton);
        if (mInfoBtn != null) {
            mInfoBtn.setOnClickListener(this);
        }
    }

    protected void initTabAdapter(View view) {
        mViewPager = (ViewPager) view.findViewById(R.id.viewPaper);
        mTabLayout = (TabLayout) view.findViewById(R.id.tabs);
        mAdapter = new BaseTabAdapter(getActivity().getSupportFragmentManager());

        addFragmentToTabAdapter(mAdapter);

        mViewPager.setAdapter(mAdapter);
        mTabLayout.setupWithViewPager(mViewPager);

        setTabSelect();

        onTabSelected(mTabLayout);
    }

    protected void setTabSelect() {
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

    protected abstract void addFragmentToTabAdapter(BaseTabAdapter adapter);

    protected void onTabSelected(TabLayout tabLayout) {
        tabLayout.setOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager) {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                super.onTabSelected(tab);
                mCurrentTapPosition = tab.getPosition();

                switch (mCurrentTapPosition) {
                    case 0:
//                        mFab.setImageResource(R.drawable.ic_discuss);
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
    public void loadData() {
        if (!TextUtils.isEmpty(mChannel.getId())) {
            try {
                JSONObject params = new JSONObject();
                params.put("id", mChannel.getId());
                mApi.call(api_channels_get, params, new AjaxCallback<JSONObject>() {
                    @Override
                    public void callback(String url, JSONObject object, AjaxStatus status) {
                        mChannel = new ChannelData(object);
                    }
                });
                updateView();
            } catch (JSONException e) {
                Log.e(TAG, "error " + e.toString());
            }
        }
        mProgress.hide();
    }

    @Override
    public void onEvent(SuccessData event) {
        super.onEvent(event);

        switch (event.type) {
            case api_channels_id_vote:
            case api_channels_id_join:
            case api_channels_id_like:
            case api_channels_create:
                String parentId = event.response.optString("parent");
                if (TextUtils.equals(mChannel.getId(), parentId)) {
                    ChannelData channelData = new ChannelData(event.response);
                    mChannel = channelData.getParent();
                }
                updateView();
                Helper.showNoticePanel(mActivity, mNoticePanel, POINT_DEFAULT + " 포인트 증가");
                break;
            case api_channels_id_unJoin:
            case api_channels_id_unLike:
                Helper.showNoticePanel(mActivity, mNoticePanel, POINT_DEFAULT + " 포인트 감소");
            case api_channels_createKeyword:
            case api_channels_id_update:
                ChannelData channelData = new ChannelData(event.response);
                if (TextUtils.equals(mChannel.getId(), channelData.getId())) {
                    mChannel = channelData;
                }
                updateView();
                break;

            case EVENT_LOOK_AROUND:
                if (mChannel.getType() != TYPE_COMMUNITY) {
                    mActivity.finish();
                }
                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fab:
                if (mCurrentTapPosition == 0) {
                    Intent intent = new Intent(mActivity, PostCreateActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("channel", mChannel.getJsonObject().toString());
                    intent.putExtra("bundle", bundle);
                    startActivity(intent);
                }
                break;

            case R.id.lookAround:
                EventBus.getDefault().post(new SuccessData(EVENT_LOOK_AROUND, mChannel.getJsonObject()));
                break;

            case R.id.infoButton:
                switch (mChannel.getType()) {
                    case TYPE_INFO_CENTER:
                        Intent webInt = new Intent(mActivity, WebViewActivity.class);
                        webInt.putExtra("url", "http://blog.naver.com/mothcar/220720734128");
                        mActivity.startActivity(webInt);
                        break;

                    case TYPE_KEYWORD_COMMUNITY:
                        Intent webInt_com = new Intent(mActivity, WebViewActivity.class);
                        webInt_com.putExtra("url", "http://blog.naver.com/mothcar/220715638989");
                        mActivity.startActivity(webInt_com);
                        Toast.makeText(mActivity, "Type community", Toast.LENGTH_SHORT).show();
                        break;

                    default:
                        Intent webInt_nor = new Intent(mActivity, WebViewActivity.class);
                        webInt_nor.putExtra("url", "http://blog.naver.com/mothcar/220720111996");
                        mActivity.startActivity(webInt_nor);
                        Toast.makeText(mActivity, "default", Toast.LENGTH_SHORT).show();

                        // 일반 사용설명 : http://blog.naver.com/mothcar/220720111996
                        // 인포센터 설명  : http://blog.naver.com/mothcar/220720734128
                        // 키워드 커뮤니티 : http://blog.naver.com/mothcar/220715638989
                        // 커뮤티니 설명 :
                }

        }
    }


    /****************************************************
     * methods for Channel
     ****************************************************/

    protected void setName(Activity activity, ChannelData channelData, String label) {
        if (!TextUtils.isEmpty(channelData.getName())) {
            mName.setText(Helper.getShortenString(channelData.getName()));
        } else {
            mName.setText(label);
        }
    }

    protected void setUserName(Activity activity, ChannelData channelData, String label) {
        if (!TextUtils.isEmpty(channelData.getUserName())) {
            mName.setText(Helper.getShortenString(channelData.getUserName()) + " " + label);
        } else {
            mName.setText(label);
        }
    }

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

    protected void setUserPhoto(Activity activity, final ChannelData userData) {
        if (userData != null) {
            String userPhoto = userData.getPhoto();
            if (userPhoto != null) {
                Glide.with(mActivity)
                        .load(userPhoto)
                        .placeholder(R.drawable.empty)
                        .animate(R.anim.abc_fade_in)
                        .override(40, 40)
                        .into(mUserPhoto);
            }

            mUserPhoto.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Helper.startActivity(mActivity, userData);
                }
            });

        } else {
            Glide.with(mActivity)
                    .load(R.drawable.avatar_default_0)
                    .animate(R.anim.abc_fade_in)
                    .into(mUserPhoto);
        }
    }

    protected void setParentName(Activity activity, final ChannelData parentData) {
        if (parentData == null) {
            mHeaderBorder.setVisibility(View.GONE);
            mParentName.setVisibility(View.GONE);
        } else {
            mHeaderBorder.setVisibility(View.VISIBLE);
            mParentName.setVisibility(View.VISIBLE);

            if (TextUtils.isEmpty(parentData.getName())) {
                switch (mChannel.getType()) {
                    case TYPE_SPOT_INNER:
                        mParentName.setText("빌딩");
                        break;
                    default:
                        mParentName.setText("상위");
                        break;

                }
            } else {
                mParentName.setText(parentData.getName());
            }


            mParentName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Helper.startActivity(mActivity, parentData, TAB_COMMUNITIES);
                }
            });
        }
    }

    protected void setPoint(Activity activity, ChannelData channelData) {

        int userPoint = channelData.getPoint();
        String strNumber = NumberFormat.getNumberInstance().format(userPoint);
        mPoint.setText(strNumber);
        mPoint.setTextColor(Color.parseColor("#ffcc00"));

        //mPoint.setText(channelData.getPoint() + "");
    }

    /*protected void setLevel(Activity activity, ChannelData channelData) {
        mLevel.setText("Lv " + channelData.getLevel());
    }*/

    protected void setMemberCount(Activity activity, ChannelData channelData) {
        ArrayList<SubLinkData> subLinks = channelData.getSubLinks(TYPE_MEMBER);
        if (mMemberCount != null && subLinks != null) {
            mMemberCount.setText(subLinks.size() + " 명");
        }
    }

    protected void setKeywords(Activity activity, ChannelData channelData) {

        String[] keywords = channelData.getKeywords();

        if (keywords != null && keywords.length > 0) {
            if (keywords.length == 1) {
                mKeywordPanel.removeAllViews();

                TextView keywordView = (TextView) LayoutInflater.from(mActivity).inflate(R.layout.include_keyword_text, null);
                mKeywordPanel.addView(keywordView);
                keywordView.setText("#" + keywords[0]);


                try {
                    JSONObject params = new JSONObject();
                    params.put("name", keywords[0]);

                    mApi.call(api_findCommunity, params, new AjaxCallback<JSONObject>() {
                        @Override
                        public void callback(String url, JSONObject json, AjaxStatus status) {
                            final ChannelData channelData1 = new ChannelData(json);

                            mKeywordPanel.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Helper.startKeywordMapActivity(mActivity, channelData1);
                                }
                            });
                        }
                    });

                } catch (JSONException e) {
                    Log.e(TAG, "error " + e.toString());
                }

            } else if (keywords.length >= 2) {
                mKeywordPanel.removeAllViews();

                TextView keywordView = (TextView) LayoutInflater.from(mActivity).inflate(R.layout.include_keyword_text, null);
                mKeywordPanel.addView(keywordView);
                keywordView.setText("#" + keywords[0]);


                try {
                    JSONObject params = new JSONObject();
                    params.put("name", keywords[0]);

                    mApi.call(api_findCommunity, params, new AjaxCallback<JSONObject>() {
                        @Override
                        public void callback(String url, JSONObject json, AjaxStatus status) {
                            final ChannelData channelData1 = new ChannelData(json);

                            mKeywordPanel.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Helper.startKeywordMapActivity(mActivity, channelData1);
                                }
                            });
                        }
                    });

                } catch (JSONException e) {
                    Log.e(TAG, "error " + e.toString());
                }

                TextView keywordView2 = (TextView) LayoutInflater.from(mActivity).inflate(R.layout.include_keyword_text, null);
                mKeywordPanel.addView(keywordView2);
                keywordView2.setText("#" + keywords[1]);


                try {
                    JSONObject params2 = new JSONObject();
                    params2.put("name", keywords[1]);

                    mApi.call(api_findCommunity, params2, new AjaxCallback<JSONObject>() {
                        @Override
                        public void callback(String url, JSONObject json, AjaxStatus status) {
                            final ChannelData channelData2 = new ChannelData(json);

                            mKeywordPanel.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Helper.startKeywordMapActivity(mActivity, channelData2);
                                }
                            });
                        }
                    });

                } catch (JSONException e) {
                    Log.e(TAG, "error " + e.toString());
                }
            }

        } else {
            mKeywordPanel.removeAllViews();

            TextView keywordView = (TextView) LayoutInflater.from(mActivity).inflate(R.layout.include_keyword_text, null);
            mKeywordPanel.addView(keywordView);
            keywordView.setText("#키워드추가");
            keywordView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Helper.startUpdateActivity(mActivity, mChannel);
                }
            });

        }
    }

}
