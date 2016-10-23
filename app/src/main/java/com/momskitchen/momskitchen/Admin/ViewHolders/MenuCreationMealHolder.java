package com.momskitchen.momskitchen.Admin.ViewHolders;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.momskitchen.momskitchen.R;
import com.momskitchen.momskitchen.model.MealItem;

/**
 * Created by hp on 9/4/2016.
 */
public class MenuCreationMealHolder extends RecyclerView.ViewHolder {
    View mView;
    public final CheckBox mCheckBox;
    public final TextView mNameTV;
    public final TextView mPriceTV;
    public MealItem mItem;
    public boolean checkedByHand;

    public MenuCreationMealHolder(View itemView) {
        super(itemView);
        mView = itemView;
        this.mCheckBox = (CheckBox) mView.findViewById(R.id.in_menu_checkbox);
        this.mNameTV = (TextView) mView.findViewById(R.id.name_of_meal);
        this.mPriceTV = (TextView) mView.findViewById(R.id.price_of_meal);
    }


}
