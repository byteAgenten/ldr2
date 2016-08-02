package de.byteagenten.ldr2.writer;

/**
 * Created by matthias on 02.08.16.
 */
public class WriterException extends Exception {

    public WriterException(String message) {
        super(message);
    }

    public WriterException(String message, Throwable cause) {
        super(message, cause);
    }

    public WriterException(Throwable cause) {
        super(cause);
    }
}
