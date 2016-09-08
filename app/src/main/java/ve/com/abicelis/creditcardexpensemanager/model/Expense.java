package ve.com.abicelis.creditcardexpensemanager.model;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.io.FileNotFoundException;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Calendar;

import ve.com.abicelis.creditcardexpensemanager.enums.Currency;
import ve.com.abicelis.creditcardexpensemanager.enums.ExpenseCategory;
import ve.com.abicelis.creditcardexpensemanager.enums.ExpenseType;

/**
 * Created by Alex on 6/8/2016.
 */
public class Expense implements Serializable {

    private int id;
    private String description;
    byte[] thumbnail;
    String fullImagePath;
    BigDecimal amount;
    Currency currency;
    Calendar date;
    ExpenseCategory expenseCategory;
    ExpenseType expenseType;


    public Expense (@NonNull String description, @Nullable byte[] thumbnail, @Nullable String fullImagePath, @NonNull BigDecimal amount, @NonNull Currency currency, @NonNull Calendar date, @NonNull ExpenseCategory expenseCategory, @NonNull ExpenseType expenseType) {
        this.description = description;
        this.fullImagePath = fullImagePath;
        this.amount = amount;
        this.currency = currency;
        this.expenseCategory = expenseCategory;
        this.expenseType = expenseType;

        this.thumbnail = thumbnail;
        if(this.thumbnail != null && this.thumbnail.equals(new byte[0]) )   //if thumbnail is empty byte array -> null
            this.thumbnail = null;

        this.date = Calendar.getInstance();
        this.date.setTimeZone(date.getTimeZone());
        this.date.setTimeInMillis(date.getTimeInMillis());
    }

    public Expense (int id, String description, byte[] thumbnail, String fullImagePath, BigDecimal amount, Currency currency, Calendar date, ExpenseCategory expenseCategory, ExpenseType expenseType) {
        this(description, thumbnail, fullImagePath, amount, currency, date, expenseCategory, expenseType);
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public byte[] getThumbnail() {
        return thumbnail;
    }

    public String getFullImagePath() {
        return fullImagePath;
    }

    public Bitmap getFullImage() throws FileNotFoundException {
        Bitmap fullImage = BitmapFactory.decodeFile(fullImagePath);

        if(fullImage == null)
            throw new FileNotFoundException("This image does not exist, has probably been deleted");

        return fullImage;
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
