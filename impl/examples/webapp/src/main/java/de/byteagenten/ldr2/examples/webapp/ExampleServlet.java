package de.byteagenten.ldr2.examples.webapp;

import de.byteagenten.ldr2.LogEvent;
import de.byteagenten.ldr2.LogEventConfig;
import de.byteagenten.ldr2.writer.ConsoleOutputLogWriter;
import de.byteagenten.ldr2.Logger;
import de.byteagenten.ldr2.writer.HtmlDumper;
import de.byteagenten.ldr2.writer.MemoryLogWriter;
import de.byteagenten.ldr2.writer.WriterException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.util.InvalidPropertiesFormatException;

/**
 * Created by matthias on 28.07.16.
 */
public class ExampleServlet extends HttpServlet {

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);

        try {
            File logConfig = new File("/Users/matthias/Development/Projects/ldr2/impl/examples/basics/src/main/resources/log.cfg.json");
            Logger.init(logConfig);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        if( req.getRequestURI().endsWith("/log")) {

            resp.setContentType("text/html");

            MemoryLogWriter memoryLogWriter = (MemoryLogWriter)Logger.getLogWriter("memory_logger_1");

            resp.getWriter().write(HtmlDumper.dumpPage(memoryLogWriter.getBuffer()));

        } else {

            if( req.getSession().isNew()) {

                Logger.log("New Session", LogEvent.Level.INFO);
            }

            if(req.getRequestURI().endsWith("/login")) {

                Logger.pushScope("user", "jbond", true);
                Logger.log(new UserLogin(1, "James", "Bond"));

            } else if(req.getRequestURI().endsWith("/logout")) {

                Logger.removeScope("user");

            }else if(req.getRequestURI().endsWith("/null")) {

                Object nullObject = null;

                try {
                    try {
                        nullObject.toString();
                    } catch (Exception e) {
                        Logger.log(e);
                        throw new WriterException("Object is null", e);
                    }
                } catch (WriterException e) {
                    Logger.log(e);
                }

            } else if(req.getRequestURI().endsWith("/msg")) {

                Logger.log(String.format("No writer specified. Adding a default one -> %s", ConsoleOutputLogWriter.class));
            }

            Logger.log("hello");

            resp.setContentType("text/plain");
            resp.getWriter().write("Happy logging!");

        }






    }
}
