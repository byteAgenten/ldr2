package de.byteagenten.ldr2;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * Created by knooma2e on 26.07.2016.
 */
@LogEvent(name = "exception_log", level = LogEvent.Level.WARN)
public class ExceptionLog {

    private Throwable exception;
    private String stackTrace;

    public ExceptionLog(Throwable throwable) {

        this.exception = throwable;

        StringWriter writer = new StringWriter(100);
        PrintWriter printWriter = new PrintWriter(writer);
        this.exception.printStackTrace(printWriter);
        this.stackTrace = writer.getBuffer().toString();
    }

    public Throwable getException() {
        return exception;
    }

    public String getStackTrace() {

        return this.stackTrace;
    }
}
