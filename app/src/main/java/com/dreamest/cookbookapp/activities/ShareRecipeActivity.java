package com.dreamest.cookbookapp.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.dreamest.cookbookapp.R;
import com.dreamest.cookbookapp.adapters.FriendAdapter;
import com.dreamest.cookbookapp.logic.User;
import com.dreamest.cookbookapp.utility.MySharedPreferences;
import com.dreamest.cookbookapp.utility.UtilityPack;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Iterator;

public class ShareRecipeActivity extends BaseActivity {
    private RecyclerView share_LST_friends;
    private TextView share_TXT_no_friends;
    private String recipeToShare;
    private ArrayList<User> friendslist;
    private User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_recipe);
        recipeToShare = getIntent().getStringExtra(UtilityPack.KEYS.RECIPE_ID);
        currentUser = (User) MySharedPreferences.getMsp().getObject(MySharedPreferences.KEYS.USER, new User(), User.class);
        friendslist = new ArrayList<>();
        findViews();
        loadFriendsFromDatabase();
    }

    private void findViews() {
        share_TXT_no_friends = findViewById(R.id.share_TXT_no_friends);
        share_LST_friends = findViewById(R.id.share_LST_friends);
    }

    private void initAdapter () {
        share_LST_friends.setLayoutManager(new LinearLayoutManager(this));
        FriendAdapter friendAdapter = new FriendAdapter(this, friendslist);

        friendAdapter.setClickListener(new FriendAdapter.ItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                shareWith(position);
            }
        });
        share_LST_friends.setAdapter(friendAdapter);
    }

    private void shareWith(int position) {
        String friendUID = friendslist.get(position).getUserID();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database
                .getReference(UtilityPack.KEYS.USERS)
                .child(friendUID)
                .child(UtilityPack.KEYS.PENDING_RECIPES).child(recipeToShare);
        ref.setValue(recipeToShare);
        // TODO: 2/5/21 assumption: Adding an onSuccess listener here for toast will work only if new value is added?
//        ref.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                long count = snapshot.getChildrenCount(); //How many pending recipes the user currently have
//                ref.child(String.valueOf(count)).setValue(recipeToShare); //Ensures the storing method is array
//                Toast.makeText(ShareRecipeActivity.this, "Recipe shared", Toast.LENGTH_SHORT).show();
//                finish();
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//                Log.w("dddd", "Failed to read value.", error.toException());
//            }
//        });
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
                if(snapshot.exists()) {
                    share_TXT_no_friends.setVisibility(View.GONE);
                    Iterator<DataSnapshot> iterator = snapshot.getChildren().iterator();
                    while(iterator.hasNext()) {
                        DataSnapshot friendSnapshot = iterator.next();
                        loadFriend(friendSnapshot, database, !iterator.hasNext());
                    }
                } else {
                    share_TXT_no_friends.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w("dddd", "Failed to read value.", error.toException());
            }
        });
    }

    private void loadFriend(DataSnapshot id, FirebaseDatabase database, boolean last) {
        DatabaseReference friendRef = database.getReference(UtilityPack.KEYS.USERS)
                .child(id.getValue(String.class));
        friendRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User friend = snapshot.getValue(User.class);
                Log.d("dddd", "The name is: " + friend.getDisplayName() );
                friendslist.add(friend);
                if(last) {
                    initAdapter();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w("dddd", "Failed to read value.", error.toException());
            }
        });
    }
}