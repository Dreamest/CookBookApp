package com.dreamest.cookbookapp.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dreamest.cookbookapp.R;
import com.dreamest.cookbookapp.adapters.FriendFirebaseAdapter;
import com.dreamest.cookbookapp.logic.User;
import com.dreamest.cookbookapp.utility.FirebaseListener;
import com.dreamest.cookbookapp.utility.FirebaseTools;
import com.dreamest.cookbookapp.utility.MySharedPreferences;
import com.dreamest.cookbookapp.utility.OnSwipeTouchListener;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class FriendsListActivity extends BaseActivity {
    private RecyclerView friendslist_LST_friends;
    private ImageButton friendslist_BTN_add_friend;
    private RelativeLayout friendslist_LAY_master;
    private MaterialButton friendslist_BTN_pending;
    private TextView friendslist_TXT_no_friends;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends_list);

        findViews();
        initViews();
        bindAdapter();
        observePendingFriends();
        observeCurrentFriends();
    }

    private void observeCurrentFriends() {
        FirebaseListener.getFirebaseListener().getFriendFirebaseAdapter().registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                friendslist_TXT_no_friends.setVisibility(View.GONE);
                super.onItemRangeInserted(positionStart, itemCount);
            }

            @Override
            public void onItemRangeRemoved(int positionStart, int itemCount) {
                int friendslistSize = FirebaseListener.getFirebaseListener().getFriendFirebaseAdapter().getItemCount();
                if(friendslistSize == 0) {
                    friendslist_TXT_no_friends.setVisibility(View.VISIBLE);
                }
                super.onItemRangeRemoved(positionStart, itemCount);
            }
        });
    }

    private void bindAdapter() {
        friendslist_LST_friends.setLayoutManager(new LinearLayoutManager(this));

        FirebaseListener.getFirebaseListener().getFriendFirebaseAdapter().setClickListener(new FriendFirebaseAdapter.ItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                openChatWith(FirebaseListener.getFirebaseListener().getFriendFirebaseAdapter().getItem(position));
            }
        });
        friendslist_LST_friends.setAdapter(FirebaseListener.getFirebaseListener().getFriendFirebaseAdapter());
    }

    private void observePendingFriends() {
        FirebaseListener.getFirebaseListener().getPendingFriendsFirebaseAdapter().registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                int pendingSize = FirebaseListener.getFirebaseListener().getPendingFriendsFirebaseAdapter().getItemCount();
                String message = getString(R.string.you_have) + " " + pendingSize + " " + getString(R.string.pending_friends);
                friendslist_BTN_pending.setText(message);
                friendslist_BTN_pending.setVisibility(View.VISIBLE);

                super.onItemRangeInserted(positionStart, itemCount);
            }

            @Override
            public void onItemRangeRemoved(int positionStart, int itemCount) {
                int pendingSize = FirebaseListener.getFirebaseListener().getPendingFriendsFirebaseAdapter().getItemCount();
                if(pendingSize == 0) {
                    friendslist_BTN_pending.setVisibility(View.GONE);
                }
                super.onItemRangeRemoved(positionStart, itemCount);
            }
        });
    }

    private void startAddFriend() {
        Intent myIntent = new Intent(FriendsListActivity.this, AddFriendActivity.class);
        startActivity(myIntent);
    }

    private void initViews() {
        friendslist_BTN_add_friend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startAddFriend();
            }
        });
        friendslist_LST_friends.setOnTouchListener(new OnSwipeTouchListener(this) {
            public void onSwipeLeft() {
                finish();
            }

        });

        friendslist_LAY_master.setOnTouchListener(new OnSwipeTouchListener(this) {
            public void onSwipeLeft() {
                finish();
            }
        });

        friendslist_BTN_pending.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pendingFriends();
            }
        });
    }

    private void pendingFriends() {
        Intent myIntent = new Intent(this, PendingFriendsActivity.class);
        startActivity(myIntent);
    }


    private void findViews() {
        friendslist_LST_friends = findViewById(R.id.friendslist_LST_friends);
        friendslist_BTN_add_friend = findViewById(R.id.friendslist_BTN_add_friend);
        friendslist_LAY_master = findViewById(R.id.friendslist_LAY_master);
        friendslist_BTN_pending = findViewById(R.id.friendslist_BTN_pending);
        friendslist_TXT_no_friends = findViewById(R.id.friendslist_TXT_no_friends);
    }


    private void openChatWith(String friendID) {
        MySharedPreferences.getMsp().putString(MySharedPreferences.KEYS.USER_ID, friendID);
        Intent myIntent = new Intent(this, ChatActivity.class);
        startActivity(myIntent);
    }
}