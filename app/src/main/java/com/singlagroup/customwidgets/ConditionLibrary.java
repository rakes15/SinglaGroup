package com.singlagroup.customwidgets;

import android.util.Log;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

/**
 * Created by Rakesh on 16-Jan-17.
 */

public class ConditionLibrary {
    private static String TAG = ConditionLibrary.class.getSimpleName();

    public static double ConvertStringToDouble(String input){
        double Output=0;
        try {
            if (input.equals("0")){
                Output = Double.parseDouble(input);
            }else{
                Output=0;
            }
        }catch (Exception e){
            Log.d(TAG,"ConvertStringToDouble Exception"+e.toString());
        }
        return Output;
    }
    public static String ConvertDoubleToString(double input){
        String Output="";
        try {
            Output = String.valueOf(input);
            if (Output.contains(".0")){
                Output = Output.substring(0,Output.length()-2);
            }
        }catch (Exception e){
            Log.d(TAG,"ConvertStringToDouble Exception"+e.toString());
        }
        return Output;
    }
    public static String ConvertStringToString(String input){
        String Output="";
        try {
            Output = input;
            if (Output.contains(".0")){
                Output = Output.substring(0,Output.length()-2);
            }else if (Output.contains(".00")){
                Output = Output.substring(0,Output.length()-3);
            }
        }catch (Exception e){
            Log.d(TAG,"ConvertStringToDouble Exception"+e.toString());
        }
        return Output;
    }
    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        long factor = (long) Math.pow(10, places);
        value = value * factor;
        long tmp = Math.round(value);
        return (double) tmp / factor;
    }
    public static float roundFloat(float value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        long factor = (long) Math.pow(10, places);
        value = value * factor;
        long tmp = Math.round(value);
        return (float) tmp / factor;
    }
    public static String ConvertRoundOff2Decimal(String input){
        String Output="";
        try {
            Output = input;
            if (input.length()>4) {
                double f = Double.parseDouble(input);
                Output = String.format("%.2f", new BigDecimal(f));
            }
        }catch (Exception e){
            Log.d(TAG,"ConvertRoundOff2Decimal Exception"+e.toString());
        }
        return Output;
    }
}
