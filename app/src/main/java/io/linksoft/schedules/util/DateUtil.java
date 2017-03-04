package io.linksoft.schedules.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class DateUtil {

    private static final String TIME_FORMAT = "H:m";

    // Temp workaround for Android claiming CET is one hour ahead
    private static final TimeZone timeZone = TimeZone.getTimeZone("CEST");

    public static String getClassTimeframe(Date start, Date end) {
        SimpleDateFormat timeStart = new SimpleDateFormat(TIME_FORMAT);
        timeStart.setTimeZone(timeZone);

        SimpleDateFormat timeEnd = new SimpleDateFormat(TIME_FORMAT);
        timeEnd.setTimeZone(timeZone);

        return String.format("%s - %s", timeStart.format(start), timeEnd.format(end));
    }

    public static String getScheduleDay(Date date) {
        DateFormat time = DateFormat.getDateInstance(DateFormat.FULL, Locale.getDefault());
        time.setTimeZone(TimeZone.getDefault());

        String timeStr = time.format(date);

        return timeStr.substring(0, 1).toUpperCase() + timeStr.substring(1);
    }

    public static Date getWeekStart(Date date, int weekOffset) {
        Calendar cal = Calendar.getInstance();

        cal.setTime(date);
        cal.set(Calendar.DAY_OF_WEEK, cal.getFirstDayOfWeek());
        cal.add(Calendar.WEEK_OF_YEAR, weekOffset);

        return cal.getTime();
    }

}
