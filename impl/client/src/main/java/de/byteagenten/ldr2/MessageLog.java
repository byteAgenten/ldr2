package de.byteagenten.ldr2;

/**
 * Created by matthias on 28.07.16.
 */
@LogEvent(name = "message_log", level = LogEvent.Level.INFO, message = "{message}")
public class MessageLog {

    private String message;

    public MessageLog(String message) {
        this.message = message;
    }

    @NoLog
    public String getMessage() {
        return message;
    }
}
