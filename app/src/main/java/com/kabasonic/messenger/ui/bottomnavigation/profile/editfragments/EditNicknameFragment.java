package com.kabasonic.messenger.ui.bottomnavigation.profile.editfragments;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.kabasonic.messenger.R;
import com.kabasonic.messenger.ui.bottomnavigation.profile.editfragments.viewmodels.EditNicknameViewModels;

import java.util.HashMap;
import java.util.Map;

public class EditNicknameFragment extends Fragment {
    public static final String TAG = "EditNicknameFragment";
    private EditText mNickName;
    private EditNicknameViewModels mViewModel;
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



        mNickName = view.findViewById(R.id.nickname);
        mSubmit = view.findViewById(R.id.submitRegistration);
        mViewModel = ViewModelProviders.of(this).get(EditNicknameViewModels.class);
        validForm();
        mSubmit.setOnClickListener(v -> {
            InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);

            Map<String,Object> newValues = new HashMap<String,Object>();
            newValues.put("nickName",mNickName.getText().toString().trim());

            mViewModel.getStatusUpdate(newValues).observe(getViewLifecycleOwner(), new Observer<String>() {
                @Override
                public void onChanged(String s) {
                    Log.d(TAG, "Status update Nick name: " + s);
                }
            });

            NavDirections action = EditNicknameFragmentDirections.actionEditNicknameFragmentToProfileFragment2();
            Navigation.findNavController(getView()).navigate(action);
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
}
