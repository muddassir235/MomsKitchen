package com.momskitchen.momskitchen;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.text.SpannableString;
import android.text.style.RelativeSizeSpan;

/**
 * Created by hp on 8/29/2016.
 */
public class MealsListFragmentsPagerAdapter extends FragmentPagerAdapter {
    public MealsListFragmentsPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        return MealListFragment.newInstance(position+1,2);
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:{
                SpannableString ss1=  new SpannableString("LUNCH");
                ss1.setSpan(new RelativeSizeSpan(1.2f), 0, 4, 0);
                return ss1;
            }
            case 1:{
                SpannableString ss1=  new SpannableString("DESSERT");
                ss1.setSpan(new RelativeSizeSpan(1.2f), 0, 6, 0);
                return ss1;
            }
            case 2: {
                SpannableString ss1=  new SpannableString("COMPLEMENTS");
                ss1.setSpan(new RelativeSizeSpan(1.2f), 0, 10, 0);
                return ss1;
            }
        }
        return null;
    }
}
