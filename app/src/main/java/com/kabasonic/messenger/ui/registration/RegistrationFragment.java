package com.kabasonic.messenger.ui.registration;

import androidx.lifecycle.ViewModelProviders;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.kabasonic.messenger.R;
import com.kabasonic.messenger.ui.authorization.otpcode.OTPCodeFragment;

public class RegistrationFragment extends Fragment {

    public static final String TAG = "RegistrationFragment";

    EditText firstName, lastName;
    FloatingActionButton submitRegistration;

    private String mFirstName = null;
    private String mLastName = null;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.registration_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        firstName = (EditText) view.findViewById(R.id.firstName);
        lastName = (EditText) view.findViewById(R.id.lastName);
        submitRegistration = (FloatingActionButton) view.findViewById(R.id.submitRegistration);

        formValidation();

        submitRegistration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    NavDirections action = RegistrationFragmentDirections.actionRegistrationFragmentToMessagesFragment();
                    Navigation.findNavController(getView()).navigate(action);
            }
        });

    }
    private void formValidation(){
        firstName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                mFirstName = firstName.getText().toString();
                if(mFirstName.isEmpty()){
                    submitRegistration.hide();
                } else{
                    submitRegistration.show();
                }
            }
        });

    }
}