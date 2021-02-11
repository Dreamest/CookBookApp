package com.dreamest.cookbookapp.activities;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.dreamest.cookbookapp.R;
import com.dreamest.cookbookapp.adapters.FriendFirebaseAdapter;
import com.dreamest.cookbookapp.utility.FirebaseTools;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class ShareRecipeActivity extends BaseActivity {
    private RecyclerView share_LST_friends;
    private TextView share_TXT_no_friends;
    private ImageView share_IMG_background;
    private String recipeToShare;
    private FriendFirebaseAdapter adapter;

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
        initFirebaseAdapter();
    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                if (adapter.getItemCount() > 0) {
                    share_TXT_no_friends.setVisibility(View.GONE);
                } else {
                    share_TXT_no_friends.setVisibility(View.VISIBLE);
                }
            }
        });
        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    private void findViews() {
        share_TXT_no_friends = findViewById(R.id.share_TXT_no_friends);
        share_LST_friends = findViewById(R.id.share_LST_friends);
        share_IMG_background = findViewById(R.id.share_IMG_background);
    }

    private void initFirebaseAdapter() {
        share_LST_friends.setLayoutManager(new LinearLayoutManager(this));

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
                shareWith(adapter.getItem(position));
            }
        });
        share_LST_friends.setAdapter(adapter);
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