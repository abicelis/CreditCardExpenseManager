package ve.com.abicelis.creditcardexpensemanager.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import ve.com.abicelis.creditcardexpensemanager.enums.CreditCardType;
import ve.com.abicelis.creditcardexpensemanager.enums.Currency;
import ve.com.abicelis.creditcardexpensemanager.exceptions.CouldNotInsertDataException;
import ve.com.abicelis.creditcardexpensemanager.model.CreditCard;

/**
 * Created by Alex on 8/8/2016.
 */
public class ExpenseManagerDAO {

    private ExpenseManagerDbHelper mDatabaseHelper;

    public ExpenseManagerDAO(Context context) {
        mDatabaseHelper = new ExpenseManagerDbHelper(context, null, null, 0);

    }

    public void insertCreditCard(CreditCard creditcard) throws CouldNotInsertDataException {
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

    }

    public List<CreditCard> getCreditCardList() {

        List<CreditCard> creditCards = new ArrayList<>();
        SQLiteDatabase db = mDatabaseHelper.getReadableDatabase();

        Cursor cursor = db.query(ExpenseManagerContract.CreditCardTable.TABLE_NAME, null, null, null, null, null, null);

        try {
            while (cursor.moveToNext()) {
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

                creditCards.add(new CreditCard(id, cardAlias, bankName, cardNumber, currency, cardType, cardExpiration, closingDay, dueDay));
            }
        } finally {
            cursor.close();
        }

        return creditCards;
    }
}
