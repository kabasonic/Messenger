package com.kabasonic.messenger.ui.authorization;

import android.content.Context;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.kabasonic.messenger.MainActivity;
import com.kabasonic.messenger.R;
import com.kabasonic.messenger.ui.authorization.viewmodels.OTPCodeViewModel;

import java.util.concurrent.TimeUnit;


public class OTPCodeFragment extends Fragment {

    public static final String TAG = "OTPCodeFragment";

    private EditText otpCode;
    private TextView textTimer, resendCode, textError, textForTimer;
    private FloatingActionButton submitCode;
    private String phoneNumber;
    private String verificationCode;
    private String code;
    public FirebaseAuth mAuth;
    private boolean resendCodePressed = false;
    private MainActivity mActivity;
    public PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    public PhoneAuthProvider.ForceResendingToken mResendToken;
    private String mVerificationId;
    private ProgressBar mProgressBar;
    private OTPCodeViewModel mViewModel;

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

        initViewElements(view);
        getArgumentsFragment();
        mViewModel = ViewModelProviders.of(this).get(OTPCodeViewModel.class);
        mViewModel.init();

        mAuth = FirebaseAuth.getInstance();


        resendCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!resendCodePressed) resendCodeAction();
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
        submitCode.setOnClickListener(v -> {
            code = otpCode.getText().toString().trim();
            actionKeyboard(false);
            submitCode.setVisibility(View.INVISIBLE);
            mProgressBar.setVisibility(View.VISIBLE);
            submitCodeVerification(code, verificationCode);
        });

    }

    private void initViewElements(View view) {
        mProgressBar = (ProgressBar) view.findViewById(R.id.progressBarCode);
        resendCode = (TextView) view.findViewById(R.id.resendCode);
        textTimer = (TextView) view.findViewById(R.id.textTimer);
        textError = (TextView) view.findViewById(R.id.textErrorCode);
        textForTimer = (TextView) view.findViewById(R.id.textForTimer);
        otpCode = (EditText) view.findViewById(R.id.otpCode);
        submitCode = (FloatingActionButton) view.findViewById(R.id.submitButtonCode);
    }

    private void formValidate() {
        otpCode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (otpCode.length() == 6) {
                    submitCode.show();
                } else {
                    submitCode.hide();
                    textError.setVisibility(View.INVISIBLE);
                }
            }
        });
    }

    private void getArgumentsFragment() {
        if (getArguments() != null) {
            OTPCodeFragmentArgs args = OTPCodeFragmentArgs.fromBundle(getArguments());
            verificationCode = args.getArg();
            phoneNumber = args.getArg1();
//            Toast.makeText(getActivity(), verificationCode, Toast.LENGTH_SHORT).show();
        }
    }

    private void submitCodeVerification(String code, String verificationCode) {

        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationCode.trim(), code);

        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(mActivity, (OnCompleteListener<AuthResult>) task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "signInWithCredential:success");

                        mViewModel.getNavigation().observe(getViewLifecycleOwner(), new Observer<String>() {
                            @Override
                            public void onChanged(String s) {
                                Log.d(TAG, "onChanged " + s);
                                if (s.equals("true")) {
                                    NavDirections action = OTPCodeFragmentDirections.actionOTPCodeFragmentToMessagesFragment();
                                    Navigation.findNavController(getView()).navigate(action);
                                } else if (!s.isEmpty() && !s.equals(true)) {
                                    NavDirections action = OTPCodeFragmentDirections.actionOTPCodeFragmentToRegistrationFragment();
                                    Navigation.findNavController(getView()).navigate(action);
                                }
                            }
                        });

                        textError.setVisibility(View.INVISIBLE);
                    } else {
                        // Sign in failed, display a message and update the UI
                        textError.setVisibility(View.VISIBLE);
                        submitCode.setVisibility(View.VISIBLE);
                        mProgressBar.setVisibility(View.INVISIBLE);
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
        if (resendCodePressed) {
            resendVerificationCode(phoneNumber, mResendToken);
            textForTimer.setVisibility(View.VISIBLE);
            textTimer.setVisibility(View.VISIBLE);
            resendCode.setTextColor(getResources().getColor(R.color.colorGray));
            new CountDownTimer(60000, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    textTimer.setText(String.valueOf(millisUntilFinished / 1000));
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
    private void actionKeyboard(boolean action) {
        View view = getActivity().getCurrentFocus();
        if (view != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            if (action) {
                inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
            } else {
                inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }

        }
    }
}