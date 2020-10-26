package com.kabasonic.messenger.ui.bottomnavigation.contacts.tabs;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class AllContactsViewModel extends ViewModel {
    private MutableLiveData<String> mText;

    public AllContactsViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("All contacts fragment");
    }

    public LiveData<String> getText(){
        return mText;
    }

}
