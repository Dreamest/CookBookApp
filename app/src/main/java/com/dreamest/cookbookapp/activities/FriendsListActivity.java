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
import android.widget.Toast;

import com.dreamest.cookbookapp.R;
import com.dreamest.cookbookapp.adapters.FriendAdapter;
import com.dreamest.cookbookapp.logic.Recipe;
import com.dreamest.cookbookapp.logic.User;
import com.dreamest.cookbookapp.utility.MySharedPreferences;
import com.dreamest.cookbookapp.utility.OnSwipeTouchListener;
import com.dreamest.cookbookapp.utility.UtilityPack;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class FriendsListActivity extends BaseActivity {
    // TODO: 2/2/21 This activity has not been tested with actual friends
    private RecyclerView friendslist_LST_friends;
    private ImageButton friendslist_BTN_add_friend;
    private TextView friends_TXT_no_friends;
    private RelativeLayout friendslist_LAY_master;
    private MaterialButton friendslist_BTN_pending;
    private ArrayList<User> friendslist;
    private User currentUser;
    private ArrayList<User> pendingFriends;


    private final int PENDING_FRIENDS = 1235;
    private final int MY_FRIENDS = 3456;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends_list);

        pendingFriends = new ArrayList<>();
        friendslist = new ArrayList<>();

        findViews();
        initViews();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadCurrentUser();
        loadFriendsFromDatabase();
        loadPendingFriends();
    }

    private void loadPendingFriends() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference(UtilityPack.KEYS.USERS)
                .child(currentUser.getUserID())
                .child(UtilityPack.KEYS.PENDING_FRIENDS);
        ref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                loadUserFromDatabase(snapshot, database, PENDING_FRIENDS);
                String message = getString(R.string.you_have) + " " + pendingFriends.size() + " " + getString(R.string.pending_recipes);
                friendslist_BTN_pending.setVisibility(View.VISIBLE);
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                removePending(snapshot);
                if (pendingFriends.size() == 0) {
                    friendslist_BTN_pending.setVisibility(View.GONE);
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w("dddd", "Failed to read value.", error.toException());
            }
        });
    }

    private void removePending(DataSnapshot id) {
        for (User user : pendingFriends) {
            if (user.getUserID().equals(id.getValue())) {
                pendingFriends.remove(user);
                return;
            }
        }
    }

    private void loadFriendsFromDatabase() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database
                .getReference(UtilityPack.KEYS.USERS)
                .child(currentUser.getUserID())
                .child(UtilityPack.KEYS.MY_FRIENDS);

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    friends_TXT_no_friends.setVisibility(View.GONE);
                    Iterable<DataSnapshot> friendsIDs = snapshot.getChildren();
                    for (DataSnapshot id : friendsIDs) {
                        loadUserFromDatabase(id, database, MY_FRIENDS);
                    }
                } else {
                    friends_TXT_no_friends.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w("dddd", "Failed to read value.", error.toException());
            }
        });
    }


    private void loadUserFromDatabase(DataSnapshot id, FirebaseDatabase database, int loadTo) {
        DatabaseReference friendRef = database.getReference(UtilityPack.KEYS.USERS)
                .child(id.getValue(String.class));
        friendRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User friend = snapshot.getValue(User.class);

                Log.d("dddd", "The name is: " + friend.getDisplayName());
                if (loadTo == MY_FRIENDS) {
                    friendslist.add(friend);
                    initAdapter();
                } else if (loadTo == PENDING_FRIENDS) {
                    pendingFriends.add(friend);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w("dddd", "Failed to read value.", error.toException());
            }
        });
    }

    private void loadCurrentUser() {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference(UtilityPack.KEYS.USERS).child(uid);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                currentUser = snapshot.getValue(User.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w("dddd", "Failed to read value.", error.toException());
            }
        });
    }

    private void initViews() {
        initAdapter();

        friendslist_BTN_add_friend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addNewFriend();
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
    }

    private void addNewFriend() {
        Intent myIntent = new Intent(this, AddFriendActivity.class);
        MySharedPreferences.getMsp().putObject(MySharedPreferences.KEYS.FRIENDS_ARRAY, friendslist);
        startActivity(myIntent);
    }

    private void findViews() {
        friendslist_LST_friends = findViewById(R.id.friendslist_LST_friends);
        friendslist_BTN_add_friend = findViewById(R.id.friendslist_BTN_add_friend);
        friends_TXT_no_friends = findViewById(R.id.friends_TXT_no_friends);
        friendslist_LAY_master = findViewById(R.id.friendslist_LAY_master);
        friendslist_BTN_pending = findViewById(R.id.friendslist_BTN_pending);
    }

    private void initAdapter() {
        friendslist_LST_friends.setLayoutManager(new LinearLayoutManager(this));
        FriendAdapter friendAdapter = new FriendAdapter(this, friendslist);

        friendAdapter.setClickListener(new FriendAdapter.ItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                openChatWith(position);
            }
        });
        friendslist_LST_friends.setAdapter(friendAdapter);
    }

    private void openChatWith(int position) {
        Toast.makeText(FriendsListActivity.this, "Not implemented yet.", Toast.LENGTH_SHORT).show();
        // TODO: 2/2/21 implement
    }
}