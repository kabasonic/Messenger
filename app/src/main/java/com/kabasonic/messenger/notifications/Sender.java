package com.kabasonic.messenger.notifications;

public class Sender {
    private Data data;
    private String sender;

    public Sender(){

    }

    public Sender(Data data, String sender) {
        this.data = data;
        this.sender = sender;
    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }
}
