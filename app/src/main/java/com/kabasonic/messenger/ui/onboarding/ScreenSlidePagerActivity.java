package com.kabasonic.messenger.ui.onboarding;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.kabasonic.messenger.R;
import com.kabasonic.messenger.ui.onboarding.pages.ScreenSlidePageFragmentOne;
import com.kabasonic.messenger.ui.onboarding.pages.ScreenSlidePageFragmentThree;
import com.kabasonic.messenger.ui.onboarding.pages.ScreenSlidePageFragmentTwo;

import java.util.ArrayList;

public class ScreenSlidePagerActivity extends FragmentActivity {
    /**
     * The pager widget, which handles animation and allows swiping horizontally to access previous
     * and next wizard steps.
     */
    private ViewPager2 viewPager;

    /**
     * The pager adapter, which provides the pages to the view pager widget.
     */
    private FragmentStateAdapter pagerAdapter;
    private ArrayList<Fragment> fragmentArrayList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_screen_slide);

        fragmentArrayList.add(new ScreenSlidePageFragmentOne());
        fragmentArrayList.add(new ScreenSlidePageFragmentTwo());
        fragmentArrayList.add(new ScreenSlidePageFragmentThree());

        // Instantiate a ViewPager2 and a PagerAdapter.
        viewPager = findViewById(R.id.pager);
        pagerAdapter = new ScreenSlidePagerAdapter(this,fragmentArrayList);
        viewPager.setAdapter(pagerAdapter);
    }

    @Override
    public void onBackPressed() {
        //TODO Kill all activity application, when user presed button back
//        if (viewPager.getCurrentItem() == 0) {
//            // If the user is currently looking at the first step, allow the system to handle the
//            // Back button. This calls finish() on this activity and pops the back stack.
//            super.onBackPressed();
//        } else {
//            // Otherwise, select the previous step.
//            viewPager.setCurrentItem(viewPager.getCurrentItem() - 1);
//        }
    }

    /**
     * A simple pager adapter that represents 3 ScreenSlidePageFragment objects, in
     * sequence.
     */
    private class ScreenSlidePagerAdapter extends FragmentStateAdapter {

        private ArrayList<Fragment> fragments;

        public ScreenSlidePagerAdapter(FragmentActivity fa,ArrayList<Fragment> fragments) {
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
}
