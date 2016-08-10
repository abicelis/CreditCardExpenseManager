package ve.com.abicelis.creditcardexpensemanager.model;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.UUID;

import ve.com.abicelis.creditcardexpensemanager.enums.Currency;
import ve.com.abicelis.creditcardexpensemanager.enums.ExpenseCategory;
import ve.com.abicelis.creditcardexpensemanager.enums.ExpenseType;

/**
 * Created by Alex on 6/8/2016.
 */
public class Expense {

    private int id;
    private String description;
    byte[] image;
    BigDecimal amount;
    Currency currency;
    Calendar date;
    ExpenseCategory expenseCategory;
    ExpenseType expenseType;

    public Expense (int id, String description, byte[] image, BigDecimal amount, Currency currency, Calendar date, ExpenseCategory expenseCategory, ExpenseType expenseType) {
        this.id = id;
        this.description = description;
        this.image = image;
        this.amount = amount;
        this.currency = currency;
        this.date = date;
        this.expenseCategory = expenseCategory;
        this.expenseType = expenseType;
    }

    public int getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public byte[] getImage() {
        return image;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public Currency getCurrency() {
        return currency;
    }

    public Calendar getDate() {
        return date;
    }

    public ExpenseCategory getExpenseCategory() {
        return expenseCategory;
    }

    public ExpenseType getExpenseType() {
        return expenseType;
    }



    @Override
    public String toString() {
        return  "ID=" + id + "\r\n" +
                " description=" + description + "\r\n" +
                " amount=" + amount + "\r\n" +
                " currency=" + currency + "\r\n" +
                " date=" + date + "\r\n" +
                " expenseCategory=" + expenseCategory + "\r\n" +
                " expenseType=" + expenseType;
    }
}
