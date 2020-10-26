package com.kabasonic.messenger.ui.bottomnavigation.groups.tabs;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class RequestGroupsViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public RequestGroupsViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("Request groups fragment");
    }

    public LiveData<String> getText(){
        return mText;
    }

}
