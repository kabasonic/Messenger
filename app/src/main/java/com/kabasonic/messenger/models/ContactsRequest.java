package com.kabasonic.messenger.models;

public class ContactsRequest {
    private String statusRequest;

    public ContactsRequest(String statusRequest) {
        this.statusRequest = statusRequest;
    }

    public String getStatusRequest() {
        return statusRequest;
    }

    public void setStatusRequest(String statusRequest) {
        this.statusRequest = statusRequest;
    }
}
