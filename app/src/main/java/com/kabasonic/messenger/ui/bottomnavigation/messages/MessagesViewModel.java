package com.kabasonic.messenger.ui.bottomnavigation.messages;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.kabasonic.messenger.models.Chat;
import com.kabasonic.messenger.models.ChatList;
import com.kabasonic.messenger.models.User;

import java.util.HashMap;
import java.util.List;

public class MessagesViewModel extends ViewModel {

    private MutableLiveData<Boolean> checkUserAuth;
    private MutableLiveData<List<Chat>> mDataMessage;
    private MutableLiveData<List<User>> mDataUseChat;

    private MessagesRepository mRepo;

    public MessagesViewModel() {
        mRepo = MessagesRepository.getInstance();
    }

    public boolean getCheckUserAuth(){
        return  mRepo.checkAuthFirebaseUser();
    }


    public LiveData<List<User>> getUserChat(){
        mDataUseChat = mRepo.getUserChat();
        return mDataUseChat;
    }

    public void removeChatForMe(String hisUid){
        mRepo.getFirebaseDeleteChatForMe(hisUid);
    }

    public void removeChatForAll(String hisUid){
        mRepo.getFirebaseDeleteChatForAll(hisUid);
    }

    public void removeAllMessages(String hisUid){
        mRepo.deleteAllMessages(hisUid);
    }

    public LiveData<List<Chat>> getDataMessage(){
        mDataMessage = mRepo.getLastMessage();
        return mDataMessage;
    }



}