package me.gurinderhans.today.fragments.todofragment.controller;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import me.gurinderhans.today.app.Keys.PagerTab;

/**
 * Created by ghans on 11/18/15.
 */
public class TodoFragmentPagerAdapter extends FragmentStatePagerAdapter {

    public static final String TAG = TodoFragmentPagerAdapter.class.getSimpleName();

    public TodoFragmentPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        return TodoFragment.newInstance(PagerTab.getTabWithIndex(position));
    }

    @Override
    public int getCount() {
        return PagerTab.values().length;
    }
}
