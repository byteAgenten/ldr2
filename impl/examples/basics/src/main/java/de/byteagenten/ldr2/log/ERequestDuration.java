package de.byteagenten.ldr2.log;

import de.byteagenten.ldr2.LogEvent;
import de.byteagenten.ldr2.LogObject;

/**
 * Created by knooma2e on 07.03.2018.
 */
@LogEvent(
        name = "request_duration",
        level = LogEvent.Level.DEBUG,
        message = "{service} => {url} [{seconds}s]"
)
public class ERequestDuration {

    private double seconds = -1;
    private final String service;
    private final String url;
    private Long timestamp;

    public ERequestDuration(long startMillis, String service, String url) {
        this(service, url);
        this.calcDuration(startMillis);
    }

    public ERequestDuration(String service, String url) {

        this.timestamp = System.currentTimeMillis();
        this.service = service;
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

    public double getSeconds() {
        return this.seconds;
    }

    public void setSeconds(double seconds) {
        this.seconds = seconds;
    }


    public void calcDuration(long startMillis) {
        this.seconds = (double)Math.round((double)(System.currentTimeMillis() - startMillis) / 10) / 100.0;
    }


    public String getService() {
        return service;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }
}
