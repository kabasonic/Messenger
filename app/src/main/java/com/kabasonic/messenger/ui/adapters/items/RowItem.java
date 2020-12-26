package com.kabasonic.messenger.ui.adapters.items;

import com.kabasonic.messenger.models.User;

public class RowItem {

    private int mIcon;
    private boolean mOnlineStatus;
    private boolean mMuteStatus;
    private String mTitle;
    private String mDesc;
    private String mStatusMessage;
    private String mTime;
    private int mCountMessage;

    public RowItem(int mIcon, String mTitle) {
        this.mIcon = mIcon;
        this.mTitle = mTitle;
    }

    public RowItem(int mIcon, boolean mOnlineStatus, String mTitle) {
        this.mIcon = mIcon;
        this.mOnlineStatus = mOnlineStatus;
        this.mTitle = mTitle;
    }

    public RowItem(int mIcon, String mTitle, String mDesc) {
        this.mIcon = mIcon;
        this.mTitle = mTitle;
        this.mDesc = mDesc;
    }

    public RowItem(int mIcon, boolean mOnlineStatus, boolean mMuteStatus, String mTitle, String mDesc, String mStatusMessage, String mTime, int mCountMessage) {
        this.mIcon = mIcon;
        this.mOnlineStatus = mOnlineStatus;
        this.mMuteStatus = mMuteStatus;
        this.mTitle = mTitle;
        this.mDesc = mDesc;
        this.mStatusMessage = mStatusMessage;
        this.mTime = mTime;
        this.mCountMessage = mCountMessage;
    }



    public int getmIcon() {
        return mIcon;
    }

    public boolean ismOnlineStatus() {
        return mOnlineStatus;
    }

    public boolean ismMuteStatus() {
        return mMuteStatus;
    }

    public String getmTitle() {
        return mTitle;
    }

    public String getmDesc() {
        return mDesc;
    }

    public String getmStatusMessage() {
        return mStatusMessage;
    }

    public String getmTime() {
        return mTime;
    }

    public int getmCountMessage() {
        return mCountMessage;
    }
}
