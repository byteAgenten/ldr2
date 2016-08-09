package de.byteagenten.ldr2.writer;

import de.byteagenten.ldr2.GenericLogEvent;
import de.byteagenten.ldr2.Logger;

import java.io.*;
import java.util.*;

/**
 * Created by matthias open 05.08.16.
 */
public class HtmlDumper {

    public static String dumpPage(List<GenericLogEvent> logEventList, Map<String, String> filterMap) {

        StringBuilder sbCss = new StringBuilder();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(HtmlDumper.class.getResourceAsStream("/ldr2.css")))) {

            reader.lines().forEach(sbCss::append);

        } catch (IOException e) {
            //todo: log
        }

        StringBuilder listSb = new StringBuilder();
        List<GenericLogEvent> dumpedEvents = dumpList(listSb, logEventList, filterMap);

        StringBuilder sb = new StringBuilder();
        sb.append("<html>");
        sb.append("<head>");
        sb.append("<style>").append(sbCss.toString()).append("</style>");
        sb.append("</head>");

        sb.append("<body>");
        dumpLogo(sb);
        sb.append(dumpHeader(dumpedEvents));
        sb.append(listSb);
        sb.append("</body>");
        sb.append("</html>");
        return sb.toString();
    }

    private static String dumpHeader(List<GenericLogEvent> logEventList) {

        GenericLogEvent oldestEntry = logEventList.size() > 0 ? logEventList.get(logEventList.size() - 1) : null;


        StringBuilder sb = new StringBuilder();

        sb.append("<section class='ldr2-header'>");

        dumpHeaderProperty(sb, "&nbsp;", "LDR2");
        dumpHeaderProperty(sb, "Entry Count", String.valueOf(logEventList.size()));
        dumpHeaderProperty(sb, "Oldest entry", oldestEntry != null ? (oldestEntry.getDateString() + " " + oldestEntry.getTimeString()) : "-");
        dumpHeaderProperty(sb, "App key", Logger.getApplicationId());

        sb.append("</section>");

        return sb.toString();
    }

    private static void dumpLogo(StringBuilder sb) {

        sb.append("<img class='logo' title='byteAgenten gmbh' alt=\"\" src=\"data:image/png;base64,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\" />");
    }

    private static void dumpHeaderProperty(StringBuilder sb, String label, String value) {

        sb.append("<section class='ldr2-header-property'>");
        sb.append("<span class='ldr2-label'>").append(label).append("</span>");
        sb.append("<span class='ldr2-value'>").append(value).append("</span>");
        sb.append("</section>");
    }

    /*
    public static String dumpList(List<GenericLogEvent> logEventList, Map<String, String> filterMap) {

        final StringBuilder sb = new StringBuilder();
        dumpList(sb, logEventList, filterMap);
        return sb.toString();
    }
    */

    public static List<GenericLogEvent> dumpList(final StringBuilder sb, List<GenericLogEvent> logEventList, final Map<String, String> filterMap) {

        sb.append("<ul class='ldr2-event-list'>");

        final List<GenericLogEvent> dumpedEvents = new ArrayList<>();

        logEventList.stream().forEach(event -> {

            if (dumpItem(sb, event, filterMap)) {
                dumpedEvents.add(event);
            }

        });

        sb.append("</ul>");

        return dumpedEvents;
    }

    private static boolean dumpItem(final StringBuilder sb, GenericLogEvent event, Map<String, String> filterMap) {

        final Map<String, String> propertiesMap = event.getPropertiesMap();

        final Gate gate = new Gate();

        if (filterMap != null) {
            filterMap.entrySet().stream().forEach(filterEntry -> {

                String key = filterEntry.getKey();
                if( key.charAt(0) == '!') key = "#" + key.substring(1, key.length());

                if (!propertiesMap.containsKey(key)) {
                    if( key.charAt(0) != '#') {
                        key = "#" + key;
                        if( !propertiesMap.containsKey(key)) {
                            gate.setOpen(false);
                            return;
                        }
                    }
                }

                if (!filterEntry.getValue().equalsIgnoreCase(propertiesMap.get(key)))
                    gate.setOpen(false);
            });
        }

        if (!gate.isOpen()) return false;

        sb.append("<li>");
        sb.append("<section class='ldr2-event-meta'>");
        sb.append("<span class='ldr2-event-time'>").append(event.getTimeString()).append("</span>");
        sb.append("<span class='ldr2-event-date'>").append(event.getLongDateString()).append("</span>");

        sb.append("<span class='ldr2-event-message'>").append(event.getProperty(GenericLogEvent.MESSAGE)).append("</span>");

        sb.append("</section>");
        sb.append("<ul class='ldr2-event-attributes'>");

        propertiesMap.forEach((key, value) -> {

            sb.append("<li>");

            sb.append("<span class='ldr2-attribute-key'>").append(key).append("</span>");
            sb.append("<span class='ldr2-attribute-value'>").append(value).append("</span>");

            sb.append("</li>");
        });

        sb.append("</ul>");
        sb.append("</li>");

        return true;
    }


    public static Map<String, String> parseFilter(String filterString) {

        final Map<String, String> filterMap = new HashMap<>();

        if (filterString == null) return filterMap;

        String[] filterItemStrings = filterString.split("&");
        Arrays.asList(filterItemStrings).stream().forEach(filterItemString -> {

            String[] item = filterItemString.split("=");
            if (item.length == 2 && item[0].length() > 0) {

                filterMap.put(item[0], item[1]);
            }
        });
        return filterMap;
    }
}

class Gate {

    private boolean open = true;

    public Gate() {
    }

    public boolean isOpen() {
        return open;
    }

    public void setOpen(boolean open) {
        this.open = open;
    }
}

