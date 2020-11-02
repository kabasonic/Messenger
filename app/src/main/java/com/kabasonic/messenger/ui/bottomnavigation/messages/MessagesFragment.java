package com.kabasonic.messenger.ui.bottomnavigation.messages;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.kabasonic.messenger.R;

public class MessagesFragment extends Fragment {
    public static final String TAG = "MessagesFragment";
    private MessagesViewModel messagesViewModel;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        messagesViewModel =
                ViewModelProviders.of(this).get(MessagesViewModel.class);
        View root = inflater.inflate(R.layout.messages_fragment, container, false);
        final TextView textView = root.findViewById(R.id.text);
        messagesViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });

        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            // User is signed in
            Toast.makeText(getActivity(),"User is signed in",Toast.LENGTH_SHORT).show();
        } else {
            // No user is signed in
            Toast.makeText(getActivity(),"No user is signed in",Toast.LENGTH_SHORT).show();
            NavDirections action = MessagesFragmentDirections.actionMessagesFragmentToOTPNumberFragment();
            Navigation.findNavController(view).navigate(action);
        }
    }

    //Create app top bar menu
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.main_menu,menu);
        menu.findItem(R.id.menu_qr_code_scan).setVisible(false);
        menu.findItem(R.id.menu_logout).setVisible(false);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int idMenuItem = item.getItemId();
        switch (idMenuItem){
            case R.id.menu_search:
                Log.i(TAG,"Click search button");
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}