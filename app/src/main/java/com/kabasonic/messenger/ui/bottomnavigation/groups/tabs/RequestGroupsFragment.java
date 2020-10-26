package com.kabasonic.messenger.ui.bottomnavigation.groups.tabs;

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

public class RequestGroupsFragment extends Fragment {

    private RequestGroupsViewModel requestGroupsViewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        requestGroupsViewModel =
                ViewModelProviders.of(this).get(RequestGroupsViewModel.class);
        View root = inflater.inflate(R.layout.fragment_request_groups, container, false);
        final TextView textView = root.findViewById(R.id.text);
        requestGroupsViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });
        return root;
    }
}
