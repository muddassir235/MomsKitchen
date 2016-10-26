package com.momskitchen.momskitchen.Customer.ActivitiesPlusFragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.momskitchen.momskitchen.Admin.MealsListFragmentsPagerAdapter;
import com.momskitchen.momskitchen.Customer.ActivitiesPlusFragments.CutomerCartFragment;
import com.momskitchen.momskitchen.R;

public class ShoppingCartActivity extends AppCompatActivity  implements CutomerCartFragment.OnFragmentInteractionListener,CustomerOrdersFragment.OnFragmentInteractionListener,CustomerPackagedMealFragment.OnFragmentInteractionListener,CustomerDeliveredMealsFragment.OnFragmentInteractionListener {

    public static final String TAB_POSITION_KEY = "position";
    public static ViewPager mealsListViewPager;
    private TabLayout tabLayout;
    private MealsListFragmentsPagerAdapter mealsListFragmentsPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping_cart);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        int position = 0;
        if(getIntent()!=null){
            position = getIntent().getIntExtra(TAB_POSITION_KEY,0);
        }
        tabLayout = (TabLayout) findViewById(R.id.tabs);
        mealsListFragmentsPagerAdapter = new MealsListFragmentsPagerAdapter(getSupportFragmentManager(),MealsListFragmentsPagerAdapter.ORDERS_CUSTOMER_LIST);
        // Set up the ViewPager with the sections adapter.
        mealsListViewPager = (ViewPager) findViewById(R.id.meal_container);
        if (mealsListViewPager != null) {
            mealsListViewPager.setAdapter(mealsListFragmentsPagerAdapter);
            mealsListViewPager.setCurrentItem(position);
        }

        tabLayout.setupWithViewPager(mealsListViewPager);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
