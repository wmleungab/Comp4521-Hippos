package com.hkust.comp4521.hippos.utils;

import android.util.Log;

import com.hkust.comp4521.hippos.datastructures.Invoice;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


/**
 * Created by TC on 5/20/2015.
 */
public class StatisticsUtils {


    public final static String[] MONTHS = new String[]{"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
    public final static String[] DAYS = new String[]{"Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"};
    private static int[] weeklyDP, monthlyDP;

    public static int[] getWeeklyDataPoints(int fromMonth) {
        weeklyDP = new int[DAYS.length];
        // List<Invoice> invoiceList = Commons.getInvoiceList();
        // For each invoice, look the respective weekday from date
        // then add count to weekday
        List<Invoice> invoiceList = new ArrayList<Invoice>();
        Invoice i = new Invoice();
        i.setDateTime("2015-05-20 23:46:17"); //wed
        invoiceList.add(i);
        i = new Invoice();
        i.setDateTime("2015-5-17 20:00:17"); //sun
        invoiceList.add(i);
        i = new Invoice();
        i.setDateTime("2015-4-30 00:46:17"); //thru
        invoiceList.add(i);
        i = new Invoice();
        i.setDateTime("2015-4-20 23:46:17"); //Mon
        invoiceList.add(i);
        i = new Invoice();
        i.setDateTime("2015-4-29 11:46:17"); //wed
        invoiceList.add(i);
        i = new Invoice();
        i.setDateTime("2015-4-27 23:46:17"); //mon
        invoiceList.add(i);

        for (Invoice invoice : invoiceList) {
            Date date = invoice.getDate();

            Calendar c = Calendar.getInstance();
            c.setTime(date);
            int dayOfWeek = c.get(Calendar.DAY_OF_WEEK);//Sunday is 1, Sat is 7
            if (dayOfWeek == 1) dayOfWeek = 6;
            else dayOfWeek -= 2;
            weeklyDP[dayOfWeek]++;
        }


        // Following are hardcoded values for testing
//        weeklyDP[0] = 12;
//        weeklyDP[1] = 38;
//        weeklyDP[2] = 23;
//        weeklyDP[3] = 14;
//        weeklyDP[4] = 20;
//        weeklyDP[5] = 43;
//        weeklyDP[6] = 55;
//
//        weeklyDP[0] += 12 * fromMonth;
        for (int j = 0; j < weeklyDP.length; j++)
            Log.i("StatisticsUtils:", "week (mon is 0)  " + (j) + ": " + weeklyDP[j]);
        return weeklyDP;
    }

    public static int[] getMonthlyDataPoints() {
        monthlyDP = new int[MONTHS.length];
        //  List<Invoice> invoiceList = Commons.getInvoiceList();
        // For each invoice, look the respective month from date
        // then add count to month
        List<Invoice> invoiceList = new ArrayList<Invoice>();
        Invoice i = new Invoice();
        i.setDateTime("2015-05-20 23:46:17"); //wed
        invoiceList.add(i);
        i = new Invoice();
        i.setDateTime("2015-5-17 20:00:17"); //sun
        invoiceList.add(i);
        i = new Invoice();
        i.setDateTime("2015-4-30 00:46:17"); //thru
        invoiceList.add(i);
        i = new Invoice();
        i.setDateTime("2015-4-20 23:46:17"); //Mon
        invoiceList.add(i);
        i = new Invoice();
        i.setDateTime("2015-4-29 11:46:17"); //wed
        invoiceList.add(i);
        i = new Invoice();
        i.setDateTime("2015-4-27 23:46:17"); //mon
        invoiceList.add(i);

        for (Invoice invoice : invoiceList) {
            Date date = invoice.getDate();

            Calendar c = Calendar.getInstance();
            c.setTime(date);
            int month = c.get(Calendar.MONTH);// JANUARY which is 0

            monthlyDP[month]++;
        }
        // Following are hardcoded values for testing
//        monthlyDP[0] = 230;
//        monthlyDP[1] = 426;
//        monthlyDP[2] = 312;
//        monthlyDP[3] = 163;
//        monthlyDP[4] = 184;
//        monthlyDP[5] = 219;
//        monthlyDP[6] = 549;
//        monthlyDP[7] = 729;
//        monthlyDP[8] = 630;
//        monthlyDP[9] = 361;
//        monthlyDP[10] = 438;
//        monthlyDP[11] = 823;
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
        //return 1000;
    }

    public static float getMaximumWeekValue() {
        int tempMax = 0;
        for (int i = 0; i < weeklyDP.length; i++) {
            if (weeklyDP[i] > tempMax) tempMax = weeklyDP[i];
        }
        return (float) tempMax;
        //return 1000;
    }
}
