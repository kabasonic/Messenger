package com.kabasonic.messenger.ui.authorization.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.kabasonic.messenger.repositories.AuthorizationRepository;

public class RegistrationViewModel extends ViewModel {

    private MutableLiveData<Boolean> action;
    private AuthorizationRepository mRepo;

    public void init(){
        if(action != null){
            return;
        }
        mRepo = AuthorizationRepository.getInstance();
    }

    public LiveData<Boolean> createUser(String firstName, String lastName){
        action = mRepo.getCreateUser(firstName, lastName);
        return action;
    }

}
