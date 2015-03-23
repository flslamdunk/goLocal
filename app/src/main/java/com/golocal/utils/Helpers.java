package com.golocal.utils;

import com.golocal.activity.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Created by lfan on 9/18/14.
 */
public class Helpers {

    public static int categoryToResource(String category){
        category = category.toUpperCase();
        if(category.equals("SPORTS")){
            return R.drawable.summer;
        }else if(category.equals("FOOD")){
            return R.drawable.food;
        }else if(category.equals("MUSIC")){
            return R.drawable.music;
        }else if(category.equals("ART")){
            return R.drawable.art;
        }else if(category.equals("FESTIVAL")){
            return R.drawable.festival;
        }else{
            return R.drawable.location;
        }
    }

    public static Date weekIndexToDate(int weekIndex) {
        Calendar cal = new GregorianCalendar();
        cal.setTime(getBaseDate());
        cal.add(Calendar.WEEK_OF_YEAR, weekIndex+1);
        cal.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
        return cal.getTime();
    }

    public static int dateToWeeksIndex(Date date) {
        Calendar c1 = new GregorianCalendar();
        c1.setTime(getBaseDate());
        int startWeek = c1.get(Calendar.WEEK_OF_YEAR);

        Calendar c2 = new GregorianCalendar();
        c1.setTime(date);
        int endWeek = c2.get(Calendar.WEEK_OF_YEAR);

        return endWeek - startWeek;
    }

    public static String getDateString(int weekIndex) {
        return Constants.monthDayFormatter.format(weekIndexToDate(weekIndex));
    }

    private static Date getBaseDate(){
        String dateInString = Constants.BASE_DATE;
        Date base = null;
        try {
            base = Constants.yearMonthDayFormatter.parse(dateInString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return base;
    }

    public static String getLikedKey(int eventId){
       return Constants.LIKED_PREFIX + eventId;
    }

    public static String getDislikedKey(int eventId){
        return Constants.DISLIKED_PREFIX + eventId;
    }
}
