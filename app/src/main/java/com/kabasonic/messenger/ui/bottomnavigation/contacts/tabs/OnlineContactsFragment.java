package com.kabasonic.messenger.ui.bottomnavigation.contacts.tabs;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.kabasonic.messenger.R;

public class OnlineContactsFragment extends Fragment {
    private OnlineContactsViewModel onlineContactsViewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        onlineContactsViewModel =
                ViewModelProviders.of(this).get(OnlineContactsViewModel.class);
        View root = inflater.inflate(R.layout.fragment_online_contacts, container, false);
        final TextView textView = root.findViewById(R.id.text);
        onlineContactsViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });
        return root;
    }
}
