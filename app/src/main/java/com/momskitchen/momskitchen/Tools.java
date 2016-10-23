package com.momskitchen.momskitchen;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.util.Log;
import android.util.TypedValue;

/**
 * Created by hp on 9/2/2016.
 */
public class Tools {

    public static int brightenColor(int color){
        float[] hsv = new float[3];
        Color.colorToHSV(color, hsv);
        Log.v("HSV: Value",""+hsv[2]);
        hsv[2] = 0.99f;// value component
        hsv[1] = 0.1f;
        Log.v("HSV: Value",""+hsv[2]);
        return Color.HSVToColor(hsv);
    }


    public static int darkenColor(int color){
        float[] hsv = new float[3];
        Color.colorToHSV(color, hsv);
        hsv[2] *= 0.8f; // value component
        return Color.HSVToColor(hsv);
    }

    public static int getInverseRGBColor(int color){
        int oppositeRed = 255 - Color.red(color);
        int oppositeGreen = 255 - Color.green(color);
        int oppositeBlue = 255 - Color.blue(color);
        return Color.rgb(oppositeRed,oppositeGreen,oppositeBlue);
    }

    public static int setAplaOfColor(int color,int alpha){
        return Color.argb(alpha, Color.red(color),Color.green(color),Color.blue(color));
    }

    public static int getPXFromDP(Context context, int dp){
        Resources r = context.getResources();
        float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics());
        return (int) px;
    }
}
