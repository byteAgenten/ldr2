package de.byteagenten.ldr2;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * Created by knooma2e on 22.07.2016.
 */
public class LogHttpFilter implements Filter {

    private static final String SESSION_CONTEXT_ATTRIBUTE_KEY = "$srsng.logging.session-context$";

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

        try {
            Logger.init(filterConfig.getServletContext().getResourceAsStream("/WEB-INF/log.cfg.json"));
        } catch (InitializeException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {


        if( servletRequest instanceof HttpServletRequest) {

            HttpServletRequest httpServletRequest = (HttpServletRequest)servletRequest;

            /*
            if( httpServletRequest.getSession().isNew()) {

                httpServletRequest.getSession().setAttribute(SESSION_CONTEXT_ATTRIBUTE_KEY, new HttpServletSessionContext(httpServletRequest.getSession()));
            }
            */

            SessionContext sessionContext = (SessionContext)httpServletRequest.getSession().getAttribute(SESSION_CONTEXT_ATTRIBUTE_KEY);
            if( sessionContext == null) {
                sessionContext = new HttpServletSessionContext(httpServletRequest.getSession());
                httpServletRequest.getSession().setAttribute(SESSION_CONTEXT_ATTRIBUTE_KEY, sessionContext );
            }

            Logger.setSessionContext(sessionContext);

            Logger.setRequestContext(new RequestContext(sessionContext.nextRequestNumber(), httpServletRequest.getRequestURI()));

            filterChain.doFilter(servletRequest, servletResponse);

            Logger.clear();


        } else {

            filterChain.doFilter(servletRequest, servletResponse);
        }
    }

    @Override
    public void destroy() {

    }
}
