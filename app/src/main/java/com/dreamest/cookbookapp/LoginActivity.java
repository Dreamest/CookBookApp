package com.dreamest.cookbookapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthSettings;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.rilixtech.CountryCodePicker;

import java.util.concurrent.TimeUnit;

public class LoginActivity extends AppCompatActivity {

    private TextInputEditText login_EDT_input;
    private MaterialButton login_BTN_cancel;
    private MaterialButton login_BTN_continue;
    private CountryCodePicker login_CCP_code;
    private String phoneNumber = "";
    private enum LOGIN_STATE {
        ENTERING_NUMBER,
        ENTERING_CODE
    }
    private LOGIN_STATE login_state = LOGIN_STATE.ENTERING_NUMBER;

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks  onVerificationStateChangedCallbacks= new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onCodeSent(String verificationId,
                               PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            Log.d("dddd", "On sent: " + verificationId);
            login_state = LOGIN_STATE.ENTERING_CODE;
            updateUI();
        }

        @Override
        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
            Log.d("dddd", "On verify: " +phoneAuthCredential);
            signInWithPhoneAuthCredential(phoneAuthCredential);
        }

        @Override
        public void onVerificationFailed(FirebaseException e) {
            Toast.makeText(LoginActivity.this, "Verification failed.", Toast.LENGTH_SHORT).show();
            Log.e("dddd", "On failed: " + e.getMessage());
            e.printStackTrace();
            login_state = LOGIN_STATE.ENTERING_NUMBER;
            updateUI();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        findViews();
        initViews();


    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        Log.d("dddd", "Trying to sign in");
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("dddd", "signInWithCredential:success");

                            FirebaseUser user = task.getResult().getUser();
                            // ...
                        } else {
                            // Sign in failed, display a message and update the UI
                            Log.w("dddd", "signInWithCredential:failure", task.getException());
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid
                            }
                        }
                    }
                });
    }

    private void initViews() {
        login_BTN_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelClicked();
            }
        });

        login_BTN_continue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(login_state == LOGIN_STATE.ENTERING_NUMBER)
                    getPhoneNumber();
                else if (login_state == LOGIN_STATE.ENTERING_CODE)
                    getConfirmationCode();
            }
        });
    }

    private void getConfirmationCode() {
        String verificationCode = login_EDT_input.getText().toString();
        Log.d("dddd", "V code: " + verificationCode);


        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseAuthSettings firebaseAuthSettings = firebaseAuth.getFirebaseAuthSettings();
        firebaseAuthSettings.setAutoRetrievedSmsCodeForPhoneNumber(phoneNumber, verificationCode);

        PhoneAuthOptions options = PhoneAuthOptions.newBuilder(firebaseAuth)
                .setPhoneNumber(phoneNumber)
                .setTimeout(60L, TimeUnit.SECONDS)
                .setActivity(this)
                .setCallbacks(onVerificationStateChangedCallbacks)
                .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    private void getPhoneNumber() {
        phoneNumber = login_EDT_input.getText().toString();
        if (phoneNumber.charAt(0) == '0')
            phoneNumber = phoneNumber.substring(1); // Skip the zero
        phoneNumber = "+" + login_CCP_code.getSelectedCountryCode() + phoneNumber;

        FirebaseAuth auth = FirebaseAuth.getInstance();
        PhoneAuthOptions options = PhoneAuthOptions.newBuilder(auth)
                .setPhoneNumber(phoneNumber)
                .setTimeout(60L, TimeUnit.SECONDS)
                .setActivity(this)
                .setCallbacks(onVerificationStateChangedCallbacks)
                .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
        Log.d("dddd", phoneNumber);

    }

    private void updateUI() {
        login_EDT_input.setText("");
        if(login_state == LOGIN_STATE.ENTERING_NUMBER) {
            login_EDT_input.setHint(getString(R.string.phone_number));
            login_CCP_code.setVisibility(View.VISIBLE);

        }
        else if (login_state == LOGIN_STATE.ENTERING_CODE) {
            login_CCP_code.setVisibility(View.GONE);
            login_EDT_input.setHint(getString(R.string.code));
        }
    }


    private void cancelClicked() {
        login_state = LOGIN_STATE.ENTERING_NUMBER;
        updateUI();

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    }

    private void findViews() {
        login_EDT_input = findViewById(R.id.login_EDT_input);
        login_BTN_cancel = findViewById(R.id.login_BTN_cancel);
        login_BTN_continue = findViewById(R.id.login_BTN_continue);
        login_CCP_code = findViewById(R.id.login_CCP_code);
    }
}