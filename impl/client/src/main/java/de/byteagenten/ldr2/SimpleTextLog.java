package de.byteagenten.ldr2;

/**
 * Created by matthias on 28.07.16.
 */
@LogEvent(name = "simple_text_log", level = LogEvent.Level.INFO, message = "this is a {message} !")
public class SimpleTextLog {

    private String message;

    public SimpleTextLog(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
