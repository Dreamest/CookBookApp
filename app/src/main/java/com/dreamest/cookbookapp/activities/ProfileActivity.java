package com.dreamest.cookbookapp.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.dreamest.cookbookapp.R;
import com.dreamest.cookbookapp.logic.User;
import com.dreamest.cookbookapp.utility.HideUI;
import com.dreamest.cookbookapp.utility.MySharedPreferences;
import com.dreamest.cookbookapp.utility.OnSwipeTouchListener;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;

public class ProfileActivity extends BaseActivity {
    private TextView profile_TXT_username;
    private ImageView profile_IMG_image;
    private EditText profile_EDT_change_name;
    private MaterialButton profile_BTN_confirm_name;
    private TextView profile_TXT_count_recipes;
    private TextView profile_TXT_count_friends;
    private User user;
    private RelativeLayout profile_LAY_master;
    private MaterialButton profile_BTN_sign_out;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        findViews();
        initViews();
        updateViews();
    }

    private void updateViews() {
        user = (User) MySharedPreferences.getMsp().getObject(MySharedPreferences.KEYS.USER, new User(), User.class);
        profile_TXT_username.setText(user.getDisplayName());
        Glide
                .with(this)
                .load(user.getProfileImage())
                .centerCrop()
                .into(profile_IMG_image)
                .onLoadStarted(getDrawable(R.drawable.ic_no_image));
        profile_TXT_count_recipes.setText(user.getMyRecipes().size() + "");
        profile_TXT_count_friends.setText(user.getMyFriends().size() + "");
    }

    private void initViews() {
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
                HideUI.clearFocus(ProfileActivity.this, profile_BTN_confirm_name);
            }
        });

        profile_EDT_change_name.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId == EditorInfo.IME_ACTION_DONE){
                    HideUI.clearFocus(ProfileActivity.this, profile_EDT_change_name);

                }
                return false;
            }
        });

        profile_LAY_master.setOnTouchListener(new OnSwipeTouchListener(this){
            public void onSwipeRight() {
                finish();
            }
        });

        //Swipes on the image don't work otherwise
        profile_IMG_image.setOnTouchListener(new OnSwipeTouchListener(this){
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
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.addAuthStateListener(new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if(firebaseAuth.getCurrentUser() == null) {
                    Log.d("dddd", "user now null");
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
    }

    private void confirmNameChange() {
        user.setDisplayName(profile_EDT_change_name.getText().toString());
        profile_TXT_username.setText(user.getDisplayName());

        user.updateFirebase();
    }

    private void changePhoto() {
        Toast.makeText(ProfileActivity.this, R.string.not_implemented, Toast.LENGTH_SHORT).show();

        // TODO: 1/31/21 implement

        user.updateFirebase();
    }
}