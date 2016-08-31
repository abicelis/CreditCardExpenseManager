package ve.com.abicelis.creditcardexpensemanager.database;

import android.provider.BaseColumns;

/**
 * Created by Alex on 8/8/2016.
 */
public final class ExpenseManagerContract {

    // To prevent someone from accidentally instantiating the contract class,
    // give it an empty constructor.
    public ExpenseManagerContract() { }

    /* CreditCard Table */
    public static abstract class CreditCardTable implements BaseColumns {
        public static final String TABLE_NAME = "creditcard";
        public static final TableColumn COLUMN_NAME_CARD_ALIAS = new TableColumn(DataType.TEXT, "cardalias");
        public static final TableColumn COLUMN_NAME_BANK_NAME = new TableColumn(DataType.TEXT, "bankname");
        public static final TableColumn COLUMN_NAME_CARD_NUMBER = new TableColumn(DataType.TEXT, "cardnumber");
        public static final TableColumn COLUMN_NAME_CURRENCY = new TableColumn(DataType.TEXT, "currency");
        public static final TableColumn COLUMN_NAME_CARD_TYPE = new TableColumn(DataType.TEXT, "cardtype");
        public static final TableColumn COLUMN_NAME_CARD_EXPIRATION = new TableColumn(DataType.TEXT, "cardexpiration");
        public static final TableColumn COLUMN_NAME_CLOSING_DAY = new TableColumn(DataType.TEXT, "closingday");
        public static final TableColumn COLUMN_NAME_DUE_DAY = new TableColumn(DataType.TEXT, "dueday");
        public static final TableColumn COLUMN_NAME_BACKGROUND = new TableColumn(DataType.TEXT, "background");
    }

    /* CreditPeriod Table */
    public static abstract class CreditPeriodTable implements BaseColumns {
        public static final String TABLE_NAME = "creditperiod";

        public static final TableColumn COLUMN_NAME_FOREIGN_KEY_CREDIT_CARD = new TableColumn(DataType.INTEGER, "fk_creditcard");

        public static final TableColumn COLUMN_NAME_PERIOD_NAME_STYLE = new TableColumn(DataType.TEXT, "periodnamestyle");
        public static final TableColumn COLUMN_NAME_START_DATE = new TableColumn(DataType.INTEGER, "startdate");
        public static final TableColumn COLUMN_NAME_END_DATE = new TableColumn(DataType.INTEGER, "enddate");
        public static final TableColumn COLUMN_NAME_CREDIT_LIMIT = new TableColumn(DataType.TEXT, "creditlimit");
    }
    /* Expense Table */
    public static abstract class ExpenseTable implements BaseColumns {
        public static final String TABLE_NAME = "expense";

        public static final TableColumn COLUMN_NAME_FOREIGN_KEY_CREDIT_PERIOD = new TableColumn(DataType.INTEGER, "fk_creditperiod");

        public static final TableColumn COLUMN_NAME_DESCRIPTION = new TableColumn(DataType.TEXT, "description");
        public static final TableColumn COLUMN_NAME_THUMBNAIL = new TableColumn(DataType.BLOB, "thumbnail");
        public static final TableColumn COLUMN_NAME_FULL_IMAGE_PATH = new TableColumn(DataType.TEXT, "fullimagepath");
        public static final TableColumn COLUMN_NAME_AMOUNT = new TableColumn(DataType.TEXT, "amount");
        public static final TableColumn COLUMN_NAME_CURRENCY = new TableColumn(DataType.TEXT, "currency");
        public static final TableColumn COLUMN_NAME_DATE = new TableColumn(DataType.INTEGER, "date");
        public static final TableColumn COLUMN_NAME_EXPENSE_CATEGORY = new TableColumn(DataType.TEXT, "expensecategory");
        public static final TableColumn COLUMN_NAME_EXPENSE_TYPE = new TableColumn(DataType.TEXT, "expensetype");
    }

    /* Payment Table */
    public static abstract class PaymentTable implements BaseColumns {
        public static final String TABLE_NAME = "payment";

        public static final TableColumn COLUMN_NAME_FOREIGN_KEY_CREDIT_PERIOD = new TableColumn(DataType.INTEGER, "fk_creditperiod");

        public static final TableColumn COLUMN_NAME_DESCRIPTION = new TableColumn(DataType.TEXT, "description");
        public static final TableColumn COLUMN_NAME_AMOUNT = new TableColumn(DataType.TEXT, "amount");
        public static final TableColumn COLUMN_NAME_CURRENCY = new TableColumn(DataType.TEXT, "currency");
        public static final TableColumn COLUMN_NAME_DATE = new TableColumn(DataType.INTEGER, "date");

    }

}
