package com.kabasonic.messenger.ui.authorization.otpcode;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthSettings;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.kabasonic.messenger.MainActivity;
import com.kabasonic.messenger.R;
import com.kabasonic.messenger.ui.authorization.otpnumber.OTPNumberFragmentDirections;

import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;


public class OTPCodeFragment extends Fragment {

    public static final String TAG = "OTPCodeFragment";

    EditText otpCode;
    TextView textTimer, resendCode, textError, textForTimer;
    FloatingActionButton submitCode;
    private String phoneNumber;
    private String verificationCode;
    private String code;
    public FirebaseAuth mAuth;
    private boolean resendCodePressed = false;
    MainActivity mActivity;
    public PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    public PhoneAuthProvider.ForceResendingToken mResendToken;
    private String mVerificationId;

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
        View view = inflater.inflate(R.layout.fragment_o_t_p_code, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        resendCode = (TextView) view.findViewById(R.id.resendCode);
        textTimer = (TextView) view.findViewById(R.id.textTimer);
        textError = (TextView) view.findViewById(R.id.textErrorCode);
        textForTimer = (TextView) view.findViewById(R.id.textForTimer);
        otpCode = (EditText) view.findViewById(R.id.otpCode);
        submitCode = (FloatingActionButton) view.findViewById(R.id.submitButtonCode);

        mAuth = FirebaseAuth.getInstance();

        getArgumentsFragment();
        //[START TEST OTP]
//        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
//        FirebaseAuthSettings firebaseAuthSettings = firebaseAuth.getFirebaseAuthSettings();
//
//        // Configure faking the auto-retrieval with the whitelisted numbers.
//        firebaseAuthSettings.setAutoRetrievedSmsCodeForPhoneNumber(phoneNumber, code);
        //[END TEST OTP]

        submitCode.setOnClickListener(v -> {
            code = otpCode.getText().toString().trim();
            submitCodeVerification(code,verificationCode);
        });

        resendCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!resendCodePressed) resendCodeAction();
            }
        });

        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
            }
            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {

            }
            @Override
            public void onCodeSent(@NonNull String verificationId, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(verificationId, forceResendingToken);
                mVerificationId = verificationId;
                mResendToken = forceResendingToken;
            }

        };

    }

    @Override
    public void onResume() {
        super.onResume();
        formValidate();
    }

    private void formValidate(){
        otpCode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }
            @Override
            public void afterTextChanged(Editable s) {
                if(otpCode.length() == 6){
                    submitCode.show();
                }else {
                    submitCode.hide();
                    textError.setVisibility(View.INVISIBLE);
                }
            }
        });
    }
    private void getArgumentsFragment(){
        if (getArguments() != null) {
            OTPCodeFragmentArgs args = OTPCodeFragmentArgs.fromBundle(getArguments());
            verificationCode = args.getArg();
            phoneNumber = args.getArg1();
//            Toast.makeText(getActivity(), verificationCode, Toast.LENGTH_SHORT).show();
        }
    }
    private void submitCodeVerification(String code, String verificationCode){

        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationCode.trim(), code);

        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(getActivity(), (OnCompleteListener<AuthResult>) task -> {
                    if (task.isSuccessful()) {

                        Log.d(TAG, "signInWithCredential:success");
                        Toast.makeText(getActivity(), "DONE", Toast.LENGTH_SHORT).show();
                        //FirebaseUser user = task.getResult().getUser();
                        // ...
                        // Sign in success, update UI with the signed-in user's information
                        NavDirections action = OTPCodeFragmentDirections.actionOTPCodeFragmentToMessagesFragment();
                        Navigation.findNavController(getView()).navigate(action);
                        //TODO check user in databse
                        textError.setVisibility(View.INVISIBLE);
                    } else {
                        // Sign in failed, display a message and update the UI
                        textError.setVisibility(View.VISIBLE);
                        Log.w(TAG, "signInWithCredential:failure", task.getException());
                        if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                            // The verification code entered was invalid

                        }
                    }
                });
    }

    private void resendVerificationCode(String phoneNumber,
                                        PhoneAuthProvider.ForceResendingToken token) {
        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber(phoneNumber)       // Phone number to verify
                        .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                        .setActivity(mActivity)                 // Activity (for callback binding)
                        .setCallbacks(mCallbacks)          // OnVerificationStateChangedCallbacks
                        .setForceResendingToken(token)     // ForceResendingToken from callbacks
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    // (onClick function) When you pressed RESEND CODE
    private void resendCodeAction() {
        this.resendCodePressed = true;
        if(resendCodePressed){
            resendVerificationCode(phoneNumber,mResendToken);
            textForTimer.setVisibility(View.VISIBLE);
            textTimer.setVisibility(View.VISIBLE);
            resendCode.setTextColor(getResources().getColor(R.color.colorGray));
            new CountDownTimer(60000,1000){
                @Override
                public void onTick(long millisUntilFinished) {
                    textTimer.setText(String.valueOf(millisUntilFinished/1000));
                }
                @Override
                public void onFinish() {
                    resendCodePressed = false;
                    textForTimer.setVisibility(View.INVISIBLE);
                    textTimer.setVisibility(View.INVISIBLE);
                    resendCode.setTextColor(getResources().getColor(R.color.colorOrange));
                }
            }.start();
        }

    }

}