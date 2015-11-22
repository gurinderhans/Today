package me.gurinderhans.today.fragments.todofragment.controller;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

/**
 * Created by ghans on 11/18/15.
 */
public class TodoFragmentPagerAdapter extends FragmentStatePagerAdapter {

    public static final String TAG = TodoFragmentPagerAdapter.class.getSimpleName();

    private static final int NUM_PAGES = 2;


    public TodoFragmentPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        return TodoFragment.newInstance(position == 0 ? "TODAY" : "TOMORROW");
    }

    @Override
    public int getCount() {
        return NUM_PAGES;
    }
}
