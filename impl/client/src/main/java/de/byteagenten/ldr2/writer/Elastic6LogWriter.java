package de.byteagenten.ldr2.writer;

import com.google.gson.JsonObject;
import de.byteagenten.ldr2.GenericLogEvent;

import java.io.DataOutputStream;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

/**
 * Created by knooma2e on 05.04.2018.
 */
public class Elastic6LogWriter implements LogWriter {

    private static final String CONFIG_PARAM_URL = "url";
    private static final String CONFIG_PARAM_QUEUE_SIZE = "queue-size";
    private static final String CONFIG_PARAM_BULK_SIZE = "bulk-size";
    private static final String CONFIG_PARAM_CONNECTION_TIMEOUT = "connection-timeout";
    private static final String CONFIG_PARAM_READ_TIMEOUT = "read-timeout";

    private static final int SLEEP_AFTER_FAILED_SEND = 5;

    private static final int MIN_QUEUE_SIZE = 100;
    private static final int MAX_QUEUE_SIZE = 100000;
    private static final int DEFAULT_QUEUE_SIZE = 1000;

    private static final int MAX_BULK_SIZE = 200;
    private static final int DEFAULT_BULK_SIZE = 50;

    private static final int DEFAULT_CONNECTION_TIMEOUT = 10; //10s
    private static final int DEFAULT_READ_TIMEOUT = 30; //10s

    private String name;
    private String elasticUrl;
    private int queueSize = DEFAULT_QUEUE_SIZE;
    private int bulkSize = DEFAULT_BULK_SIZE;
    private int connectionTimeout = DEFAULT_CONNECTION_TIMEOUT;
    private int readTimeout = DEFAULT_READ_TIMEOUT;

    private BlockingQueue<GenericLogEvent> queue;

    private ExecutorService reader = Executors.newSingleThreadExecutor();

    private boolean running = false;

    private String indexOperation;


    @Override
    public void write(GenericLogEvent logEvent) {

        boolean written = this.queue.offer(logEvent);
        if (!written) {
            System.out.println("Dismiss log event due to full queue.");
        }
    }

    private void send(List<GenericLogEvent> logEvents) throws SendException {

        try {
            StringBuilder sb = new StringBuilder();
            for (GenericLogEvent logEvent : logEvents) {
                sb.append(indexOperation).append("\n");
                sb.append(logEvent.getJsonString());
                sb.append("\n");
            }

            String payload = sb.toString();

            URL url = new URL(String.format("%s/_bulk",
                    this.elasticUrl));

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/x-ndjson");
            connection.setRequestProperty("Content-Length", Integer.toString(payload.getBytes().length));
            connection.setConnectTimeout(this.connectionTimeout * 1000);
            connection.setReadTimeout(this.readTimeout * 1000);
            connection.setDoOutput(true);

            DataOutputStream dos = new DataOutputStream(connection.getOutputStream());
            dos.write(payload.getBytes(Charset.forName("utf-8")));
            dos.flush();
            dos.close();

            int responseCode = connection.getResponseCode();
            String msg = connection.getResponseMessage();

            System.out.println(String.format("Elastic code: %s, msg: %s", responseCode, msg));

        } catch (Throwable e) {
            throw new SendException(String.format("Sending %s events to elastic failed.", logEvents.size()), e);
        }
    }

    @Override
    public void dispose() {

        this.running = false;
        this.reader.shutdownNow();
    }

    private static int readConfigInt(JsonObject configJson, String configParam, Integer minValue, Integer maxValue, int defaultValue) {

        int value = defaultValue;

        if (configJson.has(configParam)
                && configJson.get(configParam).isJsonPrimitive()
                && configJson.get(configParam).getAsJsonPrimitive().isNumber()) {

            value = configJson.get(configParam).getAsInt();
            if (minValue != null && value < minValue) value = minValue;
            if (maxValue != null && value > maxValue) value = maxValue;
        }
        return value;
    }

    @Override
    public void init(String applicationId, String name, JsonObject configJson) throws WriterException {

        this.name = name.toLowerCase().trim();

        this.indexOperation = String.format("{ \"index\" : { \"_index\" : \"%s\", \"_type\" : \"_doc\"} }", applicationId.toLowerCase().trim());

        this.elasticUrl = readConfigUrl(configJson, CONFIG_PARAM_URL);
        this.queueSize = readConfigInt(configJson, CONFIG_PARAM_QUEUE_SIZE, MIN_QUEUE_SIZE, MAX_QUEUE_SIZE, DEFAULT_QUEUE_SIZE);
        this.bulkSize = readConfigInt(configJson, CONFIG_PARAM_BULK_SIZE, 1, MAX_BULK_SIZE, DEFAULT_BULK_SIZE);
        this.connectionTimeout = readConfigInt(configJson, CONFIG_PARAM_CONNECTION_TIMEOUT, 0, null, DEFAULT_CONNECTION_TIMEOUT);
        this.readTimeout = readConfigInt(configJson, CONFIG_PARAM_READ_TIMEOUT, 0, null, DEFAULT_READ_TIMEOUT);

        queue = new ArrayBlockingQueue<>(this.queueSize);
        this.running = true;
        List<GenericLogEvent> sendEvents = new ArrayList<>();

        reader.submit(() -> {

            try {
                while (this.running) {

                    sendEvents.clear();
                    GenericLogEvent take = this.queue.take(); //Use take() to force blocking, if empty. drainTo() does not block!
                    this.queue.drainTo(sendEvents, this.bulkSize - 1); //read some more
                    sendEvents.add(take);

                    try {
                        this.send(sendEvents);
                    } catch (SendException e) {

                        e.printStackTrace();

                        //Wait a wile and write events back to queue.
                        //Waiting is required, because otherwise the reader would immediately read the events again and
                        //would try to send it again.
                        Thread.sleep(SLEEP_AFTER_FAILED_SEND * 1000);
                        for (GenericLogEvent failedEvent : sendEvents) {
                            this.write(failedEvent);
                        }
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
    }

    private static String readConfigUrl(JsonObject configJson, String configParam) throws WriterException {

        if (!configJson.get(configParam).isJsonPrimitive() || !configJson.get(configParam).getAsJsonPrimitive().isString()) {
            throw new WriterException("Specified url is not a string");
        }

        String url = configJson.get(configParam).getAsString();
        if (url.charAt(url.length() - 1) == '/')
            url = url.substring(0, url.length() - 1);

        return url;
    }

    @Override
    public String getName() {
        return this.name;
    }

    class SendException extends WriterException {

        public SendException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}


