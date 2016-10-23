package com.momskitchen.momskitchen.backend;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.momskitchen.momskitchen.model.MealItem;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

public class MenuCreator{
	static MenuCreator menuCreator;

	public static MenuCreator getInstance(){
		//TODO:get instance
		if(menuCreator==null){
			menuCreator = new MenuCreator();
			return menuCreator;
		}else{
			return menuCreator;
		}
	}

	// @param: date in "20YYMMDD"
	// @return: the start date of the week with trailing prefix "week"
	//          in the this format ("week20YYMMDD")
	public String getWeekStartFromDate(String date){
        int year = Integer.valueOf(date.substring(0,4));
        int month = Integer.valueOf(date.substring(4,6))-1;
        int day = Integer.valueOf(date.substring(6,8));
        Calendar calendar = Calendar.getInstance();
        calendar.set(year,month,day);

        if(calendar.get(Calendar.DAY_OF_WEEK)== Calendar.MONDAY){
            //DO NOTHING WE ARE AT THE FIRST DAY OF THE WEEK
        }else if(calendar.get(Calendar.DAY_OF_WEEK) == Calendar.TUESDAY){
            calendar.add(Calendar.DATE,-1);
        }else if(calendar.get(Calendar.DAY_OF_WEEK) == Calendar.WEDNESDAY){
            calendar.add(Calendar.DATE,-2);
        }else if(calendar.get(Calendar.DAY_OF_WEEK) == Calendar.THURSDAY){
            calendar.add(Calendar.DATE,-3);
        }else if(calendar.get(Calendar.DAY_OF_WEEK) == Calendar.FRIDAY){
            calendar.add(Calendar.DATE,-4);
        }else if(calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY){
            calendar.add(Calendar.DATE,-5);
        }else if(calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY){
            calendar.add(Calendar.DATE,-6);
        }

        String yearString;
        String monthString;
        String dayString;

        yearString = ""+calendar.get(Calendar.YEAR);

        if((calendar.get(Calendar.MONTH)+1)<10){
            monthString = "0"+(calendar.get(Calendar.MONTH)+1);
        }else{
            monthString = ""+(calendar.get(Calendar.MONTH)+1);
        }

        if(calendar.get(Calendar.DAY_OF_MONTH)<10){
            dayString = "0"+calendar.get(Calendar.DAY_OF_MONTH);
        }else{
            dayString = ""+calendar.get(Calendar.DAY_OF_MONTH);
        }

        return "week"+yearString+monthString+dayString;
	}

    public String getLastDateOfWeek(String startDate){
        Calendar calendar = getCalendarFromDate(startDate);
        calendar.set(Calendar.DAY_OF_WEEK,Calendar.SUNDAY);
        return getDateFromCalendar(calendar);
    }

    public String getLastDateOfMonth(String startDate){
        Calendar calendar = getCalendarFromDate(startDate);
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        while(calendar.get(Calendar.MONTH)== month){
            day = calendar.get(Calendar.DAY_OF_MONTH);
            calendar.add(Calendar.DATE,1);
        }
        calendar.set(Calendar.YEAR,year);
        calendar.set(Calendar.MONTH,month);
        calendar.set(Calendar.DAY_OF_MONTH,day);

        return getDateFromCalendar(calendar);
    }

    public List<String> getAllWeekDates(String currDate){
        String weekDate = getWeekStartFromDate(currDate);
        List<String> weekDates = new ArrayList<>();
        weekDates.add(getDateFromWeekStartAndDay(weekDate,"MON"));
        weekDates.add(getDateFromWeekStartAndDay(weekDate,"TUE"));
        weekDates.add(getDateFromWeekStartAndDay(weekDate,"WED"));
        weekDates.add(getDateFromWeekStartAndDay(weekDate,"THU"));
        weekDates.add(getDateFromWeekStartAndDay(weekDate,"FRI"));
        weekDates.add(getDateFromWeekStartAndDay(weekDate,"SAT"));
        weekDates.add(getDateFromWeekStartAndDay(weekDate,"SUN"));
        return weekDates;
    }

	// @param: week start date in the format "week20YYMMDD"
	// @param: day ("MON", "TUE", "WED", "THU", "FRI", "SAT" or "SUN")
	// @return: date in the following format ("20YYMMDD")
	public String getDateFromWeekStartAndDay(String weekStart, String dayStr){
        int year = Integer.valueOf(weekStart.substring(4,8));
        int month = Integer.valueOf(weekStart.substring(8,10))-1;
        int day = Integer.valueOf(weekStart.substring(10,12));
        Calendar calendar = Calendar.getInstance();
        calendar.set(year,month,day);

        if(dayStr.equals("MON")){
            //DO NOTHING WE ARE AT THE FIRST DAY OF THE WEEK
        }else if(dayStr.equals("TUE")){
            calendar.add(Calendar.DATE,1);
        }else if(dayStr.equals("WED")){
            calendar.add(Calendar.DATE,2);
        }else if(dayStr.equals("THU")){
            calendar.add(Calendar.DATE,3);
        }else if(dayStr.equals("FRI")){
            calendar.add(Calendar.DATE,4);
        }else if(dayStr.equals("SAT")){
            calendar.add(Calendar.DATE,5);
        }else if(dayStr.equals("SUN")){
            calendar.add(Calendar.DATE,6);
        }

        String yearString;
        String monthString;
        String dayString;

        yearString = ""+calendar.get(Calendar.YEAR);

        if((calendar.get(Calendar.MONTH)+1)<10){
            monthString = "0"+(calendar.get(Calendar.MONTH)+1);
        }else{
            monthString = ""+(calendar.get(Calendar.MONTH)+1);
        }

        if(calendar.get(Calendar.DAY_OF_MONTH)<10){
            dayString = "0"+calendar.get(Calendar.DAY_OF_MONTH);
        }else{
            dayString = ""+calendar.get(Calendar.DAY_OF_MONTH);
        }

        return yearString+monthString+dayString;
	}

    public String getDateFromWeekStartAndDay(String weekStart, int dayNumber){
        int year = Integer.valueOf(weekStart.substring(4,8));
        int month = Integer.valueOf(weekStart.substring(8,10))-1;
        int day = Integer.valueOf(weekStart.substring(10,12));
        Calendar calendar = Calendar.getInstance();
        calendar.set(year,month,day);

        calendar.add(Calendar.DATE,dayNumber);

        String yearString;
        String monthString;
        String dayString;

        yearString = ""+calendar.get(Calendar.YEAR);

        if((calendar.get(Calendar.MONTH)+1)<10){
            monthString = "0"+(calendar.get(Calendar.MONTH)+1);
        }else{
            monthString = ""+(calendar.get(Calendar.MONTH)+1);
        }

        if(calendar.get(Calendar.DAY_OF_MONTH)<10){
            dayString = "0"+calendar.get(Calendar.DAY_OF_MONTH);
        }else{
            dayString = ""+calendar.get(Calendar.DAY_OF_MONTH);
        }

        return yearString+monthString+dayString;
    }

    public String weekDayNumberToString(int dayNumber){
        switch (dayNumber){
            case 0:return "MON";
            case 1:return "TUE";
            case 2:return "WED";
            case 3:return "THU";
            case 4:return "FRI";
            case 5:return "SAT";
            case 6:return "SUN";
            default:
                try {
                    throw new Exception("week day index out of bound week only has seven days");
                } catch (Exception e) {
                    e.printStackTrace();
                }
        }
        return null;
    }

    public int weekDayStringToNumber(String dayStr){
        if(dayStr.equals("MON")){
            return 0;
        }else if(dayStr.equals("TUE")){
            return 1;
        }else if(dayStr.equals("WED")){
            return 2;
        }else if(dayStr.equals("THU")){
            return 3;
        }else if(dayStr.equals("FRI")){
            return 4;
        }else if(dayStr.equals("SAT")){
            return 5;
        }else if(dayStr.equals("SUN")){
            return 6;
        }else {
            try {
                throw new Exception("in valid week day string");
            } catch (Exception e) {
                e.printStackTrace();
            }
            return -1;
        }
    }

	// @param: date in "20YYMMDD"
    // @return: day ("MON", "TUE", "WED", "THU", "FRI", "SAT" or "SUN")
	public String getDayFromDate(String date){
        int year = Integer.valueOf(date.substring(0,4));
        int month = Integer.valueOf(date.substring(4,6))-1;
        int day = Integer.valueOf(date.substring(6,8));
        Calendar calendar = Calendar.getInstance();
        calendar.set(year,month,day);

        switch (calendar.get(Calendar.DAY_OF_WEEK)){
            case Calendar.MONDAY:return "MON";
            case Calendar.TUESDAY:return "TUE";
            case Calendar.WEDNESDAY:return "WED";
            case Calendar.THURSDAY:return "THU";
            case Calendar.FRIDAY:return "FRI";
            case Calendar.SATURDAY:return "SAT";
            case Calendar.SUNDAY:return "SUN";
            default:return null;
        }
	}

    public String getNextWeek(String currWeek){
        int year = Integer.valueOf(currWeek.substring(4,8));
        int month = Integer.valueOf(currWeek.substring(8,10))-1;
        int day = Integer.valueOf(currWeek.substring(10,12));
        Calendar calendar = Calendar.getInstance();
        calendar.set(year,month,day);

        calendar.add(Calendar.DATE,7);

        String yearString;
        String monthString;
        String dayString;

        yearString = ""+calendar.get(Calendar.YEAR);

        if((calendar.get(Calendar.MONTH)+1)<10){
            monthString = "0"+(calendar.get(Calendar.MONTH)+1);
        }else{
            monthString = ""+(calendar.get(Calendar.MONTH)+1);
        }

        if(calendar.get(Calendar.DAY_OF_MONTH)<10){
            dayString = "0"+calendar.get(Calendar.DAY_OF_MONTH);
        }else{
            dayString = ""+calendar.get(Calendar.DAY_OF_MONTH);
        }

        return "week"+yearString+monthString+dayString;
    }

    public String getDateFromCalendar(Calendar calendar){
        Calendar localCalendar = Calendar.getInstance();
        localCalendar.set(calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH));
        String yearString;
        String monthString;
        String dayString;

        yearString = ""+localCalendar.get(Calendar.YEAR);

        if((localCalendar.get(Calendar.MONTH)+1)<10){
            monthString = "0"+(localCalendar.get(Calendar.MONTH)+1);
        }else{
            monthString = ""+(localCalendar.get(Calendar.MONTH)+1);
        }

        if(localCalendar.get(Calendar.DAY_OF_MONTH)<10){
            dayString = "0"+localCalendar.get(Calendar.DAY_OF_MONTH);
        }else{
            dayString = ""+localCalendar.get(Calendar.DAY_OF_MONTH);
        }
        Log.v("CalendarToDate: ",yearString+monthString+dayString);
        return yearString+monthString+dayString;
    }

    public Calendar getCalendarFromDate(String date){
        int year = Integer.valueOf(date.substring(0,4));
        int month = Integer.valueOf(date.substring(4,6))-1;
        int day = Integer.valueOf(date.substring(6,8));
        Calendar calendar = Calendar.getInstance();
        calendar.set(year,month,day);

        Log.v("DateToCalendar:", ""+year+" "+month+" "+day);
        return calendar;
    }

    public List<String> getListOfRemainingWeekDays(String date){
        Calendar calendar = getCalendarFromDate(date);
        List<String> remainingDays = new ArrayList<>();
        switch (calendar.get(Calendar.DAY_OF_WEEK)){
            case Calendar.MONDAY:{
                remainingDays.add("MON");
                remainingDays.add("TUE");
                remainingDays.add("WED");
                remainingDays.add("THU");
                remainingDays.add("FRI");
                remainingDays.add("SAT");
                remainingDays.add("SUN");

            }break;
            case Calendar.TUESDAY:{
                remainingDays.add("TUE");
                remainingDays.add("WED");
                remainingDays.add("THU");
                remainingDays.add("FRI");
                remainingDays.add("SAT");
                remainingDays.add("SUN");
            }break;
            case Calendar.WEDNESDAY:{
                remainingDays.add("WED");
                remainingDays.add("THU");
                remainingDays.add("FRI");
                remainingDays.add("SAT");
                remainingDays.add("SUN");
            }break;
            case Calendar.THURSDAY:{
                remainingDays.add("THU");
                remainingDays.add("FRI");
                remainingDays.add("SAT");
                remainingDays.add("SUN");
            }break;
            case Calendar.FRIDAY:{
                remainingDays.add("FRI");
                remainingDays.add("SAT");
                remainingDays.add("SUN");
            }break;
            case Calendar.SATURDAY:{
                remainingDays.add("SAT");
                remainingDays.add("SUN");
            }break;
            case Calendar.SUNDAY:{
                remainingDays.add("SUN");
            }break;
            default:{}break;
        }

        return remainingDays;
    }

    public List<String> getRemainingDatesOfMonth(String date){
        List<String> remaingDates = new ArrayList<>();
        Calendar calendar = getCalendarFromDate(date);
        int month = (calendar.get(Calendar.MONTH)+1);
        while((calendar.get(Calendar.MONTH)+1)==month){
            remaingDates.add(getDateFromCalendar(calendar));
            calendar.add(Calendar.DATE,1);
        }
        return remaingDates;
    }

    public List<String> getDatesBetweenStartAndEndDate(String startDate, String endDate){
        List<String> dates = new ArrayList<>();
        Calendar calendar = getCalendarFromDate(startDate);
        while(!getDateFromCalendar(calendar).equals(endDate)){
            dates.add(getDateFromCalendar(calendar));
            calendar.add(Calendar.DATE,1);
        }
        dates.add(endDate);
        return dates;
    }

    public List<String> getRemainingDatesOfWeek(String date){
        Calendar calendar = getCalendarFromDate(date);
        List<String> remainingDays = new ArrayList<>();
        switch (calendar.get(Calendar.DAY_OF_WEEK)){
            case Calendar.MONDAY:{
                for(int i=0;i<7;i++){
                    remainingDays.add(getDateFromCalendar(calendar));
                    calendar.add(Calendar.DATE,1);
                }
            }break;
            case Calendar.TUESDAY:{
                for(int i=0;i<6;i++){
                    remainingDays.add(getDateFromCalendar(calendar));
                    calendar.add(Calendar.DATE,1);
                }
            }break;
            case Calendar.WEDNESDAY:{
                for(int i=0;i<5;i++){
                    remainingDays.add(getDateFromCalendar(calendar));
                    calendar.add(Calendar.DATE,1);
                }
            }break;
            case Calendar.THURSDAY:{
                for(int i=0;i<4;i++){
                    remainingDays.add(getDateFromCalendar(calendar));
                    calendar.add(Calendar.DATE,1);
                }
            }break;
            case Calendar.FRIDAY:{
                for(int i=0;i<3;i++){
                    remainingDays.add(getDateFromCalendar(calendar));
                    calendar.add(Calendar.DATE,1);
                }
            }break;
            case Calendar.SATURDAY:{
                for(int i=0;i<2;i++){
                    remainingDays.add(getDateFromCalendar(calendar));
                    calendar.add(Calendar.DATE,1);
                }
            }break;
            case Calendar.SUNDAY:{
                for(int i=0;i<1;i++){
                    remainingDays.add(getDateFromCalendar(calendar));
                    calendar.add(Calendar.DATE,1);
                }
            }break;
            default:{}break;
        }

        return remainingDays;
    }
	// @param: meals- (a list of meals)
	// @param: dates- (the dates that this list of meals has to be appended to)
    public void addMeals(List<MealItem> meals, List<String> dates){
        // TODO: Add meals to Firebase
        // The meals will be added to those specific dates in the following format
        // "Menu" -> "week20YYMMDD" -> "DAY(e.g. MON, TUE)" -> append the list
        // 			      ^^^^^^^^ start date of the week
        for(MealItem mealItem:meals){
            if(mealItem.dates==null){
                mealItem.dates = new ArrayList<>();
            }
            boolean anythingChanged = false;
            for(String date:dates){
                if(!mealItem.dates.contains(date)){
                    mealItem.dates.add(date);
                    anythingChanged = true;
                }
            }
            if(anythingChanged) {
                FirebaseDatabase.getInstance().getReference()
                        .child("Meals")
                        .child(mealItem.catagory)
                        .child(mealItem.id)
                        .setValue(mealItem);
            }
        }
    }

    public void addMeal(MealItem mealItem, List<String> dates){
        if(mealItem.dates==null){
            mealItem.dates = new ArrayList<>();
        }
        boolean anythingChanged = false;
        for(String date:dates){
            if(!mealItem.dates.contains(date)){
                mealItem.dates.add(date);
                anythingChanged = true;
            }

        }
        if(anythingChanged){
            FirebaseDatabase.getInstance().getReference()
                    .child("Meals")
                    .child(mealItem.catagory)
                    .child(mealItem.id)
                    .setValue(mealItem);
        }
    }

    public void removeMeal(MealItem mealItem, List<String> dates){
        if(mealItem.dates==null){
            mealItem.dates =new ArrayList<>();
        }
        boolean anythingChanged = false;
        for(String date:dates){
            if(mealItem.dates.contains(date)){
                mealItem.dates.remove(date);
                anythingChanged = true;
            }

        }
        if(anythingChanged){
            FirebaseDatabase.getInstance().getReference()
                    .child("Meals")
                    .child(mealItem.catagory)
                    .child(mealItem.id)
                    .setValue(mealItem);
        }
    }

    public void removeMeals(List<MealItem> meals, List<String> dates){
        // TODO: Add meals to Firebase
        // The meals will be added to those specific dates in the following format
        // "Menu" -> "week20YYMMDD" -> "DAY(e.g. MON, TUE)" -> append the list
        // 			      ^^^^^^^^ start date of the week
        for(MealItem mealItem:meals){
            if(mealItem.dates==null){
                mealItem.dates = new ArrayList<>();
            }
            boolean anythingChanged = false;
            for(String date:dates){
                if(mealItem.dates.contains(date)){
                    mealItem.dates.remove(date);
                    anythingChanged = true;
                }
            }
            if(anythingChanged) {
                FirebaseDatabase.getInstance().getReference()
                        .child("Meals")
                        .child(mealItem.catagory)
                        .child(mealItem.id)
                        .setValue(mealItem);
            }
        }
    }
	// @param: meals- (a list of meals)
	// @param: dates- (the dates at which the meals have to be added 
	//         @constraint the dates have to lie within the same week
	//         if not throw exception)
	// @param: tweekRepetition- he number of future weeks that the menu has to be repeated
	public void addMeals(List<MealItem> meals, int weekRepetition, List<String>  dates) throws Exception {
		// TODO: Add meals to Firebase
		// The meals will be added to those specific dates in the following format
		// "Menu" -> "week20YYMMDD" -> "DAY(e.g. MON, TUE)" -> append the list
		// 			      ^^^^^^^^ start date of the week
        String weekStart = getWeekStartFromDate(dates.get(0));
        for(String date:dates){
            if(!weekStart.equals(getWeekStartFromDate(date))){
                throw new Exception("The dates have to be within the same week");
            }
        }

        List<String> days = new ArrayList<>();
        for(String date:dates){
            days.add(getDayFromDate(date));
        }

        for(int i=0;i<weekRepetition;i++){
            for(String day:days){
                String date = getDateFromWeekStartAndDay(weekStart,day);
                List<String> datesLocal = new ArrayList<>();
                datesLocal.add(date);
                addMeals(meals,datesLocal);
            }
            weekStart = getNextWeek(weekStart);
        }
	}

    public void addMeal(MealItem mealItem, int weekRepetition, List<String>  dates) throws Exception {
        // TODO: Add meals to Firebase
        // The meals will be added to those specific dates in the following format
        // "Menu" -> "week20YYMMDD" -> "DAY(e.g. MON, TUE)" -> append the list
        // 			      ^^^^^^^^ start date of the week
        String weekStart = getWeekStartFromDate(dates.get(0));
        for(String date:dates){
            if(!weekStart.equals(getWeekStartFromDate(date))){
                throw new Exception("The dates have to be within the same week");
            }
        }

        List<String> days = new ArrayList<>();
        for(String date:dates){
            days.add(getDayFromDate(date));
        }

        for(int i=0;i<weekRepetition;i++){
            for(String day:days){
                String date = getDateFromWeekStartAndDay(weekStart,day);
                List<String> datesLocal = new ArrayList<>();
                datesLocal.add(date);
                addMeal(mealItem,datesLocal);
            }
            weekStart = getNextWeek(weekStart);
        }
    }

    public void addMealsForWholeWeek(List<MealItem> meals){
        Calendar calendar = Calendar.getInstance();
        String weekStart = getWeekStartFromDate(getDateFromCalendar(calendar));
        List<String> remainingDays = getListOfRemainingWeekDays(getDateFromCalendar(calendar));
        for(String day:remainingDays){
            String date = getDateFromWeekStartAndDay(weekStart,day);
            List<String> datesLocal = new ArrayList<>();
            datesLocal.add(date);
            addMeals(meals,datesLocal);
        }
    }

    public void addMealForWholeWeek(MealItem mealItem){
        Calendar calendar = Calendar.getInstance();
        String weekStart = getWeekStartFromDate(getDateFromCalendar(calendar));
        List<String> remainingDays = getListOfRemainingWeekDays(getDateFromCalendar(calendar));
        for(String day:remainingDays){
            String date = getDateFromWeekStartAndDay(weekStart,day);
            List<String> datesLocal = new ArrayList<>();
            datesLocal.add(date);
            addMeal(mealItem,datesLocal);
        }
    }

	// @param: meals- (a list of meals)
	// @param: dates- (the dates at which the meals have to be added 
	//         @constraint the dates have to lie within the same week
	//         if not throw exception)
	// @param: repeatForTheWholeMonth- repeat this for the current and the remaining weeks of the month
	public void addMealsForWholeMonth(List<MealItem> meals){
		// TODO: Add meals to Firebase
		// The meals will be added to those specific dates in the following format
		// "Menu" -> "week20YYMMDD" -> "DAY(e.g. MON, TUE)" -> append the list
		// 			      ^^^^^^^^ start date of the week
        Calendar calendar = Calendar.getInstance();
        String date = getDateFromCalendar(calendar);
        String weekStart = getWeekStartFromDate(getDateFromCalendar(calendar));
        int month = (calendar.get(Calendar.MONTH)+1);
        while((calendar.get(Calendar.MONTH)+1)==month){
            List<String> remainingDaysInCurrentWeek = getListOfRemainingWeekDays(date);
            for(String day:remainingDaysInCurrentWeek){
                if((getCalendarFromDate(getDateFromWeekStartAndDay(weekStart,day)).get(Calendar.MONTH) +1)== month){
                    String date1 = getDateFromWeekStartAndDay(weekStart,day);
                    List<String> datesLocal = new ArrayList<>();
                    datesLocal.add(date1);
                    addMeals(meals,datesLocal);
                }else{
                    break;
                }
            }
            weekStart = getNextWeek(weekStart);
            date = weekStart;
            calendar = getCalendarFromDate(weekStart);
        }
	}

    public void addMealForWholeMonth(MealItem mealItem){
        // TODO: Add meals to Firebase
        // The meals will be added to those specific dates in the following format
        // "Menu" -> "week20YYMMDD" -> "DAY(e.g. MON, TUE)" -> append the list
        // 			      ^^^^^^^^ start date of the week
        Calendar calendar = Calendar.getInstance();
        String date = getDateFromCalendar(calendar);
        String weekStart = getWeekStartFromDate(getDateFromCalendar(calendar));
        int month = calendar.get(Calendar.MONTH)+1;
        while((calendar.get(Calendar.MONTH)+1)==month){
            List<String> remainingDaysInCurrentWeek = getListOfRemainingWeekDays(date);
            for(String day:remainingDaysInCurrentWeek){
                if((getCalendarFromDate(getDateFromWeekStartAndDay(weekStart,day)).get(Calendar.MONTH)+1) == month){
                    String date1 = getDateFromWeekStartAndDay(weekStart,day);
                    List<String> datesLocal = new ArrayList<>();
                    datesLocal.add(date1);
                    addMeal(mealItem,datesLocal);
                }else{
                    break;
                }
            }
            weekStart = getNextWeek(weekStart);
            date = weekStart;
            calendar = getCalendarFromDate(weekStart);
        }
    }

	// @param : a hashmap of menus with the days (e.g. "MON", "TUE", "WED", "THU" ...) as the key
	// @param : the number of weeks including the current week for which these meals have 
	//          to be repeated
    // @param : weekRepetition the number of weeks including current to repeat this list of menus
	public void addMealsWeekly(HashMap<String,List<MealItem>> weekMeals, int weekRepetition){
		// TODO: Add meals to Firebase
		// The meals will be added to those specific dates in the following format
		// "Menu" -> "week20YYMMDD" -> "DAY(e.g. MON, TUE)" -> append the list
		// 			      ^^^^^^^^ start date of the week
	}

	// @param : a hashmap of menus with dates (in the following format "20YYMMDD") as keys 
	//          make sure that all the dates are of the same month otherwise throw an exception
	// @param : the number of months including the current for which the menus have to be repeated 
	public void addMealsMonthly(HashMap<String,List<MealItem>> monthMeals, int monthRepetition){
		// TODO: Add meals to Firebase
		// The meals will be added to those specific dates in the following format
		// "Menu" -> "week20YYMMDD" -> "DAY(e.g. MON, TUE)" -> append the list
		// 			      ^^^^^^^^ start date of the week
	}

	// @param: meals (list of meals)
	// @param: dateStart - to - dateEnd adds these meals to the menu all the dates
	//         between the start and the end including the start and the end dates
	public void addMeals(List<MealItem> meals, String dateStart, String dateEnd){
		// TODO: Add meals to Firebase
		// The meals will be added to those specific dates in the following format
		// "Menu" -> "week20YYMMDD" -> "DAY(e.g. MON, TUE)" -> append the list
		// 			      ^^^^^^^^ start date of the week
        Calendar calendar = getCalendarFromDate(dateStart);
        do{
            String date = getDateFromCalendar(calendar);
            List<String> datesLocal = new ArrayList<>();
            datesLocal.add(date);
            addMeals(meals,datesLocal);
            calendar.add(Calendar.DATE,1);
        }while(!getDateFromCalendar(calendar).equals(dateEnd));
	}

    public void addMeal(MealItem mealItem, String dateStart, String dateEnd){
        // TODO: Add meals to Firebase
        // The meals will be added to those specific dates in the following format
        // "Menu" -> "week20YYMMDD" -> "DAY(e.g. MON, TUE)" -> append the list
        // 			      ^^^^^^^^ start date of the week
        Calendar calendar = getCalendarFromDate(dateStart);
        do{
            String date = getDateFromCalendar(calendar);
            List<String> datesLocal = new ArrayList<>();
            datesLocal.add(date);
            addMeal(mealItem,datesLocal);
            calendar.add(Calendar.DATE,1);
        }while(!getDateFromCalendar(calendar).equals(dateEnd));
    }

	// @param: meals (list of meals)
	// @param: startDate - to endDate make sure that the start date and end date lie within the same week
	//         otherwise throw an exception
	public void addMeals(List<MealItem> meals, String dateStart, String dateEnd, int weekRepetition) throws Exception {
		// TODO: Add meals to Firebase
		// The meals will be added to those specific dates in the following format
		// "Menu" -> "week20YYMMDD" -> "DAY(e.g. MON, TUE)" -> append the list
		// 			      ^^^^^^^^ start date of the week
        List<String> dates = new ArrayList<>();
        Calendar calendar = getCalendarFromDate(dateStart);
        do{
            dates.add(getDateFromCalendar(calendar));
            calendar.add(Calendar.DATE,1);
        }while(!getDateFromCalendar(calendar).equals(dateEnd));
        String weekStart = getWeekStartFromDate(dates.get(0));
        for(String date:dates){
            if(!weekStart.equals(getWeekStartFromDate(date))){
                throw new Exception("The dates have to be within the same week");
            }
        }

        List<String> days = new ArrayList<>();
        for(String date:dates){
            days.add(getDayFromDate(date));
        }

        for(int i=0;i<weekRepetition;i++){
            for(String day:days){
                String date = getDateFromWeekStartAndDay(weekStart,day);
                List<String> datesLocal = new ArrayList<>();
                datesLocal.add(date);
                addMeals(meals,datesLocal);
            }
            weekStart = getNextWeek(weekStart);
        }
	}

    public void addMeal(MealItem mealItem, String dateStart, String dateEnd, int weekRepetition) throws Exception {
        // TODO: Add meals to Firebase
        // The meals will be added to those specific dates in the following format
        // "Menu" -> "week20YYMMDD" -> "DAY(e.g. MON, TUE)" -> append the list
        // 			      ^^^^^^^^ start date of the week
        List<String> dates = new ArrayList<>();
        Calendar calendar = getCalendarFromDate(dateStart);
        do{
            dates.add(getDateFromCalendar(calendar));
            calendar.add(Calendar.DATE,1);
        }while(!getDateFromCalendar(calendar).equals(dateEnd));
        String weekStart = getWeekStartFromDate(dates.get(0));
        for(String date:dates){
            if(!weekStart.equals(getWeekStartFromDate(date))){
                throw new Exception("The dates have to be within the same week");
            }
        }

        List<String> days = new ArrayList<>();
        for(String date:dates){
            days.add(getDayFromDate(date));
        }

        for(int i=0;i<weekRepetition;i++){
            for(String day:days){
                String date = getDateFromWeekStartAndDay(weekStart,day);
                List<String> datesLocal = new ArrayList<>();
                datesLocal.add(date);
                addMeal(mealItem,datesLocal);

            }
            weekStart = getNextWeek(weekStart);
        }
    }

    // @param: meals (a list of meals)
    public void saveMenu(List<MealItem> meals){
    	// in a seperate node save a menu for later use.
    }

	// @param: the date for the which the menu has to be deleted
	public void deleteMenu(String date){
		// TODO: delete the menu at the specific date
		// go to : "week20YYMMDD" (get this using the "getStartFromDate" function)
		//         -> day ("MON", "TUE", "WED", "THU", "FRI", "SAT" or "SUN")
		//                - get this using the "getDayFromDate" function
		//         -> null
	}

	// @param: the date at which the menu is present
	// @param: the meal ids that have to be deleted
	public void deleteMeals(String date, List<MealItem> meals){
		// TODO: delete the specific meals at that date
		// go to : "week20YYMMDD" (get this using the "getStartFromDate" function)
		//         -> day ("MON", "TUE", "WED", "THU", "FRI", "SAT" or "SUN")
		//                - get this using the "getDayFromDate" function
		//         -> mealIds -> null
        for(MealItem mealItem:meals){
            if(mealItem.dates.contains(date)) {
                mealItem.dates.remove(date);
                FirebaseDatabase.getInstance().getReference()
                        .child("Meals")
                        .child(mealItem.catagory)
                        .child(mealItem.id)
                        .setValue(mealItem);
            }
        }
	}

}