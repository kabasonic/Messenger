package com.kabasonic.messenger.ui.bottomnavigation.groups.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class AllGroupsViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public AllGroupsViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("All groups fragment");
    }

    public LiveData<String> getText(){
        return mText;
    }

}
