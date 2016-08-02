package de.byteagenten.ldr2.writer;

import de.byteagenten.ldr2.GenericLogEvent;

import java.util.Properties;

/**
 * Created by knooma2e on 26.07.2016.
 */
public interface LogWriter {

    void init(Properties properties) throws WriterException;

    void init() throws WriterException;

    void write(GenericLogEvent logEvent);

    void dispose();
}
