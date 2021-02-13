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
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.dreamest.cookbookapp.R;
import com.dreamest.cookbookapp.adapters.RecipeFirebaseAdapter;
import com.dreamest.cookbookapp.logic.Recipe;
import com.dreamest.cookbookapp.utility.FirebaseTools;
import com.dreamest.cookbookapp.utility.MySharedPreferences;
import com.dreamest.cookbookapp.utility.OnSwipeTouchListener;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MainActivity extends BaseActivity {

    private RecyclerView main_LST_recipes;
    private ImageButton main_BTN_add;
    private ImageView main_IMG_background;
    private MaterialButton main_BTN_pending;
    private TextView main_TXT_no_recipes;
    private ArrayList<String> pendingRecipes;
    private RecipeFirebaseAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViews();
        initViews();
        initAdapter();
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
                    main_TXT_no_recipes.setVisibility(View.GONE);
                } else {
                    main_TXT_no_recipes.setVisibility(View.VISIBLE);
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
    
    @Override
    protected void onResume() {
        super.onResume();
        if(FirebaseAuth.getInstance().getCurrentUser() == null) {
            preformLogout();
            return;
        }
        //Since we use lists, on reloading them they need to be emptied first.
        pendingRecipes = new ArrayList<>();
        loadPendingRecipes();
    }


    private void preformLogout() {
        Intent myIntent = new Intent(this, LoginActivity.class);
        startActivity(myIntent);
        finish();
    }

    private void loadPendingRecipes() {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference(FirebaseTools.DATABASE_KEYS.USERS)
                .child(firebaseUser.getUid())
                .child(FirebaseTools.DATABASE_KEYS.PENDING_RECIPES);
        ref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                pendingRecipes.add(snapshot.getValue(String.class));
                String message = getString(R.string.you_have) + " " + pendingRecipes.size() + " " + getString(R.string.pending_recipes);
                main_BTN_pending.setText(message);
                main_BTN_pending.setVisibility(View.VISIBLE);
            }
            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                pendingRecipes.remove(snapshot.getValue(String.class));
                if(pendingRecipes.size() == 0) {
                    main_BTN_pending.setVisibility(View.GONE);
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {}
            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {}
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w("dddd", "Failed to read value.", error.toException());
            }
        });
    }

    private void initAdapter() {
        main_LST_recipes.setLayoutManager(new LinearLayoutManager(this));

        DatabaseReference pendingRecipesRoot = FirebaseDatabase.getInstance()
                .getReference(FirebaseTools.DATABASE_KEYS.USERS)
                .child(FirebaseAuth.getInstance().getUid())
                .child(FirebaseTools.DATABASE_KEYS.MY_RECIPES);

        FirebaseRecyclerOptions<String> options
                = new FirebaseRecyclerOptions.Builder<String>()
                .setQuery(pendingRecipesRoot, String.class)
                .build();
        adapter = new RecipeFirebaseAdapter(options);

        adapter.setClickListener(new RecipeFirebaseAdapter.ItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                openRecipeActivity(position);
            }
        });
        main_LST_recipes.setAdapter(adapter);
    }

    private void initViews() {
        Glide
                .with(this)
                .load(R.drawable.background_diary)
                .fitCenter()
                .into(main_IMG_background);
        
        main_BTN_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addNewRecipe();
            }
        });

        main_LST_recipes.setOnTouchListener(new OnSwipeTouchListener(this) {
            public void onSwipeRight() {
                toFriendsList();
            }

            public void onSwipeLeft() {
                toProfile();
            }

        });

        main_BTN_pending.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openPendingRecipes();
            }
        });
    }

    private void openPendingRecipes() {
        Intent myIntent = new Intent(this, PendingRecipesActivity.class);
        startActivity(myIntent);
    }

    private void toProfile() {
        Intent myIntent = new Intent(MainActivity.this, ProfileActivity.class);
        startActivity(myIntent);

    }

    private void toFriendsList() {
        Intent myIntent = new Intent(MainActivity.this, FriendsListActivity.class);
        startActivity(myIntent);

    }

    private void addNewRecipe() {
        MySharedPreferences.getMsp().putObject(MySharedPreferences.KEYS.RECIPE, new Recipe());
        Intent myIntent = new Intent(this, EditRecipeActivity.class);
        startActivity(myIntent);
    }

    private void findViews() {
        main_BTN_add = findViewById(R.id.main_BTN_add);
        main_LST_recipes = findViewById(R.id.main_LST_recipes);
        main_IMG_background = findViewById(R.id.main_IMG_background);
        main_BTN_pending = findViewById(R.id.main_BTN_pending);
        main_TXT_no_recipes = findViewById(R.id.main_TXT_no_recipes);
    }

    private void openRecipeActivity(int position) {
        DatabaseReference ref = FirebaseDatabase.getInstance()
                .getReference(FirebaseTools.DATABASE_KEYS.RECIPES)
                .child(adapter.getItem(position));
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Recipe recipe = snapshot.getValue(Recipe.class);
                MySharedPreferences.getMsp().putObject(MySharedPreferences.KEYS.RECIPE, recipe);
                Intent myIntent = new Intent(MainActivity.this, RecipeActivity.class);
                startActivity(myIntent);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}