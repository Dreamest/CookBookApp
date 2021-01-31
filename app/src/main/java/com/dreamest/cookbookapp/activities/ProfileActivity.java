package com.dreamest.cookbookapp.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.dreamest.cookbookapp.R;
import com.google.android.material.button.MaterialButton;

public class ProfileActivity extends AppCompatActivity {
    private TextView profile_TXT_username;
    private ImageView profile_IMG_image;
    private EditText profile_EDT_change_name;
    private MaterialButton profile_BTN_confirm_name;
    private TextView profile_TXT_count_recipes;
    private TextView profile_TXT_count_friends;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        findViews();
        initViews();
        updateViews();
    }

    private void updateViews() {
        // TODO: 1/31/21 implement
    }

    private void initViews() {
        profile_TXT_username = findViewById(R.id.profile_TXT_username);
        profile_IMG_image = findViewById(R.id.profile_IMG_image);
        profile_EDT_change_name = findViewById(R.id.profile_EDT_change_name);
        profile_BTN_confirm_name = findViewById(R.id.profile_BTN_confirm_name);
        profile_TXT_count_recipes = findViewById(R.id.profile_TXT_count_recipes);
        profile_TXT_count_friends = findViewById(R.id.profile_TXT_count_friends);
    }

    private void findViews() {
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

    }

    private void confirmNameChange() {
        // TODO: 1/31/21 implement
    }

    private void changePhoto() {
        // TODO: 1/31/21 implement
    }
}