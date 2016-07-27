package de.byteagenten.ldr2;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by knooma2e on 22.07.2016.
 */
public class GenericLogEvent {

    private static final String TIMESTAMP_MILLIS = "#timestampMillis";
    private static final String TIMESTAMP = "#timestamp";
    private static final String APPLICATION_ID = "#appId";
    private static final String EVENT_NAME = "#eventName";
    private static final String EVENT_CLASS = "#eventClass";
    private static final String LOG_LEVEL = "#logLevel";

    private long timestampMillis;

    private String timestamp;

    private String applicationId;

    private String eventName;

    private String eventClass;

    private LogEvent.Level logLevel;

    private JsonObject jsonObject;

    public long getTimestampMillis() {
        return timestampMillis;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public String getApplicationId() {
        return applicationId;
    }

    public String getEventName() {
        return eventName;
    }

    public String getEventClass() {
        return eventClass;
    }

    public LogEvent.Level getLogLevel() {
        return logLevel;
    }

    public static GenericLogEvent create(String applicationId, String eventName, Class eventClass, LogEvent.Level logLevel) {

        DateFormat df = new SimpleDateFormat(Logger.ISO_UTC);

        GenericLogEvent event = new GenericLogEvent();
        event.timestampMillis = System.currentTimeMillis();
        event.timestamp = df.format(event.timestampMillis);

        event.applicationId = applicationId;
        event.eventName = eventName;
        event.eventClass = eventClass.getName();
        event.logLevel = logLevel;

        event.jsonObject = new JsonObject();
        event.jsonObject.addProperty(TIMESTAMP_MILLIS, event.timestampMillis);
        event.jsonObject.addProperty(TIMESTAMP, event.timestamp);
        event.jsonObject.addProperty(APPLICATION_ID, event.applicationId);
        event.jsonObject.addProperty(EVENT_NAME, event.eventName);
        event.jsonObject.addProperty(EVENT_CLASS, event.eventClass);
        event.jsonObject.addProperty(LOG_LEVEL, event.logLevel.name());

        return event;
    }

    public JsonObject getJsonObject() {
        return jsonObject;
    }

    public Map<String, Object> toMap() {

        Gson gson = new Gson();
        return (Map<String, Object>) gson.fromJson(this.jsonObject, HashMap.class);
    }

    public String getJsonString() {

        Gson gson = new Gson();
        return gson.toJson(this.getJsonObject());
    }
}
