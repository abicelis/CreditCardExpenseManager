package ve.com.abicelis.creditcardexpensemanager.enums;

import java.io.Serializable;

/**
 * Created by Alex on 6/8/2016.
 */
public enum Currency implements Serializable {

    USD("US Dollar"),
    CAD("Canadian Dollar"),
    VEF("Venezuelan Bolivar"),
    EUR("Euro"),
    MEX("Mexican Peso"),
    ARS("Argentine Peso"),
    BRL("Brazilian Real"),
    CLP("Chilean Peso"),
    COL("Colombian Peso"),
    UYU("Uruguayan Peso"),
    PEN("Peruvian Sol"),
    PYG("Paraguayan guarani"),
    ;

    private String mFriendlyName;

    Currency(String friendlyName) {
        mFriendlyName = friendlyName;
    }

    public String getCode(){
        return this.name();
    }

    public String getFriendlyName() {
        return mFriendlyName;
    }


    @Override
    public String toString() {
        return mFriendlyName;
    }

}
