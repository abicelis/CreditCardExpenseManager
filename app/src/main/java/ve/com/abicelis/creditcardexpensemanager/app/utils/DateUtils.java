package ve.com.abicelis.creditcardexpensemanager.app.utils;

import java.util.Calendar;
import java.util.Locale;

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
                (date.get(Calendar.AM_PM) == 0 ? "am" : "pm"));
    }
}
