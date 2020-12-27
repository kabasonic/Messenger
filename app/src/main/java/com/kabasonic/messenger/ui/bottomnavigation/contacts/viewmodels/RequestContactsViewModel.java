package com.kabasonic.messenger.ui.bottomnavigation.contacts.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.kabasonic.messenger.models.User;
import com.kabasonic.messenger.repositories.contacts.ContactsRequestRepository;

import java.util.List;

public class RequestContactsViewModel extends ViewModel {
    private MutableLiveData<List<User>> mRequests;
    private MutableLiveData<Integer> mCountRequest;

    private ContactsRequestRepository mRepo;

    public void init() {
        if (mRequests != null) {
            return;
        }
        mRepo = ContactsRequestRepository.getInstance();
        mRequests = mRepo.getRequests();
        mCountRequest = mRepo.getCountRequest();
    }

    public LiveData<List<User>> getRequests() {
        return mRequests;
    }

    public void actionRequest(String uid, boolean action) {
        mRepo.actionRequest(uid, action);
    }

    public LiveData<Integer> getCountRequests() {
        return mCountRequest;
    }
}
