package de.byteagenten.ldr2.writer;

import com.google.gson.JsonObject;
import de.byteagenten.ldr2.GenericLogEvent;
import java.io.DataOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;

public class HttpLogWriter implements LogWriter {

    private String name;

    private static final String CONFIG_PARAM_URL = "url";

    private String elasticUrl;

    @Override
    public void write(GenericLogEvent logEvent) {

        String payload = logEvent.getJsonString();
        try {
            URL url = new URL(String.format("%s/%s/%s/",
                    this.elasticUrl,
                    logEvent.getApplicationId().toLowerCase(),
                    logEvent.getEventName().toLowerCase()));

            HttpURLConnection connection = (HttpURLConnection)url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Content-Length", Integer.toString(payload.getBytes().length));
            connection.setDoOutput(true);

            DataOutputStream dos = new DataOutputStream(connection.getOutputStream());
            dos.write(payload.getBytes(Charset.forName("utf-8")));
            dos.flush();
            dos.close();

            int responseCode = connection.getResponseCode();
            System.out.println("responseCode = " + responseCode);

        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    @Override
    public void dispose() {

    }

    @Override
    public void init(String name, JsonObject configJson) throws WriterException {

        this.name = name.toLowerCase();

        if( !configJson.get(CONFIG_PARAM_URL).isJsonPrimitive() || !configJson.get(CONFIG_PARAM_URL).getAsJsonPrimitive().isString()) {
            throw new WriterException("Specified url is not a string");
        }
        this.elasticUrl = configJson.get(CONFIG_PARAM_URL).getAsString();
        if( this.elasticUrl.charAt(this.elasticUrl.length()-1) == '/') this.elasticUrl = this.elasticUrl.substring(0, this.elasticUrl.length()-1);
    }

    @Override
    public String getName() {
        return this.name;
    }
}
