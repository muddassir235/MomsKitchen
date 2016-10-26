package com.momskitchen.momskitchen.Admin.Adapters;

/**
 * Created by hp on 10/26/2016.
 */

public class RefinedDate {
    public String year;
    public String month;
    public String day;
    RefinedDate(String year, String month, String day){
        this.year=year;
        this.month=month;
        this.day=day;
    }
    RefinedDate(){
        this.year= new String();
        this.month = new String();
        this.day = new String();
    }

}