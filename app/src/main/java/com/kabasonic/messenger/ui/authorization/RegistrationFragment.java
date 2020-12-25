package com.kabasonic.messenger.ui.authorization;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kabasonic.messenger.MainActivity;
import com.kabasonic.messenger.R;
import com.kabasonic.messenger.models.User;

public class RegistrationFragment extends Fragment {

    public static final String TAG = "RegistrationFragment";

    private EditText firstName, lastName;
    private FloatingActionButton submitRegistration;

    private DatabaseReference mDatabase;
    MainActivity mActivity;
    private View mView;
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof MainActivity) {
            mActivity = (MainActivity) context;
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.registration_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.mView = view;
        firstName = (EditText) view.findViewById(R.id.firstName);
        lastName = (EditText) view.findViewById(R.id.lastName);
        submitRegistration = (FloatingActionButton) view.findViewById(R.id.submitRegistration);
        formValidation();
        submitRegistration.setOnClickListener(v -> {
                actionKeyboard(false);
                createUser();

        });
    }

    private void createUser(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String phoneNumber = FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber();
        String userId = user.getUid();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        mDatabase.child("users").child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                User user = new User(userId,"",firstName.getText().toString(),lastName.getText().toString(),phoneNumber,"","","");
                mDatabase.child("users").child(userId).setValue(user);
                navFragments();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

    }
    private void navFragments() {
//        NavDirections action = RegistrationFragmentDirections.actionRegistrationFragmentToMessagesFragment();
//        Navigation.findNavController(this.mView).navigate(action);
        Navigation.findNavController(getView()).navigate(R.id.messagesFragment);
    }


    private void formValidation(){
        firstName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                if((firstName.getText().toString()).isEmpty()){
                    submitRegistration.hide();
                } else{
                    submitRegistration.show();
                }
            }
        });

    }

    private void actionKeyboard(boolean action) {
        View view = getActivity().getCurrentFocus();
        if (view != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            if(action){
                inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
            }else{
                inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }

        }
    }
}