package com.momskitchen.momskitchen.Customer;

import android.os.Handler;
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
import com.momskitchen.momskitchen.Customer.ActivitiesPlusFragments.CustomerMenu;
import com.momskitchen.momskitchen.Customer.ActivitiesPlusFragments.CustomerMenuFragment;
import com.momskitchen.momskitchen.R;
import com.momskitchen.momskitchen.backend.MenuCreator;
import com.momskitchen.momskitchen.backend.OrderHandler;
import com.momskitchen.momskitchen.model.MealItem;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Calendar;
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

        if(mealItem.name!=null) {
            holder.mMealNameTV.setText(mealItem.name);
        }

        if(mealItem.pricePerUnit!=null) {
            holder.mMealPriceTV.setText("" + mealItem.pricePerUnit);
        }

        if(mealItem.thumbnailURL!=null) {
            Picasso.with(holder.mView.getContext()).load(mealItem.thumbnailURL).into(holder.mMealThumbnailIV);
        }

        if(meatItemQuantities!=null && position<meatItemQuantities.size()) {
            holder.mMealQuantityTV.setText("" + meatItemQuantities.get(position));
        }

        final List<String> listOfTimes = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();

        int dateInt = 0;
        if(date!=null) {
             dateInt = Integer.valueOf(date);
        }

        int currHour = calendar.get(Calendar.HOUR_OF_DAY);
        int todayDate = Integer.valueOf(MenuCreator.getInstance().getDateFromCalendar(calendar));

        if(mealItem.times!=null) {
            for (String time : mealItem.times) {
                if (dateInt == todayDate) {
                    if (getHourFromTimeString(time) > (currHour + 1)) {
                        listOfTimes.add(time);
                    }
                } else if (dateInt > todayDate) {
                    listOfTimes.add(time);
                } else {

                }
            }
        }

        listOfTimes.add("N/A");

        if(listOfTimes.size() == 1){
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            FirebaseDatabase.getInstance().getReference().
                    child("Users").
                    child(user.getUid()).
                    child("Cart").
                    child(date).
                    child(mealItem.id).
                    setValue(null);

            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    notifyDataSetChanged();
                    OrderHandler.calculateTotal();
                    if(CustomerMenuFragment.lunchListAdapter!=null){CustomerMenuFragment.lunchListAdapter.refreshData();}
                    if(CustomerMenuFragment.dessertListAdapter!=null){CustomerMenuFragment.dessertListAdapter.refreshData();}
                    if(CustomerMenuFragment.complimentListAdapter!=null){CustomerMenuFragment.complimentListAdapter.refreshData();}
                }
            },200);
        }else {

            int indexToSelect = 0;
            if (mealItems != null) {
                indexToSelect = listOfTimes.indexOf(times.get(position));
            }

            if (mealItem.times != null) {
                ArrayAdapter<String> adapter =
                        new ArrayAdapter<>(holder.mView.getContext(), android.R.layout.simple_spinner_item, listOfTimes);

                // Specify the layout to use when the list of choices appears
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                // Apply the adapter to the spinner
                holder.mMealTimesSpinner.setAdapter(adapter);
                if (indexToSelect >= 0 && indexToSelect < listOfTimes.size()) {
                    holder.mMealTimesSpinner.setSelection(indexToSelect);
                } else {
                    holder.mMealTimesSpinner.setSelection(listOfTimes.indexOf("N/A"));
                }

                holder.mMealTimesSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                        if (firebaseUser != null) {
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
    }

    int getHourFromTimeString(String time){
        String hour = time.substring(0,2);
        String amOrPM = time.substring(6,8);

        int hourInt  = Integer.valueOf(hour);
        Log.v(TAG, " hour: "+hour);
        Log.v(TAG, " am or pm: "+amOrPM);

        if(amOrPM.equals("am")){
            if(hourInt==12){
                hourInt = hourInt-12;
            }
        }else{
            if(hourInt!=12){
                hourInt = hourInt+12;
            }
        }
        Log.v(TAG, " hour int : "+hourInt);

        return hourInt;
    }

    @Override
    public int getItemCount() {
        return mealItems.size();
    }
}
