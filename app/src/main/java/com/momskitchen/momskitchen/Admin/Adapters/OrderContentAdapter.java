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
        RefinedDate date = convertDate(dates.get(position));
        holder.mDateTV.setText(""+date.month+" "+date.day+", "+date.year);
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

    public RefinedDate convertDate(String date){
        int i =0;
        String year="";
        String month="";
        String day="";
        for(; i<4; i++){
            year=year+date.charAt(i);
        }
        for(; i<6 ; i++){
            month=month+date.charAt(i);
        }
        for(;i < date.length();i++){
            day=day+date.charAt(i);
        }
        RefinedDate newDate= new RefinedDate();
        newDate.year=year;
        newDate.month=month;
        newDate.day=day;
        return getMonth(newDate);
    }

    private RefinedDate getMonth(RefinedDate date){
        String[] dates= {"Jan","Feb","Mar","Apr","May","June","Jul","Aug","Sep","Oct","Nov","Dec"};
        date.month=dates[Integer.valueOf(date.month)-1];
        return date;
    }
}
