package de.byteagenten.ldr2;

/**
 * Created by knooma2e on 29.03.2018.
 */
public abstract class LogObject {

    public void log() {
        Logger.log(this);
    }

    public void log(LogEventConfig logEventConfig) {
        Logger.log(this, logEventConfig);
    }
}
