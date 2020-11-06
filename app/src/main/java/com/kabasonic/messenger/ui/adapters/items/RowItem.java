package com.kabasonic.messenger.ui.adapters.items;

public class RowItem {

    private int mRowImage;
    private int mRowStatus;
    private String mRowTitle;
    private String mRowDesc;

    public RowItem(int mRowImage, int mRowStatus, String mRowTitle) {
        this.mRowImage = mRowImage;
        this.mRowStatus = mRowStatus;
        this.mRowTitle = mRowTitle;
    }

    public RowItem(int mRowImage, String mRowTitle, String mRowDesc) {
        this.mRowImage = mRowImage;
        this.mRowTitle = mRowTitle;
        this.mRowDesc = mRowDesc;
    }


    public String getmRowDesc() {
        return mRowDesc;
    }

    public void setmRowDesc(String mRowDesc) {
        this.mRowDesc = mRowDesc;
    }

    public int getmRowImage() {
        return mRowImage;
    }

    public void setmRowImage(int mRowImage) {
        this.mRowImage = mRowImage;
    }

    public int getmRowStatus() {
        return mRowStatus;
    }

    public void setmRowStatus(int mRowStatus) {
        this.mRowStatus = mRowStatus;
    }

    public String getmRowTitle() {
        return mRowTitle;
    }

    public void setmRowTitle(String mRowTitle) {
        this.mRowTitle = mRowTitle;
    }

}
