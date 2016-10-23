package com.momskitchen.momskitchen.model;

import android.util.Log;

import java.util.List;

/**
 * Created by hp on 9/8/2016.
 */
public class Order {

    // id of the order in main list
    public String mainListId;

    // id of the order in user's list
    public String userListId;

    // list of meal items ordered
    public List<MealItem> mealItemList;

    // list of corresponding times of day for each meal
    public List<String> times;

    // list of corresponding dates for each meal
    public List<String> dates;

    // list of corresponding quantities for each meal item
    public List<Long> quantities;

    public String userId;

    // Location to which the order has to be delivered
    public String deliveryPoint;

    // Current status of the order
    public int status;

    public Order() {
    }

    public Order(String mainListId, String userListId, List<MealItem> mealItemList, List<String> times, List<String> dates, List<Long> quantities, String userId, String deliveryPoint, int status) {
        this.mainListId = mainListId;
        this.userListId = userListId;
        this.mealItemList = mealItemList;
        this.times = times;
        this.dates = dates;
        this.quantities = quantities;
        this.userId = userId;
        this.deliveryPoint = deliveryPoint;
        this.status = status;
    }
}
