package com.momskitchen.momskitchen.Admin.ViewHolders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.momskitchen.momskitchen.R;
import com.momskitchen.momskitchen.model.MealItem;
import com.momskitchen.momskitchen.model.Order;
import com.momskitchen.momskitchen.model.User;
import com.zaihuishou.expandablerecycleradapter.viewholder.AbstractExpandableAdapterItem;

/**
 * Created by hp on 10/22/2016.
 */

public class OrderHolder extends AbstractExpandableAdapterItem {
    public View mView;
    public TextView mPersonNameTV;
    public TextView mPersonPhoneTV;
    public TextView mDeliveryPointAddressTV;
    public TextView mTotalCostTV;
    public TextView mTotalItemsTV;
    public FrameLayout mExpandListView;
    public ImageView mExpandListIconIV;
    public LinearLayout mOrderContentSchemaLL;
    public RecyclerView mOrderContentListRV;
    public OrderWrapper mOrderWrapper;


    @Override
    public void onExpansionToggled(boolean expanded) {

    }

    @Override
    public int getLayoutResId() {
        return R.layout.pending_order_layout;
    }

    @Override
    public void onBindViews(View root) {
        this.mView = root;
        this.mPersonNameTV = (TextView) mView.findViewById(R.id.name_text_view);
        this.mPersonPhoneTV = (TextView) mView.findViewById(R.id.user_phone_text_view);
        this.mDeliveryPointAddressTV = (TextView) mView.findViewById(R.id.delivery_point_text_view);
        this.mTotalCostTV = (TextView) mView.findViewById(R.id.total_cost_text_view);
        this.mTotalItemsTV = (TextView) mView.findViewById(R.id.total_items_text_view);
        this.mExpandListView = (FrameLayout) mView.findViewById(R.id.expand_order_layout);
        this.mExpandListIconIV = (ImageView) mView.findViewById(R.id.expand_order_icon_image_view);
        this.mOrderContentSchemaLL = (LinearLayout) mView.findViewById(R.id.order_contents_schema_layout);
        this.mOrderContentListRV = (RecyclerView) mView.findViewById(R.id.order_content_list);

        this.mExpandListIconIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doExpandOrUnexpand();
            }
        });
    }

    @Override
    public void onSetViews() {

    }

    @Override
    public void onUpdateViews(Object model, int position) {
        super.onUpdateViews(model, position);
        onSetViews();
        onExpansionToggled(getExpandableListItem().isExpanded());
        mOrderWrapper = (OrderWrapper) model;
        int numberOfMeals = 0;
        int totalCost = 0;
        if(mOrderWrapper.mOrder.mealItemList!=null) {
            for (MealItem mealItem : mOrderWrapper.mOrder.mealItemList) {
                numberOfMeals++;
                totalCost += mealItem.pricePerUnit;
            }
            mTotalCostTV.setText(totalCost + " PKR");
            mTotalItemsTV.setText("" + numberOfMeals);
            mDeliveryPointAddressTV.setText(mOrderWrapper.mOrder.deliveryPoint);
            if (mOrderWrapper.mOrder.userId != null) {
                FirebaseDatabase.getInstance().getReference().child("Users").child(mOrderWrapper.mOrder.userId).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot != null) {
                            User user = dataSnapshot.getValue(User.class);
                            mPersonNameTV.setText(user.name);
                            mPersonPhoneTV.setText(user.phone);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        }
    }
}
