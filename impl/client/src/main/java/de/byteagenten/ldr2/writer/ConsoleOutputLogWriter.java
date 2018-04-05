package de.byteagenten.ldr2.writer;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import de.byteagenten.ldr2.GenericLogEvent;

import java.util.Properties;

/**
 * Created by knooma2e on 26.07.2016.
 */
public class ConsoleOutputLogWriter implements LogWriter {

    private String name;

    private Gson gson = new GsonBuilder().setPrettyPrinting().create();

    @Override
    public void write(GenericLogEvent logEvent) {

        System.out.println(gson.toJson(logEvent.getJsonObject()));
    }

    @Override
    public void dispose() {

    }

    @Override
    public void init(String applicationId, String name, JsonObject writerConfigJsonObject) throws WriterException {
        this.name = name;
    }

    @Override
    public String getName() {
        return this.name;
    }
}
