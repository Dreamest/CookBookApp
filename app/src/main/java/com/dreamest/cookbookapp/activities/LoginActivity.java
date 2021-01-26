package com.dreamest.cookbookapp.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.dreamest.cookbookapp.R;
import com.dreamest.cookbookapp.utility.UtilityPack;
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

public class LoginActivity extends BaseActivity {

    private TextInputEditText login_EDT_input;
    private MaterialButton login_BTN_cancel;
    private MaterialButton login_BTN_continue;
    private CountryCodePicker login_CCP_code;
    private ProgressBar login_PROGBAR_spinner;
    private ImageView login_IMG_background;

    private String phoneInput = "";
    private enum LOGIN_STATE {
        ENTERING_NUMBER,
        ENTERING_CODE,
        LOADING
    }
    private LOGIN_STATE login_state = LOGIN_STATE.ENTERING_NUMBER;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        findViews();
        initViews();
        Glide.with(this).load(UtilityPack.randomBackground()).fitCenter().into(login_IMG_background);

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
//        if (firebaseUser != null) {
//            userSignedIn();
//            return;
//        }
        Log.d("dddd", "user: " + firebaseUser);
    }

    private void codeEntered() {
        String smsVerificationCode = login_EDT_input.getText().toString();
        Log.d("dddd", "smsVerificationCode:" + smsVerificationCode);

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseAuthSettings firebaseAuthSettings = firebaseAuth.getFirebaseAuthSettings();
        firebaseAuthSettings.setAutoRetrievedSmsCodeForPhoneNumber(phoneInput, smsVerificationCode);

        PhoneAuthOptions options = PhoneAuthOptions.newBuilder(firebaseAuth)
                .setPhoneNumber(phoneInput)
                .setTimeout(60L, TimeUnit.SECONDS)
                .setActivity(this)
                .setCallbacks(onVerificationStateChangedCallbacks)
                .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
        changeState(LOGIN_STATE.LOADING);
    }

    private void startLoginProcess() {
        phoneInput = login_EDT_input.getText().toString();
        if(phoneInput.charAt(0) == '0' && phoneInput.length() == 10)
            phoneInput = phoneInput.substring(1);
        phoneInput = login_CCP_code.getSelectedCountryCodeWithPlus() + phoneInput;
        Log.d("dddd", "phoneInput:" + phoneInput);

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        PhoneAuthOptions options = PhoneAuthOptions.newBuilder(firebaseAuth)
                .setPhoneNumber(phoneInput)
                .setTimeout(60L, TimeUnit.SECONDS)
                .setActivity(this)
                .setCallbacks(onVerificationStateChangedCallbacks)
                .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
        changeState(LOGIN_STATE.LOADING);
    }

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks onVerificationStateChangedCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onCodeSent(String verificationId, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            Log.d("dddd", "onCodeSent: " + verificationId);
            changeState(LOGIN_STATE.ENTERING_CODE);
        }

        @Override
        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
            Log.d("dddd", "onVerificationCompleted");
            signInWithPhoneAuthCredential(phoneAuthCredential);
            changeState(LOGIN_STATE.ENTERING_NUMBER);
        }

        @Override
        public void onCodeAutoRetrievalTimeOut(@NonNull String s) {
            Log.d("dddd", "onCodeAutoRetrievalTimeOut " + s);
            super.onCodeAutoRetrievalTimeOut(s);
            changeState(LOGIN_STATE.ENTERING_NUMBER);

        }

        @Override
        public void onVerificationFailed(FirebaseException e) {
            Log.d("dddd", "onVerificationFailed: " + e.getMessage());
            e.printStackTrace();
            Toast.makeText(LoginActivity.this, "VerificationFailed " + e.getMessage(), Toast.LENGTH_SHORT).show();
            changeState(LOGIN_STATE.ENTERING_NUMBER);

        }
    };

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("dddd", "signInWithCredential:success");
                            FirebaseUser user = task.getResult().getUser();
                            userSignedIn();


                        } else {
                            // Sign in failed, display a message and update the UI
                            Log.w("dddd", "signInWithCredential:failure", task.getException());
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                Toast.makeText(LoginActivity.this, "Wrong Code", Toast.LENGTH_SHORT).show();
                                updateUI();
                            }
                        }
                    }
                });
    }

    private void userSignedIn() {
        Intent myIntent = new Intent(this, MainActivity.class);
        startActivity(myIntent);
        finish();
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
                    startLoginProcess();
                else if (login_state == LOGIN_STATE.ENTERING_CODE)
                    codeEntered();
            }
        });
    }

    private void cancelClicked() {
        // TODO: 1/26/21 Cancel only works after timeout had occoured. Waiting for reply from Guy
        changeState(LOGIN_STATE.ENTERING_NUMBER);
    }

    private void findViews() {
        login_EDT_input = findViewById(R.id.login_EDT_input);
        login_BTN_cancel = findViewById(R.id.login_BTN_cancel);
        login_BTN_continue = findViewById(R.id.login_BTN_continue);
        login_CCP_code = findViewById(R.id.login_CCP_code);
        login_PROGBAR_spinner = findViewById(R.id.login_PROGBAR_spinner);
        login_IMG_background = findViewById(R.id.login_IMG_background);
    }

    private void changeState(LOGIN_STATE state) {
        login_state = state;
        updateUI();

    }

    private void updateUI() {
        login_EDT_input.setText("");
        if(login_state == LOGIN_STATE.ENTERING_NUMBER) {
            login_EDT_input.setHint(getString(R.string.phone_number));

            login_EDT_input.setVisibility(View.VISIBLE);
            login_BTN_cancel.setVisibility(View.VISIBLE);
            login_BTN_continue.setVisibility(View.VISIBLE);
            login_CCP_code.setVisibility(View.VISIBLE);
            login_PROGBAR_spinner.setVisibility(View.GONE);

        }
        else if (login_state == LOGIN_STATE.ENTERING_CODE) {
            login_EDT_input.setHint(getString(R.string.code));

            login_EDT_input.setVisibility(View.VISIBLE);
            login_BTN_cancel.setVisibility(View.VISIBLE);
            login_BTN_continue.setVisibility(View.VISIBLE);
            login_CCP_code.setVisibility(View.GONE);
            login_PROGBAR_spinner.setVisibility(View.GONE);
        }
        else if (login_state == LOGIN_STATE.LOADING) {
            login_EDT_input.setVisibility(View.GONE);
            login_BTN_cancel.setVisibility(View.GONE);
            login_BTN_continue.setVisibility(View.GONE);
            login_CCP_code.setVisibility(View.GONE);
            login_PROGBAR_spinner.setVisibility(View.VISIBLE);

        }
    }
}