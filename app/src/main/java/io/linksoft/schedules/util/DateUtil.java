package io.linksoft.schedules.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

// TODO Switch to Joda-Time library
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

    public static String getFormattedTime(Date date, int style) {
        DateFormat time = DateFormat.getDateInstance(style, Locale.getDefault());
        time.setTimeZone(TimeZone.getDefault());

        return time.format(date);
    }

    public static String getScheduleDay(Date date) {
        String timeStr = getFormattedTime(date, DateFormat.FULL);

        return timeStr.substring(0, 1).toUpperCase() + timeStr.substring(1);
    }

    public static Date getWeekStart(Date date, int weekOffset) {
        Calendar cal = Calendar.getInstance();

        cal.setTime(date);
        cal.set(Calendar.DAY_OF_WEEK, cal.getFirstDayOfWeek());
        cal.add(Calendar.WEEK_OF_YEAR, weekOffset);

        return cal.getTime();
    }

    public static Date getDateByDayOffset(Date date, int dayOffset) {
        Calendar cal = Calendar.getInstance();

        cal.setTime(date);
        cal.add(Calendar.DATE, dayOffset);

        return cal.getTime();
    }

    public static Date getStartOfDay(Date date) {
        Calendar cal = Calendar.getInstance();

        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        return cal.getTime();
    }

    public static ArrayList<Date> getDateRange(Date start, Date end, boolean skipWeekends) {
        ArrayList<Date> dates = new ArrayList<>();
        Calendar cal = Calendar.getInstance();

        start = getStartOfDay(start);
        end = getStartOfDay(end);

        dates.add(start);
        cal.setTime(start);

        while (cal.getTime().before(end)) {
            cal.add(Calendar.DATE, 1);
            if (skipWeekends && isWeekend(cal.getTime())) continue;

            if (cal.getTime().before(end))
                dates.add(cal.getTime());
        }

        return dates;
    }

    public static boolean isWeekend(Date date) {
        Calendar cal = Calendar.getInstance();

        cal.setTime(date);

        int day = cal.get(Calendar.DAY_OF_WEEK);

        return day == Calendar.SATURDAY || day == Calendar.SUNDAY;
    }

    public static boolean areDaysEqual(Date date1, Date date2) {
        return getStartOfDay(date1).equals(getStartOfDay(date2));
    }

}
