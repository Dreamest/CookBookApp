package com.dreamest.cookbookapp.activities;

import androidx.annotation.NonNull;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.dreamest.cookbookapp.R;
import com.dreamest.cookbookapp.utility.FirebaseTools;
import com.dreamest.cookbookapp.utility.HideUI;
import com.dreamest.cookbookapp.utility.UtilityPack;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rilixtech.CountryCodePicker;

import java.util.concurrent.TimeUnit;

public class LoginActivity extends BaseActivity {

    private TextInputEditText login_EDT_input;
    private MaterialButton login_BTN_cancel;
    private MaterialButton login_BTN_continue;
    private CountryCodePicker login_CCP_code;
    private ProgressBar login_PROGBAR_spinner;
    private ImageView login_IMG_background;
    private FirebaseAuth firebaseAuth;


    private String phoneInput = "";
    private String mVerificationId;
    private PhoneAuthProvider.ForceResendingToken mResendToken;

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


        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        Log.d("dddd", "user = " + firebaseUser);
        if (firebaseUser != null) {
            changeState(LOGIN_STATE.LOADING);
//            stayInActivity(); enable for testing
            userSignedIn(firebaseUser);
        }
    }

    private void stayInActivity() {
            firebaseAuth.signOut();
            finish();
    }

    private void codeEntered() {
        if(!login_EDT_input.getText().toString().trim().equals("")) {
            String smsVerificationCode = login_EDT_input.getText().toString();
            PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, smsVerificationCode);
            signInWithPhoneAuthCredential(credential);
            changeState(LOGIN_STATE.LOADING);
        } else {
            Toast.makeText(this, R.string.enter_code, Toast.LENGTH_SHORT).show();
        }

    }

    private void startLoginProcess() {
        if(!login_EDT_input.getText().toString().trim().equals("")) {
            phoneInput = UtilityPack.extractPhoneNumber(login_CCP_code, login_EDT_input);
            PhoneAuthOptions options =
                    PhoneAuthOptions.newBuilder(firebaseAuth)
                            .setPhoneNumber(phoneInput)       // Phone number to verify
                            .setTimeout(120L, TimeUnit.SECONDS) // Timeout and unit
                            .setActivity(this)                 // Activity (for callback binding)
                            .setCallbacks(mCallbacks)          // OnVerificationStateChangedCallbacks
                            .build();
            PhoneAuthProvider.verifyPhoneNumber(options);
            changeState(LOGIN_STATE.LOADING);
        } else {
            Toast.makeText(this, R.string.enter_phone_number, Toast.LENGTH_SHORT).show();
        }
    }

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onCodeAutoRetrievalTimeOut(@NonNull String s) {
            super.onCodeAutoRetrievalTimeOut(s);
            changeState(LOGIN_STATE.ENTERING_CODE);
            Toast.makeText(LoginActivity.this, R.string.time_out_try_again, Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onVerificationCompleted(PhoneAuthCredential credential) {
            // This callback will be invoked in two situations:
            // 1 - Instant verification. In some cases the phone number can be instantly
            //     verified without needing to send or enter a verification code.
            // 2 - Auto-retrieval. On some devices Google Play services can automatically
            //     detect the incoming verification SMS and perform verification without
            //     user action.
            Log.d("dddd", "onVerificationCompleted:" + credential);

            signInWithPhoneAuthCredential(credential);
            changeState(LOGIN_STATE.LOADING);
        }

        @Override
        public void onVerificationFailed(FirebaseException e) {

            if (e instanceof FirebaseAuthInvalidCredentialsException) {
                Log.e("dddd", e.getMessage());
                // Invalid request
            } else if (e instanceof FirebaseTooManyRequestsException) {
                Log.e("dddd", e.getMessage());
                // The SMS quota for the project has been exceeded
            }
            Toast.makeText(LoginActivity.this, getString(R.string.verification_failed) + e.getMessage(), Toast.LENGTH_LONG).show();
            changeState(LOGIN_STATE.ENTERING_NUMBER);
        }

        @Override
        public void onCodeSent(@NonNull String verificationId, @NonNull PhoneAuthProvider.ForceResendingToken token) {
            mVerificationId = verificationId;
            mResendToken = token;
            changeState(LOGIN_STATE.ENTERING_CODE);
        }
    };

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = task.getResult().getUser();
                            Toast.makeText(LoginActivity.this, R.string.signed_in_successfully, Toast.LENGTH_SHORT).show();
                            userSignedIn(user);
                            // ...
                        } else {
                            Log.w("dddd", "signInWithCredential:failure", task.getException());
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                Toast.makeText(LoginActivity.this, R.string.invalid_code, Toast.LENGTH_SHORT).show();
                                changeState(LOGIN_STATE.ENTERING_CODE);
                            }
                        }
                    }
                });
    }

    private void userSignedIn(FirebaseUser user) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference(FirebaseTools.DATABASE_KEYS.USERS).child(user.getUid());

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Intent myIntent;
                if (snapshot.getValue() != null) {
                    myIntent = new Intent(LoginActivity.this, MainActivity.class);
                } else {
                    myIntent = new Intent(LoginActivity.this, WelcomeActivity.class);
                }
                startActivity(myIntent);
                finish();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w("dddd", "Failed to read value.", error.toException());
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
                continueClicked();
            }
        });

        login_EDT_input.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    HideUI.clearFocus(LoginActivity.this, login_EDT_input);
                    continueClicked();
                }
                return false;
            }
        });
    }

    private void continueClicked() {
        if (login_state == LOGIN_STATE.ENTERING_NUMBER) {
            startLoginProcess();
        }
        else if (login_state == LOGIN_STATE.ENTERING_CODE) {
            codeEntered();
        }
    }

    private void cancelClicked() {
        // Known bug: Trying to input new values after cancelClicked only actually works after timeout. Guy said it's ok on email
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

    /**
     * Changes the activity state and updates the UI
     *
     * @param state state to change to
     */
    private void changeState(LOGIN_STATE state) {
        login_state = state;
        updateUI();
    }

    /**
     * updates the UI based on active state
     */
    private void updateUI() {
        login_EDT_input.setText("");
        if (login_state == LOGIN_STATE.ENTERING_NUMBER) {
            login_EDT_input.setHint(getString(R.string.phone_number));

            login_EDT_input.setVisibility(View.VISIBLE);
            login_BTN_cancel.setVisibility(View.VISIBLE);
            login_BTN_continue.setVisibility(View.VISIBLE);
            login_CCP_code.setVisibility(View.VISIBLE);
            login_PROGBAR_spinner.setVisibility(View.GONE);

        } else if (login_state == LOGIN_STATE.ENTERING_CODE) {
            login_EDT_input.setHint(getString(R.string.code));

            login_EDT_input.setVisibility(View.VISIBLE);
            login_BTN_cancel.setVisibility(View.VISIBLE);
            login_BTN_continue.setVisibility(View.VISIBLE);
            login_CCP_code.setVisibility(View.GONE);
            login_PROGBAR_spinner.setVisibility(View.GONE);
        } else if (login_state == LOGIN_STATE.LOADING) {
            login_EDT_input.setVisibility(View.GONE);
            login_BTN_cancel.setVisibility(View.GONE);
            login_BTN_continue.setVisibility(View.GONE);
            login_CCP_code.setVisibility(View.GONE);
            login_PROGBAR_spinner.setVisibility(View.VISIBLE);
        }
    }
}