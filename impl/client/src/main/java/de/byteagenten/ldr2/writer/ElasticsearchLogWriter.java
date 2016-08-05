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
    public void init(String name, JsonObject writerConfigJsonObject) throws WriterException {

        this.name = name;
        /*
        String hostProperty = properties.getProperty(HOST);
        if( hostProperty == null ) throw new WriterException(String.format("No host specified. Please add property '%s' to the writer configuration", HOST));

        String portProperty = properties.getProperty(PORT);
        if( portProperty == null ) throw new WriterException(String.format("No port specified. Please add property '%s' to the writer configuration", PORT));

        int port = 0;
        try {
            port = Integer.parseInt(portProperty);
        } catch (NumberFormatException e) {
            throw new WriterException(String.format("Specified port is not valid [port: %s]", portProperty));
        }

        try {
            this.client = TransportClient.builder().build()
                    .addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName(hostProperty), port));
        } catch (UnknownHostException e) {
            throw new WriterException("Connecting elasticsearch host failed.", e);
        }
        */
    }



    @Override
    public void dispose() {

        this.client.close();
    }


    @Override
    public void write(GenericLogEvent logEvent) {

//        IndexResponse response = client.prepareIndex(logEvent.getApplicationId().toLowerCase(), logEvent.getEventName().toLowerCase())
//                .setSource(logEvent.getJsonString())
//                .get();
    }

    @Override
    public String getName() {
        return this.name;
    }
}
