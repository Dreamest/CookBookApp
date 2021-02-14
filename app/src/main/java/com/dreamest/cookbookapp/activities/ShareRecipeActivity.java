package com.dreamest.cookbookapp.activities;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.dreamest.cookbookapp.R;
import com.dreamest.cookbookapp.adapters.FriendFirebaseAdapter;
import com.dreamest.cookbookapp.utility.FirebaseListener;
import com.dreamest.cookbookapp.utility.FirebaseTools;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ShareRecipeActivity extends BaseActivity {
    private RecyclerView share_LST_friends;
    private ImageView share_IMG_background;
    private String recipeToShare;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_recipe);
        recipeToShare = getIntent().getStringExtra(FirebaseTools.DATABASE_KEYS.RECIPE_ID);
        findViews();
        Glide
                .with(this)
                .load(R.drawable.recipe_box)
                .into(share_IMG_background);
        bindAdapter();
    }

    private void findViews() {
        share_LST_friends = findViewById(R.id.share_LST_friends);
        share_IMG_background = findViewById(R.id.share_IMG_background);
    }

    private void bindAdapter() {
        share_LST_friends.setLayoutManager(new LinearLayoutManager(this));

        FirebaseListener.getFirebaseListener().getFriendFirebaseAdapter().setClickListener(new FriendFirebaseAdapter.ItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                shareWith(FirebaseListener.getFirebaseListener().getFriendFirebaseAdapter().getItem(position));
            }
        });
        share_LST_friends.setAdapter(FirebaseListener.getFirebaseListener().getFriendFirebaseAdapter());
    }

    private void shareWith(String friendUID) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database
                .getReference(FirebaseTools.DATABASE_KEYS.USERS)
                .child(friendUID)
                .child(FirebaseTools.DATABASE_KEYS.PENDING_RECIPES).child(recipeToShare);
        ref.setValue(recipeToShare);
        Toast.makeText(this, R.string.recipe_shared, Toast.LENGTH_SHORT).show();
        finish();
    }

}