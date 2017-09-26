package com.plweegie.android.squash.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by jan on 24/09/17.
 */

public class DateUtils {

    private static final DateFormat SOURCE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
    private static final DateFormat TARGET_FORMAT = new SimpleDateFormat("dd MMMM, yyyy");

    public static String changeDateFormats(String date) throws ParseException {
        return TARGET_FORMAT.format(convertToDate(date));
    }

    public static long convertToTimestamp(String date) throws ParseException {
        return convertToDate(date).getTime();
    }

    private static Date convertToDate(String date) throws ParseException {
        String dayDate = date.split("T")[0];
        Date commitDate = SOURCE_FORMAT.parse(dayDate);
        return commitDate;
    }
}