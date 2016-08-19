package ve.com.abicelis.creditcardexpensemanager.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.StringBuilderPrinter;

import java.util.Calendar;

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
        createDatabase(sqLiteDatabase);
        insertMockData(sqLiteDatabase);
    }


    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        //TODO: FIGURE THIS PART OUT!
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        deleteDatabase(sqLiteDatabase);
        onCreate(sqLiteDatabase);
    }


    private void insertMockData(SQLiteDatabase sqLiteDatabase) {
        String statement;

        Calendar cal = Calendar.getInstance();
        // Set cal to be at midnight (start of day) today.
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        cal.add(Calendar.DAY_OF_MONTH, -10);
        int closingDay = cal.get(Calendar.DAY_OF_MONTH);        //closingDay 10 days ago
        cal.add(Calendar.DAY_OF_MONTH, 20);
        int dueDay = cal.get(Calendar.DAY_OF_MONTH);            //dueDay in 10 days
        cal.add(Calendar.DAY_OF_MONTH, -10);


        statement = "INSERT INTO " + ExpenseManagerContract.CreditCardTable.TABLE_NAME + " (" +
                        ExpenseManagerContract.CreditCardTable._ID + COMMA_SEP +
                        ExpenseManagerContract.CreditCardTable.COLUMN_NAME_CARD_ALIAS.getName() + COMMA_SEP +
                        ExpenseManagerContract.CreditCardTable.COLUMN_NAME_BANK_NAME.getName() + COMMA_SEP +
                        ExpenseManagerContract.CreditCardTable.COLUMN_NAME_CARD_NUMBER.getName() + COMMA_SEP +
                        ExpenseManagerContract.CreditCardTable.COLUMN_NAME_CURRENCY.getName() + COMMA_SEP +
                        ExpenseManagerContract.CreditCardTable.COLUMN_NAME_CARD_TYPE.getName() + COMMA_SEP +
                        ExpenseManagerContract.CreditCardTable.COLUMN_NAME_CARD_EXPIRATION.getName() + COMMA_SEP +
                        ExpenseManagerContract.CreditCardTable.COLUMN_NAME_CLOSING_DAY.getName() + COMMA_SEP +
                        ExpenseManagerContract.CreditCardTable.COLUMN_NAME_DUE_DAY.getName() +
                        ") VALUES (0, 'MockCardAlias', 'MockBank', '1234-5678-1234-5678', 'VEF', 'MASTERCARD', '0', " + closingDay + ", " + dueDay + "); ";
        sqLiteDatabase.execSQL(statement);

        cal.add(Calendar.DAY_OF_MONTH, -9);
        long period1StartDate = cal.getTimeInMillis();
        cal.add(Calendar.MONTH, 1);
        long period2StartDate = cal.getTimeInMillis();
        cal.add(Calendar.MILLISECOND, -1);
        long period1EndDate = cal.getTimeInMillis();
        cal.add(Calendar.MONTH, 1);
        long period2EndDate = cal.getTimeInMillis();

        statement = "INSERT INTO " + ExpenseManagerContract.CreditPeriodTable.TABLE_NAME + " (" +
                        ExpenseManagerContract.CreditPeriodTable._ID + COMMA_SEP +
                        ExpenseManagerContract.CreditPeriodTable.COLUMN_NAME_FOREIGN_KEY_CREDIT_CARD.getName() + COMMA_SEP +
                        ExpenseManagerContract.CreditPeriodTable.COLUMN_NAME_PERIOD_NAME_STYLE.getName() + COMMA_SEP +
                        ExpenseManagerContract.CreditPeriodTable.COLUMN_NAME_START_DATE.getName() + COMMA_SEP +
                        ExpenseManagerContract.CreditPeriodTable.COLUMN_NAME_END_DATE.getName() + COMMA_SEP +
                        ExpenseManagerContract.CreditPeriodTable.COLUMN_NAME_CREDIT_LIMIT.getName() +
                        ") VALUES (0, 0, 0, '" + period1StartDate + "', '" + period1EndDate + "', '100000'), (1, 0, 0, '" + period2StartDate + "', '" + period2EndDate + "', '150000'); ";
        sqLiteDatabase.execSQL(statement);

        cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, -6);
        long expense1period1 = cal.getTimeInMillis();
        cal.add(Calendar.DAY_OF_MONTH, 1);
        long expense2period1 = cal.getTimeInMillis();
        cal.add(Calendar.DAY_OF_MONTH, 2);
        long expense3period1 = cal.getTimeInMillis();
        long expense4period1 = cal.getTimeInMillis();
        cal.add(Calendar.DAY_OF_MONTH, 2);
        long expense5period1 = cal.getTimeInMillis();


        statement  = "INSERT INTO " + ExpenseManagerContract.ExpenseTable.TABLE_NAME + " (" +
                        ExpenseManagerContract.ExpenseTable._ID + COMMA_SEP +
                        ExpenseManagerContract.ExpenseTable.COLUMN_NAME_FOREIGN_KEY_CREDIT_PERIOD.getName() + COMMA_SEP +
                        ExpenseManagerContract.ExpenseTable.COLUMN_NAME_DESCRIPTION.getName() + COMMA_SEP +
                        ExpenseManagerContract.ExpenseTable.COLUMN_NAME_IMAGE.getName() + COMMA_SEP +
                        ExpenseManagerContract.ExpenseTable.COLUMN_NAME_AMOUNT.getName() + COMMA_SEP +
                        ExpenseManagerContract.ExpenseTable.COLUMN_NAME_CURRENCY.getName() + COMMA_SEP +
                        ExpenseManagerContract.ExpenseTable.COLUMN_NAME_DATE.getName() + COMMA_SEP +
                        ExpenseManagerContract.ExpenseTable.COLUMN_NAME_EXPENSE_CATEGORY.getName() + COMMA_SEP +
                        ExpenseManagerContract.ExpenseTable.COLUMN_NAME_EXPENSE_TYPE.getName() +
                        ") VALUES (0, 0, 'MockExpense 1', X'0102030405060708090a0b0c0d0e0f', '5300', 'VEF', '" + expense1period1 + "', 'ENTERTAINMENT', 'EXTRAORDINARY'), " +
                        "(1, 0, 'MockExpense 2', X'0102030405060708090a0b0c0d0e0f', '10000', 'VEF', '" + expense2period1 + "', 'ENTERTAINMENT', 'ORDINARY')," +
                        "(2, 0, 'MockExpense 3', X'0102030405060708090a0b0c0d0e0f', '4500', 'VEF', '" + expense3period1 + "', 'ENTERTAINMENT', 'EXTRAORDINARY')," +
                        "(3, 0, 'MockExpense 4', X'0102030405060708090a0b0c0d0e0f', '2000', 'VEF', '" + expense4period1 + "', 'ENTERTAINMENT', 'EXTRAORDINARY')," +
                        "(4, 0, 'MockExpense 5', X'0102030405060708090a0b0c0d0e0f', '12000', 'VEF', '" + expense5period1 + "', 'ENTERTAINMENT', 'ORDINARY');";
        sqLiteDatabase.execSQL(statement);

    }

    private void createDatabase(SQLiteDatabase sqLiteDatabase) {
        String statement;

        statement = "CREATE TABLE " + ExpenseManagerContract.CreditCardTable.TABLE_NAME + " (" +
                ExpenseManagerContract.CreditCardTable._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                ExpenseManagerContract.CreditCardTable.COLUMN_NAME_CARD_ALIAS.getName() + " " + ExpenseManagerContract.CreditCardTable.COLUMN_NAME_CARD_ALIAS.getDataType() + COMMA_SEP +
                ExpenseManagerContract.CreditCardTable.COLUMN_NAME_BANK_NAME.getName() + " " + ExpenseManagerContract.CreditCardTable.COLUMN_NAME_BANK_NAME.getDataType() + COMMA_SEP +
                ExpenseManagerContract.CreditCardTable.COLUMN_NAME_CARD_NUMBER.getName() + " " + ExpenseManagerContract.CreditCardTable.COLUMN_NAME_CARD_NUMBER.getDataType() + COMMA_SEP +
                ExpenseManagerContract.CreditCardTable.COLUMN_NAME_CURRENCY.getName() + " " + ExpenseManagerContract.CreditCardTable.COLUMN_NAME_CURRENCY.getDataType() + COMMA_SEP +
                ExpenseManagerContract.CreditCardTable.COLUMN_NAME_CARD_TYPE.getName() + " " + ExpenseManagerContract.CreditCardTable.COLUMN_NAME_CARD_TYPE.getDataType() + COMMA_SEP +
                ExpenseManagerContract.CreditCardTable.COLUMN_NAME_CARD_EXPIRATION.getName() + " " + ExpenseManagerContract.CreditCardTable.COLUMN_NAME_CARD_EXPIRATION.getDataType() + COMMA_SEP +
                ExpenseManagerContract.CreditCardTable.COLUMN_NAME_CLOSING_DAY.getName() + " " + ExpenseManagerContract.CreditCardTable.COLUMN_NAME_CLOSING_DAY.getDataType() + COMMA_SEP +
                ExpenseManagerContract.CreditCardTable.COLUMN_NAME_DUE_DAY.getName() + " " + ExpenseManagerContract.CreditCardTable.COLUMN_NAME_DUE_DAY.getDataType() +
                " ); " ;
        sqLiteDatabase.execSQL(statement);


        statement = "CREATE TABLE " + ExpenseManagerContract.CreditPeriodTable.TABLE_NAME + " (" +
                ExpenseManagerContract.CreditPeriodTable._ID + " INTEGER PRIMARY KEY AUTOINCREMENT" + COMMA_SEP +
                ExpenseManagerContract.CreditPeriodTable.COLUMN_NAME_PERIOD_NAME_STYLE.getName() + " " + ExpenseManagerContract.CreditPeriodTable.COLUMN_NAME_PERIOD_NAME_STYLE.getDataType() + COMMA_SEP +
                ExpenseManagerContract.CreditPeriodTable.COLUMN_NAME_START_DATE.getName() + " " + ExpenseManagerContract.CreditPeriodTable.COLUMN_NAME_START_DATE.getDataType() + COMMA_SEP +
                ExpenseManagerContract.CreditPeriodTable.COLUMN_NAME_END_DATE.getName() + " " + ExpenseManagerContract.CreditPeriodTable.COLUMN_NAME_END_DATE.getDataType() + COMMA_SEP +
                ExpenseManagerContract.CreditPeriodTable.COLUMN_NAME_CREDIT_LIMIT.getName() + " " + ExpenseManagerContract.CreditPeriodTable.COLUMN_NAME_CREDIT_LIMIT.getDataType() + COMMA_SEP +

                ExpenseManagerContract.CreditPeriodTable.COLUMN_NAME_FOREIGN_KEY_CREDIT_CARD.getName() + " " + ExpenseManagerContract.CreditPeriodTable.COLUMN_NAME_CREDIT_LIMIT.getDataType() +
                " REFERENCES " + ExpenseManagerContract.CreditCardTable.TABLE_NAME + "(" + ExpenseManagerContract.CreditCardTable._ID + ") " +

                " ); ";
        sqLiteDatabase.execSQL(statement);


        statement = "CREATE TABLE " + ExpenseManagerContract.ExpenseTable.TABLE_NAME + " (" +
                ExpenseManagerContract.ExpenseTable._ID + " INTEGER PRIMARY KEY AUTOINCREMENT" + COMMA_SEP +
                ExpenseManagerContract.ExpenseTable.COLUMN_NAME_DESCRIPTION.getName() + " " + ExpenseManagerContract.ExpenseTable.COLUMN_NAME_DESCRIPTION.getDataType() + COMMA_SEP +
                ExpenseManagerContract.ExpenseTable.COLUMN_NAME_IMAGE.getName() + " " + ExpenseManagerContract.ExpenseTable.COLUMN_NAME_IMAGE.getDataType() + COMMA_SEP +
                ExpenseManagerContract.ExpenseTable.COLUMN_NAME_AMOUNT.getName() + " " + ExpenseManagerContract.ExpenseTable.COLUMN_NAME_AMOUNT.getDataType() + COMMA_SEP +
                ExpenseManagerContract.ExpenseTable.COLUMN_NAME_CURRENCY.getName() + " " + ExpenseManagerContract.ExpenseTable.COLUMN_NAME_CURRENCY.getDataType() + COMMA_SEP +
                ExpenseManagerContract.ExpenseTable.COLUMN_NAME_DATE.getName() + " " + ExpenseManagerContract.ExpenseTable.COLUMN_NAME_DATE.getDataType() + COMMA_SEP +
                ExpenseManagerContract.ExpenseTable.COLUMN_NAME_EXPENSE_CATEGORY.getName() + " " + ExpenseManagerContract.ExpenseTable.COLUMN_NAME_EXPENSE_CATEGORY.getDataType() + COMMA_SEP +
                ExpenseManagerContract.ExpenseTable.COLUMN_NAME_EXPENSE_TYPE.getName() + " " + ExpenseManagerContract.ExpenseTable.COLUMN_NAME_EXPENSE_TYPE.getDataType() + COMMA_SEP +

                ExpenseManagerContract.ExpenseTable.COLUMN_NAME_FOREIGN_KEY_CREDIT_PERIOD.getName() + " " + ExpenseManagerContract.ExpenseTable.COLUMN_NAME_FOREIGN_KEY_CREDIT_PERIOD.getDataType() +
                " REFERENCES " + ExpenseManagerContract.CreditPeriodTable.TABLE_NAME + "(" + ExpenseManagerContract.CreditPeriodTable._ID + ") " +

                " ); ";
        sqLiteDatabase.execSQL(statement);


        statement = "CREATE TABLE " + ExpenseManagerContract.PaymentTable.TABLE_NAME + " (" +
                ExpenseManagerContract.PaymentTable._ID + " INTEGER PRIMARY KEY AUTOINCREMENT" + COMMA_SEP +

                ExpenseManagerContract.PaymentTable.COLUMN_NAME_DESCRIPTION.getName() + " " + ExpenseManagerContract.PaymentTable.COLUMN_NAME_DESCRIPTION.getDataType() + COMMA_SEP +
                ExpenseManagerContract.PaymentTable.COLUMN_NAME_AMOUNT.getName() + " " + ExpenseManagerContract.PaymentTable.COLUMN_NAME_AMOUNT.getDataType() + COMMA_SEP +
                ExpenseManagerContract.PaymentTable.COLUMN_NAME_CURRENCY.getName() + " " + ExpenseManagerContract.PaymentTable.COLUMN_NAME_CURRENCY.getDataType() + COMMA_SEP +
                ExpenseManagerContract.PaymentTable.COLUMN_NAME_DATE.getName() + " " + ExpenseManagerContract.PaymentTable.COLUMN_NAME_DATE.getDataType() + COMMA_SEP +


                ExpenseManagerContract.PaymentTable.COLUMN_NAME_FOREIGN_KEY_CREDIT_PERIOD.getName() + " " + ExpenseManagerContract.PaymentTable.COLUMN_NAME_FOREIGN_KEY_CREDIT_PERIOD.getDataType() +
                " REFERENCES " + ExpenseManagerContract.CreditPeriodTable.TABLE_NAME + "(" + ExpenseManagerContract.CreditPeriodTable._ID + ") " +

                " ); ";
        sqLiteDatabase.execSQL(statement);
    }

    private void deleteDatabase(SQLiteDatabase sqLiteDatabase) {
        String statement ;

        statement = "DROP TABLE IF EXISTS " + ExpenseManagerContract.PaymentTable.TABLE_NAME + "; ";
        sqLiteDatabase.execSQL(statement);

        statement = "DROP TABLE IF EXISTS " + ExpenseManagerContract.ExpenseTable.TABLE_NAME + "; ";
        sqLiteDatabase.execSQL(statement);

        statement = "DROP TABLE IF EXISTS " + ExpenseManagerContract.CreditPeriodTable.TABLE_NAME + "; ";
        sqLiteDatabase.execSQL(statement);

        statement = "DROP TABLE IF EXISTS " + ExpenseManagerContract.CreditCardTable.TABLE_NAME + "; ";
        sqLiteDatabase.execSQL(statement);
    }
}
