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
import com.kabasonic.messenger.ui.bottomnavigation.profile.editfragments.viewmodels.EditBioViewModels;

import java.util.HashMap;
import java.util.Map;

public class EditBioFragment extends Fragment {
    public static final String TAG = "EditBioFragment";
    private EditText mBio;
    private FloatingActionButton mSubmit;
    private EditBioViewModels mViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.bio_fragment,container,false);
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
        validForm();

        mViewModel = ViewModelProviders.of(this).get(EditBioViewModels.class);

        mSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);

                Map<String,Object> newValues = new HashMap<String,Object>();
                newValues.put("bio",mBio.getText().toString().trim());

                mViewModel.getStatusUpdate(newValues).observe(getViewLifecycleOwner(), new Observer<String>() {
                    @Override
                    public void onChanged(String s) {
                        Log.d(TAG,"Status update Bio: " + s);
                    }
                });

                NavDirections action = EditBioFragmentDirections.actionEditBioFragmentToProfileFragment3();
                Navigation.findNavController(getView()).navigate(action);
            }
        });
    }

    private void validForm(){
        mBio.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                String mBiotext = mBio.getText().toString();
                if(!(mBiotext.isEmpty())){
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
        mBio = getView().findViewById(R.id.bio);
        mSubmit = getView().findViewById(R.id.submitRegistration);
    }
}
