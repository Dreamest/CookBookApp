//package com.dreamest.cookbookapp.activities;
//
//import androidx.appcompat.app.AppCompatActivity;
//
//import android.content.Intent;
//import android.os.Bundle;
//import android.util.Log;
//import android.widget.Toast;
//
//import com.dreamest.cookbookapp.R;
//import com.firebase.ui.auth.AuthUI;
//import com.firebase.ui.auth.ErrorCodes;
//import com.firebase.ui.auth.IdpResponse;
//import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.auth.FirebaseUser;
//
//import java.util.Arrays;
//
//public class LoginActivityV2 extends AppCompatActivity {
//
//    private final int RC_SIGN_IN = 1234;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_login);
//
//
//        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
//        FirebaseUser user = firebaseAuth.getCurrentUser();
//        if (user != null) {
//            startLoginMethod();
//
////            openApp();
//        } else {
//            startLoginMethod();
//        }
//    }
//
//
//    private void startLoginMethod() {
//        Log.d("pttt", "startLoginMethod");
//
//        startActivityForResult(
//                AuthUI.getInstance()
//                        .createSignInIntentBuilder()
//                        .setAvailableProviders(Arrays.asList(
//                                new AuthUI.IdpConfig.PhoneBuilder().build()
//                        ))
//                        .setLogo(R.drawable.ic_spaghetti)
//                        .build(),
//                RC_SIGN_IN);
//    }
//
//    private void openApp() {
//        Intent myIntent = new Intent(this, MainActivity.class);
//        startActivity(myIntent);
//        finish();
//    }
//
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == RC_SIGN_IN) {
//            IdpResponse response = IdpResponse.fromResultIntent(data);
//
//            // Successfully signed in
//            if (resultCode == RESULT_OK) {
//                openApp();
//            } else {
//                // Sign in failed
//                if (response == null) {
//                    // User pressed back button
//                    showSnackbar(R.string.code);
//                    return;
//                }
//
//                if (response.getError().getErrorCode() == ErrorCodes.NO_NETWORK) {
//                    showSnackbar(R.string.code);
//                    return;
//                }
//
//                showSnackbar(R.string.code);
//                Log.e("pttt", "Sign-in error: ", response.getError());
//            }
//        }
//    }
//
//    private void showSnackbar(int id) {
//        Log.d("pttt", "toasting");
//        Toast.makeText(this, "asdasdasd" + id, Toast.LENGTH_SHORT).show();
//    }
//}