package com.umanji.umanjiapp.ui.channel.bottomWindow;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.umanji.umanjiapp.R;

import java.util.ArrayList;
import java.util.List;


public class BottomMainActivity extends AppCompatActivity {

//    Intent intent = getIntent();

    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private String mData;;

    int mEnterAnim = 0;
    int mExitAnim = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bottom_main);

        mEnterAnim = getIntent().getIntExtra("enterAnim", R.anim.slide_in_up);
        mExitAnim = getIntent().getIntExtra("exitAnim", R.anim.slide_in_down);

        this.overridePendingTransition(mEnterAnim, R.anim.move_base);

//        overridePendingTransition( R.anim.slide_in_up, R.anim.slide_out_up );

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
//        getSupportActionBar().setTitle("지역소식");
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        getSupportActionBar().setHomeButtonEnabled(true);  //Umanji
//        getSupportActionBar().setDisplayShowTitleEnabled(true);

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
    }

    private void setupViewPager(ViewPager viewPager) {
        mData = getIntent().getExtras().getString("channels");
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        Bundle bundle = new Bundle();
        bundle.putString("channels", mData);
        adapter.addFragment(TalkFragment.newInstance(bundle), "Talk");
        adapter.addFragment(new CommunityFragment(), "Community");
        viewPager.setAdapter(adapter);
    }

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
    }

    @Override
    public void finish() {

        super.finish();
        this.overridePendingTransition(mExitAnim, R.anim.move_base);
    }
}