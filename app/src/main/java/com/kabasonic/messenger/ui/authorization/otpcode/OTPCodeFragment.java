package com.kabasonic.messenger.ui.authorization.otpcode;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

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
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthSettings;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.kabasonic.messenger.R;
import com.kabasonic.messenger.ui.authorization.otpnumber.OTPNumberFragmentDirections;

import java.util.concurrent.Executor;


public class OTPCodeFragment extends Fragment {

    public static final String TAG = "OTPCodeFragment";

     EditText otpCode;
     TextView textTimer, resendCode;
     FloatingActionButton submitCode;
    private  String verificationCode;
    private String code;
    public FirebaseAuth mAuth;
    public String mNumberPhone = "+16505554567";
    String smsCode = "123456";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_o_t_p_code, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
          otpCode = (EditText) view.findViewById(R.id.otpCode);
          resendCode = (TextView) view.findViewById(R.id.resendCode);
          textTimer = (TextView) view.findViewById(R.id.textTimer);
          submitCode = (FloatingActionButton) view.findViewById(R.id.submitButtonCode);

        mAuth = FirebaseAuth.getInstance();
        FirebaseAuthSettings firebaseAuthSettings = mAuth.getFirebaseAuthSettings();
// Configure faking the auto-retrieval with the whitelisted numbers.
        firebaseAuthSettings.setAutoRetrievedSmsCodeForPhoneNumber(mNumberPhone, code);

          if(getArguments() != null){
              OTPCodeFragmentArgs args = OTPCodeFragmentArgs.fromBundle(getArguments());
              verificationCode = args.getArg();
              Toast.makeText(getActivity(),verificationCode,Toast.LENGTH_SHORT).show();
          }

          submitCode.setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View v) {
                  code = otpCode.getText().toString().trim();
                  PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationCode.toString().trim(), code);

                  mAuth.signInWithCredential(credential)
                          .addOnCompleteListener(getActivity(), (OnCompleteListener<AuthResult>) task -> {
                              if (task.isSuccessful()) {
                                  // Sign in success, update UI with the signed-in user's information
                                  Log.d(TAG, "signInWithCredential:success");
                                  Toast.makeText(getActivity(), "DONE", Toast.LENGTH_SHORT).show();
                                  //FirebaseUser user = task.getResult().getUser();
                                  // ...
                                  NavDirections action = OTPCodeFragmentDirections.actionOTPCodeFragmentToMessagesFragment();
                                  Navigation.findNavController(view).navigate(action);
                                  //TODO check user in databse


                              } else {
                                  // Sign in failed, display a message and update the UI
                                  Log.w(TAG, "signInWithCredential:failure", task.getException());
                                  Toast.makeText(getActivity(), "((((", Toast.LENGTH_SHORT).show();
                                  if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                      // The verification code entered was invalid
                                  }
                              }
                          });


              }
          });
    }

    // (onClick function) When you pressed RESEND CODE
    public void resendCode (View v){

    }

}