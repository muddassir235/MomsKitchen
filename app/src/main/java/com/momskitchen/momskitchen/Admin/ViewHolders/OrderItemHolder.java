package com.momskitchen.momskitchen.Admin.ViewHolders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.momskitchen.momskitchen.R;

/**
 * Created by hp on 10/22/2016.
 */

public class OrderItemHolder extends RecyclerView.ViewHolder {
    public final View mView;
    public final TextView mNumberOfItemsTV;
    public final TextView mItemNameTV;
    public final TextView mDateTV;
    public final TextView mItemTODTV;
    public final TextView mItemCostTV;
    public OrderItemHolder(View itemView) {
        super(itemView);
        this.mView = itemView;
        this.mNumberOfItemsTV = (TextView) mView.findViewById(R.id.number_of_items);
        this.mItemNameTV = (TextView) mView.findViewById(R.id.name_of_item);
        this.mDateTV = (TextView) mView.findViewById(R.id.date_text_view);
        this.mItemTODTV = (TextView) mView.findViewById(R.id.time_of_delivery);
        this.mItemCostTV = (TextView) mView.findViewById(R.id.item_cost);
    }
}
