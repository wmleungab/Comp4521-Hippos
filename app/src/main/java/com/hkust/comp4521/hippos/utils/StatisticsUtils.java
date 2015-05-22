package com.hkust.comp4521.hippos.utils;

import android.util.Log;

import com.hkust.comp4521.hippos.datastructures.Commons;
import com.hkust.comp4521.hippos.datastructures.Invoice;

import java.util.Calendar;
import java.util.Date;
import java.util.List;


/**
 * Created by TC on 5/20/2015.
 */
public class StatisticsUtils {


    public final static String[] MONTHS = new String[]{"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
    public final static String[] DAYS = new String[]{"Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"};
    private static int[] weeklyDP = new int[DAYS.length];
    private static int[] monthlyDP = new int[MONTHS.length];

    public static int[] getWeeklyDataPoints(int fromMonth) {
        // Clear old data
        for(int i=0; i < weeklyDP.length; i++)
            weeklyDP[i] = 0;

        List<Invoice> invoiceList = Commons.getRemoteInvoiceList();
        // For each invoice, look the respective weekday from date
        // then add count to weekday
        for (Invoice invoice : invoiceList) {
            Date date = invoice.getDate();

            Calendar c = Calendar.getInstance();
            c.setTime(date);
            int dayOfWeek = c.get(Calendar.DAY_OF_WEEK);//Sunday is 1, Sat is 7
            int respectiveMonth = c.get(Calendar.MONTH);
            if(respectiveMonth != fromMonth)
                continue;
            if (dayOfWeek == 1) dayOfWeek = 6;
            else dayOfWeek -= 2;
            weeklyDP[dayOfWeek]++;
        }

        return weeklyDP;
    }

    public static int[] getMonthlyDataPoints() {
        // Clear old data
        for(int i=0; i < monthlyDP.length; i++)
            monthlyDP[i] = 0;

        List<Invoice> invoiceList = Commons.getRemoteInvoiceList();
        // For each invoice, look the respective month from date
        // then add count to month

        for (Invoice invoice : invoiceList) {
            Date date = invoice.getDate();

            Calendar c = Calendar.getInstance();
            c.setTime(date);
            int month = c.get(Calendar.MONTH);// JANUARY which is 0

            monthlyDP[month]++;
        }

        for (int j = 0; j < monthlyDP.length; j++)
            Log.i("StatisticsUtils:", "month " + (j + 1) + ": " + monthlyDP[j]);
        return monthlyDP;
    }

    public static float getMaximumMonthValue() {
        int tempMax = 0;
        for (int i = 0; i < monthlyDP.length; i++) {
            if (monthlyDP[i] > tempMax) tempMax = monthlyDP[i];
        }
        return (float) tempMax;
    }

    public static float getMaximumWeekValue() {
        int tempMax = 0;
        for (int i = 0; i < weeklyDP.length; i++) {
            if (weeklyDP[i] > tempMax) tempMax = weeklyDP[i];
        }
        return (float) tempMax;
    }
}
