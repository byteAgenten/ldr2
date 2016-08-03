package de.byteagenten.ldr2;

import de.byteagenten.ldr2.log.*;
import de.byteagenten.ldr2.writer.MemoryLogWriter;

import java.util.List;
import java.util.Properties;

/**
 * Hello world!
 */
public class BasicExample {
    public static void main(String[] args) throws InitializeException {



        Properties properties = new Properties();

        /*
        properties.put(ElasticsearchLogWriter.HOST, "localhost");
        properties.put(ElasticsearchLogWriter.PORT, "9300");

        Logger.init("myApp", ElasticsearchLogWriter.class, properties);
        */
        properties.put(MemoryLogWriter.BUFFER_SIZE, "2");

        MemoryLogWriter memoryLogWriter = new MemoryLogWriter();

        Logger.init("myApp", memoryLogWriter, properties);

        //Logger.init("myApp", ConsoleOutputLogWriter.class);

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

        List<GenericLogEvent> events = memoryLogWriter.getBuffer();

        for (GenericLogEvent event : events) {
            System.out.println(event.getJsonString());
        }

    }
}
