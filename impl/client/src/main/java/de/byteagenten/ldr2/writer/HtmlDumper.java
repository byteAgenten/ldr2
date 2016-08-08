package de.byteagenten.ldr2.writer;

import de.byteagenten.ldr2.GenericLogEvent;
import de.byteagenten.ldr2.InitializeException;

import java.io.*;
import java.util.List;

/**
 * Created by matthias on 05.08.16.
 */
public class HtmlDumper {

    public static String dumpPage(List<GenericLogEvent> logEventList) {


        StringBuilder sbCss = new StringBuilder();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(HtmlDumper.class.getResourceAsStream("/ldr2.css")))) {

            reader.lines().forEach(sbCss::append);

        } catch (IOException e) {
            //todo: log
        }


        StringBuilder sb = new StringBuilder();
        sb.append("<html>");
        sb.append("<head>");
        sb.append("<style>").append(sbCss.toString()).append("</style>");
        sb.append("</head>");

        sb.append("<body>");
        sb.append(dumpList(logEventList));
        sb.append("</body>");
        sb.append("</html>");
        return sb.toString();
    }

    public static String dumpList(List<GenericLogEvent> logEventList) {

        final StringBuilder sb = new StringBuilder();
        dumpList(sb, logEventList);
        return sb.toString();
    }

    public static void dumpList(final StringBuilder sb, List<GenericLogEvent> logEventList) {

        sb.append("<ul class='ldr2-event-list'>");

        logEventList.stream().forEach(event -> {

            sb.append("<li>");
            dumpItem(sb, event);
            sb.append("</li>");
        });

        sb.append("</ul>");

    }

    private static void dumpItem(final StringBuilder sb, GenericLogEvent event) {

        sb.append("<section class='ldr2-event-meta'>");
        sb.append("<span class='ldr2-event-time'>").append(event.getTimeString()).append("</span>");
        sb.append("<span class='ldr2-event-date'>").append(event.getLongDateString()).append("</span>");
        /*
        sb.append("<span class='ldr2-event-name'>").append(event.getEventName()).append("</span>");
        sb.append("<span class='ldr2-event-session-id'>").append(event.getProperty(GenericLogEvent.SESSION_ID)).append("</span>");
        sb.append("<span class='ldr2-event-request-index'>").append(event.getProperty(GenericLogEvent.REQUEST_INDEX)).append("</span>");
        */
        sb.append("<span class='ldr2-event-message'>").append(event.getProperty(GenericLogEvent.MESSAGE)).append("</span>");

        sb.append("</section>");
        sb.append("<ul class='ldr2-event-attributes'>");

        event.getPropertiesMap().forEach((key, value) -> {

            sb.append("<li>");

            sb.append("<span class='ldr2-attribute-key'>").append(key).append("</span>");
            sb.append("<span class='ldr2-attribute-value'>").append(value).append("</span>");

            sb.append("</li>");
        });

        sb.append("</ul>");
    }
}
