package com.momskitchen.momskitchen.Admin.Adapters;

import com.momskitchen.momskitchen.model.MealItem;

/**
 * Created by hp on 10/22/2016.
 */

public class OrderItem {
    public String userListId;
    public String adminListId;
    public MealItem mealItem;
    public Long quantity;
    public String date;
    public String time;
    public String userId;
    public String deliveryPoint;

    public OrderItem(String userListId, String adminListId, MealItem mealItem, Long quantity, String date, String time, String userId, String deliveryPoint) {
        this.userListId = userListId;
        this.adminListId = adminListId;
        this.mealItem = mealItem;
        this.quantity = quantity;
        this.date = date;
        this.time = time;
        this.userId = userId;
        this.deliveryPoint = deliveryPoint;
    }
}
