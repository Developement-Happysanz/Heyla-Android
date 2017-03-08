package com.findafun.helper;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by zahid.r on 2/11/2015.
 */
public class FindAFunHelper {


    public static void hideKeyboard(Activity activity) {
        // Check if no view has focus:
        View view = activity.getCurrentFocus();
        if (view != null) {
            InputMethodManager inputManager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    public static String getCurrentTimeStamp() {
        Long tsLong = System.currentTimeMillis() / 1000;
        String ts = tsLong.toString();
        return ts;
    }

    public static String getDateCurrentTimeZone(String value) {
        try {
            Long timestamp = Long.parseLong(value);
            Calendar calendar = Calendar.getInstance();
            TimeZone tz = TimeZone.getDefault();
            calendar.setTimeInMillis(timestamp * 1000);
            calendar.add(Calendar.MILLISECOND, tz.getOffset(calendar.getTimeInMillis()));
            SimpleDateFormat sdf = new SimpleDateFormat("EEE, MMM dd");
            Date currenTimeZone = calendar.getTime();
            return sdf.format(currenTimeZone);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static void enableTouchEvent(Activity activity, boolean enabled) {
        if (enabled) {
            activity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        } else {
            activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        }
    }

    public static void deleteFolder(File fileOrDirectory) {
        if (fileOrDirectory.isDirectory())
            for (File child : fileOrDirectory.listFiles())
                deleteFolder(child);

        fileOrDirectory.delete();
    }

    //helper for parsing the date from string. Expected format "10-28-2015 00:00:00"
    public static String getDate(String dateTime){
        String dateVal = null;
        try {
            if ((dateTime != null) && !dateTime.isEmpty() && dateTime.length() >= 10) {
                dateVal = dateTime.substring(0, 10);
                String month = getMonthName(Integer.parseInt(dateVal.substring(0, 2)));
                String day = dateVal.substring(3, 5);
                String year = dateVal.substring(6,10);
                if( (month != null) && (day != null) && (year != null)){
                    dateVal = month+"  "+ day+ ","+ year+" ";
                }

            }
        }catch (NumberFormatException numE){
            numE.printStackTrace();

        }
        catch (Exception e){
            e.printStackTrace();
        }

        return dateVal;
    }
    //helper for parsing the time from string
    public static String getTime(String dateTime){
        if((dateTime != null) && !dateTime.isEmpty() && dateTime.length()>=16){
            return dateTime.substring(11,16);

        }

        return null;

    }

    private static  String getMonthName(int month){
        String monthVal = null;
        switch (month){
            case 1:
            monthVal = "Jan";
            break;
            case 2:
                monthVal = "Feb";
                break;
            case 3:
                monthVal = "Mar";
                break;
            case 4:
                monthVal = "Apr";
                break;
            case 5:
                monthVal = "May";
                break;
            case 6:
                monthVal = "Jun";
                break;
            case 7:
                monthVal = "Jul";
                break;
            case 8:
                monthVal = "Aug";
                break;
            case 9:
                monthVal = "Sep";
                break;
            case 10:
                monthVal = "Oct";
                break;
            case 11:
                monthVal = "Nov";
                break;
            case 12:
                monthVal = "Dec";
                break;
        }

        return  monthVal;
    }

}
