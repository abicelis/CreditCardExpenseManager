package ve.com.abicelis.creditcardexpensemanager.enums;

/**
 * Created by Alex on 6/8/2016.
 */
public enum TransactionType {
    EXPENSE("Expense"),
    PAYMENT("Income");

    private String mFriendlyName;

    TransactionType (String friendlyName) {
        this.mFriendlyName = friendlyName;
    }


    public String getCode(){
        return this.name();
    }

    public String getFriendlyName() {
        return mFriendlyName;
    }

    @Override
    public String toString() {
        return this.mFriendlyName;
    }
}
