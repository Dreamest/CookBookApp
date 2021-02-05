package com.dreamest.cookbookapp.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.Toast;

import com.dreamest.cookbookapp.R;
import com.dreamest.cookbookapp.adapters.PendingRecipeAdapter;
import com.dreamest.cookbookapp.logic.Recipe;
import com.dreamest.cookbookapp.logic.User;
import com.dreamest.cookbookapp.utility.MySharedPreferences;
import com.dreamest.cookbookapp.utility.UtilityPack;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class PendingRecipesActivity extends BaseActivity {
    // TODO: 2/4/21 untested.
    private RecyclerView pending_recipe_LST_recipes;
    private ArrayList<Recipe> pendingRecipes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pending_recipes);
        Type listType = new TypeToken<ArrayList<Recipe>>(){}.getType();
        pendingRecipes = (ArrayList<Recipe>) MySharedPreferences.getMsp().getObject(MySharedPreferences.KEYS.MY_RECIPES_ARRAY, new ArrayList<Recipe>(), listType);
        findViews();
        initAdapter();
    }

    private void initAdapter() {
        pending_recipe_LST_recipes.setLayoutManager(new LinearLayoutManager(this));
        PendingRecipeAdapter pendingRecipeAdapter = new PendingRecipeAdapter(this, pendingRecipes);

        pendingRecipeAdapter.setClickListener(new PendingRecipeAdapter.ItemClickListener() {
            @Override
            public void onAddClick(int position) {
                Recipe recipe = pendingRecipes.get(position);
                User.actionToCurrentUserDatabase(User.ADD, recipe.getRecipeID(), UtilityPack.KEYS.MY_RECIPES);
                User.actionToCurrentUserDatabase(User.REMOVE, recipe.getRecipeID(), UtilityPack.KEYS.PENDING_RECIPES);
                pendingRecipes.remove(position);
                Toast.makeText(PendingRecipesActivity.this, "Recipe added", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onRemoveClick(int position) {
                Recipe recipe = pendingRecipes.get(position);
                pendingRecipes.remove(position);
                User.actionToCurrentUserDatabase(User.REMOVE, recipe.getRecipeID(), UtilityPack.KEYS.PENDING_RECIPES);
                Toast.makeText(PendingRecipesActivity.this, "Recipe removed", Toast.LENGTH_SHORT).show();
            }
        });
        pending_recipe_LST_recipes.setAdapter(pendingRecipeAdapter);
    }

    private void findViews() {
        pending_recipe_LST_recipes = findViewById(R.id.pending_recipe_LST_recipes);
    }

}