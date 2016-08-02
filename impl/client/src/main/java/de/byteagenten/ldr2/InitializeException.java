package de.byteagenten.ldr2;

/**
 * Created by matthias on 02.08.16.
 */
public class InitializeException extends Exception {


    public InitializeException(String message) {
        super(message);
    }

    public InitializeException(String message, Throwable cause) {
        super(message, cause);
    }

    public InitializeException(Throwable cause) {
        super(cause);
    }
}
