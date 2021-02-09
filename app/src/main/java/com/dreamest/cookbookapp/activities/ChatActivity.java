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
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.dreamest.cookbookapp.R;
import com.dreamest.cookbookapp.adapters.ChatFirebaseAdapter;
import com.dreamest.cookbookapp.logic.ChatMessage;
import com.dreamest.cookbookapp.logic.User;
import com.dreamest.cookbookapp.utility.FirebaseTools;
import com.dreamest.cookbookapp.utility.MySharedPreferences;
import com.dreamest.cookbookapp.utility.UtilityPack;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class ChatActivity extends BaseActivity {
    private TextView chat_TXT_other_person;
    private RecyclerView chat_LST_messages;
    private MaterialButton chat_BTN_send;
    private EditText chat_EDT_input;
    private ProgressBar chat_PROGBAR_spinner;

    private ChatFirebaseAdapter adapter;
    private String chatKey;
    private User currentUser;
    private User friend;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        String friendID = MySharedPreferences.getMsp().getString(MySharedPreferences.KEYS.USER_ID, null);
        chatKey = FirebaseTools.createChatKey(FirebaseAuth.getInstance().getUid(), friendID);
        loadUsers(friendID);
        findViews();
        initViews();
        initAdapter();
        chat_LST_messages.setVisibility(View.GONE);
        chat_PROGBAR_spinner.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    private void initAdapter() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);

        chat_LST_messages.setLayoutManager(linearLayoutManager);

        DatabaseReference chatRoot = FirebaseDatabase.getInstance()
                .getReference(UtilityPack.KEYS.CHATS)
                .child(chatKey);

        FirebaseRecyclerOptions<ChatMessage> options = new FirebaseRecyclerOptions.Builder<ChatMessage>()
                .setQuery(chatRoot, ChatMessage.class)
                .build();
        adapter = new ChatFirebaseAdapter(options);

        chat_LST_messages.setAdapter(adapter);
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
                .setSenderID(FirebaseAuth.getInstance().getUid())
                .setSenderName(currentUser.getDisplayName())
                .setText(message)
                .setTimestamp(timestamp);

        FirebaseTools.uploadMessage(chatMessage, chatKey, timestamp, currentUser.getUserID());
        chat_LST_messages.smoothScrollToPosition(adapter.getItemCount());
    }

    private void findViews() {
        chat_TXT_other_person = findViewById(R.id.chat_TXT_other_person);
        chat_LST_messages = findViewById(R.id.chat_LST_messages);
        chat_BTN_send = findViewById(R.id.chat_BTN_send);
        chat_EDT_input = findViewById(R.id.chat_EDT_input);
        chat_PROGBAR_spinner = findViewById(R.id.chat_PROGBAR_spinner);
    }

    private void loadUsers(String friendID) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference(UtilityPack.KEYS.USERS);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String myID = FirebaseAuth.getInstance().getCurrentUser().getUid();
                currentUser = snapshot.child(myID).getValue(User.class);
                friend = snapshot.child(friendID).getValue(User.class);
                chat_TXT_other_person.setText(friend.getDisplayName());
                chat_PROGBAR_spinner.setVisibility(View.GONE);
                chat_LST_messages.setVisibility(View.VISIBLE);

            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w("dddd", "Failed to read value.", error.toException());
            }
        });
    }
}