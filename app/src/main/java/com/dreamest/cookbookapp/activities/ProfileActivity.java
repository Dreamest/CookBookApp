package com.dreamest.cookbookapp.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.dreamest.cookbookapp.R;
import com.dreamest.cookbookapp.logic.User;
import com.dreamest.cookbookapp.utility.HideUI;
import com.dreamest.cookbookapp.utility.MySharedPreferences;
import com.google.android.material.button.MaterialButton;

public class ProfileActivity extends BaseActivity {
    private TextView profile_TXT_username;
    private ImageView profile_IMG_image;
    private EditText profile_EDT_change_name;
    private MaterialButton profile_BTN_confirm_name;
    private TextView profile_TXT_count_recipes;
    private TextView profile_TXT_count_friends;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        findViews();
        initViews();
        updateViews();
    }

    private void updateViews() {
        user = (User) MySharedPreferences.getMsp().getObject(MySharedPreferences.KEYS.USER, new User());
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
            }
        });

        profile_EDT_change_name.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId == EditorInfo.IME_ACTION_DONE){
                    profile_EDT_change_name.clearFocus();
                    HideUI.hideSystemUI(ProfileActivity.this);

                }
                return false;
            }
        });

    }

    private void findViews() {
        profile_TXT_username = findViewById(R.id.profile_TXT_username);
        profile_IMG_image = findViewById(R.id.profile_IMG_image);
        profile_EDT_change_name = findViewById(R.id.profile_EDT_change_name);
        profile_BTN_confirm_name = findViewById(R.id.profile_BTN_confirm_name);
        profile_TXT_count_recipes = findViewById(R.id.profile_TXT_count_recipes);
        profile_TXT_count_friends = findViewById(R.id.profile_TXT_count_friends);
    }

    private void confirmNameChange() {
        user.setDisplayName(profile_EDT_change_name.getText().toString());
        profile_TXT_username.setText(user.getDisplayName());

        user.updateFirebase();
    }

    private void changePhoto() {
        // TODO: 1/31/21 implement

        user.updateFirebase();
    }
}