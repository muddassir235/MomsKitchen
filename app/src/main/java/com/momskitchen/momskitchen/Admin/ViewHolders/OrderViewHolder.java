package com.momskitchen.momskitchen.Admin.ViewHolders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.momskitchen.momskitchen.R;
import com.momskitchen.momskitchen.model.Order;

/**
 * Created by hp on 10/22/2016.
 */

public class OrderViewHolder extends RecyclerView.ViewHolder{
    public View mView;
    public TextView mPersonNameTV;
    public TextView mPersonPhoneTV;
    public TextView mDeliveryPointAddressTV;
    public TextView mTotalCostTV;
    public TextView mTotalItemsTV;
    public FrameLayout mExpandListView;
    public ImageView mExpandListIconIV;
    public RelativeLayout mOrderContentSchemaLL;
    public RecyclerView mOrderContentListRV;
    public Order mOrder;


    public OrderViewHolder(View itemView) {
        super(itemView);
        this.mView = itemView;
        this.mPersonNameTV = (TextView) mView.findViewById(R.id.name_text_view);
        this.mPersonPhoneTV = (TextView) mView.findViewById(R.id.user_phone_text_view);
        this.mDeliveryPointAddressTV = (TextView) mView.findViewById(R.id.delivery_point_text_view);
        this.mTotalCostTV = (TextView) mView.findViewById(R.id.total_cost_text_view);
        this.mTotalItemsTV = (TextView) mView.findViewById(R.id.total_items_text_view);
        this.mExpandListView = (FrameLayout) mView.findViewById(R.id.expand_order_layout);
        this.mExpandListIconIV = (ImageView) mView.findViewById(R.id.expand_order_icon_image_view);
        this.mOrderContentSchemaLL = (RelativeLayout) mView.findViewById(R.id.order_contents_schema_layout);
        this.mOrderContentListRV = (RecyclerView) mView.findViewById(R.id.order_content_list);
    }
}
