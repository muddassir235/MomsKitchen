package com.momskitchen.momskitchen.model;

/**
 * Created by hp on 9/8/2016.
 */
public class UserCartEntry {
    public String userId;
    public MealItem mealItem;
    public String date;
    public int quantity;
    public String time;

    public UserCartEntry() {
    }

    public UserCartEntry(String userId, MealItem mealItem, String date, int quantity) {
        this.userId = userId;
        this.mealItem = mealItem;
        this.date = date;
        this.quantity = quantity;
    }

    public UserCartEntry(String userId, MealItem mealItem, String date, int quantity, String time) {
        this.userId = userId;
        this.mealItem = mealItem;
        this.date = date;
        this.quantity = quantity;
        this.time = time;
    }
}
