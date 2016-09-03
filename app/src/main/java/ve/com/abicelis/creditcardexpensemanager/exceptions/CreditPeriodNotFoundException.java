package ve.com.abicelis.creditcardexpensemanager.exceptions;

/**
 * Created by Alex on 3/9/2016.
 */
public class CreditPeriodNotFoundException extends Exception {

    public static final String DEFAULT_MESSAGE = "Credit Period does not exist";

    public CreditPeriodNotFoundException(String message) {
        super(message);
    }

    public CreditPeriodNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
