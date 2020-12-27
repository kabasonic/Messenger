package com.kabasonic.messenger.ui.bottomnavigation.profile.editfragments.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.kabasonic.messenger.repositories.ProfileRepository;

import java.util.Map;

public class EditBioViewModels extends ViewModel {

    private MutableLiveData<String> statusUpdate;
    private ProfileRepository mRepo;

    public EditBioViewModels() {
        mRepo = ProfileRepository.getInstance();
    }

    public LiveData<String> getStatusUpdate(Map<String, Object> newValues){
        statusUpdate = mRepo.getUpdateUser(newValues);
        return statusUpdate;
    }

}
