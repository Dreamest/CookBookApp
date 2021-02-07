package com.dreamest.cookbookapp.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.dreamest.cookbookapp.R;
import com.dreamest.cookbookapp.utility.UtilityPack;
import com.google.android.material.button.MaterialButton;
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
    private ArrayList<String> chatKeys;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        findViews();
        initViews();
    }

    private void initViews() {
        chat_BTN_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
            }
        });
    }

    private void sendMessage() {
        String message = chat_EDT_input.getText().toString();
        if(message.trim().equals("")) {
            return;
        }

    }

    private void findViews() {
        chat_TXT_other_person = findViewById(R.id.chat_TXT_other_person);
        chat_LST_messages = findViewById(R.id.chat_LST_messages);
        chat_BTN_send = findViewById(R.id.chat_BTN_send);
        chat_EDT_input = findViewById(R.id.chat_EDT_input);
    }
}