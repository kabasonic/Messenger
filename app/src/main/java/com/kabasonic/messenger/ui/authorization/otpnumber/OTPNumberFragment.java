package com.kabasonic.messenger.ui.authorization.otpnumber;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthSettings;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.kabasonic.messenger.MainActivity;
import com.kabasonic.messenger.R;

import java.sql.Time;
import java.util.concurrent.TimeUnit;

public class OTPNumberFragment extends Fragment {

    public static final String TAG = "OTPNumberFragment";

    EditText codeCountry, phoneNumber;
    FloatingActionButton submitCode;
    MainActivity mActivity;

    public String mNumberPhone = "+48731679458";
    public FirebaseAuth mAuth;
    public PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    public String verificationCode;
    String smsCode = "123456";



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

        codeCountry = (EditText) view.findViewById(R.id.codeCountry);
        phoneNumber = (EditText) view.findViewById(R.id.phoneNumber);
        submitCode = (FloatingActionButton) view.findViewById(R.id.submitButtonPhone);
        NavController navController = Navigation.findNavController(mActivity, R.id.fragment);


        mAuth = FirebaseAuth.getInstance();

        submitCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                codeCountry.getText().toString().trim();
                phoneNumber.getText().toString().trim();
                Toast.makeText(getContext(), "Pressed", Toast.LENGTH_SHORT).show();

                PhoneAuthOptions options =
                        PhoneAuthOptions.newBuilder(mAuth)
                                .setPhoneNumber(mNumberPhone)
                                .setTimeout(10L, TimeUnit.SECONDS)
                                .setActivity(mActivity)
                                .setCallbacks(mCallbacks)
                                .build();
                PhoneAuthProvider.verifyPhoneNumber(options);

            }
        });

        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                Toast.makeText(mActivity, "verification completed", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
                Toast.makeText(mActivity, "verification failed", Toast.LENGTH_SHORT).show();
                Log.d("FirebaseException", e.toString());
            }

            @Override
            public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(s, forceResendingToken);
                verificationCode = s;
                Log.d("VerificationCode", verificationCode);
                Toast.makeText(mActivity, "Code Sent", Toast.LENGTH_SHORT).show();

                OTPNumberFragmentDirections.ActionOTPNumberFragmentToOTPCodeFragment action = OTPNumberFragmentDirections.actionOTPNumberFragmentToOTPCodeFragment();
                action.setArg(verificationCode);
                navController.navigate(action);

            }

            @Override
            public void onCodeAutoRetrievalTimeOut(@NonNull String s) {
                super.onCodeAutoRetrievalTimeOut(s);
            }
        };

    }

    //Create app top bar menu
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        mActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        super.onCreate(savedInstanceState);
    }


}
