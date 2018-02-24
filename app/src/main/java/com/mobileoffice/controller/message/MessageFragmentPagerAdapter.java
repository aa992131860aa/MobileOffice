package com.mobileoffice.controller.message;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

/**
 * Created by 99213 on 2017/6/21.
 */

public  class MessageFragmentPagerAdapter extends FragmentPagerAdapter {
    List<Fragment> innerFragments;
    public MessageFragmentPagerAdapter(FragmentManager fm, List<Fragment> fragments) {
        super(fm);
        innerFragments = fragments;
    }

    @Override
    public Fragment getItem(int position) {

        return innerFragments.get(position);

    }

    @Override
    public int getCount() {

        return innerFragments.size();
    }

}
