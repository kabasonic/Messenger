package com.kabasonic.messenger.ui.adapters.items;

public class RowItem {

    private int mImageUser;
    private int mStatusUser;
    private String mUsername;
    private String mUserBio;

    public RowItem(int mImageUser, int mStatusUser, String mUsername) {
        this.mImageUser = mImageUser;
        this.mStatusUser = mStatusUser;
        this.mUsername = mUsername;
    }

    public RowItem(int mImageUser, String mUsername, String mUserBio) {
        this.mImageUser = mImageUser;
        this.mUsername = mUsername;
        this.mUserBio = mUserBio;
    }


    public String getmUserBio() {
        return mUserBio;
    }

    public void setmUserBio(String mUserBio) {
        this.mUserBio = mUserBio;
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

}
