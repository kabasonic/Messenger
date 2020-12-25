package com.kabasonic.messenger.models;

public class Contacts {

    private String userUid;

    public Contacts(String userUid) {
        this.userUid = userUid;
    }

    public String getUserUid() {
        return userUid;
    }

    public void setUserUid(String userUid) {
        this.userUid = userUid;
    }
}
