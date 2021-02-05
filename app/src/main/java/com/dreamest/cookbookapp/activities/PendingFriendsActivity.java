package com.dreamest.cookbookapp.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.dreamest.cookbookapp.R;
import com.dreamest.cookbookapp.adapters.PendingFriendAdapter;
import com.dreamest.cookbookapp.logic.User;
import com.dreamest.cookbookapp.utility.MySharedPreferences;
import com.dreamest.cookbookapp.utility.UtilityPack;
import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class PendingFriendsActivity extends BaseActivity {
    // TODO: 2/4/21 untested. 
    private RecyclerView pending_friend_LST_recipes;
    private ArrayList<User> pendingFriends;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pending_friends);
        Type listType = new TypeToken<ArrayList<User>>(){}.getType();
        pendingFriends = (ArrayList<User>) MySharedPreferences.getMsp().getObject(UtilityPack.KEYS.PENDING_FRIENDS, new ArrayList<User>(), listType);

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
                User.actionToCurrentUserDatabase(User.ADD, friend.getUserID(), UtilityPack.KEYS.MY_FRIENDS);
                friend.getMyFriends().put(FirebaseAuth.getInstance().getUid(), FirebaseAuth.getInstance().getUid());
                friend.updateFirebase();
                User.actionToCurrentUserDatabase(User.ADD, friend.getUserID(), UtilityPack.KEYS.PENDING_FRIENDS);
                pendingFriends.remove(position);
                Toast.makeText(PendingFriendsActivity.this, "Friend added", Toast.LENGTH_SHORT).show();

                pending_friend_LST_recipes.setAdapter(pendingFriendAdapter);

            }

            @Override
            public void onRemoveClick(int position) {
                User friend = pendingFriends.get(position);
                pendingFriends.remove(position);

                User.actionToCurrentUserDatabase(User.REMOVE, friend.getUserID(), UtilityPack.KEYS.PENDING_FRIENDS);
                Toast.makeText(PendingFriendsActivity.this, "Request removed", Toast.LENGTH_SHORT).show();

                pending_friend_LST_recipes.setAdapter(pendingFriendAdapter);

            }
        });
        pending_friend_LST_recipes.setAdapter(pendingFriendAdapter);
    }

    private void findViews() {
        pending_friend_LST_recipes = findViewById(R.id.pending_friend_LST_recipes);
    }
}