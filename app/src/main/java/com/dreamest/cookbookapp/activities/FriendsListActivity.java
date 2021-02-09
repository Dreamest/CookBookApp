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
import com.dreamest.cookbookapp.utility.FirebaseTools;
import com.dreamest.cookbookapp.utility.MySharedPreferences;
import com.dreamest.cookbookapp.utility.OnSwipeTouchListener;
import com.firebase.ui.database.FirebaseRecyclerOptions;
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
    private TextView friends_TXT_no_friends;
    private RelativeLayout friendslist_LAY_master;
    private MaterialButton friendslist_BTN_pending;
    private ArrayList<User> pendingFriends;
    private ArrayList<User> friendslist;
    private FriendFirebaseAdapter adapter;
    private final int PENDING_FRIENDS = 1;
    private final int FRIENDSLIST = 2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends_list);

        findViews();
        initViews();
        initAdapter();
    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();

        // TODO: 2/9/21 Observer untested
        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() { // TODO: 2/8/21 test
            @Override
            public void onChanged() {
                super.onChanged();
                if (adapter.getItemCount() > 0) {
                    friends_TXT_no_friends.setVisibility(View.GONE);
                } else {
                    friends_TXT_no_friends.setVisibility(View.VISIBLE);
                }
            }
        });
        adapter.notifyItemRangeRemoved(0, adapter.getItemCount());
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    @Override
    protected void onResume() {
        super.onResume();
        pendingFriends = new ArrayList<>();
        loadPendingFriends();
    }

    private void initAdapter() {
        friendslist_LST_friends.setLayoutManager(new LinearLayoutManager(this));

        DatabaseReference friendslistRoot = FirebaseDatabase.getInstance()
                .getReference(FirebaseTools.DATABASE_KEYS.USERS)
                .child(FirebaseAuth.getInstance().getUid())
                .child(FirebaseTools.DATABASE_KEYS.MY_FRIENDS);

        FirebaseRecyclerOptions<String> options
                = new FirebaseRecyclerOptions.Builder<String>()
                .setQuery(friendslistRoot, String.class)
                .build();
        adapter = new FriendFirebaseAdapter(options);

        adapter.setClickListener(new FriendFirebaseAdapter.ItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                openChatWith(adapter.getItem(position));
            }
        });
        friendslist_LST_friends.setAdapter(adapter);
    }

    private void loadPendingFriends() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference(FirebaseTools.DATABASE_KEYS.USERS)
                .child(FirebaseAuth.getInstance().getUid())
                .child(FirebaseTools.DATABASE_KEYS.PENDING_FRIENDS);
        ref.addChildEventListener(new ChildEventListener() { //using ChildListener so in case a pending request happens, we'll be notified.
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                loadFriend(snapshot, database, PENDING_FRIENDS, false);
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                removePending(snapshot.getValue(String.class));
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

    private void removePending(String id) {
        for (User user : pendingFriends) {
            if (user.getUserID().equals(id)) {
                pendingFriends.remove(user);
                return;
            }
        }
    }

    private void loadFriend(DataSnapshot id, FirebaseDatabase database, int loadTo, boolean last) {
        DatabaseReference friendRef = database.getReference(FirebaseTools.DATABASE_KEYS.USERS)
                .child(id.getValue(String.class));
        friendRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User friend = snapshot.getValue(User.class);
                if (loadTo == FRIENDSLIST) {
                    friendslist.add(friend);
                    if(last) {
                        Intent myIntent = new Intent(FriendsListActivity.this, AddFriendActivity.class);
                        MySharedPreferences.getMsp().putObject(MySharedPreferences.KEYS.FRIENDSLIST_ARRAY, friendslist);
                        MySharedPreferences.getMsp().putObject(MySharedPreferences.KEYS.PENDING_FRIENDS_ARRAY, pendingFriends);
                        startActivity(myIntent);
                    }
                } else if (loadTo == PENDING_FRIENDS) {
                    pendingFriends.add(friend);
                    String message = getString(R.string.you_have) + " " + pendingFriends.size() + " " + getString(R.string.pending_friends);
                    friendslist_BTN_pending.setText(message);
                    friendslist_BTN_pending.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w("dddd", "Failed to read value.", error.toException());
            }
        });
    }

    private void initViews() {
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
        Intent myIntent = new Intent(this, PendingFriendsActivity.class);
        startActivity(myIntent);
    }

    private void addNewFriend() {
        friendslist = new ArrayList<>();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database
                .getReference(FirebaseTools.DATABASE_KEYS.USERS)
                .child(FirebaseAuth.getInstance().getUid())
                .child(FirebaseTools.DATABASE_KEYS.MY_FRIENDS);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot friend : snapshot.getChildren()) {
                    loadFriend(friend, database, FRIENDSLIST, true);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void findViews() {
        friendslist_LST_friends = findViewById(R.id.friendslist_LST_friends);
        friendslist_BTN_add_friend = findViewById(R.id.friendslist_BTN_add_friend);
        friends_TXT_no_friends = findViewById(R.id.friends_TXT_no_friends);
        friendslist_LAY_master = findViewById(R.id.friendslist_LAY_master);
        friendslist_BTN_pending = findViewById(R.id.friendslist_BTN_pending);
    }


    private void openChatWith(String friendID) {
        MySharedPreferences.getMsp().putString(MySharedPreferences.KEYS.USER_ID, friendID);
        Intent myIntent = new Intent(this, ChatActivity.class);
        startActivity(myIntent);
    }
}