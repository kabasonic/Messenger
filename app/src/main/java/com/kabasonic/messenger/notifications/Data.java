package com.kabasonic.messenger.notifications;

public class Data {

    private String user;
    private String body;
    private String title;
    private String sent;

    private Integer image;

    public Data(long l){

    }

    public Data(String user, String body, String title, String sent, Integer image) {
        this.user = user;
        this.body = body;
        this.title = title;
        this.sent = sent;
        this.image = image;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSent() {
        return sent;
    }

    public void setSent(String sent) {
        this.sent = sent;
    }

    public Integer getImage() {
        return image;
    }

    public void setImage(Integer image) {
        this.image = image;
    }
}
