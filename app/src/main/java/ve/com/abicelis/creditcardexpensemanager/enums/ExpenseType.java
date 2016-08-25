package ve.com.abicelis.creditcardexpensemanager.enums;

import ve.com.abicelis.creditcardexpensemanager.R;

/**
 * Created by Alex on 6/8/2016.
 */
public enum ExpenseType {

    ORDINARY("Ordinary", "ORD", R.color.expense_category_4),
    EXTRAORDINARY("Extraordinary", "EXT", R.color.expense_category_7);




    private String mFriendlyName;
    private String mShortName;
    private int mColor;

    ExpenseType(String friendlyName, String shortName, int color) {

        mFriendlyName = friendlyName;
        mShortName = shortName;
        mColor = color;
    }


    public String getCode(){
        return this.name();
    }

    public String getFriendlyName() {
        return mFriendlyName;
    }

    public String getShortName() {
        return mShortName;
    }

    public int getColor() {
        return mColor;
    }

    @Override
    public String toString() {
        return mFriendlyName;
    }


}
