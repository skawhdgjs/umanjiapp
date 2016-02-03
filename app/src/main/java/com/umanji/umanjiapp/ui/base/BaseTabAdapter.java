package com.umanji.umanjiapp.ui.base;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;

public class BaseTabAdapter extends FragmentPagerAdapter {
    private static final String TAG = "BaseTabAdapter";


    protected final ArrayList<Fragment> mFragments = new ArrayList<>();
    protected final ArrayList<String> mFragmentTitles = new ArrayList<>();


    public BaseTabAdapter(FragmentManager fm) {
        super(fm);
    }


    public void addFragment(Fragment fragment, String title) {
        mFragments.add(fragment);
        mFragmentTitles.add(title);
    }

    public void addFragment(int index, Fragment fragment, String title) {
        mFragments.add(index, fragment);
        mFragmentTitles.add(index, title);
    }

    @Override
    public Fragment getItem(int position) {
        return mFragments.get(position);
    }

    @Override
    public int getCount() {
        return mFragments.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mFragmentTitles.get(position);
    }

}
