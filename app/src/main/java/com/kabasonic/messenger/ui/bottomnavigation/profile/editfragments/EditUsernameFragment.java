package com.kabasonic.messenger.ui.bottomnavigation.profile.editfragments;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DatabaseReference;
import com.kabasonic.messenger.R;
import com.kabasonic.messenger.database.Database;

import java.util.HashMap;
import java.util.Map;

public class EditUsernameFragment extends Fragment {

    private DatabaseReference mDatabase;

    private String firstName, lastName;
    private EditText mFirstName, mLastName;
    private FloatingActionButton mSubmit;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.registration_fragment,container,false);
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
        validForm();
        mSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
                firstName = mFirstName.getText().toString().trim();
                lastName = mLastName.getText().toString().trim();
                updateUser(firstName,lastName);
                Navigation.findNavController(getView()).navigate(R.id.profileFragment);
            }
        });
    }
    private void validForm(){
        mFirstName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                String mFirstname = mFirstName.getText().toString();
                if(!(mFirstname.isEmpty())){
                    mSubmit.setVisibility(View.VISIBLE);
                } else {
                    mSubmit.setVisibility(View.INVISIBLE);
                }
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {}
        });
    }
    private void initView(View view){
        mFirstName = getView().findViewById(R.id.firstName);
        mLastName = getView().findViewById(R.id.lastName);
        mSubmit = getView().findViewById(R.id.submitRegistration);
    }

    private void updateUser(String firstName, String lastName){
        Map<String,Object> newValues = new HashMap<String,Object>();
        newValues.put("firstName",firstName);
        newValues.put("lastName",lastName);

        Database database = new Database();
        database.updateUser(newValues);
//        FirebaseUser userId = FirebaseAuth.getInstance().getCurrentUser();
//        mDatabase = FirebaseDatabase.getInstance().getReference();
//        User user = new User(firstName,lastName);
//        if (userId!=null){
//            mDatabase.child("users").child(userId.getUid()).setValue(user);
//        }
//        User user = new User();
//        user.setFirstName(firstName);
//        user.setLastName(lastName);
//        Database database = new Database();
//        database.updateUser(user);
    }

}
