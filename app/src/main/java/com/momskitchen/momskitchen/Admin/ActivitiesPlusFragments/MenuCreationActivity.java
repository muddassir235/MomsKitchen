package com.momskitchen.momskitchen.Admin.ActivitiesPlusFragments;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.DialogFragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.momskitchen.momskitchen.Admin.MealsListFragmentsPagerAdapter;
import com.momskitchen.momskitchen.R;
import com.momskitchen.momskitchen.backend.MenuCreator;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MenuCreationActivity extends AppCompatActivity implements MenuCreationMealsList.OnListFragmentInteractionListener {

    ViewPager mViewPager;
    TabLayout mTabLayout;
    private MealsListFragmentsPagerAdapter mealsListFragmentsPagerAdapter;

    static TextView mStartDateTV;
    static TextView mEndDateTV;
    Switch mRepeatWholeWeekSwitch;
    Switch mRepeatWholeMonthSwitch;

    Button mMoreOptionsButton;
    RelativeLayout mEndDateLayout;
    RelativeLayout mRepeatWholeWeekLayout;
    RelativeLayout mRepeatWholeMonthLayout;

    static public String startDate;
    static public String endDate;
    public Boolean repeatForWholeWeek;
    public Boolean repeatForWholeMonth;

    boolean justToggledRepeatWholeWeek;
    boolean justToggledRepeatWholeMonth;
    public static boolean weekEndIsNearerThanMonthEnd;
    public static boolean monthEndIsNearerThanWeekEnd;

    public static List<String> dates;
    static List<String> prevDates;

    boolean allOptionsVisible;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_creation);
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        bindViews();

        justToggledRepeatWholeMonth = false;
        justToggledRepeatWholeMonth = false;

        mTabLayout = (TabLayout) findViewById(R.id.tabs_menu_creation);
        mViewPager = (ViewPager) findViewById(R.id.meal_container_menu_creation);

        repeatForWholeWeek = false;
        repeatForWholeMonth = false;

        mealsListFragmentsPagerAdapter = new MealsListFragmentsPagerAdapter(getSupportFragmentManager(),MealsListFragmentsPagerAdapter.MENU_CREATION_MEALS_LIST);

        if(mViewPager!=null){
            mViewPager.setAdapter(mealsListFragmentsPagerAdapter);
            mViewPager.setCurrentItem(1);
        }

        mTabLayout.setupWithViewPager(mViewPager);

        allOptionsVisible = false;

        startDate = getIntent().getStringExtra("CurrentDate");
        endDate = startDate;
        dates = MenuCreator.getInstance().getDatesBetweenStartAndEndDate(startDate,endDate);
        String readableDate = getReadableDateFromCalendar(MenuCreator.getInstance().getCalendarFromDate(startDate));
        mStartDateTV.setText(readableDate);
        mEndDateTV.setText(readableDate);
        addPrevDates();

        ((ImageButton) findViewById(R.id.cancel_menu_creation_button)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        findWhichIsNearerWeekEndOrMonthEnd();

        mRepeatWholeWeekSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                justToggledRepeatWholeMonth = false;
                justToggledRepeatWholeWeek = true;
                if(!repeatForWholeWeek) {
                    List<String> tempDates = MenuCreator.getInstance().getRemainingDatesOfWeek(startDate);
                    String tempEndDate = tempDates.get(tempDates.size()-1);
                    if(Integer.valueOf(tempEndDate)>Integer.valueOf(endDate)){
                        MenuCreationMealsList.lunchListAdapter.notifyDataSetChanged();
                        MenuCreationMealsList.dessertListAdapter.notifyDataSetChanged();
                        MenuCreationMealsList.complimentListAdapter.notifyDataSetChanged();
                        endDate = tempEndDate;
                        mEndDateTV.setText(AdminMainActivity.DatePickerFragment.getReadableDateFromCalendar(MenuCreator.getInstance().getCalendarFromDate(endDate)));
                        if((monthEndIsNearerThanWeekEnd && !repeatForWholeMonth)||weekEndIsNearerThanMonthEnd) {
                            prevDates = new ArrayList<String>();
                            for (String date : dates) {
                                prevDates.add(date);
                            }
                        }
                        dates = tempDates;
                    }else {
                        justToggledRepeatWholeWeek = false;
                    }
                    repeatForWholeWeek = true;
                }else{
                    if(justToggledRepeatWholeWeek&&!repeatForWholeMonth&&weekEndIsNearerThanMonthEnd){
                        if(prevDates!=null) {
                            MenuCreationMealsList.lunchListAdapter.notifyDataSetChanged();
                            MenuCreationMealsList.dessertListAdapter.notifyDataSetChanged();
                            MenuCreationMealsList.complimentListAdapter.notifyDataSetChanged();
                            dates = new ArrayList<String>();
                            for (String date : prevDates) {
                                dates.add(date);
                            }
                            startDate = dates.get(0);
                            endDate = dates.get(dates.size() - 1);
                            mStartDateTV.setText(getReadableDateFromCalendar(MenuCreator.getInstance().getCalendarFromDate(startDate)));
                            mEndDateTV.setText(getReadableDateFromCalendar(MenuCreator.getInstance().getCalendarFromDate(endDate)));
                        }
                    }
                    repeatForWholeWeek = false;
                }
            }
        });

        mStartDateTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment dialogFragment = new StartDatePickerFragment();
                dialogFragment.show(getSupportFragmentManager(),"startdate");
                justToggledRepeatWholeWeek = false;
                justToggledRepeatWholeMonth = false;
            }
        });

        mEndDateTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment dialogFragment = new EndDatePickerFragment();
                dialogFragment.show(getSupportFragmentManager(),"enddate");
                justToggledRepeatWholeWeek = false;
                justToggledRepeatWholeMonth = false;
            }
        });

        ((Button) findViewById(R.id.done_creating_menu_button)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MenuCreator.getInstance().addMeals(MenuCreationMealsList.mealsToAdd,dates);
                MenuCreator.getInstance().removeMeals(MenuCreationMealsList.mealsToRemove,dates);
                finish();
            }
        });

        mRepeatWholeMonthSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                justToggledRepeatWholeWeek = false;
                justToggledRepeatWholeMonth = true;
                if(!repeatForWholeMonth){
                    List<String> tempDates = MenuCreator.getInstance().getRemainingDatesOfMonth(startDate);
                    String tempEndDate = tempDates.get(tempDates.size()-1);
                    if(Integer.valueOf(tempEndDate)>Integer.valueOf(endDate)){
                        MenuCreationMealsList.lunchListAdapter.notifyDataSetChanged();
                        MenuCreationMealsList.dessertListAdapter.notifyDataSetChanged();
                        MenuCreationMealsList.complimentListAdapter.notifyDataSetChanged();
                        endDate = tempEndDate;
                        if((weekEndIsNearerThanMonthEnd && !repeatForWholeWeek)||monthEndIsNearerThanWeekEnd) {
                            prevDates = new ArrayList<String>();
                            for (String date : dates) {
                                prevDates.add(date);
                            }
                        }
                        dates = tempDates;
                        mEndDateTV.setText(AdminMainActivity.DatePickerFragment.getReadableDateFromCalendar(MenuCreator.getInstance().getCalendarFromDate(endDate)));
                    }else {
                        justToggledRepeatWholeMonth = false;
                    }
                    repeatForWholeMonth = true;
                }else{
                    if(justToggledRepeatWholeMonth&&!repeatForWholeWeek) {
                        if (prevDates != null) {
                            MenuCreationMealsList.lunchListAdapter.notifyDataSetChanged();
                            MenuCreationMealsList.dessertListAdapter.notifyDataSetChanged();
                            MenuCreationMealsList.complimentListAdapter.notifyDataSetChanged();
                            dates = new ArrayList<String>();
                            for (String date : prevDates) {
                                dates.add(date);
                            }
                            startDate = dates.get(0);
                            endDate = dates.get(dates.size() - 1);
                            mStartDateTV.setText(getReadableDateFromCalendar(MenuCreator.getInstance().getCalendarFromDate(startDate)));
                            mEndDateTV.setText(getReadableDateFromCalendar(MenuCreator.getInstance().getCalendarFromDate(endDate)));
                        }
                    }
                    repeatForWholeMonth = false;
                }
            }
        });

        mMoreOptionsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!allOptionsVisible){
                    mMoreOptionsButton.setText("LESS OPTIONS");
                    mEndDateLayout.setVisibility(View.VISIBLE);
                    mRepeatWholeWeekLayout.setVisibility(View.VISIBLE);
                    mRepeatWholeMonthLayout.setVisibility(View.VISIBLE);
                    allOptionsVisible = true;
                }else{
                    mMoreOptionsButton.setText("MORE OPTIONS");
                    mEndDateLayout.setVisibility(View.GONE);
                    mRepeatWholeWeekLayout.setVisibility(View.GONE);
                    mRepeatWholeMonthLayout.setVisibility(View.GONE);
                    allOptionsVisible = false;
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    void bindViews(){
        mMoreOptionsButton = (Button) findViewById(R.id.more_options_button);
        mEndDateLayout = (RelativeLayout) findViewById(R.id.to_date_layout);
        mRepeatWholeWeekLayout = (RelativeLayout) findViewById(R.id.repeat_whole_week_layout);
        mRepeatWholeMonthLayout = (RelativeLayout) findViewById(R.id.repeat_whole_month_layout);
        mStartDateTV = (TextView) findViewById(R.id.from_date_text_view);
        mEndDateTV = (TextView) findViewById(R.id.to_date_text_view);
        mRepeatWholeMonthSwitch = (Switch) findViewById(R.id.repeat_whole_month_switch);
        mRepeatWholeWeekSwitch = (Switch) findViewById(R.id.repeat_whole_week_switch);
    }

    @Override
    public void onListFragmentInteraction() {

    }

    public String getReadableDateFromCalendar(Calendar c){
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

    public static void findWhichIsNearerWeekEndOrMonthEnd(){
        if(Integer.valueOf(MenuCreator.getInstance().getLastDateOfWeek(startDate))>Integer.valueOf(MenuCreator.getInstance().getLastDateOfMonth(startDate))){
            monthEndIsNearerThanWeekEnd = true;
            weekEndIsNearerThanMonthEnd = false;
        }else if(Integer.valueOf(MenuCreator.getInstance().getLastDateOfWeek(startDate))<Integer.valueOf(MenuCreator.getInstance().getLastDateOfMonth(startDate))){
            weekEndIsNearerThanMonthEnd = true;
            monthEndIsNearerThanWeekEnd = false;
        }else{
            monthEndIsNearerThanWeekEnd = false;
            weekEndIsNearerThanMonthEnd = false;
        }
    }

    public static void addPrevDates(){
        prevDates = new ArrayList<String>();
        for(String date:dates){
            prevDates.add(date);
        }
    }

}
