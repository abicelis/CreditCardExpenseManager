package ve.com.abicelis.creditcardexpensemanager.database;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.math.BigDecimal;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ve.com.abicelis.creditcardexpensemanager.enums.CreditCardBackground;
import ve.com.abicelis.creditcardexpensemanager.enums.CreditCardType;
import ve.com.abicelis.creditcardexpensemanager.enums.Currency;
import ve.com.abicelis.creditcardexpensemanager.enums.ExpenseCategory;
import ve.com.abicelis.creditcardexpensemanager.enums.ExpenseType;
import ve.com.abicelis.creditcardexpensemanager.exceptions.CouldNotDeleteDataException;
import ve.com.abicelis.creditcardexpensemanager.exceptions.CreditCardNotFoundException;
import ve.com.abicelis.creditcardexpensemanager.exceptions.CreditPeriodNotFoundException;
import ve.com.abicelis.creditcardexpensemanager.exceptions.CouldNotGetDataException;
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

    /**
     * Returns a List of stored Credit Cards.
     * Note: The Credit Cards will not contain CreditPeriods, Expenses or Payments.
     */
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

    /**
     * Returns a List of CreditPeriods associated with ccardId.
     * Note: The periods will not contain Expenses or Payments.
     * @param cardId The ID of the Credit Card
     */
    public List<CreditPeriod> getCreditPeriodListFromCard(int cardId) {
        List<CreditPeriod> creditPeriods = new ArrayList<>();
        SQLiteDatabase db = mDatabaseHelper.getReadableDatabase();

        Cursor cursor =  db.query(ExpenseManagerContract.CreditPeriodTable.TABLE_NAME, null,
                ExpenseManagerContract.CreditPeriodTable.COLUMN_NAME_FOREIGN_KEY_CREDIT_CARD.getName() + " == " + cardId, null, null, null,
                ExpenseManagerContract.CreditPeriodTable.COLUMN_NAME_START_DATE.getName() + " DESC");

        try {
            while (cursor.moveToNext()) {
                creditPeriods.add(getCreditPeriodFromCursor(cursor));
            }
        } finally {
            cursor.close();
        }

        return creditPeriods;
    }



    /**
     * Finds a CreditCard with the supplied cardId, will not return creditPeriods associated with
     * the card
     * @param cardId The ID of the Credit Card
     */
    public CreditCard getCreditCard(int cardId) throws CreditCardNotFoundException {
        String[] selectionArgs = new String[]{String.valueOf(cardId)};
        SQLiteDatabase db = mDatabaseHelper.getReadableDatabase();

        Cursor cursor =  db.query(ExpenseManagerContract.CreditCardTable.TABLE_NAME, null,
                ExpenseManagerContract.CreditCardTable._ID + " =? ", selectionArgs, null, null, null);

        if(cursor.getCount() == 0)
            throw new CreditCardNotFoundException("Specified Credit Card not found in the database. Passed value=" + cardId);
        if(cursor.getCount() > 1)
            throw new CreditCardNotFoundException("Database UNIQUE constraint failure, more than one record found. Passed value=" + cardId);

        cursor.moveToNext();
        return getCreditCardFromCursor(cursor);
    }

    /**
     * Returns a Credit Card with the supplied cardId and the specified periodIndex.
     * Note: PeriodIndex must be either zero (returns credit period contaning current date) or a
     * negative value (-1 returns previous credit period, and so on)
     * @param cardId The ID of the Credit Card
     * @param periodIndex An integer <= 0
     */
    public CreditCard getCreditCardWithCreditPeriod(int cardId, int periodIndex) throws CreditCardNotFoundException, CreditPeriodNotFoundException, InvalidParameterException{
        if(periodIndex > 0)
            throw new InvalidParameterException("PeriodIndex must be >=0. Passed value=" + periodIndex);

        CreditCard creditCard = getCreditCard(cardId);

        //Get ordered (DESC) list of credit periods
        List<CreditPeriod> creditPeriods = getCreditPeriodListFromCard(cardId);

        //Find the creditPeriod according to periodIndex
        CreditPeriod cp = getCreditPeriodFromPeriodIndex(creditPeriods, periodIndex, creditCard.getClosingDay());

        //Create Map and set it on the Credit Card
        Map<Integer, CreditPeriod> creditPeriodMap = new HashMap<>();
        creditPeriodMap.put(periodIndex, cp);
        creditCard.setCreditPeriods(creditPeriodMap);

        return creditCard;
    }


    /**
     * Returns a Credit Card with the supplied cardId and as many credit periods as numPeriods
     * indicates. Counting backwards starting on periodIndex.
     * Note: PeriodIndex must be either zero (current creditPriod) or a
     * negative value (-1 refers to the previous credit period, and so on)
     * Note2: Passing periodIndex=-1, numPeriods=3 will return three periods (Period -1, -2 and -3)
     * @param cardId The ID of the Credit Card
     * @param periodIndex An integer <= 0
     * @param numPeriods An integer >= 1
     */
    public CreditCard getCreditCardWithCreditPeriodRange(int cardId, int periodIndex, int numPeriods) throws CreditCardNotFoundException, CreditPeriodNotFoundException, InvalidParameterException {
        if (periodIndex > 0)
            throw new InvalidParameterException("PeriodIndex must be less or equal than 0. Passed value=" + periodIndex);
        if (numPeriods <= 0)
            throw new InvalidParameterException("NumPeriods must be more or equal than 1. Passed value=" + periodIndex);

        CreditCard creditCard = getCreditCard(cardId);

        //Get ordered (DESC) list of credit periods
        List<CreditPeriod> creditPeriods = getCreditPeriodListFromCard(cardId);

        Map<Integer, CreditPeriod> creditPeriodMap = new HashMap<>();

        //Find the creditPeriod according to periodIndex
        for (int i = periodIndex; i > (periodIndex-numPeriods); --i) {
            CreditPeriod cp = getCreditPeriodFromPeriodIndex(creditPeriods, i, creditCard.getClosingDay());
            creditPeriodMap.put(i, cp);
        }

        //Set creditPeriods in creditCard and return
        creditCard.setCreditPeriods(creditPeriodMap);
        return creditCard;
    }


    /**
     * Returns a Credit Period from a List of creditPeriods which complies with the supplied
     * periodIndex and closingDay.
     * Note: PeriodIndex must be either zero (returns credit period contaning current date) or a
     * negative value (-1 returns previous credit period, and so on)
     * Note2: closingDay must be between 1 and 28.
     * @param creditPeriods List of Credit Periods to search into
     * @param periodIndex An integer <= 0
     * @param closingDay An integer between 1 and 28
     */
    private CreditPeriod getCreditPeriodFromPeriodIndex(List<CreditPeriod> creditPeriods, int periodIndex, int closingDay) throws CreditPeriodNotFoundException, InvalidParameterException {
        if(closingDay < 1 || closingDay > 28)
            throw new InvalidParameterException("ClosingDay must be between 1 and 28. Passed value=" + closingDay);
        if(periodIndex > 0)
            throw new InvalidParameterException("PeriodIndex must be >=0. Passed value=" + periodIndex);
        if(creditPeriods == null || creditPeriods.size() == 0)
            throw new InvalidParameterException("creditPeriods is null or empty");

        //Get a day which will be between the starting and ending dates of the wanted creditPeriod
        Calendar day = Calendar.getInstance();
        if(periodIndex != 0)
            day.add(Calendar.MONTH, periodIndex);

        for (CreditPeriod cp : creditPeriods) {
            if(cp.getStartDate().compareTo(day) <=0 && cp.getEndDate().compareTo(day) >=0) {
                cp.setExpenses(getExpensesFromCreditPeriod(cp.getId()));
                cp.setPayments(getPaymentsFromCreditPeriod(cp.getId()));
                return cp;

            }
        }

        throw new CreditPeriodNotFoundException("Credit period (index " + periodIndex + ") not found in passed creditPeriods List");
    }

    /**
     * Returns a Credit Period with the supplied periodId, containing its Expenses and Payments
     * @param periodId The ID of the Credit Period
     */
    public CreditPeriod getCreditPeriodFromPeriodId(int periodId) throws CreditPeriodNotFoundException {
        SQLiteDatabase db = mDatabaseHelper.getReadableDatabase();
        CreditPeriod creditPeriod;

        Cursor cursor =  db.query(ExpenseManagerContract.CreditPeriodTable.TABLE_NAME, null, ExpenseManagerContract.CreditPeriodTable._ID + " == " + periodId, null, null, null, null);

        if(cursor.getCount() == 0)
            throw new CreditPeriodNotFoundException("Specified Credit Period not found in the database. Passed value=" + periodId);
        if(cursor.getCount() > 1)
            throw new CreditPeriodNotFoundException("Database UNIQUE constraint failure, more than one record found. Passed Value=" + periodId);

        try{
            cursor.moveToNext();
            creditPeriod = getCreditPeriodFromCursor(cursor);
            creditPeriod.setExpenses(getExpensesFromCreditPeriod(periodId));
            creditPeriod.setPayments(getPaymentsFromCreditPeriod(periodId));
        } finally {
            cursor.close();
        }

        return creditPeriod;
    }


    /**
     * Returns a List of Expenses, given a creditPeriodId
     * @param periodId the Id of the Credit period
     */
    public List<Expense> getExpensesFromCreditPeriod(int periodId) {
        List<Expense> expenses = new ArrayList<>();
        SQLiteDatabase db = mDatabaseHelper.getReadableDatabase();

        Cursor cursor =  db.query(ExpenseManagerContract.ExpenseTable.TABLE_NAME, null,
                ExpenseManagerContract.ExpenseTable.COLUMN_NAME_FOREIGN_KEY_CREDIT_PERIOD.getName() + " == " + periodId,
                null, null, null, ExpenseManagerContract.ExpenseTable.COLUMN_NAME_DATE.getName() + " DESC");


        try {
            while (cursor.moveToNext()) {
                expenses.add(getExpenseFromCursor(cursor));
            }
        } finally {
            cursor.close();
        }

        return expenses;
    }

    /**
     * Returns a List of Payments, given a creditPeriodId
     * @param periodId the Id of the Credit period
     */
    public List<Payment> getPaymentsFromCreditPeriod(int periodId) {
        List<Payment> payments = new ArrayList<>();
        SQLiteDatabase db = mDatabaseHelper.getReadableDatabase();

        Cursor cursor =  db.query(ExpenseManagerContract.PaymentTable.TABLE_NAME, null,
                ExpenseManagerContract.PaymentTable.COLUMN_NAME_FOREIGN_KEY_CREDIT_PERIOD.getName() + " == " + periodId,
                null, null, null, ExpenseManagerContract.PaymentTable.COLUMN_NAME_DATE.getName() + " DESC");

        try {
            while (cursor.moveToNext()) {
                payments.add(getPaymentFromCursor(cursor));
            }
        } finally {
            cursor.close();
        }

        return payments;
    }



    /* Delete data from database */


    public boolean deleteExpense(int expenseId) throws CouldNotDeleteDataException {
        SQLiteDatabase db = mDatabaseHelper.getWritableDatabase();
        String[] whereArgs = new String[]{String.valueOf(expenseId)};

        return db.delete(ExpenseManagerContract.ExpenseTable.TABLE_NAME,
                ExpenseManagerContract.ExpenseTable._ID + " =?",
                whereArgs) > 0;
    }





    /* Insert data into database */

    public long insertCreditCard(CreditCard creditcard, BigDecimal firstCreditPeriodLimit) throws CouldNotInsertDataException {
        SQLiteDatabase db = mDatabaseHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(ExpenseManagerContract.CreditCardTable.COLUMN_NAME_CARD_ALIAS.getName(), creditcard.getCardAlias());
        values.put(ExpenseManagerContract.CreditCardTable.COLUMN_NAME_BANK_NAME.getName(), creditcard.getBankName());
        values.put(ExpenseManagerContract.CreditCardTable.COLUMN_NAME_CARD_NUMBER.getName(), creditcard.getCardNumber());
        values.put(ExpenseManagerContract.CreditCardTable.COLUMN_NAME_CURRENCY.getName(), creditcard.getCurrency().getCode());
        values.put(ExpenseManagerContract.CreditCardTable.COLUMN_NAME_CARD_TYPE.getName(), creditcard.getCardType().getCode());
        values.put(ExpenseManagerContract.CreditCardTable.COLUMN_NAME_CLOSING_DAY.getName(), creditcard.getClosingDay());
        values.put(ExpenseManagerContract.CreditCardTable.COLUMN_NAME_DUE_DAY.getName(), creditcard.getDueDay());
        values.put(ExpenseManagerContract.CreditCardTable.COLUMN_NAME_BACKGROUND.getName(), creditcard.getCreditCardBackground().getCode());

        if(creditcard.getCardExpiration() != null)
            values.put(ExpenseManagerContract.CreditCardTable.COLUMN_NAME_CARD_EXPIRATION.getName(), creditcard.getCardExpiration().getTimeInMillis());
        else
            values.put(ExpenseManagerContract.CreditCardTable.COLUMN_NAME_CARD_EXPIRATION.getName(), "");

        long newRowId;
        newRowId = db.insert(ExpenseManagerContract.CreditCardTable.TABLE_NAME, null, values);

        if(newRowId == -1)
            throw new CouldNotInsertDataException("There was a problem inserting the Credit Card: " + creditcard.toString());
        else {
            //Insert first creditPeriod

            // Set dates to be at midnight (start of day) today.
            Calendar startDate = Calendar.getInstance();
            startDate.set(Calendar.HOUR_OF_DAY, 0);
            startDate.set(Calendar.MINUTE, 0);
            startDate.set(Calendar.SECOND, 0);
            startDate.set(Calendar.MILLISECOND, 0);

            Calendar endDate = Calendar.getInstance();
            endDate.setTimeInMillis(startDate.getTimeInMillis());

            //Set start date's DAY_OF_MONTH to closingDay and endDate's DAY_OF_MONTH to closingDay-1ms
            startDate.set(Calendar.DAY_OF_MONTH, creditcard.getClosingDay());
            endDate.set(Calendar.DAY_OF_MONTH, creditcard.getClosingDay());
            endDate.add(Calendar.MILLISECOND, -1);


            if(creditcard.getClosingDay() <= Calendar.getInstance().get(Calendar.DAY_OF_MONTH))
                endDate.add(Calendar.MONTH, 1);
            else
                startDate.add(Calendar.MONTH, -1);

            CreditPeriod creditPeriod = new CreditPeriod(CreditPeriod.PERIOD_NAME_COMPLETE, startDate, endDate, firstCreditPeriodLimit);
            insertCreditPeriod((int)newRowId, creditPeriod);
        }

        return newRowId;

    }

    public long insertCreditPeriod(int creditCardId, CreditPeriod creditPeriod) throws CouldNotInsertDataException {
        SQLiteDatabase db = mDatabaseHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(ExpenseManagerContract.CreditPeriodTable.COLUMN_NAME_FOREIGN_KEY_CREDIT_CARD.getName(), creditCardId);
        values.put(ExpenseManagerContract.CreditPeriodTable.COLUMN_NAME_PERIOD_NAME_STYLE.getName(), creditPeriod.getPeriodNameStyle());
        values.put(ExpenseManagerContract.CreditPeriodTable.COLUMN_NAME_START_DATE.getName(), creditPeriod.getStartDate().getTimeInMillis());
        values.put(ExpenseManagerContract.CreditPeriodTable.COLUMN_NAME_END_DATE.getName(), creditPeriod.getEndDate().getTimeInMillis());
        values.put(ExpenseManagerContract.CreditPeriodTable.COLUMN_NAME_CREDIT_LIMIT.getName(), creditPeriod.getCreditLimit().toPlainString());


        long newRowId;
        newRowId = db.insert(ExpenseManagerContract.CreditPeriodTable.TABLE_NAME, null, values);

        if(newRowId == -1)
            throw new CouldNotInsertDataException("There was a problem inserting the Credit Period: " + creditPeriod.toString());

        return newRowId;

    }

    public long insertExpense(int creditPeriodId, Expense expense) throws CouldNotInsertDataException {
        SQLiteDatabase db = mDatabaseHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(ExpenseManagerContract.ExpenseTable.COLUMN_NAME_FOREIGN_KEY_CREDIT_PERIOD.getName(), new Integer(creditPeriodId));
        values.put(ExpenseManagerContract.ExpenseTable.COLUMN_NAME_DESCRIPTION.getName(), expense.getDescription());
        values.put(ExpenseManagerContract.ExpenseTable.COLUMN_NAME_THUMBNAIL.getName(), expense.getThumbnail());
        values.put(ExpenseManagerContract.ExpenseTable.COLUMN_NAME_FULL_IMAGE_PATH.getName(), expense.getFullImagePath());
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




    /* Cursor to Model */

    private CreditCard getCreditCardFromCursor(Cursor cursor) {
        int id = cursor.getInt(cursor.getColumnIndex(ExpenseManagerContract.CreditCardTable._ID));
        String cardAlias = cursor.getString(cursor.getColumnIndex(ExpenseManagerContract.CreditCardTable.COLUMN_NAME_CARD_ALIAS.getName()));
        String bankName = cursor.getString(cursor.getColumnIndex(ExpenseManagerContract.CreditCardTable.COLUMN_NAME_BANK_NAME.getName()));
        String cardNumber = cursor.getString(cursor.getColumnIndex(ExpenseManagerContract.CreditCardTable.COLUMN_NAME_CARD_NUMBER.getName()));
        Currency currency = Currency.valueOf(cursor.getString(cursor.getColumnIndex(ExpenseManagerContract.CreditCardTable.COLUMN_NAME_CURRENCY.getName())));
        CreditCardType cardType = CreditCardType.valueOf(cursor.getString(cursor.getColumnIndex(ExpenseManagerContract.CreditCardTable.COLUMN_NAME_CARD_TYPE.getName())));
        int closingDay = cursor.getInt(cursor.getColumnIndex(ExpenseManagerContract.CreditCardTable.COLUMN_NAME_CLOSING_DAY.getName()));
        int dueDay = cursor.getInt(cursor.getColumnIndex(ExpenseManagerContract.CreditCardTable.COLUMN_NAME_DUE_DAY.getName()));
        CreditCardBackground creditCardBackground = CreditCardBackground.valueOf(cursor.getString(cursor.getColumnIndex(ExpenseManagerContract.CreditCardTable.COLUMN_NAME_BACKGROUND.getName())));


        Calendar cardExpiration = null;
        if(!cursor.getString(cursor.getColumnIndex(ExpenseManagerContract.CreditCardTable.COLUMN_NAME_CARD_EXPIRATION.getName())).isEmpty()) {
            cardExpiration = Calendar.getInstance();
            cardExpiration.setTimeInMillis(cursor.getLong(cursor.getColumnIndex(ExpenseManagerContract.CreditCardTable.COLUMN_NAME_CARD_EXPIRATION.getName())));
        }

        return new CreditCard(id, cardAlias, bankName, cardNumber, currency, cardType, cardExpiration, closingDay, dueDay, creditCardBackground);
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
        byte[] image = cursor.getBlob(cursor.getColumnIndex(ExpenseManagerContract.ExpenseTable.COLUMN_NAME_THUMBNAIL.getName()));
        String fullImagePath = cursor.getString(cursor.getColumnIndex(ExpenseManagerContract.ExpenseTable.COLUMN_NAME_FULL_IMAGE_PATH.getName()));
        BigDecimal amount = new BigDecimal(cursor.getString(cursor.getColumnIndex(ExpenseManagerContract.ExpenseTable.COLUMN_NAME_AMOUNT.getName())));
        Currency currency = Currency.valueOf(cursor.getString(cursor.getColumnIndex(ExpenseManagerContract.ExpenseTable.COLUMN_NAME_CURRENCY.getName())));
        Calendar date = Calendar.getInstance();
        date.setTimeInMillis(cursor.getLong(cursor.getColumnIndex(ExpenseManagerContract.ExpenseTable.COLUMN_NAME_DATE.getName())));
        ExpenseCategory expenseCategory = ExpenseCategory.valueOf(cursor.getString(cursor.getColumnIndex(ExpenseManagerContract.ExpenseTable.COLUMN_NAME_EXPENSE_CATEGORY.getName())));
        ExpenseType expenseType = ExpenseType.valueOf(cursor.getString(cursor.getColumnIndex(ExpenseManagerContract.ExpenseTable.COLUMN_NAME_EXPENSE_TYPE.getName())));

        return new Expense(id, description, image, fullImagePath, amount, currency, date, expenseCategory, expenseType);
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
