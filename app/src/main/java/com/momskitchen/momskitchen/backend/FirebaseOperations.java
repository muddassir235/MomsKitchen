package com.momskitchen.momskitchen.backend;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.momskitchen.momskitchen.model.MealItem;

import java.util.HashMap;

/**
 * Created by hp on 8/29/2016.
 */
public class FirebaseOperations {
    public static DatabaseReference firebaseDatabase = FirebaseDatabase.getInstance().getReference();

    public static void addNewMealItem(MealItem mealItem){
        DatabaseReference pushRef = firebaseDatabase.child("Meals").push();
        String pushKey = pushRef.getKey();
        HashMap<String, Object> dateCreatedObj = new HashMap<String, Object>();
        dateCreatedObj.put("date", ServerValue.TIMESTAMP);
        mealItem.timeStampCreated = dateCreatedObj;
        mealItem.id = pushKey;
        pushRef.setValue(mealItem);
    }

    public static void updateMealItem(String key, MealItem mealItem){
        //TODO: Define this function
    }

    public static void deleteMealItem(String key){
        //TODO: Define this function
    }

    public static MealItem getMealItem(String key){
        //TODO: Implement this function some how
        return null;
    }

}
