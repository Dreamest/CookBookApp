package com.dreamest.cookbookapp.activities;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.util.Util;
import com.dreamest.cookbookapp.R;
import com.dreamest.cookbookapp.logic.User;
import com.dreamest.cookbookapp.utility.HideUI;
import com.dreamest.cookbookapp.utility.UtilityPack;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.opensooq.supernova.gligar.GligarPicker;
import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.io.IOException;

public class WelcomeActivity extends BaseActivity {
    private ImageView welcome_IMG_user_image;
    private TextInputEditText welcome_EDT_name;
    private MaterialButton welcome_BTN_submit;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        findViews();
        initViews();
    }

    private void initViews() {
        welcome_IMG_user_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takePhoto();
            }
        });

        welcome_BTN_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitData();
            }
        });

        welcome_EDT_name.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId == EditorInfo.IME_ACTION_DONE){
                    HideUI.clearFocus(WelcomeActivity.this, welcome_EDT_name);
                }
                return false;
            }
        });
    }

    private void submitData() {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        String userName;
        if(!welcome_EDT_name.getText().toString().equals("")) {
            userName = welcome_EDT_name.getText().toString();
        } else {
            userName = firebaseUser.getPhoneNumber();
        }
        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest
                .Builder()
                .setDisplayName(userName)
                .build();
        firebaseUser.updateProfile(profileUpdates);
        firebaseAuth.updateCurrentUser(firebaseUser);

        User user = new User()
                .setUserID(firebaseUser.getUid())
                .setDisplayName(userName)
                .setPhoneNumber(firebaseUser.getPhoneNumber());
//                .setProfileImage() 
        // TODO: 2/2/21 add setProfileImage to the mix
        user.updateFirebase();

        moveToMainActivity();
    }

    private void moveToMainActivity() {
        Intent myIntent = new Intent(this, MainActivity.class);
        startActivity(myIntent);
        finish();
    }

    private void takePhoto() {
        Toast.makeText(WelcomeActivity.this, R.string.not_implemented, Toast.LENGTH_SHORT).show();
        new GligarPicker().requestCode(UtilityPack.REQUEST_CODES.GILGAR).withActivity(this).limit(1).show();
        // TODO: 2/6/21 image not stored on firebase yet
// If no picture taken, use a default one that will be stored in firebase storage
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != Activity.RESULT_OK) {
            return;
        }
        switch (requestCode){
            case UtilityPack.REQUEST_CODES.GILGAR : {
                File image = new File(data.getExtras().getStringArray(GligarPicker.IMAGES_RESULT)[0]);
                UtilityPack.cropImage(this, image, FirebaseAuth.getInstance().getCurrentUser().getUid());
                break;
            }

            case UtilityPack.REQUEST_CODES.UCROP : {
                UtilityPack.loadUCropResult(this, data, welcome_IMG_user_image, R.drawable.ic_man_avatar);
                break;
            }
        }
    }

    private void findViews() {
        welcome_IMG_user_image = findViewById(R.id.welcome_IMG_user_image);
        welcome_EDT_name = findViewById(R.id.welcome_EDT_name);
        welcome_BTN_submit = findViewById(R.id.welcome_BTN_submit);
    }
}