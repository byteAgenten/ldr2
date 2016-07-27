package de.byteagenten.ldr2;

import com.google.gson.JsonObject;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by knooma2e on 21.07.2016.
 */
public class Logger {

    private static final String APPLICATION_ID_PROPERTY = "srsng.log.app.id";

    private static final String SESSION_SCOPE_ID = "#sessionId";
    private static final String SESSION_START_TIMESTAMP = "#sessionStart";
    private static final String REQUEST_INDEX = "#requestIndex";
    private static final String REQUEST_START_TIMESTAMP = "#requestStart";
    private static final String REQUEST_URL = "#requestUrl";
    private static final String THREAD_ID = "#threadId";
    private static final String THREAD_NAME = "#threadName";
    private static final String MESSAGE = "#message";


    public static final String ISO_UTC = "YYYY-MM-dd'T'HH:mm:ss.S";
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

    private static LogWriter logWriter;

    private static String applicationId;

    public static void init(String applicationId, Class logWriterClass) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {

        Logger.logWriter = (LogWriter) logWriterClass.getConstructor().newInstance();
        Logger.applicationId = applicationId;
        Logger.logWriter.init();
    }


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

    public static void log(Object evt) {
        log(evt, null);
    }

    public static void log(Object evt, LogEventConfig specificLogEventConfig) {

        if (Logger.applicationId == null)
            throw new IllegalStateException("Logger not initialized. Please call Logger.init() first.");

        if (evt instanceof Class) {

            try {
                Constructor constructor = ((Class) evt).getConstructor();
                evt = constructor.newInstance();
            } catch (Exception e) {
                return;
            }
        } else if (evt instanceof Throwable) {

            evt = new ThrowableWrapper((Throwable) evt);
        }

        final Object event = evt;

        LogEvent logEvent = event.getClass().getAnnotation(LogEvent.class);
        LogEventConfig logEventConfig = LogEventConfig.fromLogEvent(logEvent);
        logEventConfig.merge(specificLogEventConfig);

        if (logEventConfig.getName().length() == 0) logEventConfig.setName(event.getClass().getName());


        GenericLogEvent genericLogEvent = GenericLogEvent.create(
                Logger.applicationId,
                logEventConfig.getName(),
                event.getClass(),
                logEventConfig.getLevel());


        JsonObject jsonObject = genericLogEvent.getJsonObject();

        DateFormat df = new SimpleDateFormat(ISO_UTC);
        df.setTimeZone(UTC);


        if (logEventConfig.isSessionAware() && Logger.sessionContext.get() != null) {

            jsonObject.addProperty(SESSION_SCOPE_ID, Logger.sessionContext.get().getSessionId());
            jsonObject.addProperty(SESSION_START_TIMESTAMP, df.format(Logger.sessionContext.get().getStartTimestamp()));
        }
        if (logEventConfig.isRequestAware() && Logger.requestContext.get() != null) {

            jsonObject.addProperty(REQUEST_INDEX, Logger.requestContext.get().getIndex());
            jsonObject.addProperty(REQUEST_URL, Logger.requestContext.get().getUrl());
            jsonObject.addProperty(REQUEST_START_TIMESTAMP, df.format(Logger.requestContext.get().getStartTimestamp()));
        }

        if (logEventConfig.isThreadAware()) {
            jsonObject.addProperty(THREAD_ID, Thread.currentThread().getId());
            jsonObject.addProperty(THREAD_NAME, Thread.currentThread().getName());
        }

        if (logEventConfig.getMessage() != null && logEventConfig.getMessage().length() > 0) {

            jsonObject.addProperty(MESSAGE, logEventConfig.getMessage());
        }


        Logger.scopeStack.get().all().stream().forEach(scope -> {

            jsonObject.addProperty("$" + scope.getName(), scope.getValue());
        });

        try {
            Arrays.asList(Introspector.getBeanInfo(event.getClass())
                    .getPropertyDescriptors()).stream()
                    .filter(pd -> {
                        return pd.getReadMethod() != null && !pd.getReadMethod().getName().contains("getClass");
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

            if (Logger.logWriter != null) Logger.logWriter.write(genericLogEvent);

        } catch (IntrospectionException e) {
            e.printStackTrace();
        }
    }


    public static LogEvent.Level getDefaultLogLevel() {
        return LogEvent.Level.INFO;
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
        if (sessionContext.get() != null && removedScope.isSessionPersistent()) {
            sessionContext.get().removeScope(removedScope.getName());
        }
    }

}
