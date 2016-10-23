package com.momskitchen.momskitchen.Admin.ViewHolders;

import android.view.View;
import android.widget.TextView;

import com.momskitchen.momskitchen.Admin.Adapters.OrderItem;
import com.momskitchen.momskitchen.R;
import com.momskitchen.momskitchen.model.Order;
import com.zaihuishou.expandablerecycleradapter.viewholder.AbstractAdapterItem;

/**
 * Created by hp on 10/22/2016.
 */

public class OrderItemWrapper extends AbstractAdapterItem {
    public View mView;
    public TextView mNumberOfItemsTV;
    public TextView mItemNameTV;
    public TextView mDateTV;
    public TextView mItemTODTV;
    public TextView mItemCostTV;

    @Override
    public int getLayoutResId() {
        return R.layout.order_content_list_item_layout;
    }

    @Override
    public void onBindViews(View root) {
        this.mView = root;
        this.mNumberOfItemsTV = (TextView) mView.findViewById(R.id.number_of_items);
        this.mItemNameTV = (TextView) mView.findViewById(R.id.name_of_item);
        this.mDateTV = (TextView) mView.findViewById(R.id.date_text_view);
        this.mItemTODTV = (TextView) mView.findViewById(R.id.time_of_delivery);
        this.mItemCostTV = (TextView) mView.findViewById(R.id.item_cost);
    }

    @Override
    public void onSetViews() {

    }

    @Override
    public void onUpdateViews(Object model, int position) {
        if(model instanceof OrderItem) {
            OrderItem item = (OrderItem) model;
            mNumberOfItemsTV.setText("" + item.quantity);
            mItemNameTV.setText("" + item.mealItem.name);
            mDateTV.setText("" + item.date);
            mItemTODTV.setText("" + item.time);
            mItemCostTV.setText("" + item.mealItem.pricePerUnit + " PKR");
        }
    }
}
