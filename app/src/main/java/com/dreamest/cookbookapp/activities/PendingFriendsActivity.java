package com.dreamest.cookbookapp.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.Toast;

import com.dreamest.cookbookapp.R;
import com.dreamest.cookbookapp.adapters.PendingFriendAdapter;
import com.dreamest.cookbookapp.logic.User;
import com.dreamest.cookbookapp.utility.MySharedPreferences;
import com.dreamest.cookbookapp.utility.UtilityPack;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

public class PendingFriendsActivity extends AppCompatActivity {
    // TODO: 2/4/21 untested. 
    private RecyclerView pending_friend_LST_recipes;
    private ArrayList<User> pendingFriends;
    private int friendsCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pending_friends);
        pendingFriends = (ArrayList<User>) MySharedPreferences.getMsp().getObject(UtilityPack.KEYS.PENDING_FRIENDS, new ArrayList<>());
        friendsCount = getIntent().getIntExtra(MySharedPreferences.KEYS.FRIENDS_COUNT, -1);
        findViews();
        initAdapter();
    }

    private void initAdapter() {
        pending_friend_LST_recipes.setLayoutManager(new LinearLayoutManager(this));
        PendingFriendAdapter pendingFriendAdapter = new PendingFriendAdapter(this, pendingFriends);

        pendingFriendAdapter.setClickListener(new PendingFriendAdapter.ItemClickListener() {
            @Override
            public void onAddClick(int position) {
                User friend = pendingFriends.get(position);
                User.addToCurrentUserDatabase(friend.getUserID(), friendsCount++, UtilityPack.KEYS.MY_FRIENDS);
                friend.getMyFriends().add(FirebaseAuth.getInstance().getUid());
                friend.updateFirebase();
                User.removePendingFromCurrentUser(position, UtilityPack.KEYS.PENDING_FRIENDS);
                pendingFriends.remove(position);
                Toast.makeText(PendingFriendsActivity.this, "Friend added", Toast.LENGTH_SHORT);

                pending_friend_LST_recipes.setAdapter(pendingFriendAdapter); // TODO: 2/4/21 see if needed 

            }

            @Override
            public void onRemoveClick(int position) {
                pendingFriends.remove(position);

                User.removePendingFromCurrentUser(position, UtilityPack.KEYS.PENDING_FRIENDS);
                Toast.makeText(PendingFriendsActivity.this, "Recipe removed", Toast.LENGTH_SHORT);

                pending_friend_LST_recipes.setAdapter(pendingFriendAdapter);// TODO: 2/4/21 see if needed 

            }
        });
        pending_friend_LST_recipes.setAdapter(pendingFriendAdapter);
    }

    private void findViews() {
        pending_friend_LST_recipes = findViewById(R.id.pending_friend_LST_recipes);
    }
}