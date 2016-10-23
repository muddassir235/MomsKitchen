package com.momskitchen.momskitchen.Customer;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.momskitchen.momskitchen.R;
import com.momskitchen.momskitchen.model.MealItem;

/**
 * Created by hp on 9/6/2016.
 */
public class CustomerMenuItemHolder extends RecyclerView.ViewHolder{
    View mView;
    public final ImageView mMealThumbnailIV;
    public final TextView mMealNameTV;
    public final TextView mMealDescriptionTV;
    public final TextView mMealPriceTV;
    public final ImageButton mIncrementButton;
    public final ImageButton mDecrementButton;
    public final TextView mQuantityTV;
    public int mCurrentQuantity;
    public int mQuantityToIncrement;
    public boolean underProcess;
    public MealItem mealItem;
    public CustomerMenuItemHolder(View itemView) {
        super(itemView);
        this.mView = itemView;
        this.mMealThumbnailIV = (ImageView) mView.findViewById(R.id.meal_thumbnail_image_view);
        this.mMealNameTV = (TextView) mView.findViewById(R.id.meal_name_text_view);
        this.mMealDescriptionTV = (TextView) mView.findViewById(R.id.meal_description_text_view);
        this.mMealPriceTV = (TextView) mView.findViewById(R.id.meal_price_text_view);
        this.mIncrementButton = (ImageButton) mView.findViewById(R.id.increment_quantity_button);
        this.mDecrementButton = (ImageButton) mView.findViewById(R.id.decrement_quantity_button);
        this.mQuantityTV = (TextView) mView.findViewById(R.id.quantity_text_view);
        mCurrentQuantity = Integer.valueOf(this.mQuantityTV.getText().toString());
    }
}
