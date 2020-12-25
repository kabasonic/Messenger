package com.kabasonic.messenger.ui.bottomnavigation.contacts.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.kabasonic.messenger.models.User;
import com.kabasonic.messenger.repositories.AllContactsRepository;

import java.util.List;

public class AllContactsViewModel extends ViewModel {

    private MutableLiveData<List<User>> mAllUser;
    private MutableLiveData<List<User>> mSearchedUsers;
    private AllContactsRepository mRepo;


    public void init(){
        if(mAllUser != null){
            return;
        }
        mRepo = AllContactsRepository.getInstance();
        mAllUser = mRepo.getAllContacts();
    }


    public LiveData<List<User>> getAllContacts(){
        return mAllUser;
    }

    public void deleteContact (String contactUid){
        mRepo.setUidContact(contactUid);
    }

    public void searchUsers(String query){
        mRepo.setmQuery(query);
    }

    public LiveData<List<User>> getSearchedUsers(){
        mSearchedUsers = mRepo.getSearchedUsers();
        return mSearchedUsers;
    }

}