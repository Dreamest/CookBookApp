package com.dreamest.cookbookapp.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.dreamest.cookbookapp.R;
import com.dreamest.cookbookapp.logic.User;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class WelcomeActivity extends AppCompatActivity {
    private ImageView welcome_IMG_user_image;
    private TextInputEditText welcome_EDT_name;
    private MaterialButton welcome_BTN_submit;
    private final String USERS = "users";

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
//                .setPhotoUri() // TODO: 2/1/21 Implement after adding firebase storage and taking pictures
                .build();
        firebaseUser.updateProfile(profileUpdates);
        firebaseAuth.updateCurrentUser(firebaseUser);

        User user = new User()
                .setUserID(firebaseUser.getUid())
                .setDisplayName(userName)
                .setPhoneNumber(firebaseUser.getPhoneNumber());
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference(USERS).child(user.getUserID());
        ref.setValue(user);


    }

    private void takePhoto() {
    }

    private void findViews() {
        welcome_IMG_user_image = findViewById(R.id.welcome_IMG_user_image);
        welcome_EDT_name = findViewById(R.id.welcome_EDT_name);
        welcome_BTN_submit = findViewById(R.id.welcome_BTN_submit);
    }
}