package de.byteagenten.ldr2;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by knooma2e on 21.07.2016.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface LogEvent {

    public enum Level {
        DEBUG,
        INFO,
        WARN,
        ERROR,
        FATAL
    }

    Level level() default Level.ERROR;

    String name() default "";

    String message() default "";

    boolean requestAware() default true;

    boolean sessionAware() default true;

    boolean threadAware() default true;
}
