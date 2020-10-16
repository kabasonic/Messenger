package com.kabasonic.messenger.ui.bottomnavigation.groups;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class GroupsViewModel extends ViewModel {
    private MutableLiveData<String> mText;

    public GroupsViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("Groups");
    }

    public LiveData<String> getText() {
        return mText;
    }
}