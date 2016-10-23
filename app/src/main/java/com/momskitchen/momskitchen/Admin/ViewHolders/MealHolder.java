package com.momskitchen.momskitchen.Admin.ViewHolders;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.momskitchen.momskitchen.Admin.ActivitiesPlusFragments.MealDetailsActivityAdmin;
import com.momskitchen.momskitchen.Admin.ActivitiesPlusFragments.MealListFragment;
import com.momskitchen.momskitchen.R;
import com.momskitchen.momskitchen.model.MealItem;

/**
 * Created by hp on 8/30/2016.
 */
public class MealHolder extends RecyclerView.ViewHolder {
    View mView;
    public final ImageView mealPosterIV;
    public final TextView mNameTV;
    public final TextView mPriceTV;
    public final LinearLayout mMealInfoLayout;
    public MealItem mItem;

    public MealHolder(View itemView) {
        super(itemView);
        mView = itemView;
        this.mealPosterIV = (ImageView) mView.findViewById(R.id.meal_thumbnail_image_view);
        this.mNameTV = (TextView) mView.findViewById(R.id.meal_name_text_view);
        this.mPriceTV = (TextView) mView.findViewById(R.id.meal_price_text_view);
        mMealInfoLayout = (LinearLayout) mView.findViewById(R.id.meal_info_layout);

        mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putSerializable("Meal",mItem);
                Intent intent = new Intent(MealListFragment.context.getApplicationContext(),MealDetailsActivityAdmin.class);
                intent.putExtras(bundle);
                MealListFragment.context.startActivity(intent);
            }
        });
    }

    @Override
    public String toString() {
        return super.toString() + " '" + mNameTV.getText() + "'";
    }
}
