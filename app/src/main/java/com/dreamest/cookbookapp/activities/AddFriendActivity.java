package com.dreamest.cookbookapp.activities;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.bumptech.glide.Glide;
import com.dreamest.cookbookapp.R;
import com.dreamest.cookbookapp.adapters.FirebaseAdapterManager;
import com.dreamest.cookbookapp.utility.FirebaseTools;
import com.dreamest.cookbookapp.utility.HideUI;
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

public class AddFriendActivity extends BaseActivity {
    private CountryCodePicker add_friend_CCP_code_picker;
    private TextInputEditText add_friend_EDT_input;
    private MaterialButton add_friend_BTN_search;
    private MaterialButton add_friend_BTN_contacts;
    private ProgressBar add_friend_PROGBAR_spinner;
    private ImageView add_friend_IMG_background;
    private ArrayList<String> currentFriends;
    private ArrayList<String> pendingFriends;

    private final int CONTACT_REQUEST_CODE = 99;
    private final int PERMISSION_REQUEST_CODE = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friend);
        findViews();
        initViews();
        loadUserLists();
    }

    /**
     * Loads friends and pending friends lists from the database for easy access. While loading, it'll show a spinner instead of the interface.
     */
    private void loadUserLists() {
        showSpinner();
        int friendsCount = FirebaseAdapterManager.getFirebaseAdapterManager().getFriendFirebaseAdapter().getItemCount();
        int pendingFriendsCount = FirebaseAdapterManager.getFirebaseAdapterManager().getPendingFriendsFirebaseAdapter().getItemCount();
        currentFriends = new ArrayList<>();
        pendingFriends = new ArrayList<>();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference(FirebaseTools.DATABASE_KEYS.USERS);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (int i = 0; i < friendsCount; i++) {
                    String friendID = FirebaseAdapterManager.getFirebaseAdapterManager().getFriendFirebaseAdapter().getItem(i);
                    currentFriends.add(snapshot.child(friendID).child(FirebaseTools.DATABASE_KEYS.PHONE_NUMBER).getValue(String.class));
                }
                for (int i = 0; i < pendingFriendsCount; i++) {
                    String pendingID = FirebaseAdapterManager.getFirebaseAdapterManager().getPendingFriendsFirebaseAdapter().getItem(i);
                    pendingFriends.add(snapshot.child(pendingID).child(FirebaseTools.DATABASE_KEYS.PHONE_NUMBER).getValue(String.class));
                }
                hideSpinner();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w(UtilityPack.LOGS.FIREBASE_LOG, "Failed to read value.", error.toException());
            }
        });
    }

    /**
     * Shows spinner, hides interface
     */
    private void showSpinner() {
        add_friend_PROGBAR_spinner.setVisibility(View.VISIBLE);
        add_friend_CCP_code_picker.setVisibility(View.GONE);
        add_friend_EDT_input.setVisibility(View.GONE);
        add_friend_BTN_search.setVisibility(View.GONE);
        add_friend_BTN_contacts.setVisibility(View.GONE);

    }

    /**
     * Hides spinner, shows interface
     */
    private void hideSpinner() {
        add_friend_PROGBAR_spinner.setVisibility(View.GONE);
        add_friend_CCP_code_picker.setVisibility(View.VISIBLE);
        add_friend_EDT_input.setVisibility(View.VISIBLE);
        add_friend_BTN_search.setVisibility(View.VISIBLE);
        add_friend_BTN_contacts.setVisibility(View.VISIBLE);
    }

    private void initViews() {
        Glide
                .with(this)
                .load(R.drawable.background_simple_waves)
                .into(add_friend_IMG_background);
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

    /**
     * searches if given searchValue already exists in pending and current friendslists.
     *
     * @param searchValue phone number if new friend
     * @return if the number is duplicate or not
     */
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

    private boolean userInList(ArrayList<String> list, String searchValue, String message) {
        for (String friendPhoneNumber : list) {
            if (friendPhoneNumber.equals(searchValue)) {
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
        add_friend_PROGBAR_spinner = findViewById(R.id.add_friend_PROGBAR_spinner);
        add_friend_IMG_background= findViewById(R.id.add_friend_IMG_background);
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
                            if (!duplicateNumber(phoneNumber)) {
                                FirebaseTools.addUserToPending(this, phoneNumber, true);
                            }
                        }
                        c.close();
                        numbers.close();
                    }
                }
            }
        }
    }
}
