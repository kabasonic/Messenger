package com.kabasonic.messenger.ui.bottomnavigation.contacts.tabs;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class OnlineContactsViewModel extends ViewModel {
    private MutableLiveData<String> mText;

    public OnlineContactsViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("Online contacts fragment");
    }

    public LiveData<String> getText(){
        return mText;
    }

}
