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
import com.kabasonic.messenger.R;
import com.kabasonic.messenger.database.Database;

import java.util.HashMap;
import java.util.Map;

public class EditNicknameFragment extends Fragment {

    private EditText mNickName;
    private String nickName;
    private FloatingActionButton mSubmit;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.nickname_fragment,container,false);
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
        validForm();
        mSubmit.setOnClickListener(v -> {
            InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
            nickName = mNickName.getText().toString().trim();
            updateNickname(nickName);
            Navigation.findNavController(getView()).navigate(R.id.profileFragment);
        });
    }
    private void validForm(){
        mNickName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                String mNickname = mNickName.getText().toString();
                if(!(mNickname.isEmpty())){
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
        mNickName = getView().findViewById(R.id.nickname);
        mSubmit = getView().findViewById(R.id.submitRegistration);
    }

    private void updateNickname(String nickname){
        Map<String,Object> newValues = new HashMap<String,Object>();
        newValues.put("nickName",nickname);
        Database database = new Database();
        database.updateUser(newValues);
    }

}
