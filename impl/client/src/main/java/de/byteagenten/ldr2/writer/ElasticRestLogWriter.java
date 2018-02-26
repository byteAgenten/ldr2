package de.byteagenten.ldr2.writer;

import com.google.gson.JsonObject;
import de.byteagenten.ldr2.GenericLogEvent;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.entity.ContentType;
import org.apache.http.nio.entity.NStringEntity;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.ResponseListener;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.transport.InetSocketTransportAddress;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Collections;
import java.util.Map;

/**
 * Created by knooma2e on 26.02.2018.
 */
public class ElasticRestLogWriter implements LogWriter {

    private String name;

    private RestClient client;

    @Override
    public void write(GenericLogEvent logEvent) {

        HttpEntity entity = new NStringEntity(logEvent.getJsonString(), ContentType.APPLICATION_JSON);


        client.performRequestAsync(
                "POST",
                String.format("%s/%s/", logEvent.getApplicationId().toLowerCase(), logEvent.getEventName().toLowerCase()),
                Collections.emptyMap(),
                entity,
                new ResponseListener() {
                    @Override
                    public void onSuccess(Response response) {
                        int i = 0;
                    }

                    @Override
                    public void onFailure(Exception e) {
                        e.printStackTrace();
                    }
                }
        );
    }

    @Override
    public void dispose() {

        try {
            this.client.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

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

        RestClientBuilder builder = RestClient.builder(new HttpHost(host, port, "http"));
        builder.setFailureListener(new RestClient.FailureListener() {
            @Override
            public void onFailure(HttpHost host) {

            }
        });
        this.client = builder.build();
    }

    @Override
    public String getName() {
        return this.name;
    }
}
