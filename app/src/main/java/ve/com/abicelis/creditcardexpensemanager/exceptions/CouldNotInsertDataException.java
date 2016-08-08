package ve.com.abicelis.creditcardexpensemanager.exceptions;

/**
 * Created by Alex on 8/8/2016.
 */
public class CouldNotInsertDataException extends Exception {

    public static final String DEFAULT_MESSAGE = "There was a problem while inserting data into the database";

    public CouldNotInsertDataException(String message) {
        super(message);
    }

    public CouldNotInsertDataException(String message, Throwable cause) {
        super(message, cause);
    }
}
