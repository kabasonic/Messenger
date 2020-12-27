package com.kabasonic.messenger.ui.authorization;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.kabasonic.messenger.MainActivity;
import com.kabasonic.messenger.R;
import com.kabasonic.messenger.ui.authorization.viewmodels.RegistrationViewModel;

public class RegistrationFragment extends Fragment {

    public static final String TAG = "RegistrationFragment";

    private EditText firstName,
                     lastName;
    private FloatingActionButton submitRegistration;
    private ProgressBar mProgressBar;
    private MainActivity mActivity;
    private RegistrationViewModel mViewModel;
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

        mViewModel = ViewModelProviders.of(this).get(RegistrationViewModel.class);
        mViewModel.init();

        initViewElements(view);
        formValidation();
        submitRegistration.setOnClickListener(v -> {
                actionKeyboard(false);
                submitRegistration.setVisibility(View.INVISIBLE);
                mProgressBar.setVisibility(View.VISIBLE);
                //createUser();
                mViewModel.createUser(firstName.getText().toString(),lastName.getText().toString()).observe(getViewLifecycleOwner(), new Observer<Boolean>() {
                    @Override
                    public void onChanged(Boolean aBoolean) {
                        if(aBoolean){
                            NavDirections action = RegistrationFragmentDirections.actionRegistrationFragmentToMessagesFragment();
                            Navigation.findNavController(getView()).navigate(action);
                        }
                    }
                });
        });
    }

    private void initViewElements(View view){
        firstName = (EditText) view.findViewById(R.id.firstName);
        mProgressBar = (ProgressBar) view.findViewById(R.id.progressBarRegistration);
        lastName = (EditText) view.findViewById(R.id.lastName);
        submitRegistration = (FloatingActionButton) view.findViewById(R.id.submitRegistration);
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