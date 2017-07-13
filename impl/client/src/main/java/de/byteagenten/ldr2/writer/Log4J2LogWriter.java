package de.byteagenten.ldr2.writer;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import de.byteagenten.ldr2.GenericLogEvent;
import org.apache.logging.log4j.LogManager;

import java.util.Properties;

/**
 * Created by knooma2e on 26.07.2016.
 */
public class Log4J2LogWriter implements LogWriter {

    private static final org.apache.logging.log4j.Logger log4jLogger = LogManager.getLogger();

    private String name;

    private Gson gson = new GsonBuilder().setPrettyPrinting().create();

    @Override
    public void write(GenericLogEvent logEvent) {

        String msg = gson.toJson(logEvent.getJsonObject());

        switch (logEvent.getLogLevel()) {

            case DEBUG:
                if( log4jLogger.isDebugEnabled()) log4jLogger.debug(msg);
                break;
            case INFO:
                if( log4jLogger.isInfoEnabled()) log4jLogger.info(msg);
                break;
            case WARN:
                if( log4jLogger.isWarnEnabled()) log4jLogger.warn(msg);
                break;
            case ERROR:
                if( log4jLogger.isErrorEnabled()) log4jLogger.error(msg);
                break;
            case FATAL:
                if( log4jLogger.isFatalEnabled()) log4jLogger.fatal(msg);
                break;
        }
    }

    @Override
    public void init(String name, JsonObject writerConfigJsonObject) throws WriterException {
        this.name = name;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public void dispose() {

    }
}
