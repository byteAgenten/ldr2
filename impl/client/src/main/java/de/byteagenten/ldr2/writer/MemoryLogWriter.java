package de.byteagenten.ldr2.writer;

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

    private static int bufferSize = 1000;

    private static final List<GenericLogEvent> buffer = new ArrayList<>();

    @Override
    public void init(Properties properties) throws WriterException {

        String bufferSizeProperty = properties.getProperty(BUFFER_SIZE);
        if (bufferSizeProperty == null) return;

        try {
            int specifiedBufferSize = Integer.parseInt(bufferSizeProperty);
            if (specifiedBufferSize < 1)
                throw new WriterException("Buffer size of memory writer must not be smaller than 1");

            bufferSize = specifiedBufferSize;

        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void init() throws WriterException {

    }

    @Override
    public void write(GenericLogEvent logEvent) {

        synchronized (MemoryLogWriter.buffer) {
            if (buffer.size() == bufferSize) {

                buffer.remove(buffer.size() - 1);
            }
            buffer.add(0,logEvent);
        }

    }

    public void clear() {

        synchronized (MemoryLogWriter.buffer) {
            buffer.clear();
        }
    }

    public List<GenericLogEvent> getBuffer() {

        synchronized (MemoryLogWriter.buffer) {
            return Collections.unmodifiableList(buffer);
        }

    }

    @Override
    public void dispose() {


    }
}
