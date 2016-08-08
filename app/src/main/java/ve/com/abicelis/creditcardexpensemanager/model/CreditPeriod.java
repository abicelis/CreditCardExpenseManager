package ve.com.abicelis.creditcardexpensemanager.model;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

/**
 * Created by Alex on 7/8/2016.
 */

public class CreditPeriod {

    public static final int PERIOD_NAME_LARGEST_MONTH = 0;
    public static final int PERIOD_NAME_COMPLETE = 1;
    public static final int PERIOD_NAME_COMPLETE_NUMERIC = 2;

    private int id;
    private String periodName;
    private int periodNameStyle;
    private Calendar startDate;
    private Calendar endDate;
    private BigDecimal creditLimit;



    private List<Expense> expenses;
    private List<Payment> payments;


    public CreditPeriod (int id, int periodNameStyle, Calendar startDate, Calendar endDate, BigDecimal creditLimit, List<Expense> expenses, List<Payment> payments) {

        this.id = id;
        this.periodNameStyle = periodNameStyle;
        this.startDate = startDate;
        this.endDate = endDate;
        this.creditLimit = creditLimit;
        this.expenses = expenses;
        this.payments = payments;

        initPeriodName(periodNameStyle);
    }


    public int getId() {
        return id;
    }

    public String getPeriodName() {
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

    public List<Expense> getExpenses() {
        return expenses;
    }

    public List<Payment> getPayments() {
        return payments;
    }

    /**
     * Returns the sum of all the expenses, minus all the payments for this CreditPeriod
     * Positive value means there are unpaid expenses
     * @return
     */
    public BigDecimal getExpensesTotal() {
        BigDecimal balance = new BigDecimal(0);

        for (Expense e: expenses) {
            balance.add(e.getAmount());
        }

        for (Payment p : payments) {
            balance.subtract(p.getAmount());
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
     * Initializes the periodName String which can be either
     * PERIOD_NAME_LARGEST_MONTH    -> "February", "March" or
     * PERIOD_NAME_COMPLETE         -> "Feb 4 - Mar 3"
     * PERIOD_NAME_COMPLETE_NUMERIC -> "02/04 - 03/03"
     * @param periodNameStyle
     */

    private void initPeriodName(int periodNameStyle) {
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
        }
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
