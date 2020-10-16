package com.kabasonic.messenger.ui.bottomnavigation.messages;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class MessagesViewModel extends ViewModel {
    private MutableLiveData<String> mText;

    public MessagesViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("Messages");
    }

    public LiveData<String> getText() {
        return mText;
    }
}