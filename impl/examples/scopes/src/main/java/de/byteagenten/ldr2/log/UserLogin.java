package de.byteagenten.ldr2.log;

/**
 * Created by knooma2e on 26.07.2016.
 */
public class UserLogin {

    private long id;

    private String givenName;

    private String sureName;

    public UserLogin(long id, String givenName, String sureName) {
        this.id = id;
        this.givenName = givenName;
        this.sureName = sureName;
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
}
