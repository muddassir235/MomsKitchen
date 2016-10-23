package com.momskitchen.momskitchen.Customer;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.momskitchen.momskitchen.R;

/**
 * Created by hp on 9/11/2016.
 */
public class CartRootRecyclerHolder extends RecyclerView.ViewHolder {
    public View mView;
    public final TextView mDayTextView;
    public final RecyclerView mMealListRecyclerView;

    public CartRootRecyclerHolder(View itemView) {
        super(itemView);
        this.mView = itemView;
        this.mDayTextView = (TextView) mView.findViewById(R.id.day_name_tv);
        this.mMealListRecyclerView =(RecyclerView) mView.findViewById(R.id.cart_day_meal_list);
    }
}
