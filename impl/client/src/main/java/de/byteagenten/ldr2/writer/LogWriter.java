package de.byteagenten.ldr2.writer;

import com.google.gson.JsonObject;
import de.byteagenten.ldr2.GenericLogEvent;

import java.util.Properties;

/**
 * Created by knooma2e on 26.07.2016.
 */
public interface LogWriter {

    void write(GenericLogEvent logEvent);

    void dispose();

    void init(String name, JsonObject writerConfigJsonObject) throws WriterException;

    String getName();
}
