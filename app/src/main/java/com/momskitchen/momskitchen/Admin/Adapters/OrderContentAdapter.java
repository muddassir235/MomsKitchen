package com.momskitchen.momskitchen.Admin.Adapters;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.momskitchen.momskitchen.Admin.ViewHolders.OrderItemHolder;
import com.momskitchen.momskitchen.R;
import com.momskitchen.momskitchen.model.MealItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hp on 10/22/2016.
 */

public class OrderContentAdapter extends RecyclerView.Adapter<OrderItemHolder> {
    List<MealItem> meals;
    List<Long> quantities;
    List<String> dates;
    List<String> times;

    public OrderContentAdapter(
            List<MealItem> meals,
            List<Long> quantities,
            List<String> dates,
            List<String> times) {
        super();
        this.meals = meals;
        this.quantities = quantities;
        this.dates = dates;
        this.times = times;
        notifyDataSetChanged();
    }

    @Override
    public OrderItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.order_content_list_item_layout,parent,false);
        return new OrderItemHolder(view);
    }

    @Override
    public void onBindViewHolder(OrderItemHolder holder, int position) {
        if(position%2 == 0){
            holder.mView.setBackgroundColor(Color.parseColor("#22FF5252"));
        }
        holder.mNumberOfItemsTV.setText(""+quantities.get(position));
        holder.mItemNameTV.setText(""+meals.get(position).name);
        holder.mDateTV.setText(""+dates.get(position));
        holder.mItemTODTV.setText(""+times.get(position));
        holder.mItemCostTV.setText(""+meals.get(position).pricePerUnit*quantities.get(position)+" PKR");
    }

    @Override
    public int getItemCount() {
        if(meals!=null) {
            return meals.size();
        }else {
            return 0;
        }
    }
}
