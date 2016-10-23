package com.momskitchen.momskitchen.Customer;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.momskitchen.momskitchen.R;

/**
 * Created by hp on 9/11/2016.
 */
public class CartDayListItemHolder extends RecyclerView.ViewHolder{
    View mView;
    public final ImageView mMealThumbnailIV;
    public final TextView mMealNameTV;
    public final Spinner mMealTimesSpinner;
    public final TextView mMealPriceTV;
    public final TextView mMealQuantityTV;

    public CartDayListItemHolder(View itemView){
        super(itemView);
        this.mView = itemView;
        this.mMealThumbnailIV = (ImageView) mView.findViewById(R.id.meal_thumbnail_image_view);
        this.mMealNameTV  = (TextView) mView.findViewById(R.id.meal_name_text_view);
        this.mMealTimesSpinner = (Spinner) mView.findViewById(R.id.meal_times_available_spinner);
        this.mMealPriceTV = (TextView) mView.findViewById(R.id.meal_price_text_view);
        this.mMealQuantityTV = (TextView) mView.findViewById(R.id.meal_quantity_text_view);
    }
}
