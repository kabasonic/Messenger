package com.kabasonic.messenger.ui.bottomnavigation.profile.adapter;

public class ModelItemProfile  {
    private String title;
    private String subtitle;
    private int image;

    public ModelItemProfile(){
    }

    public ModelItemProfile(String title, int image){
        this.title = title;
        this.image = image;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    public ModelItemProfile(String title, String subtitle, int image){
        this.title = title;
        this.subtitle = subtitle;
        this.image = image;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }



}
