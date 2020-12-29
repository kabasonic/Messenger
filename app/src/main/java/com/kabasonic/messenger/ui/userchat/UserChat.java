package com.kabasonic.messenger.ui.userchat;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.kabasonic.messenger.R;
import com.kabasonic.messenger.models.Chat;
import com.kabasonic.messenger.ui.adapters.AdapterChat;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserChat extends AppCompatActivity {

    public static final String TAG = "UserChat";

    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private ImageView imageView;
    private TextView userName, userStatus;
    private EditText messageText;
    private Button sendButton;
    private Button addFile;
    private ImageView arrowBack;
    private String hisUid;
    private String myUid;
    private String imageUri;
    private AdapterChat adapterChat;

    private UserChatViewModel mViewModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_activity_chat);

        initViewElements();

        getArguments();

        FirebaseUser userLogin = FirebaseAuth.getInstance().getCurrentUser();
        myUid = userLogin.getUid();

        mViewModel = ViewModelProviders.of(this).get(UserChatViewModel.class);
        if(hisUid != null){
            mViewModel.getUserValues(hisUid).observe(this, new Observer<HashMap<String, Object>>() {
                @Override
                public void onChanged(HashMap<String, Object> hashMap) {
                    if(!hashMap.isEmpty()){
                        setUserValues(hashMap);
                    }
                }
            });
        }

        setListenerArrowBack();

        setListenerAttachFiles();

        if(hisUid != null){
            setListenerSendMessage();
        }

    }

    private void initViewElements(){
        toolbar = findViewById(R.id.chat_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        recyclerView = findViewById(R.id.rvChat);
        imageView = findViewById(R.id.userImage);
        userName = findViewById(R.id.userNameChat);
        userStatus = findViewById(R.id.userStatusChat);
        messageText = findViewById(R.id.messageEdit);
        sendButton = (Button) findViewById(R.id.sendButton);
        addFile = (Button) findViewById(R.id.addFile);
        arrowBack = (ImageView) findViewById(R.id.chat_arrowBack);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(linearLayoutManager);
    }

    private void getArguments(){
        Intent intent = getIntent();
        hisUid = intent.getStringExtra("hisUid");
        this.hisUid = getIntent().getStringExtra("uid");
    }

    private void setUserValues(HashMap<String, Object> hashMap) {
        for(Map.Entry<String, Object> map : hashMap.entrySet()){
            switch(map.getKey()){
                case "firstName":
                    userName.setText(String.valueOf(map.getValue()));
                    break;
                case "lastName":
                    String temp = userName.getText().toString().trim() + String.valueOf(map.getValue());
                    userName.setText(temp);
                    break;
                case "status":
                    userStatus.setText(String.valueOf(map.getValue()));
                    break;
                case "imageUser":
                    imageUri = String.valueOf(map.getValue());
                    mViewModel.getUserImage(imageUri,this).observe(this, new Observer<Bitmap>() {
                        @Override
                        public void onChanged(Bitmap bitmap) {
                            Log.d(TAG,"onChange bitmap");
                            if(bitmap != null){
                                imageView.setImageBitmap(bitmap);
                            }else{
                                Log.d(TAG,"onChange bitmap is null");
                            }
                        }
                    });
                    break;
            }
        }
    }

    private void setListenerArrowBack(){
        arrowBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void setListenerAttachFiles(){
        addFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }

    private void setListenerSendMessage(){
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = messageText.getText().toString();
                if (TextUtils.isEmpty(message)) {
                    Toast.makeText(getApplicationContext(), "Cannot sent the empty message", Toast.LENGTH_SHORT).show();
                } else {
                    mViewModel.sentUserMessage(hisUid,myUid,message);
               }
                messageText.setText("");
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        mViewModel.setUserStatus(true);

        if(hisUid != null){
            mViewModel.getMessages(hisUid,myUid).observe(this, new Observer<List<Chat>>() {
                @Override
                public void onChanged(List<Chat> chats) {
                    if(!chats.isEmpty()){
                        adapterChat = new AdapterChat(UserChat.this, chats);
                        recyclerView.setAdapter(adapterChat);
                        adapterChat.notifyDataSetChanged();
                    }
                }
            });
        }
        mViewModel.setUpdateStatusMessages(myUid);
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG,"onPause");
        mViewModel.setRemoveListenerToSeen(true);
        mViewModel.setUserStatus(false);
    }
}
