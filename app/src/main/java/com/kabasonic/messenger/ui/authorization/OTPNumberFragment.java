package com.kabasonic.messenger.ui.authorization;

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
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import com.kabasonic.messenger.MainActivity;
import com.kabasonic.messenger.R;
import com.kabasonic.messenger.ui.authorization.OTPNumberFragmentDirections;

import java.util.concurrent.TimeUnit;

public class OTPNumberFragment extends Fragment {

    public static final String TAG = "OTPNumberFragment";

    private TextView textViewError;
    private EditText editCodeCountry, editPhoneNumber;
    private FloatingActionButton submitButtonPhone;
    private MainActivity mActivity;
    private String[] arrayCodeCountry;
    private String mCodeCountry = null;
    private String mPhoneNumber = null;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private FirebaseAuth mAuth;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    private String verificationCode;



    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof MainActivity) {
            mActivity = (MainActivity) context;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_o_t_p_number, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //mActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        textViewError = (TextView) view.findViewById(R.id.textError);
        editCodeCountry = (EditText) view.findViewById(R.id.codeCountry);
        editPhoneNumber = (EditText) view.findViewById(R.id.phoneNumber);
        submitButtonPhone = (FloatingActionButton) view.findViewById(R.id.submitButtonPhone);

        arrayCodeCountry = getResources().getStringArray(R.array.country_code);

        mActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        //NavController navController = Navigation.findNavController(mActivity, R.id.fragment);

        mAuth = FirebaseAuth.getInstance();

        submitButtonPhone.setOnClickListener(v -> {
            actionKeyboard("close");
            validationNumber(this.mCodeCountry, this.mPhoneNumber);
        });

        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                Toast.makeText(mActivity, "verification completed", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
                //Toast.makeText(mActivity, "verification failed", Toast.LENGTH_SHORT).show();
                Log.d(TAG,"Start onVerificationFailed");
                Log.d("FirebaseException", e.toString());
                Log.d(TAG,"End onVerificationFailed");
            }

            @Override
            public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                Log.d(TAG,"Start onCodeSent");
                super.onCodeSent(s, forceResendingToken);
                verificationCode = s;
                mResendToken = forceResendingToken;
                Log.d("VerificationCode", verificationCode);
                Toast.makeText(mActivity, "Code Sent", Toast.LENGTH_SHORT).show();
                OTPNumberFragmentDirections.ActionOTPNumberFragmentToOTPCodeFragment action = OTPNumberFragmentDirections.actionOTPNumberFragmentToOTPCodeFragment();
                action.setArg(verificationCode);
                action.setArg1(mPhoneNumber);
                //navController.navigate(action);
                Navigation.findNavController(getView()).navigate(action);
                Log.d(TAG,"End onCodeSent");
            }
        };
    }

    @Override
    public void onResume() {
        super.onResume();
        formValidation();
    }

    private void formValidation() {
        editCodeCountry.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                textViewError.setVisibility(View.INVISIBLE);
                mCodeCountry = editCodeCountry.getText().toString().trim();
                if (mCodeCountry != null && mCodeCountry.length() <= 6) {

                    for (int i = 0; i < arrayCodeCountry.length; i++) {
                        if (("+" + mCodeCountry).equals(arrayCodeCountry[i].trim())) {
                            //Toast.makeText(mActivity, "Code OK", Toast.LENGTH_SHORT).show();
                            actionKeyboard("close");
                            editPhoneNumber.requestFocus();
                            actionKeyboard("open");
                            break;
                        }
                    }
                }
            }
        });
        editPhoneNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                textViewError.setVisibility(View.INVISIBLE);
                mPhoneNumber = editPhoneNumber.getText().toString().trim();
                if (!mPhoneNumber.isEmpty()) submitButtonPhone.show();
                else if (mPhoneNumber.isEmpty()) submitButtonPhone.hide();
            }
        });
    }

    private void validationNumber(String code, String number) {
        Log.d(TAG,"Start validationNumber");

        //String phoneNumber = "+" + code + number;
        String phoneNumber = "+" + code + number;

        Toast.makeText(mActivity, "phoneNumber: "+ phoneNumber, Toast.LENGTH_SHORT).show();
        PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
        try {
            Phonenumber.PhoneNumber phoneNumberProto = phoneUtil.parse(phoneNumber, null);
            boolean isValid = phoneUtil.isValidNumber(phoneNumberProto); // returns true if valid
            if (isValid) {

                Toast.makeText(mActivity, "VALID OK", Toast.LENGTH_SHORT).show();
                startPhoneNumberVerification(phoneNumber);
                // Actions to perform if the number is valid
            } else {
                Toast.makeText(mActivity, "VALID NOK", Toast.LENGTH_SHORT).show();
                textViewError.setVisibility(View.VISIBLE);
                // Do necessary actions if its not valid
            }
        } catch (NumberParseException e) {
            System.err.println("NumberParseException was thrown: " + e.toString());
        }
        Log.d(TAG,"End validationNumber");
    }

    private void actionKeyboard(String action) {
        View view = mActivity.getCurrentFocus();
        if (view != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) mActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
            switch (action) {
                case "close":
                    inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    break;
                case "open":
                    inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
                    break;
                default:
                    break;
            }
        }
    }
    private void startPhoneNumberVerification(String phoneNumber) {
        Log.d(TAG,"Start startPhoneNumberVerification");
        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber(phoneNumber)
                        .setTimeout(60L, TimeUnit.SECONDS)
                        .setActivity(mActivity)
                        .setCallbacks(mCallbacks)
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
        Log.d(TAG,"End startPhoneNumberVerification");
    }
}
