package com.umanji.umanjiapp.ui.channel.bottomWindow;

import android.media.Image;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.umanji.umanjiapp.AppConfig;
import com.umanji.umanjiapp.R;
import com.umanji.umanjiapp.helper.ApiHelper;
import com.umanji.umanjiapp.model.ChannelData;
import com.umanji.umanjiapp.model.ErrorData;
import com.umanji.umanjiapp.model.PaulBusData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;


public class BottomMainActivity extends AppCompatActivity{

    private static final String TAG = "BottomMainActivity";

//    Intent intent = getIntent();

    private Toolbar toolbar;
    private TextView mToolbarTitle;

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private String mData;
    private String mParams;
    private JSONObject mParamsObj;

    private String thisType;
    private String paramType;
    private String currentTitle;
    private String keywordName;
    private ImageView mBottomCloseBtn;
    private String zoom;

    private AlphaAnimation buttonClick = new AlphaAnimation(0F, 1F);

    int mEnterAnim = 0;
    int mExitAnim = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bottom_main);

//        EventBus.getDefault().register(this);
        mParams = getIntent().getStringExtra("params");
        thisType = getIntent().getStringExtra("type");
        currentTitle = getIntent().getStringExtra("currentAddress");
        keywordName = getIntent().getStringExtra("keywordName");
        if(getIntent().getStringExtra("zoom") != null){
            zoom = getIntent().getStringExtra("zoom");
        }

        try {
            mParamsObj = new JSONObject(mParams);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            paramType = mParamsObj.getString("type");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (thisType.equals("talkMode")){
            thisType = "talk";
        } else {
            thisType = "KeywordChannelMode";
        }

        mEnterAnim = getIntent().getIntExtra("enterAnim", R.anim.slide_in_up);
        mExitAnim = getIntent().getIntExtra("exitAnim", R.anim.slide_in_down);

        this.overridePendingTransition(mEnterAnim, R.anim.move_base);

        mToolbarTitle = (TextView) findViewById(R.id.toolbar_title);
        mToolbarTitle.setText(currentTitle);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager, thisType);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        mBottomCloseBtn = (ImageView) findViewById(R.id.bottom_out_btn);
        mBottomCloseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBottomCloseBtn.startAnimation(buttonClick);
                buttonClick.setDuration(500);
                finish();
            }
        });
    }


    private void setupViewPager(ViewPager viewPager, String thisType) {
//        mData = getIntent().getExtras().getString("channels");
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        Bundle bundle = new Bundle();
//        bundle.putString("channels", mData);
        Log.d("Tester_2",mParamsObj.toString());
        bundle.putString("params", mParams);
        bundle.putString("thisType", thisType);
        if(thisType.equals("keywordCommunity")){
            bundle.putString("keywordName", keywordName);
            bundle.putString("zoom", zoom);
        } else {
            bundle.putString("keywordName", keywordName);
        }

        adapter.addFragment(TalkFragment.newInstance(bundle), "Talk");
        adapter.addFragment(CommunityFragment.newInstance(bundle), "Community");
        if (thisType.equals("keywordCommunity")){
            adapter.addFragment(ProfessionalFragment.newInstance(bundle), "professional");
        }
//        adapter.addFragment(new CommunityFragment(), "Community");
        viewPager.setAdapter(adapter);
    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    public void finish() {

        super.finish();
        this.overridePendingTransition(mExitAnim, R.anim.move_base);
    }
/*

    public void onEvent(PaulBusData event) {
        String type = event.type;
        mParams = event.response;
        Log.d("Paul", type + " " + mParams);
    }
*/

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            super.destroyItem(container, position, object);
        }
    }


}