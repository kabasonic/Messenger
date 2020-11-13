package com.kabasonic.messenger.models;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.Map;

@IgnoreExtraProperties
public class User {
    public String uid;
    public String imageUser;
    public String firstName;
    public String lastName;
    public String phoneNumber;
    public String nickName;
    public String status;
    public String bio;

//    public User() {
//        // Default constructor required for calls to DataSnapshot.getValue(User.class)
//    }


    public User() {

    }

    public User(String uid, String imageUser, String firstName, String lastName, String phoneNumber, String nickName, String status, String bio) {
        this.uid = uid;
        this.imageUser = imageUser;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
        this.nickName = nickName;
        this.status = status;
        this.bio = bio;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getImageUser() {
        return imageUser;
    }

    public void setImageUser(String imageUser) {
        this.imageUser = imageUser;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    //    @Exclude
//    public Map<String, Object> toMap(){
//        HashMap<String, Object> result = new HashMap<>();
//        result.put("firstName",firstName);
//        result.put("lastName",firstName);
//        result.put("phoneNumber",phoneNumber);
//        result.put("nickName",nickName);
//        result.put("status",status);
//        result.put("bio",bio);
//        return result;
//    }

}
