package com.kabasonic.messenger.ui.bottomnavigation.groups.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class MyGroupsViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public MyGroupsViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("My groups fragment");
    }

    public LiveData<String> getText(){
        return mText;
    }

}
