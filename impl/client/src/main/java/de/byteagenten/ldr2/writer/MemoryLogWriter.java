package de.byteagenten.ldr2.writer;

import com.google.gson.JsonObject;
import de.byteagenten.ldr2.GenericLogEvent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

/**
 * Created by matthias on 03.08.16.
 */
public class MemoryLogWriter implements LogWriter {

    public static final String BUFFER_SIZE = "ldr2.memory.buffer.size";
    private static final int MAX_BUFFER_SIZE = 1000000;
    private static final int MAX_MAX_AGE_MINUTES = 365 * 24 * 60;
    private static final int DEFAULT_BUFFER_SIZE = 1000;
    private static final int DEFAULT_MAX_AGE_MINUTES = 24 * 60;

    private static int bufferSize = DEFAULT_BUFFER_SIZE;
    private static int maxAgeMinutes = DEFAULT_MAX_AGE_MINUTES;

    private String name;
    private final List<GenericLogEvent> buffer = new ArrayList<>();

    @Override
    public void write(GenericLogEvent logEvent) {

        synchronized (this.buffer) {
            if (buffer.size() == bufferSize) {

                buffer.remove(buffer.size() - 1);
            }
            buffer.add(0, logEvent);
        }

    }

    public void clear() {

        synchronized (this.buffer) {
            buffer.clear();
        }
    }

    public List<GenericLogEvent> getBuffer() {

        synchronized (this.buffer) {
            return Collections.unmodifiableList(buffer);
        }

    }

    @Override
    public void dispose() {


    }


    @Override
    public void init(String name, JsonObject configJson) throws WriterException {

        this.name = name;

        if (configJson.has("bufferSize")) {

            if (!configJson.get("bufferSize").isJsonPrimitive() || !configJson.get("bufferSize").getAsJsonPrimitive().isNumber())
                throw new WriterException("Specified bufferSize is not a number");

            long bufferSize = Math.round(configJson.get("bufferSize").getAsNumber().doubleValue());
            MemoryLogWriter.bufferSize = bufferSize > 0 && bufferSize < MAX_BUFFER_SIZE ? (int) bufferSize : DEFAULT_BUFFER_SIZE;
        }

        if (configJson.has("maxAgeMinutes")) {

            if (!configJson.get("maxAgeMinutes").isJsonPrimitive() || !configJson.get("maxAgeMinutes").getAsJsonPrimitive().isNumber())
                throw new WriterException("Specified maxAgeMinutes is not a number");
            long maxAgeMinutes = Math.round(configJson.get("maxAgeMinutes").getAsNumber().doubleValue());
            MemoryLogWriter.maxAgeMinutes = maxAgeMinutes > 0 && maxAgeMinutes < MAX_MAX_AGE_MINUTES ? (int) maxAgeMinutes : DEFAULT_MAX_AGE_MINUTES;
        }
    }

    @Override
    public String getName() {
        return this.name;
    }
}
