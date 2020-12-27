package com.kabasonic.messenger.ui.userprofile;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.kabasonic.messenger.repositories.ContactProfileRepository;

import java.util.Map;

public class UserProfileViewModel extends ViewModel {

    private MutableLiveData<Map<String, Object>> mUserProfile;
    private MutableLiveData<Boolean> mAddContact;
    private MutableLiveData<Boolean> mCheckContact;
    private ContactProfileRepository mRepo;


    public void init(){
        if(mUserProfile != null){
            return;
        }
        mRepo = ContactProfileRepository.getInstance();

    }

    public LiveData<Boolean> getCheckFriend(String uid){
        mCheckContact = mRepo.getCheckToFriend(uid);
        return mCheckContact;
    }

    public LiveData<Boolean> getAddUser(String uid){
        mAddContact = mRepo.getAddContact(uid);
        return mAddContact;
    }

    public LiveData<Map<String, Object>> getUserProfile(String uid){
        mUserProfile = mRepo.getProfileUser(uid);
        return mUserProfile;
    }


}
