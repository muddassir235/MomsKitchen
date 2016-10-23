package com.momskitchen.momskitchen.Admin;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.text.SpannableString;
import android.text.style.RelativeSizeSpan;

import com.momskitchen.momskitchen.Admin.ActivitiesPlusFragments.AdminDeliveredMealsFragment;
import com.momskitchen.momskitchen.Admin.ActivitiesPlusFragments.AdminMenuListFragment;
import com.momskitchen.momskitchen.Admin.ActivitiesPlusFragments.AdminOrderFragment;
import com.momskitchen.momskitchen.Admin.ActivitiesPlusFragments.AdminPackagedMealsFragment;
import com.momskitchen.momskitchen.Admin.ActivitiesPlusFragments.MealListFragment;
import com.momskitchen.momskitchen.Admin.ActivitiesPlusFragments.MenuCreationMealsList;
import com.momskitchen.momskitchen.Customer.ActivitiesPlusFragments.CustomerDeliveredMealsFragment;
import com.momskitchen.momskitchen.Customer.ActivitiesPlusFragments.CustomerMenuFragment;
import com.momskitchen.momskitchen.Customer.ActivitiesPlusFragments.CustomerOrdersFragment;
import com.momskitchen.momskitchen.Customer.ActivitiesPlusFragments.CustomerPackagedMealFragment;
import com.momskitchen.momskitchen.Customer.ActivitiesPlusFragments.CutomerCartFragment;

/**
 * Created by hp on 8/29/2016.
 */
public class MealsListFragmentsPagerAdapter extends FragmentPagerAdapter {

    public static final int ALL_MEALS_LIST_FRAGMENT = 0;
    public static final int MENU_CREATION_MEALS_LIST = 1;
    public static final int ADMIN_MENU_LIST = 3;
    public static final int CUSTOMER_MENU_LIST = 4;
    public static final int ORDERS_CUSTOMER_LIST = 5;
    public static final int ORDER_ADMIN_PAGER = 6;

    int type;
    public MealsListFragmentsPagerAdapter(FragmentManager fm, int type) {
        super(fm);
        this.type = type;
    }

    @Override
    public Fragment getItem(int position) {
        switch (type){
            case ALL_MEALS_LIST_FRAGMENT:{return MealListFragment.newInstance(position + 1, 2);}
            case MENU_CREATION_MEALS_LIST:{return  MenuCreationMealsList.newInstance(position+1,1);}
            case ADMIN_MENU_LIST:{return  AdminMenuListFragment.newInstance(position+1,1);}
            case CUSTOMER_MENU_LIST:{return CustomerMenuFragment.newInstance(position+1,1);}
            case ORDERS_CUSTOMER_LIST:{
                switch (position){
                    case 0:{return CutomerCartFragment.newInstance(position+1);}
                    case 1:{return CustomerOrdersFragment.newInstance();}
                    case 2:{return CustomerPackagedMealFragment.newInstance();}
                    case 3:{return CustomerDeliveredMealsFragment.newInstance();}
                    default:{return null;}
                }
            }
            case ORDER_ADMIN_PAGER:{
                switch (position){
                    case 0:{return AdminOrderFragment.newInstance();}
                    case 1:{return AdminPackagedMealsFragment.newInstance();}
                    case 2:{return AdminDeliveredMealsFragment.newInstance();}
                    default:{return null;}
                }
            }
            default:{return null;}
        }
    }

    @Override
    public int getCount() {
        if(type == ORDERS_CUSTOMER_LIST){
            return 4;
        }
        return 3;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (type) {
            case ORDERS_CUSTOMER_LIST:{
                switch (position) {
                    case 0: {
                        SpannableString ss1 = new SpannableString("CART");
                        ss1.setSpan(new RelativeSizeSpan(1.2f), 0, 3, 0);
                        return ss1;
                    }
                    case 1: {
                        SpannableString ss1 = new SpannableString("ORDERS");
                        ss1.setSpan(new RelativeSizeSpan(1.2f), 0, 5, 0);
                        return ss1;
                    }
                    case 2: {
                        SpannableString ss1 = new SpannableString("PACKAGED");
                        ss1.setSpan(new RelativeSizeSpan(1.2f), 0, 7, 0);
                        return ss1;
                    }

                    case 3:{
                        SpannableString ss1 = new SpannableString("RECEIVED");
                        ss1.setSpan(new RelativeSizeSpan(1.2f), 0, 7, 0);
                        return ss1;
                    }
                }
                return null;
            }
            case ORDER_ADMIN_PAGER:{
                switch (position) {
                    case 0: {
                        SpannableString ss1 = new SpannableString("PENDING");
                        ss1.setSpan(new RelativeSizeSpan(1.2f), 0, 6, 0);
                        return ss1;
                    }
                    case 1: {
                        SpannableString ss1 = new SpannableString("PACKAGED");
                        ss1.setSpan(new RelativeSizeSpan(1.2f), 0, 7, 0);
                        return ss1;
                    }
                    case 2: {
                        SpannableString ss1 = new SpannableString("DELIVERED");
                        ss1.setSpan(new RelativeSizeSpan(1.2f), 0, 8, 0);
                        return ss1;
                    }
                }
                return null;
            }
            default: {
                switch (position) {
                    case 0: {
                        SpannableString ss1 = new SpannableString("LUNCH");
                        ss1.setSpan(new RelativeSizeSpan(1.2f), 0, 4, 0);
                        return ss1;
                    }
                    case 1: {
                        SpannableString ss1 = new SpannableString("DESSERT");
                        ss1.setSpan(new RelativeSizeSpan(1.2f), 0, 6, 0);
                        return ss1;
                    }
                    case 2: {
                        SpannableString ss1 = new SpannableString("COMPLEMENTS");
                        ss1.setSpan(new RelativeSizeSpan(1.2f), 0, 10, 0);
                        return ss1;
                    }
                }
                return null;
            }
        }
    }
}
