package de.byteagenten.ldr2;

/**
 * Created by knooma2e on 26.07.2016.
 */
public interface LogWriter {

    void init();

    void write(GenericLogEvent logEvent);

    void dispose();
}
