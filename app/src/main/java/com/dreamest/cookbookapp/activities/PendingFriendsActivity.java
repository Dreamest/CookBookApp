package com.dreamest.cookbookapp.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.Toast;

import com.dreamest.cookbookapp.R;
import com.dreamest.cookbookapp.adapters.PendingFriendsFirebaseAdapter;
import com.dreamest.cookbookapp.logic.User;
import com.dreamest.cookbookapp.utility.UtilityPack;
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
//        initAdapter();
    }

    private void initAdapter() {
        pending_friend_LST_recipes.setLayoutManager(new LinearLayoutManager(this));

        DatabaseReference pendingFriendsRoot = FirebaseDatabase.getInstance()
                .getReference(UtilityPack.KEYS.USERS)
                .child(FirebaseAuth.getInstance().getUid())
                .child(UtilityPack.KEYS.PENDING_FRIENDS);

        FirebaseRecyclerOptions<String> options
                = new FirebaseRecyclerOptions.Builder<String>()
                .setQuery(pendingFriendsRoot, String.class)
                .build();
        adapter = new PendingFriendsFirebaseAdapter(options);

        adapter.setClickListener(new PendingFriendsFirebaseAdapter.ItemClickListener() {
            @Override
            public void onAddClick(int position) {
                String friendID = adapter.getItem(position);
                addFriend(friendID);
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
        User.actionToCurrentUserDatabase(User.REMOVE, friendID, UtilityPack.KEYS.PENDING_FRIENDS); //removes them from my pending list
        Toast.makeText(PendingFriendsActivity.this, R.string.request_removed, Toast.LENGTH_SHORT).show();
    }

    private void addFriend(String friendID) {
        String myID = FirebaseAuth.getInstance().getUid();

        User.actionToCurrentUserDatabase(User.ADD, friendID, UtilityPack.KEYS.MY_FRIENDS); //adds them to my friendslist

        DatabaseReference ref = FirebaseDatabase.getInstance()
                .getReference(UtilityPack.KEYS.USERS)
                .child(friendID)
                .child(UtilityPack.KEYS.MY_FRIENDS)
                .child(myID);
        ref.setValue(myID); //adds me to their friendslist

        User.actionToCurrentUserDatabase(User.REMOVE, friendID, UtilityPack.KEYS.PENDING_FRIENDS); //removes them from my pending list
        Toast.makeText(PendingFriendsActivity.this, R.string.friend_added, Toast.LENGTH_SHORT).show();
    }

    private void findViews() {
        pending_friend_LST_recipes = findViewById(R.id.pending_friend_LST_recipes);
    }
}