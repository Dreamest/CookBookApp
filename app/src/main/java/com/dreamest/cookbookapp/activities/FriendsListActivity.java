package com.dreamest.cookbookapp.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.dreamest.cookbookapp.R;
import com.dreamest.cookbookapp.logic.FriendAdapter;
import com.dreamest.cookbookapp.logic.Recipe;
import com.dreamest.cookbookapp.logic.RecipeAdapter;
import com.dreamest.cookbookapp.logic.User;
import com.dreamest.cookbookapp.utility.MySharedPreferences;
import com.dreamest.cookbookapp.utility.UtilityPack;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class FriendsListActivity extends BaseActivity {
    // TODO: 2/2/21 This activity has not been tested with actual friends
    private RecyclerView friendslist_LST_friends;
    private ImageButton friendslist_BTN_add_friend;
    private TextView friends_TXT_no_friends;
    private ArrayList<User> friendslist;
    private User currentUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends_list);

        friendslist = new ArrayList<>();

        findViews();
        loadUser();
        loadFriendsFromDatabase();
        initViews();
    }

    private void loadFriendsFromDatabase() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference(UtilityPack.KEYS.USERS)
                .child(currentUser.getUserID())
                .child(UtilityPack.KEYS.MY_FRIENDS);

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()) {
                    friends_TXT_no_friends.setVisibility(View.GONE);
                    Iterable<DataSnapshot> friendsIDs = snapshot.getChildren();
                    for (DataSnapshot id : friendsIDs) {
                        loadUserFromDatabase(id, database);
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

    private void loadUserFromDatabase(DataSnapshot id, FirebaseDatabase database) {
        DatabaseReference friendRef = database.getReference(UtilityPack.KEYS.RECIPES)
                .child(id.getValue(String.class));
        friendRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                // TODO: 2/2/21 Use this code if loading ingredients becomes an issue.
                User friend = new User()
                        .setDisplayName(snapshot.child(UtilityPack.KEYS.DISPLAY_NAME).getValue(String.class))
                        .setProfileImage(snapshot.child(UtilityPack.KEYS.PROFILE_IMAGE).getValue(StorageReference.class));
                friendslist.add(friend);
                //Don't care about other values
                initAdapter();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w("dddd", "Failed to read value.", error.toException());
            }
        });
    }

        private void loadUser () {
            currentUser = (User) MySharedPreferences.getMsp().getObject(MySharedPreferences.KEYS.USER, new User());
        }

        private void initViews () {
            initAdapter();

            friendslist_BTN_add_friend.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    addNewFriend();
                }
            });
        }

        private void addNewFriend () {
            // TODO: 2/2/21 implement
        }

        private void findViews () {
            friendslist_LST_friends = findViewById(R.id.friendslist_LST_friends);
            friendslist_BTN_add_friend = findViewById(R.id.friendslist_BTN_add_friend);
            friends_TXT_no_friends = findViewById(R.id.friends_TXT_no_friends);
        }

        private void initAdapter () {
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
        private void openChatWith ( int position){
            // TODO: 2/2/21 implement
        }
    }