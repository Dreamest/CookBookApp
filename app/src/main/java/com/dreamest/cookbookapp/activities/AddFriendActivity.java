package com.dreamest.cookbookapp.activities;

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
import com.dreamest.cookbookapp.utility.FirebaseTools;
import com.dreamest.cookbookapp.utility.HideUI;
import com.dreamest.cookbookapp.utility.MySharedPreferences;
import com.dreamest.cookbookapp.utility.UtilityPack;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
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

        loadUserLists();
        findViews();
        initViews();
    }

    private void loadUserLists() {
        Type listType = new TypeToken<ArrayList<User>>() {}.getType();
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
        if (!phoneNumber.trim().equals("")) {
            if (!duplicateNumber(phoneNumber)) {
                FirebaseTools.addUserToPending(this, phoneNumber, true);
            }
        }
    }



    private boolean duplicateNumber(String searchValue) {
        String currentUserPhoneNumber = FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber();
        if (searchValue.equals(currentUserPhoneNumber)) {
            notifyNotHappening(getString(R.string.cant_add_self));
            return true;
        } else {
            return userInList(currentFriends, searchValue, getString(R.string.already_friend)) ||
                    userInList(pendingFriends, searchValue, getString(R.string.already_pending));
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
                            FirebaseTools.addUserToPending(this, phoneNumber, true);
                        }
                        c.close();
                        numbers.close();
                    }
                }
            }
        }
    }
}
