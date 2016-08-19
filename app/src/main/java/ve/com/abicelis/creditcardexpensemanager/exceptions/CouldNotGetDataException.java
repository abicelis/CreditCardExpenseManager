package ve.com.abicelis.creditcardexpensemanager.exceptions;

/**
 * Created by Alex on 8/8/2016.
 */
public class CouldNotGetDataException extends Exception {

    public static final String DEFAULT_MESSAGE = "There was a problem while getting the data from the database";

    public CouldNotGetDataException(String message) {
        super(message);
    }

    public CouldNotGetDataException(String message, Throwable cause) {
        super(message, cause);
    }
}
