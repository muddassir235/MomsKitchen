package com.momskitchen.momskitchen;

import android.content.SharedPreferences;

/**
 * Created by hp on 8/28/2016.
 */
public class Constants {
    public static final int DONT_KNOW_USER_TYPE = -1;
    public static final int USER_IS_ADMIN = 0;
    public static final int USER_IS_CUSTOMER = 1;
    public static final int FIREBASE_OPERATION_SELECT_PICTURE = 0;
    public static final int FIREBASE_OPERATION_UPLOAD_THUMBNAIL= 0;
    public static final int FIREBASE_OPERATION_UPLOAD_POSTER= 1;
    public static final int LOCAL_OPERATION_SELECT_THUMBNAIL = 1;
    public static final int LOCAL_OPERATION_SELECT_POSTER = 2;
    public static final String FIREBASE_OPERATION_CATEGORY_LUNCH= "lunch/";
    public static final String FIREBASE_OPERATION_CATEGORY_DESSERT= "dessert/";
    public static final String FIREBASE_OPERATION_CATEGORY_COMPLIMENT= "compliments/";

    public static final int ORDER_PENDING = 0;
    public static final int ORDER_PACKAGED = 1;
    public static final int ORDER_RECIEVED = 2;
}
