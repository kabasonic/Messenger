package com.kabasonic.messenger.ui.authorization.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.kabasonic.messenger.repositories.AuthorizationRepository;

public class OTPCodeViewModel extends ViewModel {

    private MutableLiveData<String> navigation;
    private AuthorizationRepository mRepo;


    public void init(){
        if(navigation != null){
            return;
        }
        mRepo = AuthorizationRepository.getInstance();

    }

    public LiveData<String> getNavigation(){
        navigation = mRepo.getCheckedUser();
        return navigation;
    }

}
