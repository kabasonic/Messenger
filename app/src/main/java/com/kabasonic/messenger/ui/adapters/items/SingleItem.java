package com.kabasonic.messenger.ui.adapters.items;

public class SingleItem {
    private int mImageUser;
    private int mStatusUser;
    private String mUsername;
    private int mButtonMore;

    public SingleItem(int mImageUser, int mStatusUser, String mUsername) {
        this.mImageUser = mImageUser;
        this.mStatusUser = mStatusUser;
        this.mUsername = mUsername;
    }

    public int getmImageUser() {
        return mImageUser;
    }

    public void setmImageUser(int mImageUser) {
        this.mImageUser = mImageUser;
    }

    public int getmStatusUser() {
        return mStatusUser;
    }

    public void setmStatusUser(int mStatusUser) {
        this.mStatusUser = mStatusUser;
    }

    public String getmUsername() {
        return mUsername;
    }

    public void setmUsername(String mUsername) {
        this.mUsername = mUsername;
    }

    public int getmButtonMore() {
        return mButtonMore;
    }

    public void setmButtonMore(int mButtonMore) {
        this.mButtonMore = mButtonMore;
    }
}
