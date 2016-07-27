package de.byteagenten.ldr2;

import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by knooma2e on 26.07.2016.
 */
public class HttpServletSessionContext extends SessionContext {

    private static final String LOGGER_SCOPES = "$ldr.scopes$";
    private HttpSession session;

    public HttpServletSessionContext(HttpSession session) {
        this.session = session;
        Map<String, Scope> existingScopes = getScopeMap();
        this.session.setAttribute(LOGGER_SCOPES, existingScopes != null ? existingScopes : new HashMap<String, Scope>());
    }

    @Override
    protected Map<String, Scope> getScopeMap() {
        return (Map<String, Scope>) this.session.getAttribute(LOGGER_SCOPES);

    }

    @Override
    public String getSessionId() {
        return this.session.getId();
    }


}
