package ve.com.abicelis.creditcardexpensemanager.app.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

/**
 * Created by Alex on 8/9/2016.
 */
public class DateUtils {
    public static String getRelativeTimeSpanString(Calendar date) {
        return android.text.format.DateUtils.getRelativeTimeSpanString(date.getTimeInMillis(), System.currentTimeMillis(), android.text.format.DateUtils.SECOND_IN_MILLIS).toString();
    }
    public static String getShortDateString(Calendar date) {
        return String.format(Locale.getDefault(), "%d/%02d/%02d @ %02d:%02d%s",
                date.get(Calendar.YEAR),
                (date.get(Calendar.MONTH)+1),
                date.get(Calendar.DAY_OF_MONTH),
                date.get(Calendar.HOUR),
                date.get(Calendar.MINUTE),
                (date.get(Calendar.AM_PM) == Calendar.AM ? "am" : "pm"));
    }

    public static String getDayShortMonthString(Calendar cal) {
        return new SimpleDateFormat("d MMM", Locale.getDefault()).format(cal.getTime());
    }


    /**
     * Returns the days in this period, startDate - endDate
     */
    public static int getDaysBetween(Calendar startDate, Calendar endDate) {

        // Create copies so we don't update the original calendars.
        Calendar start = Calendar.getInstance();
        start.setTimeZone(startDate.getTimeZone());
        start.setTimeInMillis(startDate.getTimeInMillis());

        Calendar end = Calendar.getInstance();
        end.setTimeZone(endDate.getTimeZone());
        end.setTimeInMillis(endDate.getTimeInMillis());

        // Add one millisecond so that the subtraction below works.
        // If end = Aug 5 23:59:59 we add one millisecond
        // so end = Aug 6 00:00:00 so that Aug 5 counts
        end.add(Calendar.MILLISECOND, 1);


        // At this point, each calendar is set to midnight on
        // their respective days. Now use TimeUnit.MILLISECONDS to
        // compute the number of full days between the two of them.
        int days = (int) TimeUnit.MILLISECONDS.toDays(Math.abs(end.getTimeInMillis() - start.getTimeInMillis()));
        return days;
    }
}
