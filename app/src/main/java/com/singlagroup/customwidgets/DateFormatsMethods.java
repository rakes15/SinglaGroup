package com.singlagroup.customwidgets;

import android.text.format.DateFormat;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

/**
 * Created by Rakesh on 16-Jan-17.
 */

public class DateFormatsMethods {
    private static String TAG = DateFormatsMethods.class.getSimpleName();

    public static String DateFormat_DD_MM_YYYY(String InputDate){
        String OutputDate="";
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date date = sdf.parse(InputDate);

            sdf = new SimpleDateFormat("dd-MM-yyyy");

            OutputDate = sdf.format(date);
            //System.out.println("Date Format(dd-MM-yyyy) : "+sdf.format(date));
        }catch (Exception e){
            Log.e(TAG,"Date Format Exception : "+e.toString());
            OutputDate = InputDate;
        }
        return OutputDate;
    }
    public static String DateFormat_DD_MM_YYYY_HH_MM_SS(String InputDate){
        String OutputDate="";
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = sdf.parse(InputDate);

            sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");

            OutputDate = sdf.format(date);
            //System.out.println("Date Format(dd-MM-yyyy) : "+sdf.format(date));
        }catch (Exception e){
            Log.e(TAG,"Date Format Exception : "+e.toString());
            OutputDate = InputDate;
        }
        return OutputDate;
    }
    public static String DateFormat_DD_MM_YYYY_HH_MM_SS_SSS(String InputDate){
        String OutputDate="";
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
            Date date = sdf.parse(InputDate);

            sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss.SSS");

            OutputDate = sdf.format(date);
            //System.out.println("Date Format(dd-MM-yyyy) : "+sdf.format(date));
        }catch (Exception e){
            Log.e(TAG,"Date Format Exception : "+e.toString());
            OutputDate = InputDate;
        }
        return OutputDate;
    }
    public static String DateFormat_YYYY_MM_DD(String InputDate){
        String OutputDate="";
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
            Date date = sdf.parse(InputDate);

            sdf = new SimpleDateFormat("yyyy-MM-dd");

            OutputDate = sdf.format(date);
            //System.out.println("Date Format(yyyy-MM-dd) : "+sdf.format(date));
        }catch (Exception e){
            Log.e(TAG,"Date Format Exception : "+e.toString());
            OutputDate = InputDate;
        }
        return OutputDate;
    }
    public static String DateFormat_YYYY_MM_DD_HH_MM_SS(String InputDate){
        String OutputDate="";
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
            Date date = sdf.parse(InputDate);

            sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

            OutputDate = sdf.format(date);
            //System.out.println("Date Format(yyyy-MM-dd) : "+sdf.format(date));
        }catch (Exception e){
            Log.e(TAG,"Date Format Exception : "+e.toString());
            OutputDate = InputDate;
        }
        return OutputDate;
    }
    public static String getDateTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        Date date = new Date();
        return dateFormat.format(date);
    }
    public static String GetTime(String InputDate){
        String OutputDate="";
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
            Date date = sdf.parse(InputDate);

            OutputDate = sdf.format(date);
            System.out.println("Time Format(HH:mm:ss) : "+sdf.format(date));
        }catch (Exception e){
            Log.e(TAG,"Time Format Exception : "+e.toString());
            OutputDate = InputDate;
        }
        return OutputDate;
    }
    public static String YearsMonthsDaysCount(String EntryDateTime){
        String flag="";
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date1 = sdf.parse(EntryDateTime);
            Date date2 = new Date();
            String CurrentDate =  sdf.format(date2).substring(0,10);

            long diff = date2.getTime() - date1.getTime();
//            int numOfYear = (int) ((diff / (1000 * 60 * 60 * 24))/365);
//            int numOfMonths = (int) (diff / (1000 * 60 * 60 * 24)/12);
//            int numOfDays = (int) (diff / (1000 * 60 * 60 * 24));
//            int hours = (int) (diff / (1000 * 60 * 60));
//            int minutes = (int) (diff / (1000 * 60));
//            int seconds = (int) (diff / (1000));
            long secondsInMilli = 1000;
            long minutesInMilli = secondsInMilli * 60;
            long hoursInMilli = minutesInMilli * 60;
            long daysInMilli = hoursInMilli * 24;
            long monthsInMilli = daysInMilli * 30;
            long yearsInMilli = monthsInMilli * 12;

            long elapsedYears = diff / yearsInMilli;
            diff = diff % yearsInMilli;

            long elapsedMonths = diff / monthsInMilli;
            diff = diff % monthsInMilli;

            long elapsedDays = diff / daysInMilli;
            diff = diff % daysInMilli;

//            long elapsedHours = diff / hoursInMilli;
//            diff = diff % hoursInMilli;
//
//            long elapsedMinutes = diff / minutesInMilli;
//            diff = diff % minutesInMilli;
//
//            long elapsedSeconds = diff / secondsInMilli;

            String Year = (elapsedYears==0)?"" : (elapsedYears==1)? elapsedYears+" Y " : elapsedYears+ " Y ";
            String Month = (elapsedMonths==0)?"": (elapsedMonths==1)? elapsedMonths+" M ":elapsedMonths+ " M ";
            String Day = (elapsedDays==0)?"": (elapsedDays==1)? elapsedDays+" D ":elapsedDays+ " D ";
            flag =  ((elapsedYears + elapsedMonths + elapsedDays) == 0 ? "Now" : (Year + Month + Day));
        }catch (Exception e) {
            // TODO: handle exception
            Log.e(TAG,"Date Format Exception : "+e.toString());
        }
        return flag;
    }
    public static String getDateDifferenceInDDMMYYYY(Date from, Date to) {
        Calendar fromDate=Calendar.getInstance();
        Calendar toDate=Calendar.getInstance();
        fromDate.setTime(from);
        toDate.setTime(to);
        int increment = 0;
        int year,month,day;
        System.out.println(fromDate.getActualMaximum(Calendar.DAY_OF_MONTH));
        if (fromDate.get(Calendar.DAY_OF_MONTH) > toDate.get(Calendar.DAY_OF_MONTH)) {
            increment =fromDate.getActualMaximum(Calendar.DAY_OF_MONTH);
        }
        System.out.println("increment"+increment);
        // DAY CALCULATION
        if (increment != 0) {
            day = (toDate.get(Calendar.DAY_OF_MONTH) + increment) - fromDate.get(Calendar.DAY_OF_MONTH);
            increment = 1;
        } else {
            day = toDate.get(Calendar.DAY_OF_MONTH) - fromDate.get(Calendar.DAY_OF_MONTH);
        }

        // MONTH CALCULATION
        if ((fromDate.get(Calendar.MONTH) + increment) > toDate.get(Calendar.MONTH)) {
            month = (toDate.get(Calendar.MONTH) + 12) - (fromDate.get(Calendar.MONTH) + increment);
            increment = 1;
        } else {
            month = (toDate.get(Calendar.MONTH)) - (fromDate.get(Calendar.MONTH) + increment);
            increment = 0;
        }

        // YEAR CALCULATION
        year = toDate.get(Calendar.YEAR) - (fromDate.get(Calendar.YEAR) + increment);

        String str = (year==0 ? "" : year+"\tYears\t\t") + (month==0 ? "" : month+"\tMonths\t\t") + (day==0 ? "" : day+"\tDays");

        return  str;
    }
    public static String DaysHoursMinutesCount(String EntryDateTime){
        String result="";
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date1 = sdf.parse(EntryDateTime);
            Date date2 = new Date();
            String CurrentDate =  sdf.format(date2).substring(0,10);

            long diff = date2.getTime() - date1.getTime();

            long secondsInMilli = 1000;
            long minutesInMilli = secondsInMilli * 60;
            long hoursInMilli = minutesInMilli * 60;
            long daysInMilli = hoursInMilli * 24;

            long elapsedDays = diff / daysInMilli;
            diff = diff % daysInMilli;

            long elapsedHours = diff / hoursInMilli;
            diff = diff % hoursInMilli;

            long elapsedMinutes = diff / minutesInMilli;
            diff = diff % minutesInMilli;

            long elapsedSeconds = diff / secondsInMilli;

            String days = (elapsedDays==0)?"" : (elapsedDays==1)? elapsedDays+" Day " : elapsedDays+ " Days ";
            String hours = (elapsedHours==0)?"": (elapsedHours==1)? elapsedHours+" Hour ":elapsedHours+ " Hours ";
            String minute = (elapsedMinutes==0)?"": (elapsedMinutes==1)? elapsedMinutes+" Minute ":elapsedMinutes+ " Minutes ";
            result =  ((elapsedDays + elapsedHours + elapsedMinutes) == 0 ? "Now" : (days + hours + minute));

        }catch (Exception e) {
            // TODO: handle exception
            Log.e(TAG,"Date Format Exception : "+e.toString());
        }
        return result;
    }
    public static String SumOf2Date(String FirstDate,String SecondDate){
        String OutputDate="";
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date1 = sdf.parse(FirstDate);
            Date date2 = sdf.parse(SecondDate);

            long sum = date1.getTime() + date2.getTime();
            Date sumDate = new Date(sum);
            OutputDate = sdf.format(sumDate);

            System.out.println("Date Format(yyyy-MM-dd) : "+sdf.format(sumDate));
        }catch (Exception e){
            Log.e(TAG,"Date Format Exception : "+e.toString());
            OutputDate = SecondDate;
        }
        return OutputDate;
    }
    public static String formatSeconds(int timeInSeconds){
        int secondsLeft = timeInSeconds % 3600 % 60;
        int minutes = (timeInSeconds % 3600 / 60);
        int hours = (timeInSeconds / 3600);

        int HH = hours < 10 ? 0 : hours;
        int MM = minutes < 10 ? 0 : minutes;
        int SS = secondsLeft < 10 ? 0 : secondsLeft;

        return String.format(String.format("%02d", HH) + ":" + String.format("%02d", MM) + ":" + String.format("%02d", SS));
    }
    public static String PastDateNotSelect(String InputDate){
        String result = null;
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        String CurrentDate=sdf.format(new Date());
        Date date;
        Date Curdate;
        try {
            date = sdf.parse(InputDate);
            Curdate = sdf.parse(CurrentDate);
            Curdate.before(date);
            if(Curdate.before(date)==true){
                result=InputDate;
                System.out.println(" "+result);
            }
            else{
                result=CurrentDate;
                System.out.println(" "+result);
            }
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return result;
    }
    public static String daysAgo(int days) {
        GregorianCalendar gc = new GregorianCalendar();
        gc.add(Calendar.DATE, -days);
        SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        fmt.setCalendar(gc);
        String dateFormatted = fmt.format(gc.getTime());
        return dateFormatted;
    }
    public static String convertDate(String dateInMilliseconds,String dateFormat) {
        if (numberOrNot(dateInMilliseconds))
            return DateFormat.format(dateFormat, Long.parseLong(dateInMilliseconds)).toString();
        else
            return DateFormat_YYYY_MM_DD_HH_MM_SS(dateInMilliseconds);
    }
    public static long getTimeDifferenceInMinutes(String from, String to) {
        long diff = 0L;
        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

            Date startDate = format.parse(from);// Set start date
            Date endDate   = format.parse(to);// Set end date

            long duration  = endDate.getTime() - startDate.getTime();

            long diffInSeconds = TimeUnit.MILLISECONDS.toSeconds(duration);
            long diffInMinutes = TimeUnit.MILLISECONDS.toMinutes(duration);
            long diffInHours = TimeUnit.MILLISECONDS.toHours(duration);

            diff = diffInMinutes;
        }catch (Exception e){
            Log.e(TAG , "Exception: "+e.toString());
        }
        return  diff;
    }
    public static boolean numberOrNot(String input)
    {
        try
        {
            Long.parseLong(input);
        }catch(NumberFormatException ex){
            return false;
        }
        return true;
    }
}
