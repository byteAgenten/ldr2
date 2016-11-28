package de.byteagenten.ldr2;

import de.byteagenten.ldr2.log.AppStarted;
import de.byteagenten.ldr2.log.ApplicationStarted;
import de.byteagenten.ldr2.log.UserLogin;

import java.io.File;

/**
 * Hello world!
 */
public class BasicExample {
    public static void main(String[] args) throws InitializeException {


        File config = new File("/Users/matthias/Development/Projects/ldr2/impl/examples/basics/src/main/resources/log.cfg.json");
        Logger.init(config);

        //Most simple log call!
        Logger.log();


        Logger.log(null);


        Logger.log("this is my first log");

        Logger.log("this is my second log", LogEvent.Level.WARN);

        Logger.log(ApplicationStarted.class);

        Logger.log(ApplicationStarted.class, LogEventConfig.create().setLevel(LogEvent.Level.DEBUG).setName("application_start"));

        Logger.log(AppStarted.class);


        Logger.log(new UserLogin(1, "James", "Bond"));


        Logger.log(new UserLogin(2, "Austin", "Powers"), LogEventConfig.create().setLevel(LogEvent.Level.WARN).setThreadAware(false));


        Object nullObject = null;


        try {
            nullObject.toString();
        } catch (Exception e) {
            Logger.log(e);
        }

        try {
            nullObject.toString();
        } catch (Exception e) {
            Logger.log(e, LogEventConfig.create().setLevel(LogEvent.Level.ERROR).setMessage("bad luck"));
        }


        Logger.log(LogEventConfig.create().setMessage("This is my message"));

        Logger.log(LogEventConfig.create().setMessage("This is my message").setLevel(LogEvent.Level.DEBUG));


    }
}
