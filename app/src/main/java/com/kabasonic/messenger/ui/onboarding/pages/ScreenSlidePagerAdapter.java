package com.kabasonic.messenger.ui.onboarding.pages;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.util.ArrayList;

public class ScreenSlidePagerAdapter extends FragmentStateAdapter {
    /**
     * A simple pager adapter that represents 3 ScreenSlidePageFragment objects, in
     * sequence.
     */
    private ArrayList<Fragment> fragments;

    public ScreenSlidePagerAdapter(FragmentActivity fa, ArrayList<Fragment> fragments) {
        super(fa);
        this.fragments = fragments;
    }

    @Override
    public Fragment createFragment(int position) {
        return fragments.get(position);
    }

    @Override
    public int getItemCount() {
        return fragments.size();
    }
}
