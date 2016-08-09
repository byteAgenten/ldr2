package de.byteagenten.ldr2.writer;

import com.google.gson.JsonObject;
import de.byteagenten.ldr2.GenericLogEvent;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.transport.InetSocketTransportAddress;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Properties;

/**
 * Created by matthias on 26.07.16.
 */
public class ElasticsearchLogWriter implements LogWriter {

    private Client client;

    private String name;

    public static final String HOST = "ldr2.elasticsearch.host";
    public static final String PORT = "ldr2.elasticsearch.port";

    @Override
    public void init(String name, JsonObject configJson) throws WriterException {

        this.name = name;

        String host = "localhost";
        Integer port = 9300;

        if (configJson.has("host")) {

            if (!configJson.get("host").isJsonPrimitive() || !configJson.get("host").getAsJsonPrimitive().isString())
                throw new WriterException("Specified host is not a string");

            host = configJson.get("host").getAsString();
        }

        if (configJson.has("port")) {

            if (!configJson.get("port").isJsonPrimitive() || !configJson.get("port").getAsJsonPrimitive().isNumber())
                throw new WriterException("Specified port is not a number");
            port = (int) Math.round(configJson.get("port").getAsNumber().doubleValue());
        }

        try {
            this.client = TransportClient.builder().build()
                    .addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName(host), port));
        } catch (UnknownHostException e) {
            throw new WriterException("Connecting elasticsearch host failed.", e);
        }
    }

    @Override
    public void dispose() {

        this.client.close();
    }

    @Override
    public void write(GenericLogEvent logEvent) {

        IndexResponse response = client.prepareIndex(logEvent.getApplicationId().toLowerCase(), logEvent.getEventName().toLowerCase())
                .setSource(logEvent.getJsonString())
                .get();
    }

    @Override
    public String getName() {
        return this.name;
    }
}
