package com.dreamest.cookbookapp.activities;

import android.os.Bundle;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dreamest.cookbookapp.R;
import com.dreamest.cookbookapp.adapters.PendingRecipeFirebaseAdapter;
import com.dreamest.cookbookapp.utility.FirebaseListener;
import com.dreamest.cookbookapp.utility.FirebaseTools;


public class PendingRecipesActivity extends BaseActivity {
    private RecyclerView pending_recipe_LST_recipes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pending_recipes);
        findViews();
        bindAdapter();
    }

    private void bindAdapter() {
        pending_recipe_LST_recipes.setLayoutManager(new LinearLayoutManager(this));

        FirebaseListener.getFirebaseListener().getPendingRecipeFirebaseAdapter().setClickListener(new PendingRecipeFirebaseAdapter.ItemClickListener() {
            @Override
            public void onAddClick(int position) {
                String recipeID = FirebaseListener.getFirebaseListener().getPendingRecipeFirebaseAdapter().getItem(position);
                addRecipe(recipeID);
            }

            @Override
            public void onRemoveClick(int position) {
                String recipeID = FirebaseListener.getFirebaseListener().getPendingRecipeFirebaseAdapter().getItem(position);
                ignoreRecipe(recipeID);
            }
        });

        pending_recipe_LST_recipes.setAdapter(FirebaseListener.getFirebaseListener().getPendingRecipeFirebaseAdapter());
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