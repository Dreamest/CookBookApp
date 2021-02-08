package com.dreamest.cookbookapp.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.dreamest.cookbookapp.R;
import com.dreamest.cookbookapp.adapters.ChatAdapter;
import com.dreamest.cookbookapp.logic.ChatMessage;
import com.dreamest.cookbookapp.logic.User;
import com.dreamest.cookbookapp.utility.FirebaseTools;
import com.dreamest.cookbookapp.utility.MySharedPreferences;
import com.dreamest.cookbookapp.utility.UtilityPack;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Iterator;

public class ChatActivity extends AppCompatActivity {
    private TextView chat_TXT_other_person;
    private RecyclerView chat_LST_messages;
    private MaterialButton chat_BTN_send;
    private EditText chat_EDT_input;
    private ArrayList<String> timestamps;
    private String chatKey;
    private User currentUser;
    private User friend;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        timestamps = new ArrayList<>();
        loadUsers();
        findViews();
        initViews();

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

    }

    private void readMessages() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference(UtilityPack.KEYS.CHATS).child(chatKey);
        ref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                timestamps.add(snapshot.getKey());
                initAdapter();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void initAdapter() {

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true); //ensures we stay at the bottom on updates
        chat_LST_messages.setLayoutManager(linearLayoutManager);

        ChatAdapter chatAdapter = new ChatAdapter(this, timestamps, chatKey);

        chatAdapter.setClickListener(new ChatAdapter.ItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
            }
        });
        chat_LST_messages.setAdapter(chatAdapter);
    }

    private void initViews() {
        chat_BTN_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
            }
        });

        chat_EDT_input.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    sendMessage();
                }
                return false;
            }
        });    }

    private void sendMessage() {
        String message = chat_EDT_input.getText().toString();
        if(message.trim().equals("")) {
            return;
        }
        chat_EDT_input.setText("");
        long timestamp = System.currentTimeMillis();
        ChatMessage chatMessage= new ChatMessage()
                .setSenderID(currentUser.getUserID())
                .setSenderName(currentUser.getDisplayName())
                .setText(message)
                .setTimestamp(timestamp);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database
                .getReference(UtilityPack.KEYS.CHATS)
                .child(chatKey)
                .child(String.valueOf(timestamp));
        ref.setValue(chatMessage);
    }

    private void findViews() {
        chat_TXT_other_person = findViewById(R.id.chat_TXT_other_person);
        chat_LST_messages = findViewById(R.id.chat_LST_messages);
        chat_BTN_send = findViewById(R.id.chat_BTN_send);
        chat_EDT_input = findViewById(R.id.chat_EDT_input);
    }

    private void loadUsers() {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference(UtilityPack.KEYS.USERS).child(uid);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                currentUser = snapshot.getValue(User.class);
                friend = (User) MySharedPreferences.getMsp().getObject(MySharedPreferences.KEYS.USER, new User(), User.class);
                chatKey = FirebaseTools.createChatKey(currentUser.getUserID(), friend.getUserID());
                chat_TXT_other_person.setText(friend.getDisplayName());
                readMessages();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w("dddd", "Failed to read value.", error.toException());
            }
        });
    }
}