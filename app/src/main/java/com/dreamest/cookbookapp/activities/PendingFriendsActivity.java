package com.dreamest.cookbookapp.activities;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.Toast;

import com.dreamest.cookbookapp.R;
import com.dreamest.cookbookapp.adapters.PendingFriendsFirebaseAdapter;
import com.dreamest.cookbookapp.utility.FirebaseTools;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class PendingFriendsActivity extends BaseActivity {
    // TODO: 2/9/21 test new adapter
    private RecyclerView pending_friend_LST_recipes;
    private PendingFriendsFirebaseAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pending_friends);
        findViews();
        initAdapter();
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
        pending_friend_LST_recipes.setLayoutManager(new LinearLayoutManager(this));

        DatabaseReference pendingFriendsRoot = FirebaseDatabase.getInstance()
                .getReference(FirebaseTools.DATABASE_KEYS.USERS)
                .child(FirebaseAuth.getInstance().getUid())
                .child(FirebaseTools.DATABASE_KEYS.PENDING_FRIENDS);

        FirebaseRecyclerOptions<String> options
                = new FirebaseRecyclerOptions.Builder<String>()
                .setQuery(pendingFriendsRoot, String.class)
                .build();
        adapter = new PendingFriendsFirebaseAdapter(options);

        adapter.setClickListener(new PendingFriendsFirebaseAdapter.ItemClickListener() {
            @Override
            public void onAddClick(int position) {
                String friendID = adapter.getItem(position);
                createFriendship(friendID);
            }

            @Override
            public void onRemoveClick(int position) {
                String friendID = adapter.getItem(position);
                ignoreFriendRequest(friendID);

            }
        });
        pending_friend_LST_recipes.setAdapter(adapter);
    }

    private void ignoreFriendRequest(String friendID) {
        FirebaseTools.actionToCurrentUserDatabase(FirebaseTools.REMOVE, friendID, FirebaseTools.DATABASE_KEYS.PENDING_FRIENDS); //removes them from my pending list
        Toast.makeText(PendingFriendsActivity.this, R.string.request_removed, Toast.LENGTH_SHORT).show();
    }

    private void createFriendship(String friendID) {
        String myID = FirebaseAuth.getInstance().getUid();

        FirebaseTools.actionToCurrentUserDatabase(FirebaseTools.ADD, friendID, FirebaseTools.DATABASE_KEYS.MY_FRIENDS); //adds them to my friendslist

        DatabaseReference ref = FirebaseDatabase.getInstance()
                .getReference(FirebaseTools.DATABASE_KEYS.USERS)
                .child(friendID)
                .child(FirebaseTools.DATABASE_KEYS.MY_FRIENDS)
                .child(myID);
        ref.setValue(myID); //adds me to their friendslist

        FirebaseTools.actionToCurrentUserDatabase(FirebaseTools.REMOVE, friendID, FirebaseTools.DATABASE_KEYS.PENDING_FRIENDS); //removes them from my pending list
        Toast.makeText(PendingFriendsActivity.this, R.string.friend_added, Toast.LENGTH_SHORT).show();
    }

    private void findViews() {
        pending_friend_LST_recipes = findViewById(R.id.pending_friend_LST_recipes);
    }
}