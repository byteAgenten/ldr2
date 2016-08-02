package de.byteagenten.ldr2.writer;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import de.byteagenten.ldr2.GenericLogEvent;

import java.util.Properties;

/**
 * Created by knooma2e on 26.07.2016.
 */
public class ConsoleOutputLogWriter implements LogWriter {

    private Gson gson = new GsonBuilder().setPrettyPrinting().create();

    @Override
    public void write(GenericLogEvent logEvent) {

        System.out.println(gson.toJson(logEvent.getJsonObject()));
    }

    @Override
    public void init(Properties properties) throws WriterException {

    }

    @Override
    public void init() throws WriterException{

    }

    @Override
    public void dispose() {

    }
}