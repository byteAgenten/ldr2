package de.byteagenten.ldr2;

import java.util.Date;
import java.util.Map;

/**
 * Created by knooma2e on 21.07.2016.
 */
public abstract class SessionContext {

    protected Date startTimestamp = new Date();

    private int requestNumber;

    public int nextRequestNumber() {

        synchronized (this) {
            requestNumber++;
            return requestNumber;
        }
    }

    public abstract String getSessionId();

    public Date getStartTimestamp() {
        return startTimestamp;
    }

    protected abstract Map<String, Scope> getScopeMap();

    public void addScope(Scope scope) {

        this.getScopeMap().put(scope.getName(), scope);
    }

    public void removeScope(String scopeName) {

        this.getScopeMap().remove(scopeName);
    }
}
