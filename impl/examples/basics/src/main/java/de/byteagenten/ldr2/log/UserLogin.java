package de.byteagenten.ldr2.log;


import de.byteagenten.ldr2.LogEvent;
import de.byteagenten.ldr2.LogObject;

/**
 * Created by knooma2e on 26.07.2016.
 */
@LogEvent(
        name = "user_login",
        level = LogEvent.Level.DEBUG,
        message = "User {givenName} {sureName} (id: {id}) logged in"
)
public class UserLogin extends LogObject {

    private long id;

    private String givenName;

    private String sureName;

    private Long timestamp;

    private UserLogin(long id, String givenName, String sureName) {
        this.id = id;
        this.givenName = givenName;
        this.sureName = sureName;
        this.timestamp = System.currentTimeMillis();
    }

    public static UserLogin create(long id, String givenName, String sureName) {

        return new UserLogin(id, givenName, sureName);
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getGivenName() {
        return givenName;
    }

    public void setGivenName(String givenName) {
        this.givenName = givenName;
    }

    public String getSureName() {
        return sureName;
    }

    public void setSureName(String sureName) {
        this.sureName = sureName;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }
}
