package com.kabasonic.messenger.ui.bottomnavigation.profile.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.kabasonic.messenger.repositories.ProfileRepository;

import java.util.Map;

public class ProfileViewModel extends ViewModel {

    private MutableLiveData<Map<String, Object>> mMyProfile;
    private MutableLiveData<String> deleteImage;
    private ProfileRepository mRepo;


    public void init(){
        if(mMyProfile != null){
            return;
        }
        mRepo = ProfileRepository.getInstance();
        mMyProfile = mRepo.getMyProfile();

    }

    public LiveData<String> getDeleteImage(){
        deleteImage = mRepo.getDeleteImage();
        return deleteImage;
    }

    public LiveData<Map<String, Object>> getMyProfile(){
        return mMyProfile;
    }

}