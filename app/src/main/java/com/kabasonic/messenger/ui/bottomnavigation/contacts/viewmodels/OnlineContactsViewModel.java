package com.kabasonic.messenger.ui.bottomnavigation.contacts.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.kabasonic.messenger.models.User;
import com.kabasonic.messenger.repositories.ContactsOnlineRepository;

import java.util.List;

public class OnlineContactsViewModel extends ViewModel {

    private MutableLiveData <List<User>> mAllUser;
    private MutableLiveData<List<User>> mSearchedUsers;

    private ContactsOnlineRepository mRepo;

    public void init(){
        if(mAllUser !=null){
            return;
        }
        mRepo = ContactsOnlineRepository.getInstance();
        mAllUser = mRepo.getOnlineContacts();
    }

    public LiveData<List<User>> getOnlineContacts(){
        return mAllUser;
    }
    public void deleteContact (String contactUid){
        mRepo.setUidContact(contactUid);
    }


}
