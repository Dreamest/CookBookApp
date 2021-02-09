package com.dreamest.cookbookapp.activities;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.Toast;

import com.dreamest.cookbookapp.R;

import com.dreamest.cookbookapp.adapters.PendingRecipeFirebaseAdapter;
import com.dreamest.cookbookapp.utility.FirebaseTools;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class PendingRecipesActivity extends BaseActivity {
    private RecyclerView pending_recipe_LST_recipes;
    private PendingRecipeFirebaseAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pending_recipes);
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
        pending_recipe_LST_recipes.setLayoutManager(new LinearLayoutManager(this));

        DatabaseReference pendingRecipesRoot = FirebaseDatabase.getInstance()
                .getReference(FirebaseTools.DATABASE_KEYS.USERS)
                .child(FirebaseAuth.getInstance().getUid())
                .child(FirebaseTools.DATABASE_KEYS.PENDING_RECIPES);

        FirebaseRecyclerOptions<String> options
                = new FirebaseRecyclerOptions.Builder<String>()
                .setQuery(pendingRecipesRoot, String.class)
                .build();
        adapter = new PendingRecipeFirebaseAdapter(options);

        adapter.setClickListener(new PendingRecipeFirebaseAdapter.ItemClickListener() {
            @Override
            public void onAddClick(int position) {
                String recipeID = adapter.getItem(position);
                addRecipe(recipeID);
            }

            @Override
            public void onRemoveClick(int position) {
                String recipeID = adapter.getItem(position);
                ignoreRecipe(recipeID);
            }
        });

        pending_recipe_LST_recipes.setAdapter(adapter);
    }

    private void ignoreRecipe(String recipeID) {
        FirebaseTools.actionToCurrentUserDatabase(FirebaseTools.REMOVE, recipeID, FirebaseTools.DATABASE_KEYS.PENDING_RECIPES);
        Toast.makeText(PendingRecipesActivity.this, R.string.recipe_ignored, Toast.LENGTH_SHORT).show();
    }

    private void addRecipe(String recipeID) {
        FirebaseTools.actionToCurrentUserDatabase(FirebaseTools.ADD, recipeID, FirebaseTools.DATABASE_KEYS.MY_RECIPES);
        FirebaseTools.actionToCurrentUserDatabase(FirebaseTools.REMOVE, recipeID, FirebaseTools.DATABASE_KEYS.PENDING_RECIPES);
        Toast.makeText(PendingRecipesActivity.this, R.string.recipe_added, Toast.LENGTH_SHORT).show();
    }

    private void findViews() {
        pending_recipe_LST_recipes = findViewById(R.id.pending_recipe_LST_recipes);
    }

}