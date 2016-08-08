package de.byteagenten.ldr2;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by knooma2e on 22.07.2016.
 */
public class GenericLogEvent {


    public static final String SESSION_ID = "#sessionId";
    public static final String SESSION_START_TIMESTAMP = "#sessionStart";
    public static final String REQUEST_INDEX = "#requestIndex";
    public static final String REQUEST_START_TIMESTAMP = "#requestStart";
    public static final String REQUEST_URL = "#requestUrl";
    public static final String THREAD_ID = "#threadId";
    public static final String THREAD_NAME = "#threadName";
    public static final String MESSAGE = "#message";
    public static final String TIMESTAMP_MILLIS = "#timestampMillis";
    public static final String TIMESTAMP = "#timestamp";
    public static final String APPLICATION_ID = "#appId";
    public static final String EVENT_NAME = "#eventName";
    public static final String EVENT_CLASS = "#eventClass";
    public static final String LOG_LEVEL = "#logLevel";

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
        event.eventClass = eventClass != null ? eventClass.getName() : null;
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

    public String getMessage() {

        return getProperty(MESSAGE);
    }

    public String getProperty(String name) {

        if( !this.jsonObject.has(name)) return "";
        return this.jsonObject.get(name).getAsString();
    }

    public Map<String, String> getPropertiesMap() {

        final Map<String, String> propertiesMap = new LinkedHashMap<>();

        this.jsonObject.entrySet().stream().forEach(set -> {

            propertiesMap.put(set.getKey(), set.getValue().getAsString());
        });
        return propertiesMap;
    }

    public String getTimeString() {

        DateFormat df = new SimpleDateFormat(Logger.TIME_PATTERN);
        return df.format(this.timestampMillis);
    }

    public String getDateString() {
        DateFormat df = new SimpleDateFormat(Logger.DATE_PATTERN);
        return df.format(this.timestampMillis);
    }

    public String getLongDateString() {
        DateFormat df = new SimpleDateFormat(Logger.LONG_DATE_PATTERN);
        return df.format(this.timestampMillis);
    }
}
