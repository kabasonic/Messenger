package com.kabasonic.messenger.ui.bottomnavigation.contacts.tabs;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class RequestContactsViewModel extends ViewModel {
    private MutableLiveData<String> mText;

    public RequestContactsViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("Request contacts fragment");
    }

    public LiveData<String> getText(){
        return mText;
    }

}
