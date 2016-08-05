package de.byteagenten.ldr2.writer;

import de.byteagenten.ldr2.GenericLogEvent;

import java.util.List;

/**
 * Created by matthias on 05.08.16.
 */
public class HtmlDumper {

    public static String dumpPage(List<GenericLogEvent> logEventList) {

        StringBuilder sb = new StringBuilder();
        sb.append("<html>");
        sb.append("<head>");
        sb.append("<link rel='stylesheet' type='text/css' href='../ldr2.css'>");
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
        sb.append("<span class='ldr2-timestamp'>").append(event.getTimestamp()).append("</span>");
        sb.append("<span class='ldr2-event-name'>").append(event.getEventName()).append("</span>");
        sb.append("<span class='ldr2-session-id'>").append(event.getProperty(GenericLogEvent.SESSION_ID)).append("</span>");
        sb.append("<span class='ldr2-request-index'>").append(event.getProperty(GenericLogEvent.REQUEST_INDEX)).append("</span>");
        sb.append("</section>");
        sb.append("<ul class='ldr2-event-attributes'>");

        event.getPropertiesMap().forEach((key, value) -> {

            sb.append("<li>");

            sb.append("<span>").append(key).append("</span>");
            sb.append("<span>").append(value).append("</span>");

            sb.append("</li>");
        });

        sb.append("</ul>");
    }
}
