package com.mobileoffice.utils;

import android.content.Context;
import android.widget.Toast;

import java.util.Calendar;

/**
 * Created by Administrator on 2016-08-29.
 */
public class MyTools {
    private static int hour = 88888;
    public static long getTimestamp(){
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.HOUR,hour);
        return calendar.getTime().getTime();
    }
    public static void showToast(Context context,String content){
        Toast.makeText(context , content, Toast.LENGTH_SHORT).show();
    }
}
