package ve.com.abicelis.creditcardexpensemanager.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.StringBuilderPrinter;

import ve.com.abicelis.creditcardexpensemanager.mocks.CreditMock;



/**
 * Created by Alex on 8/8/2016.
 */
public class ExpenseManagerDbHelper extends SQLiteOpenHelper {

    // If you change the database schema, you must increment the database version.
    public static final String DATABASE_NAME = "CreditCardExpenseManager.db";
    public static final int DATABASE_VERSION = 1;
    private static final String COMMA_SEP = ", ";

    public ExpenseManagerDbHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, DATABASE_NAME, factory, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(generateCreateStatement());
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        //TODO: FIGURE THIS PART OUT!
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        sqLiteDatabase.execSQL(generateDeleteStatement());
        onCreate(sqLiteDatabase);
    }




    private String generateCreateStatement() {
        String createStatement =
                "CREATE TABLE " + ExpenseManagerContract.CreditCardTable.TABLE_NAME + " (" +
                        ExpenseManagerContract.CreditCardTable._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        ExpenseManagerContract.CreditCardTable.COLUMN_NAME_CARD_ALIAS.getName() + " " + ExpenseManagerContract.CreditCardTable.COLUMN_NAME_CARD_ALIAS.getDataType() + COMMA_SEP +
                        ExpenseManagerContract.CreditCardTable.COLUMN_NAME_BANK_NAME.getName() + " " + ExpenseManagerContract.CreditCardTable.COLUMN_NAME_BANK_NAME.getDataType() + COMMA_SEP +
                        ExpenseManagerContract.CreditCardTable.COLUMN_NAME_CARD_NUMBER.getName() + " " + ExpenseManagerContract.CreditCardTable.COLUMN_NAME_CARD_NUMBER.getDataType() + COMMA_SEP +
                        ExpenseManagerContract.CreditCardTable.COLUMN_NAME_CURRENCY.getName() + " " + ExpenseManagerContract.CreditCardTable.COLUMN_NAME_CURRENCY.getDataType() + COMMA_SEP +
                        ExpenseManagerContract.CreditCardTable.COLUMN_NAME_CARD_TYPE.getName() + " " + ExpenseManagerContract.CreditCardTable.COLUMN_NAME_CARD_TYPE.getDataType() + COMMA_SEP +
                        ExpenseManagerContract.CreditCardTable.COLUMN_NAME_CARD_EXPIRATION.getName() + " " + ExpenseManagerContract.CreditCardTable.COLUMN_NAME_CARD_EXPIRATION.getDataType() + COMMA_SEP +
                        ExpenseManagerContract.CreditCardTable.COLUMN_NAME_CLOSING_DAY.getName() + " " + ExpenseManagerContract.CreditCardTable.COLUMN_NAME_CLOSING_DAY.getDataType() + COMMA_SEP +
                        ExpenseManagerContract.CreditCardTable.COLUMN_NAME_DUE_DAY.getName() + " " + ExpenseManagerContract.CreditCardTable.COLUMN_NAME_DUE_DAY.getDataType() +
                        " ); " +



                        "CREATE TABLE " + ExpenseManagerContract.CreditPeriodTable.TABLE_NAME + " (" +
                        ExpenseManagerContract.CreditPeriodTable._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +

                        "FOREIGN KEY " + ExpenseManagerContract.CreditPeriodTable.COLUMN_NAME_FOREIGN_KEY_CREDIT_CARD.getName() + " REFERENCES " +
                        ExpenseManagerContract.CreditCardTable.TABLE_NAME + " (" + ExpenseManagerContract.CreditCardTable._ID + ")" +

                        ExpenseManagerContract.CreditPeriodTable.COLUMN_NAME_PERIOD_NAME_STYLE.getName() + " " + ExpenseManagerContract.CreditPeriodTable.COLUMN_NAME_PERIOD_NAME_STYLE.getDataType() + COMMA_SEP +
                        ExpenseManagerContract.CreditPeriodTable.COLUMN_NAME_START_DATE.getName() + " " + ExpenseManagerContract.CreditPeriodTable.COLUMN_NAME_START_DATE.getDataType() + COMMA_SEP +
                        ExpenseManagerContract.CreditPeriodTable.COLUMN_NAME_END_DATE.getName() + " " + ExpenseManagerContract.CreditPeriodTable.COLUMN_NAME_END_DATE.getDataType() + COMMA_SEP +
                        ExpenseManagerContract.CreditPeriodTable.COLUMN_NAME_CREDIT_LIMIT.getName() + " " + ExpenseManagerContract.CreditPeriodTable.COLUMN_NAME_CREDIT_LIMIT.getDataType() +
                        " ); " +



                        "CREATE TABLE " + ExpenseManagerContract.ExpenseTable.TABLE_NAME + " (" +
                        ExpenseManagerContract.ExpenseTable._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +

                        "FOREIGN KEY " + ExpenseManagerContract.ExpenseTable.COLUMN_NAME_FOREIGN_KEY_CREDIT_PERIOD.getName() + " REFERENCES " +
                        ExpenseManagerContract.CreditPeriodTable.TABLE_NAME + " (" + ExpenseManagerContract.CreditPeriodTable._ID + ")" +

                        ExpenseManagerContract.ExpenseTable.COLUMN_NAME_DESCRIPTION.getName() + " " + ExpenseManagerContract.ExpenseTable.COLUMN_NAME_DESCRIPTION.getDataType() + COMMA_SEP +
                        ExpenseManagerContract.ExpenseTable.COLUMN_NAME_IMAGE.getName() + " " + ExpenseManagerContract.ExpenseTable.COLUMN_NAME_IMAGE.getDataType() + COMMA_SEP +
                        ExpenseManagerContract.ExpenseTable.COLUMN_NAME_AMOUNT.getName() + " " + ExpenseManagerContract.ExpenseTable.COLUMN_NAME_AMOUNT.getDataType() + COMMA_SEP +
                        ExpenseManagerContract.ExpenseTable.COLUMN_NAME_CURRENCY.getName() + " " + ExpenseManagerContract.ExpenseTable.COLUMN_NAME_CURRENCY.getDataType() + COMMA_SEP +
                        ExpenseManagerContract.ExpenseTable.COLUMN_NAME_DATE.getName() + " " + ExpenseManagerContract.ExpenseTable.COLUMN_NAME_DATE.getDataType() + COMMA_SEP +
                        ExpenseManagerContract.ExpenseTable.COLUMN_NAME_EXPENSE_CATEGORY.getName() + " " + ExpenseManagerContract.ExpenseTable.COLUMN_NAME_EXPENSE_CATEGORY.getDataType() + COMMA_SEP +
                        ExpenseManagerContract.ExpenseTable.COLUMN_NAME_EXPENSE_TYPE.getName() + " " + ExpenseManagerContract.ExpenseTable.COLUMN_NAME_EXPENSE_TYPE.getDataType() +
                        " ); " +



                        "CREATE TABLE " + ExpenseManagerContract.PaymentTable.TABLE_NAME + " (" +
                        ExpenseManagerContract.PaymentTable._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +

                        "FOREIGN KEY " + ExpenseManagerContract.PaymentTable.COLUMN_NAME_FOREIGN_KEY_CREDIT_PERIOD.getName() + " REFERENCES " +
                        ExpenseManagerContract.CreditPeriodTable.TABLE_NAME + " (" + ExpenseManagerContract.CreditPeriodTable._ID + ")" +

                        ExpenseManagerContract.PaymentTable.COLUMN_NAME_DESCRIPTION.getName() + " " + ExpenseManagerContract.PaymentTable.COLUMN_NAME_DESCRIPTION.getDataType() + COMMA_SEP +
                        ExpenseManagerContract.PaymentTable.COLUMN_NAME_AMOUNT.getName() + " " + ExpenseManagerContract.PaymentTable.COLUMN_NAME_AMOUNT.getDataType() + COMMA_SEP +
                        ExpenseManagerContract.PaymentTable.COLUMN_NAME_CURRENCY.getName() + " " + ExpenseManagerContract.PaymentTable.COLUMN_NAME_CURRENCY.getDataType() + COMMA_SEP +
                        ExpenseManagerContract.PaymentTable.COLUMN_NAME_DATE.getName() + " " + ExpenseManagerContract.PaymentTable.COLUMN_NAME_DATE.getDataType() +

                        " ); ";

        return createStatement;
    }

    private String generateDeleteStatement() {
        String deleteStatement =
                "DROP TABLE IF EXISTS " + ExpenseManagerContract.CreditCardTable.TABLE_NAME + "; " +
                        "DROP TABLE IF EXISTS " + ExpenseManagerContract.CreditPeriodTable.TABLE_NAME + "; " +
                        "DROP TABLE IF EXISTS " + ExpenseManagerContract.ExpenseTable.TABLE_NAME + "; " +
                        "DROP TABLE IF EXISTS " + ExpenseManagerContract.PaymentTable.TABLE_NAME + "; ";

        return deleteStatement;
    }
}
