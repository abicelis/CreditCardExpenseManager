package ve.com.abicelis.creditcardexpensemanager.exceptions;

/**
 * Created by Alex on 31/8/2016.
 */
public class SharedPreferenceNotFoundException extends Exception {

    public static final String DEFAULT_MESSAGE = "Preference not found";

    public SharedPreferenceNotFoundException(String message) {
        super(message);
    }

    public SharedPreferenceNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
