package de.byteagenten.ldr2;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by knooma2e on 26.07.2016.
 */
public class SimpleSessionContext extends SessionContext {

    private Map<String, Scope> scopeMap = new HashMap<>();

    private UUID sessionId = UUID.randomUUID();

    protected Map<String, Scope> getScopeMap() {
        return this.scopeMap;
    }

    @Override
    public String getSessionId() {
        return this.sessionId.toString();
    }
}
