package de.byteagenten.ldr2.examples.webapp;

import de.byteagenten.ldr2.ConsoleOutputLogWriter;
import de.byteagenten.ldr2.Logger;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by matthias on 28.07.16.
 */
public class ExampleServlet extends HttpServlet {

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);

        try {
            Logger.init("example-webapp", ConsoleOutputLogWriter.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {


        if(req.getRequestURI().endsWith("/login")) {

            Logger.pushScope("user", "jbond", true);

        } else if(req.getRequestURI().endsWith("/logout")) {

            Logger.popScope();
        }

        Logger.log("hello");

        resp.setContentType("text/plain");
        resp.getWriter().write("hello");
    }
}
