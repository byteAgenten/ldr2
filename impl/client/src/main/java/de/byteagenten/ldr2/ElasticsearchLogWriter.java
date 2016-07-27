package de.byteagenten.ldr2;

import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.transport.InetSocketTransportAddress;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Created by matthias on 26.07.16.
 */
public class ElasticsearchLogWriter implements LogWriter {

    private Client client;

    @Override
    public void init() {

        try {
            this.client = TransportClient.builder().build()
                    .addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("localhost"), 9300));
        } catch (UnknownHostException e) {
            e.printStackTrace();
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
}
