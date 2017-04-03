package com.inspirenetz.app.proapp.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.inspirenetz.app.proapp.customer.CustomerHomePageDetailsFragment;
import com.inspirenetz.app.proapp.customer.CustomerHomePageTabsFragment;
import com.inspirenetz.app.proapp.promotions.PromotionListFragment;

/**
 * Created by raju on 2/1/17.
 */

public class CustomerHomePagerAdapter extends FragmentPagerAdapter {

    private static int NUM_OF_TABS = 2;

    public CustomerHomePagerAdapter(FragmentManager fragmentManager, int NUM_OF_TABS){

        super(fragmentManager);

        this.NUM_OF_TABS = NUM_OF_TABS;
    }

    @Override
    public Fragment getItem(int position) {

        switch (position){

            case 0:
                return new CustomerHomePageDetailsFragment();
            case 1:
                return new PromotionListFragment();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return NUM_OF_TABS;
    }
}
