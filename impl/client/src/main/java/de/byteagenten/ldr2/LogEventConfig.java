package de.byteagenten.ldr2;

/**
 * Created by knooma2e on 26.07.2016.
 */
public class LogEventConfig {

    private LogEvent.Level level = Logger.getDefaultLogLevel();

    private String name;

    private String message;

    private Boolean requestAware;

    private Boolean sessionAware;

    private Boolean threadAware;

    public static LogEventConfig create() {
        return new LogEventConfig();
    }

    public static LogEventConfig fromLogEvent(LogEvent logEvent) {

        LogEventConfig config = create();

        if( logEvent == null) {

            config.name = "";
            config.message = "";
            config.requestAware = true;
            config.sessionAware = true;
            config.threadAware = true;

            return config;
        }

        config.name = logEvent.name();
        config.message = logEvent.message();
        config.level = logEvent.level();
        config.requestAware = logEvent.requestAware();
        config.sessionAware = logEvent.sessionAware();
        config.threadAware = logEvent.threadAware();
        return config;
    }

    public LogEvent.Level getLevel() {
        return level;
    }

    public LogEventConfig setLevel(LogEvent.Level level) {
        this.level = level;
        return this;
    }

    public String getName() {
        return name;
    }

    public LogEventConfig setName(String name) {
        this.name = name;
        return this;
    }

    public String getMessage() {
        return message;
    }

    public LogEventConfig setMessage(String message) {
        this.message = message;
        return this;
    }

    public Boolean isRequestAware() {
        return requestAware;
    }

    public LogEventConfig setRequestAware(Boolean requestAware) {
        this.requestAware = requestAware;
        return this;
    }

    public Boolean isSessionAware() {
        return sessionAware;
    }

    public LogEventConfig setSessionAware(Boolean sessionAware) {
        this.sessionAware = sessionAware;
        return this;
    }

    public Boolean isThreadAware() {
        return threadAware;
    }

    public LogEventConfig setThreadAware(Boolean threadAware) {
        this.threadAware = threadAware;
        return this;
    }

    public void merge(LogEventConfig specificLogEventConfig) {

        if( specificLogEventConfig == null ) return;

        if( specificLogEventConfig.getName() != null) this.name = specificLogEventConfig.getName();
        if( specificLogEventConfig.getLevel() != null) this.level = specificLogEventConfig.getLevel();
        if( specificLogEventConfig.getMessage() != null ) this.message = specificLogEventConfig.getMessage();
        if( specificLogEventConfig.isSessionAware() != null) this.sessionAware = specificLogEventConfig.isSessionAware();
        if( specificLogEventConfig.isRequestAware() != null) this.requestAware = specificLogEventConfig.isRequestAware();
        if( specificLogEventConfig.isThreadAware() != null ) this.threadAware = specificLogEventConfig.isThreadAware();
    }
}
