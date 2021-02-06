package com.dreamest.cookbookapp.activities;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
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
import com.google.gson.reflect.TypeToken;
import com.rilixtech.CountryCodePicker;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class AddFriendActivity extends BaseActivity {
    private CountryCodePicker add_friend_CCP_code_picker;
    private TextInputEditText add_friend_EDT_input;
    private MaterialButton add_friend_BTN_search;
    private MaterialButton add_friend_BTN_contacts;
    private ArrayList<User> currentFriends;
    private ArrayList<User> pendingFriends;

    private final int CONTACT_REQUEST_CODE = 99;
    private final int PERMISSION_REQUEST_CODE = 101;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friend);

        loadCurrentFriends();
        findViews();
        initViews();
    }

    private void loadCurrentFriends() {
        Type listType = new TypeToken<ArrayList<User>>() {
        }.getType();
        currentFriends = (ArrayList<User>) MySharedPreferences.getMsp().getObject(MySharedPreferences.KEYS.FRIENDSLIST_ARRAY, new ArrayList<User>(), listType);
        pendingFriends = (ArrayList<User>) MySharedPreferences.getMsp().getObject(MySharedPreferences.KEYS.PENDING_FRIENDS_ARRAY, new ArrayList<User>(), listType);
    }

    private void initViews() {
        add_friend_EDT_input.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    HideUI.clearFocus(AddFriendActivity.this, add_friend_EDT_input);
                    searchByNumber();
                }
                return false;
            }
        });

        add_friend_BTN_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchByNumber();
            }
        });

        add_friend_BTN_contacts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchByContact();
            }
        });
    }

    private void searchByContact() {
        ActivityCompat.requestPermissions(AddFriendActivity.this, new String[]{Manifest.permission.READ_CONTACTS}, PERMISSION_REQUEST_CODE);

        Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
        startActivityForResult(intent, CONTACT_REQUEST_CODE);
    }

    private void searchByNumber() {
        String phoneNumber = UtilityPack.extractPhoneNumber(add_friend_CCP_code_picker, add_friend_EDT_input);
        if (!phoneNumber.equals("")) {
            searchInFirebase(phoneNumber);
        }
    }

    private void searchInFirebase(String searchValue) {
        if (duplicateNumber(searchValue)) {
            return;
        }
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference(UtilityPack.KEYS.USERS);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot user : snapshot.getChildren()) {
                    if (user.child(UtilityPack.KEYS.PHONE_NUMBER).getValue(String.class).equals(searchValue)) {
                        String currentUserID = FirebaseAuth.getInstance().getUid();
                        String friendID = user.child(UtilityPack.KEYS.USER_ID).getValue(String.class);
                        long pendingRequests = user.child(UtilityPack.KEYS.PENDING_FRIENDS).getChildrenCount();
                        ref.child(friendID).child(UtilityPack.KEYS.PENDING_FRIENDS).child(currentUserID).setValue(currentUserID); //could've called User.actionToCurrentUser instead, but half the work has already been done here so just continuing.
                        Toast.makeText(AddFriendActivity.this, R.string.friend_request_send, Toast.LENGTH_SHORT).show();
                        finish();
                        return;
                    }
                }
                Toast.makeText(AddFriendActivity.this, R.string.user_not_in_database, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private boolean duplicateNumber(String searchValue) {
        String currentUserPhoneNumber = FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber();
        if (searchValue.equals(currentUserPhoneNumber)) {
            notifyNotHappening("You can't befriend yourself");
            return true;
        } else {
            return userInList(currentFriends, searchValue, "User is already a friend") ||
                    userInList(pendingFriends, searchValue, "User is pending");
        }
    }

    private boolean userInList(ArrayList<User> list, String searchValue, String message) {
        for (User friend : list) {
            if (friend.getPhoneNumber().equals(searchValue)) {
                notifyNotHappening(message);
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
        add_friend_BTN_contacts = findViewById(R.id.add_friend_BTN_contacts);
    }

    @Override
    public void onActivityResult(int reqCode, int resultCode, Intent data) {
        super.onActivityResult(reqCode, resultCode, data);
        if (reqCode == CONTACT_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                Uri contactData = data.getData();
                Cursor c = getContentResolver().query(contactData, null, null, null, null);
                if (c.moveToFirst()) {
                    String contactId = c.getString(c.getColumnIndex(ContactsContract.Contacts._ID));
                    String hasNumber = c.getString(c.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));
                    if (Integer.parseInt(hasNumber) == 1) {
                        Cursor numbers = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + contactId, null, null);
                        while (numbers.moveToNext()) {
                            String phoneNumber = numbers.getString(numbers.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                            searchInFirebase(phoneNumber);
                        }
                        c.close();
                        numbers.close();
                    }
                }
            }
        }
    }
}
