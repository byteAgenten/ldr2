package de.byteagenten.ldr2.log;

import de.byteagenten.ldr2.LogEvent;

/**
 * Created by knooma2e on 26.07.2016.
 */
@LogEvent(name = "app_started",
        level = LogEvent.Level.DEBUG,
        message = "application has started",
        threadAware = false)
public class AppStarted {


}
