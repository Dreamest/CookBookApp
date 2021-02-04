package com.dreamest.cookbookapp.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.dreamest.cookbookapp.R;
import com.dreamest.cookbookapp.adapters.IngredientAdapterRemoveBTN;
import com.dreamest.cookbookapp.adapters.PendingRecipeAdapter;
import com.dreamest.cookbookapp.logic.Recipe;
import com.dreamest.cookbookapp.logic.User;
import com.dreamest.cookbookapp.utility.HideUI;
import com.dreamest.cookbookapp.utility.MySharedPreferences;
import com.dreamest.cookbookapp.utility.UtilityPack;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class PendingRecipesActivity extends AppCompatActivity {
    // TODO: 2/4/21 untested.
    private RecyclerView pending_LST_recipes;
    private TextView pending_TXT_title;
    private ArrayList<Recipe> pendingRecipes;
    private int recipeCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pending_recipes);
        recipeCount = getIntent().getIntExtra(MySharedPreferences.KEYS.RECIPE_COUNT, -1);
        pendingRecipes = (ArrayList<Recipe>) MySharedPreferences.getMsp().getObject(MySharedPreferences.KEYS.RECIPES_LIST, new ArrayList<>());
        findViews();
        initAdapter();
    }

    private void initAdapter() {
        pending_LST_recipes.setLayoutManager(new LinearLayoutManager(this));
        PendingRecipeAdapter pendingRecipeAdapter = new PendingRecipeAdapter(this, pendingRecipes);

        pendingRecipeAdapter.setClickListener(new PendingRecipeAdapter.ItemClickListener() {
            @Override
            public void onAddClick(int position) {
                Recipe recipe = pendingRecipes.get(position);
                User.addRecipeToCurrentUserDatabase(recipe.getRecipeID(), recipeCount++);
                User.removePendingRecipe(position);
                pendingRecipes.remove(position);
                Toast.makeText(PendingRecipesActivity.this, "Recipe added", Toast.LENGTH_SHORT);
            }

            @Override
            public void onRemoveClick(int position) {
                pendingRecipes.remove(position);
                User.removePendingRecipe(position);
                Toast.makeText(PendingRecipesActivity.this, "Recipe removed", Toast.LENGTH_SHORT);
            }
        });
        pending_LST_recipes.setAdapter(pendingRecipeAdapter);
    }



    private void findViews() {
        pending_LST_recipes = findViewById(R.id.pending_LST_recipes);
        pending_TXT_title = findViewById(R.id.pending_TXT_title);
    }

}