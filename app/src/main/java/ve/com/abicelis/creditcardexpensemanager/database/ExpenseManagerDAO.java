package ve.com.abicelis.creditcardexpensemanager.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import ve.com.abicelis.creditcardexpensemanager.enums.CreditCardType;
import ve.com.abicelis.creditcardexpensemanager.enums.Currency;
import ve.com.abicelis.creditcardexpensemanager.enums.ExpenseCategory;
import ve.com.abicelis.creditcardexpensemanager.enums.ExpenseType;
import ve.com.abicelis.creditcardexpensemanager.exceptions.CouldNotInsertDataException;
import ve.com.abicelis.creditcardexpensemanager.model.CreditCard;
import ve.com.abicelis.creditcardexpensemanager.model.CreditPeriod;
import ve.com.abicelis.creditcardexpensemanager.model.Expense;
import ve.com.abicelis.creditcardexpensemanager.model.Payment;

/**
 * Created by Alex on 8/8/2016.
 */
public class ExpenseManagerDAO {

    private ExpenseManagerDbHelper mDatabaseHelper;

    public ExpenseManagerDAO(Context context) {
        mDatabaseHelper = new ExpenseManagerDbHelper(context, null, null, 0);

    }

    public long insertCreditCard(CreditCard creditcard) throws CouldNotInsertDataException {
        SQLiteDatabase db = mDatabaseHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(ExpenseManagerContract.CreditCardTable.COLUMN_NAME_CARD_ALIAS.getName(), creditcard.getCardAlias());
        values.put(ExpenseManagerContract.CreditCardTable.COLUMN_NAME_BANK_NAME.getName(), creditcard.getBankName());
        values.put(ExpenseManagerContract.CreditCardTable.COLUMN_NAME_CARD_NUMBER.getName(), creditcard.getCardNumber());
        values.put(ExpenseManagerContract.CreditCardTable.COLUMN_NAME_CURRENCY.getName(), creditcard.getCurrency().getCode());
        values.put(ExpenseManagerContract.CreditCardTable.COLUMN_NAME_CARD_TYPE.getName(), creditcard.getCardType().getCode());
        values.put(ExpenseManagerContract.CreditCardTable.COLUMN_NAME_CARD_EXPIRATION.getName(), creditcard.getCardExpiration().getTimeInMillis());
        values.put(ExpenseManagerContract.CreditCardTable.COLUMN_NAME_CLOSING_DAY.getName(), creditcard.getClosingDay());
        values.put(ExpenseManagerContract.CreditCardTable.COLUMN_NAME_DUE_DAY.getName(), creditcard.getDueDay());

        long newRowId;
        newRowId = db.insert(ExpenseManagerContract.CreditCardTable.TABLE_NAME, null, values);

        if(newRowId == -1)
            throw new CouldNotInsertDataException("There was a problem inserting the Credit Card: " + creditcard.toString());

        return newRowId;

    }

    public List<CreditCard> getCreditCardList() {

        List<CreditCard> creditCards = new ArrayList<>();
        SQLiteDatabase db = mDatabaseHelper.getReadableDatabase();

        Cursor cursor = db.query(ExpenseManagerContract.CreditCardTable.TABLE_NAME, null, null, null, null, null, null);

        try {
            while (cursor.moveToNext()) {
                creditCards.add(getCreditCardFromCursor(cursor));
            }
        } finally {
            cursor.close();
        }

        return creditCards;
    }

    public List<CreditPeriod> getCreditPeriodListFromCard(int cardId) {
        List<CreditPeriod> creditPeriods = new ArrayList<>();
        SQLiteDatabase db = mDatabaseHelper.getReadableDatabase();

        Cursor cursor =  db.query(ExpenseManagerContract.CreditPeriodTable.TABLE_NAME, null,
                ExpenseManagerContract.CreditPeriodTable.COLUMN_NAME_FOREIGN_KEY_CREDIT_CARD.getName() + " == " + cardId, null, null, null, null);

        try {
            while (cursor.moveToNext()) {
                creditPeriods.add(getCreditPeriodFromCursor(cursor));
            }
        } finally {
            cursor.close();
        }

        return creditPeriods;
    }

    public List<Expense> getExpensesFromCreditPeriod(int periodId) {
        List<Expense> expenses = new ArrayList<>();
        SQLiteDatabase db = mDatabaseHelper.getReadableDatabase();

        Cursor cursor =  db.query(ExpenseManagerContract.ExpenseTable.TABLE_NAME, null,
                ExpenseManagerContract.ExpenseTable.COLUMN_NAME_FOREIGN_KEY_CREDIT_PERIOD.getName() + " == " + periodId, null, null, null, null);

        try {
            while (cursor.moveToNext()) {
                expenses.add(getExpenseFromCursor(cursor));
            }
        } finally {
            cursor.close();
        }

        return expenses;
    }

    public List<Payment> getPaymentsFromCreditPeriod(int periodId) {
        List<Payment> payments = new ArrayList<>();
        SQLiteDatabase db = mDatabaseHelper.getReadableDatabase();

        Cursor cursor =  db.query(ExpenseManagerContract.PaymentTable.TABLE_NAME, null,
                ExpenseManagerContract.PaymentTable.COLUMN_NAME_FOREIGN_KEY_CREDIT_PERIOD.getName() + " == " + periodId, null, null, null, null);

        try {
            while (cursor.moveToNext()) {
                payments.add(getPaymentFromCursor(cursor));
            }
        } finally {
            cursor.close();
        }

        return payments;
    }




    public long insertExpense(int creditPeriodId, Expense expense) throws CouldNotInsertDataException {
        SQLiteDatabase db = mDatabaseHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(ExpenseManagerContract.ExpenseTable.COLUMN_NAME_FOREIGN_KEY_CREDIT_PERIOD.getName(), new Integer(creditPeriodId));
        values.put(ExpenseManagerContract.ExpenseTable.COLUMN_NAME_DESCRIPTION.getName(), expense.getDescription());
        values.put(ExpenseManagerContract.ExpenseTable.COLUMN_NAME_IMAGE.getName(), expense.getImage());
        values.put(ExpenseManagerContract.ExpenseTable.COLUMN_NAME_AMOUNT.getName(), expense.getAmount().toPlainString());
        values.put(ExpenseManagerContract.ExpenseTable.COLUMN_NAME_CURRENCY.getName(), expense.getCurrency().getCode());
        values.put(ExpenseManagerContract.ExpenseTable.COLUMN_NAME_DATE.getName(), expense.getDate().getTimeInMillis());
        values.put(ExpenseManagerContract.ExpenseTable.COLUMN_NAME_EXPENSE_CATEGORY.getName(), expense.getExpenseCategory().getCode());
        values.put(ExpenseManagerContract.ExpenseTable.COLUMN_NAME_EXPENSE_TYPE.getName(), expense.getExpenseType().getCode());


        long newRowId;
        newRowId = db.insert(ExpenseManagerContract.ExpenseTable.TABLE_NAME, null, values);

        if(newRowId == -1)
            throw new CouldNotInsertDataException("There was a problem inserting the Expense: " + expense.toString());

        return newRowId;
    }


    public long insertPayment(int creditPeriodId, Payment payment) throws CouldNotInsertDataException {
        SQLiteDatabase db = mDatabaseHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(ExpenseManagerContract.PaymentTable.COLUMN_NAME_FOREIGN_KEY_CREDIT_PERIOD.getName(), new Integer(creditPeriodId));
        values.put(ExpenseManagerContract.PaymentTable.COLUMN_NAME_DESCRIPTION.getName(), payment.getDescription());
        values.put(ExpenseManagerContract.PaymentTable.COLUMN_NAME_AMOUNT.getName(), payment.getAmount().toPlainString());
        values.put(ExpenseManagerContract.PaymentTable.COLUMN_NAME_CURRENCY.getName(), payment.getCurrency().getCode());
        values.put(ExpenseManagerContract.PaymentTable.COLUMN_NAME_DATE.getName(), payment.getDate().getTimeInMillis());


        long newRowId;
        newRowId = db.insert(ExpenseManagerContract.ExpenseTable.TABLE_NAME, null, values);

        if(newRowId == -1)
            throw new CouldNotInsertDataException("There was a problem inserting the Payment: " + payment.toString());

        return newRowId;
    }






    private CreditCard getCreditCardFromCursor(Cursor cursor) {
        int id = cursor.getInt(cursor.getColumnIndex(ExpenseManagerContract.CreditCardTable._ID));
        String cardAlias = cursor.getString(cursor.getColumnIndex(ExpenseManagerContract.CreditCardTable.COLUMN_NAME_CARD_ALIAS.getName()));
        String bankName = cursor.getString(cursor.getColumnIndex(ExpenseManagerContract.CreditCardTable.COLUMN_NAME_BANK_NAME.getName()));
        String cardNumber = cursor.getString(cursor.getColumnIndex(ExpenseManagerContract.CreditCardTable.COLUMN_NAME_CARD_NUMBER.getName()));
        Currency currency = Currency.valueOf(cursor.getString(cursor.getColumnIndex(ExpenseManagerContract.CreditCardTable.COLUMN_NAME_CURRENCY.getName())));
        CreditCardType cardType = CreditCardType.valueOf(cursor.getString(cursor.getColumnIndex(ExpenseManagerContract.CreditCardTable.COLUMN_NAME_CARD_TYPE.getName())));
        Calendar cardExpiration = Calendar.getInstance();
        cardExpiration.setTimeInMillis(cursor.getLong(cursor.getColumnIndex(ExpenseManagerContract.CreditCardTable.COLUMN_NAME_CARD_EXPIRATION.getName())));
        int closingDay = cursor.getInt(cursor.getColumnIndex(ExpenseManagerContract.CreditCardTable.COLUMN_NAME_CLOSING_DAY.getName()));
        int dueDay = cursor.getInt(cursor.getColumnIndex(ExpenseManagerContract.CreditCardTable.COLUMN_NAME_DUE_DAY.getName()));

        return new CreditCard(id, cardAlias, bankName, cardNumber, currency, cardType, cardExpiration, closingDay, dueDay);
    }


    private CreditPeriod getCreditPeriodFromCursor(Cursor cursor) {
        int id = cursor.getInt(cursor.getColumnIndex(ExpenseManagerContract.CreditPeriodTable._ID));
        int periodNameStyle = cursor.getInt(cursor.getColumnIndex(ExpenseManagerContract.CreditPeriodTable.COLUMN_NAME_PERIOD_NAME_STYLE.getName()));
        Calendar startDate = Calendar.getInstance();
        startDate.setTimeInMillis(cursor.getLong(cursor.getColumnIndex(ExpenseManagerContract.CreditPeriodTable.COLUMN_NAME_START_DATE.getName())));
        Calendar endDate = Calendar.getInstance();
        endDate.setTimeInMillis(cursor.getLong(cursor.getColumnIndex(ExpenseManagerContract.CreditPeriodTable.COLUMN_NAME_END_DATE.getName())));
        BigDecimal creditLimit =  new BigDecimal(cursor.getString(cursor.getColumnIndex(ExpenseManagerContract.CreditPeriodTable.COLUMN_NAME_CREDIT_LIMIT.getName())));

        return new CreditPeriod(id, periodNameStyle, startDate, endDate, creditLimit);
    }


    private Expense getExpenseFromCursor(Cursor cursor) {

        int id = cursor.getInt(cursor.getColumnIndex(ExpenseManagerContract.ExpenseTable._ID));
        String description = cursor.getString(cursor.getColumnIndex(ExpenseManagerContract.ExpenseTable.COLUMN_NAME_DESCRIPTION.getName()));
        byte[] image = cursor.getBlob(cursor.getColumnIndex(ExpenseManagerContract.ExpenseTable.COLUMN_NAME_IMAGE.getName()));
        BigDecimal amount = new BigDecimal(cursor.getString(cursor.getColumnIndex(ExpenseManagerContract.ExpenseTable.COLUMN_NAME_AMOUNT.getName())));
        Currency currency = Currency.valueOf(cursor.getString(cursor.getColumnIndex(ExpenseManagerContract.ExpenseTable.COLUMN_NAME_CURRENCY.getName())));
        Calendar date = Calendar.getInstance();
        date.setTimeInMillis(cursor.getLong(cursor.getColumnIndex(ExpenseManagerContract.ExpenseTable.COLUMN_NAME_DATE.getName())));
        ExpenseCategory expenseCategory = ExpenseCategory.valueOf(cursor.getString(cursor.getColumnIndex(ExpenseManagerContract.ExpenseTable.COLUMN_NAME_EXPENSE_CATEGORY.getName())));
        ExpenseType expenseType = ExpenseType.valueOf(cursor.getString(cursor.getColumnIndex(ExpenseManagerContract.ExpenseTable.COLUMN_NAME_EXPENSE_TYPE.getName())));

        return new Expense(id, description, image, amount, currency, date, expenseCategory, expenseType);
    }


    private Payment getPaymentFromCursor(Cursor cursor) {

        int id = cursor.getInt(cursor.getColumnIndex(ExpenseManagerContract.PaymentTable._ID));
        String description = cursor.getString(cursor.getColumnIndex(ExpenseManagerContract.PaymentTable.COLUMN_NAME_DESCRIPTION.getName()));
        BigDecimal amount = new BigDecimal(cursor.getString(cursor.getColumnIndex(ExpenseManagerContract.PaymentTable.COLUMN_NAME_AMOUNT.getName())));
        Currency currency = Currency.valueOf(cursor.getString(cursor.getColumnIndex(ExpenseManagerContract.PaymentTable.COLUMN_NAME_CURRENCY.getName())));
        Calendar date = Calendar.getInstance();
        date.setTimeInMillis(cursor.getLong(cursor.getColumnIndex(ExpenseManagerContract.PaymentTable.COLUMN_NAME_DATE.getName())));

        return new Payment(id, description, amount, currency, date);
    }
}
