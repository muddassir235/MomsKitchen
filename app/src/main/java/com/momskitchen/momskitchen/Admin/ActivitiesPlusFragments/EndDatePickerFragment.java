package com.momskitchen.momskitchen.Admin.ActivitiesPlusFragments;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.widget.DatePicker;

import com.momskitchen.momskitchen.Admin.ActivitiesPlusFragments.MenuCreationActivity;
import com.momskitchen.momskitchen.Admin.ActivitiesPlusFragments.MenuCreationMealsList;
import com.momskitchen.momskitchen.backend.MenuCreator;

import java.util.Calendar;

/**
 * Created by hp on 9/5/2016.
 */
public class EndDatePickerFragment extends DialogFragment
        implements DatePickerDialog.OnDateSetListener {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current date as the default date in the picker
        int year = Integer.valueOf(MenuCreationActivity.endDate.substring(0,4));
        int month = Integer.valueOf(MenuCreationActivity.endDate.substring(4,6))-1;
        int day = Integer.valueOf(MenuCreationActivity.endDate.substring(6,8));

        // Create a new instance of DatePickerDialog and return it
        return new DatePickerDialog(getActivity(), this, year, month, day);
    }

    public void onDateSet(DatePicker view, int year, int month, int day) {
        Calendar calendar= Calendar.getInstance();
        calendar.set(year,month,day);
        MenuCreationActivity.endDate = MenuCreator.getInstance().getDateFromCalendar(calendar);
        String dateString = getReadableDateFromCalendar(calendar);
        if(Integer.valueOf(MenuCreationActivity.startDate)>Integer.valueOf(MenuCreationActivity.endDate)){
            MenuCreationActivity.startDate = MenuCreationActivity.endDate;
            MenuCreationActivity.findWhichIsNearerWeekEndOrMonthEnd();
            MenuCreationActivity.mStartDateTV.setText(dateString);
        }
        MenuCreationActivity.mEndDateTV.setText(dateString);
        MenuCreationActivity.dates = MenuCreator.getInstance().
                getDatesBetweenStartAndEndDate(MenuCreationActivity.startDate,MenuCreationActivity.endDate);
        MenuCreationActivity.addPrevDates();
        MenuCreationMealsList.lunchListAdapter.notifyDataSetChanged();
        MenuCreationMealsList.dessertListAdapter.notifyDataSetChanged();
        MenuCreationMealsList.complimentListAdapter.notifyDataSetChanged();
    }

    public static String getReadableDateFromCalendar(Calendar c){
        String monthString;
        String dayString;
        int month = c.get(Calendar.MONTH);
        switch (month){
            case Calendar.JANUARY: monthString = "Jan";break;
            case Calendar.FEBRUARY: monthString = "Feb";break;
            case Calendar.MARCH: monthString = "Mar";break;
            case Calendar.APRIL: monthString = "Apr";break;
            case Calendar.MAY: monthString = "May";break;
            case Calendar.JUNE: monthString = "Jun";break;
            case Calendar.JULY: monthString = "Jul";break;
            case Calendar.AUGUST: monthString = "Aug";break;
            case Calendar.SEPTEMBER: monthString = "Sep";break;
            case Calendar.OCTOBER: monthString = "Oct";break;
            case Calendar.NOVEMBER: monthString = "Nov";break;
            default: monthString = "Dec";break;
        }
        int day = c.get(Calendar.DAY_OF_MONTH);
        int dayOfWeek = c.get(Calendar.DAY_OF_WEEK);
        Calendar calendarForWeek = Calendar.getInstance();
        int today = calendarForWeek.get(Calendar.DAY_OF_WEEK);
        Log.v("Compare: ",""+ Calendar.SUNDAY+" and "+today+" and "+dayOfWeek );
        calendarForWeek.add(Calendar.DAY_OF_WEEK, 1);
        int tommorow = calendarForWeek.get(Calendar.DAY_OF_WEEK);

        if(dayOfWeek == today){
            //Log.v("Date", " Today "+dayOfWeek+" "+today);
            dayString = "Today";
        }else if(dayOfWeek == tommorow ){
            dayString = "Tommorrow";
        }else if(dayOfWeek == Calendar.MONDAY){
            dayString = "Mon";
        }else if (dayOfWeek == Calendar.TUESDAY){
            dayString = "Tue";
        }else if (dayOfWeek == Calendar.WEDNESDAY){
            dayString = "Wed";
        }else if (dayOfWeek == Calendar.THURSDAY){
            dayString = "Thu";
        }else if (dayOfWeek == Calendar.FRIDAY){
            dayString = "Fri";
        }else if (dayOfWeek == Calendar.SATURDAY){
            dayString = "Sat";
        }else{
            //Log.v("Date", " Sunday"+" "+dayOfWeek+" "+Calendar.SUNDAY);
            dayString = "Sun";
        }
        int year = c.get(Calendar.YEAR);

        return dayString+", "+monthString+" "+day+", "+year;
    }


}
