package com.kabasonic.messenger.ui.userchat;

import android.content.Context;
import android.graphics.Bitmap;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.kabasonic.messenger.models.Chat;

import java.util.HashMap;
import java.util.List;

public class UserProfileViewModel extends ViewModel {

    private MutableLiveData<HashMap<String, Object>> mUserValues;
    private MutableLiveData<Bitmap> mUserImage;
    private MutableLiveData<List<Chat>> mMessages;

    private UserProfileRepository mRepo;

    public UserProfileViewModel() {
        mRepo = UserProfileRepository.getInstance();
    }

    public LiveData<List<Chat>> getMessages(String hisUid, String myUid){
        mMessages = mRepo.getUserMessages(hisUid, myUid);
        return mMessages;
    }
    public LiveData<Bitmap> getUserImage(String imageUri, Context context){
        mUserImage = mRepo.getUserImage(imageUri, context);
        return mUserImage;
    }
    public LiveData<HashMap<String, Object>> getUserValues(String uid){
        mUserValues = mRepo.getUserValues(uid);
        return mUserValues;
    }
    public void setUserStatus(boolean status){
        mRepo.setUserStatus(status);
    }
    public void setRemoveListenerToSeen(boolean action){
        mRepo.setRemoveListenerToSeen(action);
    }
    public void setUpdateStatusMessages(String myUid){
        mRepo.updateStatusMessages(myUid);
    }
    public void sentUserMessage(String hisUid, String myUid, String message){
        mRepo.sentActionMessage(hisUid, myUid, message);
    }
}
