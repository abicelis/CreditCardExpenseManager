package ve.com.abicelis.creditcardexpensemanager.exceptions;

/**
 * Created by Alex on 8/8/2016.
 */
public class CouldNotUpdateDataException extends Exception {

    public static final String DEFAULT_MESSAGE = "There was a problem while updating data on the database";

    public CouldNotUpdateDataException(String message) {
        super(message);
    }

    public CouldNotUpdateDataException(String message, Throwable cause) {
        super(message, cause);
    }
}
