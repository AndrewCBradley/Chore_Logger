package com.example.chorelogger;

import java.util.Calendar;

public class DateUtil {

    public static String getCurrentDate() {
        return (Calendar.getInstance().get(Calendar.MONTH) + 1) + "/"
                + Calendar.getInstance().get(Calendar.DAY_OF_MONTH) + "/"
                + (Calendar.getInstance().get(Calendar.YEAR) % 100);

    }

    public static String setDate;
}
