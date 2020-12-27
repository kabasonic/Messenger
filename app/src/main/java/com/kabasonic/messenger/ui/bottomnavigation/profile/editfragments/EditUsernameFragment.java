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
import com.kabasonic.messenger.ui.bottomnavigation.profile.editfragments.viewmodels.EditUsernameViewModels;

import java.util.HashMap;
import java.util.Map;

public class EditUsernameFragment extends Fragment {

    public static final String TAG ="EditUsernameFragment";

    private EditText mFirstName, mLastName;
    private FloatingActionButton mSubmit;
    private EditUsernameViewModels mViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.registration_fragment,container,false);
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mViewModel = ViewModelProviders.of(this).get(EditUsernameViewModels.class);
        mFirstName = getView().findViewById(R.id.firstName);
        mLastName = getView().findViewById(R.id.lastName);
        mSubmit = getView().findViewById(R.id.submitRegistration);

        validForm();

        mSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);

                Map<String,Object> newValues = new HashMap<String,Object>();
                newValues.put("firstName",mFirstName.getText().toString().trim());
                newValues.put("lastName",mLastName.getText().toString().trim());

                mViewModel.getStatusUpdate(newValues).observe(getViewLifecycleOwner(), new Observer<String>() {
                    @Override
                    public void onChanged(String s) {
                        Log.d(TAG,"Status update User name: " + s);
                    }
                });

                NavDirections action = EditUsernameFragmentDirections.actionEditUsernameFragmentToProfileFragment2();
                Navigation.findNavController(getView()).navigate(action);
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
}
