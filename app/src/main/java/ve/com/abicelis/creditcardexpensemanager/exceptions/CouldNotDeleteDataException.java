package ve.com.abicelis.creditcardexpensemanager.exceptions;

/**
 * Created by Alex on 8/8/2016.
 */
public class CouldNotDeleteDataException extends Exception {

    public static final String DEFAULT_MESSAGE = "There was a problem while deleting the data from the database";

    public CouldNotDeleteDataException(String message) {
        super(message);
    }

    public CouldNotDeleteDataException(String message, Throwable cause) {
        super(message, cause);
    }
}
