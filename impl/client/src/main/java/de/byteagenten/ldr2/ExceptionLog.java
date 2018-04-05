package de.byteagenten.ldr2;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * Created by knooma2e on 26.07.2016.
 */
@LogEvent(name = "exception_log", message = "{message}", level = LogEvent.Level.WARN)
public class ExceptionLog {

    private Throwable exception;
    private String stackTrace;

    private String message;

    public ExceptionLog(Throwable throwable, String message) {

        this.exception = throwable;

        StringWriter writer = new StringWriter(100);
        PrintWriter printWriter = new PrintWriter(writer);
        this.exception.printStackTrace(printWriter);
        this.stackTrace = writer.getBuffer().toString();

        this.message = message != null ? message : this.exception.getMessage() != null ? this.exception.getMessage() : this.exception.toString();
    }

    public ExceptionLog(Throwable throwable) {

        this(throwable, null);
    }

    public String getStackTrace() {

        return this.stackTrace;
    }

    @NoLog
    public String getMessage() {
        return this.message;
    }
}
