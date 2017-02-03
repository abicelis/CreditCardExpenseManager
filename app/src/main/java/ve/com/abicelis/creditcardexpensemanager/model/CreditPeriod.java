package ve.com.abicelis.creditcardexpensemanager.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

/**
 * Created by Alex on 7/8/2016.
 */

public class CreditPeriod {

    public static final int PERIOD_NAME_LARGEST_MONTH = 0;
    public static final int PERIOD_NAME_COMPLETE = 1;
    public static final int PERIOD_NAME_COMPLETE_NUMERIC = 2;

    private int id = -1;
    private String periodName;
    private int periodNameStyle;
    private Calendar startDate;
    private Calendar endDate;
    private BigDecimal creditLimit;

    private List<Expense> expenses = new ArrayList<>();
    private List<Payment> payments = new ArrayList<>();


    public CreditPeriod(int periodNameStyle, Calendar startDate, Calendar endDate, BigDecimal creditLimit) {
        this.periodNameStyle = periodNameStyle;
        this.creditLimit = creditLimit;

        this.startDate = Calendar.getInstance();
        this.startDate.setTimeZone(startDate.getTimeZone());
        this.startDate.setTimeInMillis(startDate.getTimeInMillis());

        this.endDate = Calendar.getInstance();
        this.endDate.setTimeZone(endDate.getTimeZone());
        this.endDate.setTimeInMillis(endDate.getTimeInMillis());
    }

    public CreditPeriod(int id, int periodNameStyle, Calendar startDate, Calendar endDate, BigDecimal creditLimit) {
        this(periodNameStyle, startDate, endDate, creditLimit);

        this.id = id;
    }

    public CreditPeriod(int id, int periodNameStyle, Calendar startDate, Calendar endDate, BigDecimal creditLimit, List<Expense> expenses, List<Payment> payments) {
        this(id, periodNameStyle, startDate, endDate, creditLimit);

        this.expenses = expenses;
        this.payments = payments;
    }



    public int getId() {
        return id;
    }



    /**
     * returns periodName String according to periodNameStyle, which can be either
     * PERIOD_NAME_LARGEST_MONTH    -> "February", "March"  or
     * PERIOD_NAME_COMPLETE         -> "Feb 4 - Mar 3"      or
     * PERIOD_NAME_COMPLETE_NUMERIC -> "02/04 - 03/03"
     */
    public String getPeriodName() {
        if(periodName == null) {
            StringBuilder sb = new StringBuilder(40);
            switch(periodNameStyle) {
                case PERIOD_NAME_LARGEST_MONTH:
                    if (getStartDate().get(Calendar.DAY_OF_MONTH) <= 15)
                        periodName = getStartDate().getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault());
                    else
                        periodName = getEndDate().getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault());
                    break;

                case PERIOD_NAME_COMPLETE:
                    sb.append(getStartDate().getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.getDefault()));
                    sb.append(" ");
                    sb.append(getStartDate().get(Calendar.DAY_OF_MONTH));
                    sb.append(" - ");
                    sb.append(getEndDate().getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.getDefault()));
                    sb.append(" ");
                    sb.append(getEndDate().get(Calendar.DAY_OF_MONTH));
                    periodName = sb.toString();
                    break;

                case PERIOD_NAME_COMPLETE_NUMERIC:
                    sb.append(getStartDate().get(Calendar.MONTH) + 1);
                    sb.append(" ");
                    sb.append(getStartDate().get(Calendar.DAY_OF_MONTH));
                    sb.append(" - ");
                    sb.append(getEndDate().get(Calendar.MONTH) + 1);
                    sb.append(" ");
                    sb.append(getEndDate().get(Calendar.DAY_OF_MONTH));
                    periodName = sb.toString();
                    break;

                default:
                    periodName = "ERROR: undefined periodNameStyle";
            }
        }

        return periodName;
    }

    public int getPeriodNameStyle() {
        return periodNameStyle;
    }

    public Calendar getStartDate() {
        return startDate;
    }

    public Calendar getEndDate() {
        return endDate;
    }

    public BigDecimal getCreditLimit() {
        return creditLimit;
    }

    public void setCreditLimit(BigDecimal creditLimit) {
        this.creditLimit = new BigDecimal(creditLimit.toPlainString());
    }

    public List<Expense> getExpenses() {
        return expenses;
    }

    public void setExpenses(List<Expense> expenses) {
        this.expenses = expenses;
    }

    public List<Payment> getPayments() {
        return payments;
    }

    public void setPayments(List<Payment> payments) {
        this.payments = payments;
    }

    /**
     * Returns the sum of all the expenses, minus all the payments for this CreditPeriod
     * Positive value means there are unpaid expenses
     * @return
     */
    public BigDecimal getExpensesTotal() {
        BigDecimal balance = new BigDecimal(0);

        for (Expense e: expenses) {
            balance = balance.add(e.getAmount());
        }

        for (Payment p : payments) {
            balance = balance.subtract(p.getAmount());
        }

        return balance;
    }


    /**
     * Returns creditLimit - expensesTotal = the available credit
     */
    public BigDecimal getAvailableCredit() {
        return getCreditLimit().subtract(getExpensesTotal());
    }


    /**
     * Returns the days in this period, startDate - endDate
     */
    public int getTotalDaysInPeriod() {

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
        int days = (int)TimeUnit.MILLISECONDS.toDays(Math.abs(end.getTimeInMillis() - start.getTimeInMillis()));
        return days;
    }

    public List<DailyExpense> getDailyExpenses() {
        List<DailyExpense> dailyExpenses = new ArrayList<>();

        Calendar cal = Calendar.getInstance();
        cal.setTimeZone(startDate.getTimeZone());
        cal.setTimeInMillis(startDate.getTimeInMillis());

        for (int i = 0; i < getTotalDaysInPeriod(); i++) {
            dailyExpenses.add(i, new DailyExpense(cal, new BigDecimal(0)));

            cal.add(Calendar.DAY_OF_MONTH, 1);
        }

        for (Expense expense: expenses) {
            int index = getDateIndex(expense.getDate());
            dailyExpenses.get(index).addToAmount(expense.getAmount());
        }

        return dailyExpenses;
    }


    public List<DailyExpense> getAccumulatedDailyExpenses() {
        List<DailyExpense> dailyAccumulatedExpenses = getDailyExpenses();
        BigDecimal accumulatedExpenses = new BigDecimal(0);

        Calendar cal = Calendar.getInstance();
        cal.setTimeZone(startDate.getTimeZone());
        cal.setTimeInMillis(startDate.getTimeInMillis());

        for (int i = 0; i < getTotalDaysInPeriod(); i++) {

            accumulatedExpenses = accumulatedExpenses.add(dailyAccumulatedExpenses.get(i).getAmount());
            dailyAccumulatedExpenses.remove(i);
            dailyAccumulatedExpenses.add(i, new DailyExpense(cal, accumulatedExpenses));

            cal.add(Calendar.DAY_OF_MONTH, 1);
        }
        return dailyAccumulatedExpenses;
    }


    private Calendar getStartOfDay(Calendar date) {

        Calendar cal = Calendar.getInstance();
        cal.setTimeZone(date.getTimeZone());
        cal.setTimeInMillis(date.getTimeInMillis());

        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        return cal;
    }

    public int getDateIndex(Calendar dateToBeIndexed) {

        // Create copies so we don't update the original calendars.
        Calendar start = Calendar.getInstance();
        start.setTimeZone(startDate.getTimeZone());
        start.setTimeInMillis(startDate.getTimeInMillis());

        Calendar end = Calendar.getInstance();
        end.setTimeZone(dateToBeIndexed.getTimeZone());
        end.setTimeInMillis(dateToBeIndexed.getTimeInMillis());

        // Set the copies to be at midnight, but keep the day information.
        start.set(Calendar.HOUR_OF_DAY, 0);
        start.set(Calendar.MINUTE, 0);
        start.set(Calendar.SECOND, 0);
        start.set(Calendar.MILLISECOND, 0);
        end.set(Calendar.HOUR_OF_DAY, 0);
        end.set(Calendar.MINUTE, 0);
        end.set(Calendar.SECOND, 0);
        end.set(Calendar.MILLISECOND, 0);

        // At this point, each calendar is set to midnight on
        // their respective days. Now use TimeUnit.MILLISECONDS to
        // compute the number of full days between the two of them.


        int days = (int) TimeUnit.MILLISECONDS.toDays(Math.abs(end.getTimeInMillis() - start.getTimeInMillis()));
        return days;
    }

    @Override
    public String toString() {
        return  "ID=" + id + "\r\n" +
                " periodName=" + periodName + "\r\n" +
                " periodNameStyle=" + periodNameStyle + "\r\n" +
                " startDate=" + startDate.getTimeInMillis() + "\r\n" +
                " endDate=" + endDate.getTimeInMillis() + "\r\n" +
                " creditLimit=" + creditLimit;
    }

}
