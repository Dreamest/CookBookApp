package com.dreamest.cookbookapp.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;
import android.widget.Toast;

import com.dreamest.cookbookapp.R;
import com.dreamest.cookbookapp.logic.User;
import com.dreamest.cookbookapp.utility.HideUI;
import com.dreamest.cookbookapp.utility.MySharedPreferences;
import com.dreamest.cookbookapp.utility.UtilityPack;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rilixtech.CountryCodePicker;

import java.util.ArrayList;

public class AddFriendActivity extends AppCompatActivity {
    private CountryCodePicker add_friend_CCP_code_picker;
    private TextInputEditText add_friend_EDT_input;
    private MaterialButton add_friend_BTN_search;
    private ArrayList<User> currentFriends;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friend);

        loadCurrentFriends();
        findViews();
        initViews();
    }

    private void loadCurrentFriends() {
        currentFriends = (ArrayList<User>) MySharedPreferences.getMsp().getObject(MySharedPreferences.KEYS.FRIENDS_ARRAY, new ArrayList<>());
    }

    private void initViews() {
        add_friend_EDT_input.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    HideUI.clearFocus(AddFriendActivity.this, add_friend_EDT_input);
                    search();
                }
                return false;
            }
        });

        add_friend_BTN_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                search();
            }
        });
    }

    private void search() {
        String searchValue = UtilityPack.extractPhoneNumber(add_friend_CCP_code_picker, add_friend_EDT_input);
        if(duplicateNumber(searchValue)) {
            return;
        }
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference(UtilityPack.KEYS.USERS);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot user: snapshot.getChildren()) {

                    if(user.child(UtilityPack.KEYS.PHONE_NUMBER).getValue(String.class).equals(searchValue)) {
                        User.addFriendToCurrentUserDatabase(user.child(UtilityPack.KEYS.USER_ID).getValue(String.class), currentFriends.size());
                        String userName = user.child(UtilityPack.KEYS.DISPLAY_NAME).getValue(String.class);
                        Toast.makeText(AddFriendActivity.this, "User " + userName + " added.", Toast.LENGTH_SHORT).show();
                        finish();
                        return;
                    }
                }
                Toast.makeText(AddFriendActivity.this, "User not in database", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private boolean duplicateNumber(String searchValue) { // TODO: 2/3/21 untested.
        String currentUserPhoneNumber = FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber();
        if(searchValue.equals(currentUserPhoneNumber)) {
            notifyNotHappening("You can't befriend yourself");
            return true;
        } else{
            return (userInFriendsAlready(searchValue));
        }
    }

    private boolean userInFriendsAlready(String searchValue) {
        for(User friend: currentFriends) {
            if(friend.getPhoneNumber().equals(searchValue)) {
                notifyNotHappening("User is already a friend");
                return true;
            }
        }
        return false;
    }

    private void notifyNotHappening(String s) {
        Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
        add_friend_EDT_input.setText("");

    }

    private void findViews() {
        add_friend_CCP_code_picker = findViewById(R.id.add_friend_CCP_code_picker);
        add_friend_EDT_input = findViewById(R.id.add_friend_EDT_input);
        add_friend_BTN_search = findViewById(R.id.add_friend_BTN_search);
    }
}