package com.kabasonic.messenger.ui.bottomnavigation.messages;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.kabasonic.messenger.MainActivity;
import com.kabasonic.messenger.R;
import com.kabasonic.messenger.models.Chat;
import com.kabasonic.messenger.models.User;
import com.kabasonic.messenger.ui.adapters.AdapterMessageItem;
import com.kabasonic.messenger.ui.userchat.UserChat;

import java.util.List;

public class MessagesFragment extends Fragment {
    public static final String TAG = "MessagesFragment";

    private RecyclerView recyclerView;
    private MessagesViewModel mViewModel;
    private AdapterMessageItem mAdapter;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof MainActivity) {
            MainActivity mActivity = (MainActivity) context;
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.messages_fragment, container, false);
        return root;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.rvMessages);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);
        mAdapter = new AdapterMessageItem(getContext());
        recyclerView.setAdapter(mAdapter);

        mViewModel = ViewModelProviders.of(this).get(MessagesViewModel.class);


        if (mViewModel.getCheckUserAuth()) {
            NavDirections action = MessagesFragmentDirections.actionMessagesFragmentToOTPNumberFragment();
            Navigation.findNavController(view).navigate(action);
        }


    }


    @Override
    public void onResume() {
        super.onResume();
        mViewModel.getUserChat().observe(getViewLifecycleOwner(), new Observer<List<User>>() {
            @Override
            public void onChanged(List<User> users) {
                Log.d(TAG, "onChanged");
                mAdapter.setUserArrayList(users);
                mAdapter.notifyDataSetChanged();
            }
        });

        mViewModel.getDataMessage().observe(getViewLifecycleOwner(), new Observer<List<Chat>>() {
            @Override
            public void onChanged(List<Chat> list) {
                for (int i = 0; i < list.size(); i++) {
                    mAdapter.setReceiverUID(list.get(i).getReceiver());
                    mAdapter.setMyUid(list.get(i).getMyUid());
                    mAdapter.setCountMessage(list.get(i).getUid(), String.valueOf(list.get(i).getCountMessage()));
                    mAdapter.setStatusMessageMap(list.get(i).getUid(), list.get(i).getSeen());
                    mAdapter.setTimeStampMap(list.get(i).getUid(), list.get(i).getTimestamp());
                    mAdapter.setLastMessageMap(list.get(i).getUid(), list.get(i).getMessage());
                    mAdapter.notifyDataSetChanged();
                }
            }
        });

        mAdapter.setOnItemClickListener(new AdapterMessageItem.ItemRow() {
            @Override
            public void onItemClick(String hisUid, int position) {
                Intent intent = new Intent(getContext(), UserChat.class);
                intent.putExtra("uid", hisUid);
                startActivity(intent);
            }

            @Override
            public void onItemLongClick(String hisUid, int position) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setItems(R.array.chatList, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (i == 0) {
                            createAlertDialog(hisUid);
                        }
                    }
                });
                builder.show();
            }
        });

    }

    private void createAlertDialog(String hisUid) {
        View alertDialogView = getLayoutInflater().inflate(R.layout.dialog_message, null);
        Button submit = alertDialogView.findViewById(R.id.dialog_buttonOkay);
        Button cancel = alertDialogView.findViewById(R.id.dialog_buttonCancel);
        CheckBox checkBox = alertDialogView.findViewById(R.id.checkBox1);
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setView(alertDialogView);
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkBox.isChecked()) {
                    Log.d(TAG, "Remove for all");
                    //delete all messegas and chats
                    mViewModel.removeChatForAll(hisUid);
                    mViewModel.removeAllMessages(hisUid);
                } else {
                    Log.d(TAG, "Remove for me");
                    mViewModel.removeChatForMe(hisUid);
                }
                alertDialog.dismiss();
            }
        });
    }

    //Create app top bar menu
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.main_menu, menu);
        menu.findItem(R.id.menu_add_to_contacts).setVisible(false);
        menu.findItem(R.id.menu_settings).setVisible(false);
        menu.findItem(R.id.menu_logout).setVisible(false);
        menu.findItem(R.id.menu_create_group).setVisible(false);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int idMenuItem = item.getItemId();
        switch (idMenuItem) {
            case R.id.menu_search:
                Log.i(TAG, "Click search button");
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
