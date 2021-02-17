package com.dreamest.cookbookapp.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.dreamest.cookbookapp.R;
import com.dreamest.cookbookapp.logic.User;
import com.dreamest.cookbookapp.adapters.FirebaseAdapterManager;
import com.dreamest.cookbookapp.utility.FirebaseTools;
import com.dreamest.cookbookapp.utility.HideUI;
import com.dreamest.cookbookapp.utility.OnSwipeTouchListener;
import com.dreamest.cookbookapp.utility.UtilityPack;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.opensooq.supernova.gligar.GligarPicker;
import com.yalantis.ucrop.UCrop;

import java.io.File;

public class ProfileActivity extends BaseActivity {
    private TextView profile_TXT_username;
    private ImageView profile_IMG_image;
    private TextInputEditText profile_EDT_change_name;
    private MaterialButton profile_BTN_confirm_name;
    private TextView profile_TXT_count_recipes;
    private TextView profile_TXT_count_friends;
    private User currentUser;
    private RelativeLayout profile_LAY_master;
    private MaterialButton profile_BTN_sign_out;
    private ImageView profile_IMG_background;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        findViews();
        initViews();
        loadUser();
    }

    private void loadUser() {
        DatabaseReference ref = FirebaseDatabase.getInstance()
                .getReference(FirebaseTools.DATABASE_KEYS.USERS)
                .child(FirebaseAuth.getInstance().getUid());
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                currentUser = snapshot.getValue(User.class);
                visualizeUser();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w(UtilityPack.LOGS.FIREBASE_LOG, "Failed to read value.", error.toException());
            }
        });
    }

    private void visualizeUser() {
        profile_TXT_username.setText(currentUser.getDisplayName());
        FirebaseTools.downloadImage(ProfileActivity.this, currentUser.getImagePath(), currentUser.getUserID(),
                FirebaseTools.FILE_KEYS.JPG, profile_IMG_image, ContextCompat.getDrawable(this, R.drawable.ic_loading), R.drawable.ic_man_avatar);
        profile_TXT_count_recipes.setText(currentUser.getMyRecipes().size() + "");
        profile_TXT_count_friends.setText(currentUser.getMyFriends().size() + "");
    }

    private void initViews() {
        Glide
                .with(this)
                .load(R.drawable.background_simple_waves)
                .into(profile_IMG_background);
        profile_IMG_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changePhoto();
            }
        });

        profile_BTN_confirm_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmNameChange();
                HideUI.clearFocus(ProfileActivity.this, profile_EDT_change_name);
            }
        });

        profile_EDT_change_name.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    HideUI.clearFocus(ProfileActivity.this, profile_EDT_change_name);
                    confirmNameChange();
                }
                return false;
            }
        });

        profile_LAY_master.setOnTouchListener(new OnSwipeTouchListener(this) {
            public void onSwipeRight() {
                finish();
            }
        });


        profile_BTN_sign_out.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout();
            }
        });
    }

    private void logout() {
        FirebaseAdapterManager.getFirebaseAdapterManager().stopListeningAll();
        FirebaseAdapterManager.getFirebaseAdapterManager().destroy();

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.addAuthStateListener(new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() == null) {
                    Log.d(UtilityPack.LOGS.LOGIN_LOG, "sign out preformed. User = " + firebaseAuth.getCurrentUser());
                    Toast.makeText(ProfileActivity.this, R.string.logging_out, Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        });
        firebaseAuth.signOut();
    }

    private void findViews() {
        profile_TXT_username = findViewById(R.id.profile_TXT_username);
        profile_IMG_image = findViewById(R.id.profile_IMG_image);
        profile_EDT_change_name = findViewById(R.id.profile_EDT_change_name);
        profile_BTN_confirm_name = findViewById(R.id.profile_BTN_confirm_name);
        profile_TXT_count_recipes = findViewById(R.id.profile_TXT_count_recipes);
        profile_TXT_count_friends = findViewById(R.id.profile_TXT_count_friends);
        profile_LAY_master = findViewById(R.id.profile_LAY_master);
        profile_BTN_sign_out = findViewById(R.id.profile_BTN_sign_out);
        profile_IMG_background = findViewById(R.id.profile_IMG_background);
    }

    private void confirmNameChange() {
        if (!profile_EDT_change_name.getText().toString().trim().equals("")) {
            currentUser.setDisplayName(profile_EDT_change_name.getText().toString());
            profile_TXT_username.setText(currentUser.getDisplayName());
            FirebaseTools.storeUser(currentUser);
        } else {
            Toast.makeText(this, R.string.no_name, Toast.LENGTH_SHORT).show();
        }

    }

    private void changePhoto() {
        new GligarPicker().requestCode(UtilityPack.REQUEST_CODES.GILGAR).withActivity(this).limit(1).show();
        FirebaseTools.storeUser(currentUser);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != Activity.RESULT_OK) {
            return;
        }
        switch (requestCode) {
            case UtilityPack.REQUEST_CODES.GILGAR: { //Image picked
                File image = new File(data.getExtras().getStringArray(GligarPicker.IMAGES_RESULT)[0]);
                UtilityPack.cropImage(this, image, FirebaseAuth.getInstance().getCurrentUser().getUid());
                break;
            }

            case UtilityPack.REQUEST_CODES.UCROP: { //Image cropped
                String path = UCrop.getOutput(data).getPath();
                UtilityPack.loadUCropResult(this, path, profile_IMG_image, R.drawable.ic_man_avatar);
                FirebaseStorage storage = FirebaseStorage.getInstance();
                StorageReference storageReference = storage.getReference(FirebaseTools.STORAGE_KEYS.PROFILE_IMAGES).child(currentUser.getUserID());
                FirebaseTools.uploadImage(this, storageReference, path, false);
                break;
            }
        }
    }
}