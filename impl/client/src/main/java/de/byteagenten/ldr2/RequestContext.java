package de.byteagenten.ldr2;

import java.util.Date;

/**
 * Created by knooma2e on 22.07.2016.
 */
public class RequestContext {

    private Date startTimestamp = new Date();

    private int index;

    private String url;

    public RequestContext(int index, String url) {
        this.index = index;
        this.url = url;
    }

    public int getIndex() {
        return index;
    }

    public String getUrl() {
        return url;
    }

    public Date getStartTimestamp() {
        return startTimestamp;
    }
}
