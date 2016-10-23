package com.momskitchen.momskitchen.Admin.ViewHolders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.momskitchen.momskitchen.R;

public class MealHolderAdminMenuList extends RecyclerView.ViewHolder{
    View mView;
    public final ImageView mMealThumbnailIV;
    public final TextView mMealNameTV;
    public final TextView mMealDescriptionTV;
    public final TextView mMealPriceTV;
    public MealHolderAdminMenuList(View itemView) {
        super(itemView);
        this.mView = itemView;
        this.mMealThumbnailIV = (ImageView) mView.findViewById(R.id.meal_thumbnail_image_view);
        this.mMealNameTV = (TextView) mView.findViewById(R.id.meal_name_text_view);
        this.mMealDescriptionTV = (TextView) mView.findViewById(R.id.meal_description_text_view);
        this.mMealPriceTV = (TextView) mView.findViewById(R.id.meal_price_text_view);
    }
}
