package de.byteagenten.ldr2;

/**
 * Created by knooma2e on 22.07.2016.
 */
public class Scope {

    public static String HTTP_SESSION_SCOPE = "srsng.log.scope.session";
    public static String HTTP_REQUEST_SCOPE = "srsng.log.scope.request";

    private long creationTimestamp;

    private String name;

    private String value;

    private boolean sessionPersistent;

    public Scope(String name, String value) {
        this(name, value, false);
    }
    public Scope(String name, String value, boolean sessionPersitent) {
        this.sessionPersistent = sessionPersitent;
        this.creationTimestamp = System.currentTimeMillis();
        this.name = name;
        this.value = value;
    }

    public boolean isSessionPersistent() {
        return sessionPersistent;
    }

    public long getCreationTimestamp() {
        return creationTimestamp;
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }
}
