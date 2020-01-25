package com.plweegie.android.squash.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;


public class DateUtils {

    public static String changeDateFormats(String date) throws ParseException {
        DateFormat targetFormat =  new SimpleDateFormat("d MMMM, yyyy, HH:mm");
        return targetFormat.format(convertToDate(date));
    }

    public static long convertToTimestamp(String date) throws ParseException {
        return convertToDate(date).getTime();
    }

    private static Date convertToDate(String date) throws ParseException {
        DateFormat sourceFormat =  new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        sourceFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

        String dayDate = date.split("Z")[0];
        return sourceFormat.parse(dayDate);
    }
}
