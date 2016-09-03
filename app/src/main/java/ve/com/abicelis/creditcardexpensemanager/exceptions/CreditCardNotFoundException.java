package ve.com.abicelis.creditcardexpensemanager.exceptions;

/**
 * Created by Alex on 3/9/2016.
 */
public class CreditCardNotFoundException extends Exception {

    public static final String DEFAULT_MESSAGE = "Credit Card does not exist";

    public CreditCardNotFoundException(String message) {
        super(message);
    }

    public CreditCardNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
