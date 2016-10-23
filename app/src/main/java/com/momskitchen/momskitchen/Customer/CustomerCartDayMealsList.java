package com.momskitchen.momskitchen.Customer;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.SpinnerAdapter;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.momskitchen.momskitchen.R;
import com.momskitchen.momskitchen.model.MealItem;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hp on 10/20/2016.
 */

public class CustomerCartDayMealsList extends RecyclerView.Adapter<CartDayListItemHolder>{
    private static final String TAG = CustomerCartDayMealsList.class.getName();

    List<MealItem> mealItems;
    List<Long> meatItemQuantities;
    String date;
    List<String> times;

    public CustomerCartDayMealsList(List<MealItem> mealItems, List<Long> meatItemQuantities, String date, List<String> times) {
        super();
        this.mealItems = mealItems;
        this.meatItemQuantities = meatItemQuantities;
        this.date = date;
        this.times = times;
    }

    @Override
    public CartDayListItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_layout_meal_list_item_layout, parent,false);
        CartDayListItemHolder cartDayListItemHolder = new CartDayListItemHolder(view);
        return cartDayListItemHolder;
    }

    @Override
    public void onBindViewHolder(CartDayListItemHolder holder, int position) {
        final MealItem mealItem = mealItems.get(position);

        holder.mMealNameTV.setText(mealItem.name);

        holder.mMealPriceTV.setText(""+mealItem.pricePerUnit);

        Picasso.with(holder.mView.getContext()).load(mealItem.thumbnailURL).into(holder.mMealThumbnailIV);

        holder.mMealQuantityTV.setText(""+meatItemQuantities.get(position));

        final List<String> listOfTimes = new ArrayList<>();
        listOfTimes.addAll(mealItem.times);
        listOfTimes.add("N/A");

        int indexToSelect = listOfTimes.indexOf(times.get(position));

        if(mealItem.times!=null) {
            ArrayAdapter<String> adapter =
                    new ArrayAdapter<>(holder.mView.getContext(), android.R.layout.simple_spinner_item, listOfTimes);

            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

            // Apply the adapter to the spinner
            holder.mMealTimesSpinner.setAdapter(adapter);
            holder.mMealTimesSpinner.setSelection(indexToSelect);

            holder.mMealTimesSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                    if(firebaseUser!=null) {
                        FirebaseDatabase.getInstance().getReference().
                                child("Users").
                                child(firebaseUser.getUid()).
                                child("Cart").
                                child(date).
                                child(mealItem.id).
                                child("time").
                                setValue(listOfTimes.get(position));
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });

        }
    }

    @Override
    public int getItemCount() {
        return mealItems.size();
    }
}
