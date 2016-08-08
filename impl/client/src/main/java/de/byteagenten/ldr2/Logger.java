package de.byteagenten.ldr2;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import de.byteagenten.ldr2.writer.ConsoleOutputLogWriter;
import de.byteagenten.ldr2.writer.LogWriter;
import de.byteagenten.ldr2.writer.WriterException;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by knooma2e on 21.07.2016.
 */
public class Logger {

    private static final String APPLICATION_ID_PROPERTY = "srsng.log.app.id";

    public static final String DAY_OF_WEEK_PATTERN  = "EEEE";
    public static final String TIME_PATTERN  = "HH:mm:ss.S";
    public static final String DATE_PATTERN  = "YYYY-MM-dd";
    public static final String LONG_DATE_PATTERN  = DAY_OF_WEEK_PATTERN + "',' YYYY-MM-dd";
    public static final String ISO_UTC = DATE_PATTERN + "'T'" + TIME_PATTERN;
    public static final TimeZone UTC = TimeZone.getTimeZone("utc");


    private static final ThreadLocal<SessionContext> sessionContext = new ThreadLocal<SessionContext>() {
        @Override
        protected SessionContext initialValue() {
            return null;
        }
    };

    private static final ThreadLocal<RequestContext> requestContext = new ThreadLocal<RequestContext>() {
        @Override
        protected RequestContext initialValue() {
            return null;
        }
    };

    private static final ThreadLocal<ScopeStack> scopeStack = new ThreadLocal<ScopeStack>() {
        @Override
        protected ScopeStack initialValue() {
            return new ScopeStack();
        }
    };

    private static List<LogWriter> logWriter = new ArrayList<>();

    private static String applicationId;

    public static void setSessionContext(SessionContext sessionContext) {
        Logger.sessionContext.set(sessionContext);
        sessionContext.getScopeMap().values().forEach(scope -> scopeStack.get().push(scope));
    }

    public static void setRequestContext(RequestContext requestContext) {
        Logger.requestContext.set(requestContext);
    }

    public static ScopeStack createChildScopeStack() {
        return scopeStack.get().createChild();
    }

    public static void log(String message, LogEvent.Level logLevel) {

        LogEventConfig logEventConfig = LogEventConfig.create();
        logEventConfig.setLevel(logLevel);
        log(message, logEventConfig);
    }

    public static void log() {
        log(null);
    }

    public static void log(Object evt) {
        log(evt, null);
    }


    public static void log(Object inEvent, LogEventConfig specificLogEventConfig) {

        if (Logger.applicationId == null)
            throw new IllegalStateException("Logger not initialized. Please call Logger.init() first.");

        if (inEvent == null) inEvent = new NullLog();

        if (inEvent instanceof Class) {

            try {
                Constructor constructor = ((Class) inEvent).getConstructor();
                inEvent = constructor.newInstance();
            } catch (Exception e) {
                return;
            }
        } else if (inEvent instanceof Throwable) {

            inEvent = new ExceptionLog((Throwable) inEvent);

        } else if (inEvent instanceof String) {

            inEvent = new MessageLog((String) inEvent);
        }


        final Object event = inEvent;

        LogEvent logEvent = event != null ? event.getClass().getAnnotation(LogEvent.class) : null;
        LogEventConfig logEventConfig = LogEventConfig.fromLogEvent(logEvent);
        logEventConfig.merge(specificLogEventConfig);

        if (logEventConfig.getName().length() == 0)
            logEventConfig.setName(event != null ? event.getClass().getName() : "unknown");


        GenericLogEvent genericLogEvent = GenericLogEvent.create(
                Logger.applicationId,
                logEventConfig.getName(),
                event != null ? event.getClass() : null,
                logEventConfig.getLevel());


        JsonObject jsonObject = genericLogEvent.getJsonObject();

        DateFormat df = new SimpleDateFormat(ISO_UTC);
        df.setTimeZone(UTC);


        if (logEventConfig.isSessionAware() && Logger.sessionContext.get() != null) {

            jsonObject.addProperty(GenericLogEvent.SESSION_ID, Logger.sessionContext.get().getSessionId());
            jsonObject.addProperty(GenericLogEvent.SESSION_START_TIMESTAMP, df.format(Logger.sessionContext.get().getStartTimestamp()));
        }
        if (logEventConfig.isRequestAware() && Logger.requestContext.get() != null) {

            jsonObject.addProperty(GenericLogEvent.REQUEST_INDEX, Logger.requestContext.get().getIndex());
            jsonObject.addProperty(GenericLogEvent.REQUEST_URL, Logger.requestContext.get().getUrl());
            jsonObject.addProperty(GenericLogEvent.REQUEST_START_TIMESTAMP, df.format(Logger.requestContext.get().getStartTimestamp()));
        }

        if (logEventConfig.isThreadAware()) {
            jsonObject.addProperty(GenericLogEvent.THREAD_ID, Thread.currentThread().getId());
            jsonObject.addProperty(GenericLogEvent.THREAD_NAME, Thread.currentThread().getName());
        }

        if (logEventConfig.getMessage() != null && logEventConfig.getMessage().length() > 0) {

            jsonObject.addProperty(GenericLogEvent.MESSAGE, buildMessage(event, logEventConfig.getMessage()));
        }


        Logger.scopeStack.get().all().stream().forEach(scope -> {

            jsonObject.addProperty("$" + scope.getName(), scope.getValue());
        });

        try {
            if (event != null) {

                Arrays.asList(Introspector.getBeanInfo(event.getClass())
                        .getPropertyDescriptors()).stream()
                        .filter(pd -> {

                            Method readMethod = pd.getReadMethod();
                            if (readMethod == null) return false;
                            if (readMethod.getName().contains("getClass")) return false;

                            NoLog noLogAnnotation = readMethod.getAnnotation(NoLog.class);
                            if (noLogAnnotation != null) return false;

                            return true;

                        }).forEach(pd -> {
                    try {

                        Class<?> pt = pd.getReadMethod().getReturnType();

                        if (pt.isAssignableFrom(String.class)) {

                            jsonObject.addProperty(pd.getName(), (String) pd.getReadMethod().invoke(event));

                        } else if (pt.isAssignableFrom(Long.class) || (pt.isPrimitive() && (pt == Long.TYPE))) {

                            jsonObject.addProperty(pd.getName(), (Long) pd.getReadMethod().invoke(event));

                        } else if (pt.isAssignableFrom(Integer.class) || (pt.isPrimitive() && pt == Integer.TYPE)) {

                            jsonObject.addProperty(pd.getName(), (Integer) pd.getReadMethod().invoke(event));

                        } else if (pt.isAssignableFrom(Double.class) || (pt.isPrimitive() && pt == Double.TYPE)) {

                            jsonObject.addProperty(pd.getName(), (Double) pd.getReadMethod().invoke(event));

                        } else if (pt.isAssignableFrom(Float.class) || (pt.isPrimitive() && pt == Float.TYPE)) {

                            jsonObject.addProperty(pd.getName(), (Float) pd.getReadMethod().invoke(event));

                        } else if (pt.isAssignableFrom(Date.class)) {


                            Date value = (Date) pd.getReadMethod().invoke(event);
                            jsonObject.addProperty(pd.getName(), value != null ? df.format(value) : null);

                        } else if (pt.isAssignableFrom(Boolean.class) || (pt.isPrimitive() && pt == Boolean.TYPE)) {

                            jsonObject.addProperty(pd.getName(), (Boolean) pd.getReadMethod().invoke(event));

                        } else if (pt.isAssignableFrom(Throwable.class)) {

                            jsonObject.addProperty(pd.getName(), ((Throwable) pd.getReadMethod().invoke(event)).toString());
                        }

                    } catch (IllegalAccessException | InvocationTargetException e) {
                        e.printStackTrace();
                    }
                });
            }

            logWriter.stream().forEach( writer -> writer.write(genericLogEvent));


        } catch (IntrospectionException e) {
            e.printStackTrace();
        }
    }

    private static String buildMessage(Object event, String message) {

        if (event == null || message == null || message.trim().length() == 0) return message;
        if (!message.contains("{")) return message;

        List<String> placeholders = new ArrayList<>();

        StringBuilder sb = new StringBuilder();

        boolean in = false;

        for (char c : message.toCharArray()) {
            if (!in && c == '{') {
                in = true;
                continue;
            }
            if (in) {
                if (c == '}') {
                    in = false;
                    placeholders.add(sb.toString());
                    sb = new StringBuilder();
                    continue;
                }
                sb.append(c);
            }
        }

        for (String placeholder : placeholders) {

            String firstChar = placeholder.substring(0, 1);
            String getterName = "get" + firstChar.toUpperCase() + placeholder.substring(1, placeholder.length());

            Object o = null;

            try {
                Method method = event.getClass().getMethod(getterName);
                o = method.invoke(event);
            } catch (Exception e) {
                e.printStackTrace();
            }
            String replacement = o != null ? o.toString() : "";
            message = message.replace("{" + placeholder + "}", replacement);
        }


        return message;
    }


    public static LogEvent.Level getDefaultLogLevel() {
        return LogEvent.Level.ERROR;
    }

    public static void clear() {


    }


    public static void setScopeStack(ScopeStack scopeStack) {

        Logger.scopeStack.set(scopeStack);
    }

    public static void pushScope(String scopeName, String scopeValue, boolean sessionPersistent) {

        Scope newScope = new Scope(scopeName, scopeValue, sessionPersistent);

        Logger.scopeStack.get().push(newScope);

        if (sessionContext.get() != null && newScope.isSessionPersistent()) {
            sessionContext.get().addScope(newScope);
        }
    }

    public static void pushScope(String scopeName, String scopeValue) {

        Logger.pushScope(scopeName, scopeValue, false);
    }

    public static void popScope() {

        Scope removedScope = Logger.scopeStack.get().pop();

    }

    public static void removeScope(String scopeName) {

        Scope removedScope = Logger.scopeStack.get().remove(scopeName);
        if (sessionContext.get() != null && removedScope != null && removedScope.isSessionPersistent()) {
            sessionContext.get().removeScope(removedScope.getName());
        }
    }

    public static LogWriter getLogWriter(String name) {
        return logWriter.stream().filter( w -> w.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
    }

    public static void init(File config) throws InitializeException {

        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(config));
        } catch (FileNotFoundException e) {
            throw new InitializeException(String.format("No config file found at: %s", config.getAbsolutePath()));
        }

        StringBuffer stringBuffer = new StringBuffer();
        reader.lines().forEach(line -> stringBuffer.append(line));

        JsonParser parser = new JsonParser();
        JsonObject json = (JsonObject) parser.parse(stringBuffer.toString());

        if (!json.has("appKey")) throw new InitializeException("No 'appKey' specified in configuration.");

        applicationId = json.get("appKey").getAsString(); //todo: check that it is a string primitive

        if (!json.has("writer")) {
            logWriter.add(new ConsoleOutputLogWriter());
            System.out.println(String.format("No writer specified. Adding a default one -> %s", ConsoleOutputLogWriter.class.getName()));
        }

        if (!json.get("writer").isJsonArray()) {
            throw new InitializeException("Config attribute 'writer' has to be an array.");
        }

        JsonArray writerJsonArray = (JsonArray) json.get("writer");
        writerJsonArray.iterator().forEachRemaining(writerConfig -> {
            if (!writerConfig.isJsonObject()) return;

            JsonObject writerConfigJson = (JsonObject) writerConfig;

            String loggerName = "logger_" + logWriter.size();
            if (writerConfigJson.has("name")
                    && writerConfigJson.get("name").isJsonPrimitive()
                    && writerConfigJson.get("name").getAsJsonPrimitive().isString()) {

                loggerName = writerConfigJson.get("name").getAsString();
            }


            if (!writerConfigJson.has("class")) {
                //todo: log message
                return;
            }
            if (!writerConfigJson.get("class").isJsonPrimitive()) {
                //todo: log
                return;
            }

            String clazz = writerConfigJson.get("class").getAsString();


            LogWriter lwr = null;
            try {
                lwr = (LogWriter) Class.forName(clazz).getConstructor().newInstance();
            } catch (Exception e) {
                //throw new InitializeException("LogWriter can't be instantiated.", e);
                //todo: log
                return;
            }

            JsonObject writerConfigJsonObject = null;

            if (writerConfigJson.has("config")) {

                if (!writerConfigJson.get("config").isJsonObject()) {
                    //todo: log
                    return;
                }

                writerConfigJsonObject = writerConfigJson.get("config").getAsJsonObject();
            }

            try {
                lwr.init(loggerName, writerConfigJsonObject);
            } catch (WriterException e) {
                //todo: log
                return;
            }

            logWriter.add(lwr);
        });


    }
}
