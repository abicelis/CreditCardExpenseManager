package ve.com.abicelis.creditcardexpensemanager.enums;

import ve.com.abicelis.creditcardexpensemanager.R;

/**
 * Created by Alex on 6/8/2016.
 */
public enum ExpenseCategory {

    FOOD("Food", R.color.expense_category_1),
    LEISURE("Leisure", R.color.expense_category_2),
    ENTERTAINMENT("Entertainment", R.color.expense_category_3),
    CLOTHING("Clothing", R.color.expense_category_4),
    EDUCATION("Education", R.color.expense_category_5);



    private String mFriendlyName;
    private int mColor;

    ExpenseCategory(String friendlyName, int color) {

        mFriendlyName = friendlyName;
        mColor = color;
    }


    public String getCode(){
        return this.name();
    }

    public int getColor() {
        return mColor;
    }

    public String getFriendlyName() {
        return mFriendlyName;
    }

    @Override
    public String toString() {
        return mFriendlyName;
    }


}
