package com.kabasonic.messenger.models;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.google.firebase.database.IgnoreExtraProperties;

@Entity (tableName = "user_table")
@IgnoreExtraProperties
public class User {
    @PrimaryKey(autoGenerate = true)
    private int id;
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


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public User() {

    }

    public User(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
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

}
