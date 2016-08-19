package ve.com.abicelis.creditcardexpensemanager.model;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Locale;

/**
 * Created by Alex on 18/8/2016.
 */
public class DailyExpense {

    Calendar date;
    BigDecimal amount;

    public DailyExpense (Calendar date, BigDecimal amount) {
        this.amount = amount;

        this.date = Calendar.getInstance();
        this.date.setTimeZone(date.getTimeZone());
        this.date.setTimeInMillis(date.getTimeInMillis());
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void addToAmount(BigDecimal amountToAdd) {
        amount = amount.add(amountToAdd);
    }

    public Calendar getDate() {
        return date;
    }

    public String getFormattedDate() {
        String formattedDate = String.format(Locale.getDefault(), "%1s %2$02d", date.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.getDefault()), date.get(Calendar.DAY_OF_MONTH));

        return formattedDate;
    }

}

