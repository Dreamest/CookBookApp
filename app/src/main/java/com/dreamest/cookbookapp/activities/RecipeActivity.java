package com.dreamest.cookbookapp.activities;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.dreamest.cookbookapp.R;
import com.dreamest.cookbookapp.adapters.IngredientAdapter;
import com.dreamest.cookbookapp.logic.Recipe;
import com.dreamest.cookbookapp.utility.FirebaseTools;
import com.dreamest.cookbookapp.utility.MySharedPreferences;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class RecipeActivity extends BaseActivity {
    private ImageButton recipe_BTN_share;
    private ImageButton recipe_BTN_edit;
    private ImageButton recipe_BTN_remove;
    private TextView recipe_TXT_title;
    private TextView recipe_TXT_owner;
    private TextView recipe_TXT_date;
    private TextView recipe_TXT_method;
    private ShapeableImageView recipe_IMG_image;
    private RecyclerView recipe_LST_ingredients;
    private Recipe recipe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe);
        findViews();
        initViews();

    }

    @Override
    protected void onResume() {
        super.onResume();
        loadRecipe(); //So that recipe will update when returning from Edit as well.
        initAdapter(); //Needs to run after the recipe was loaded
        visualizeRecipe();
    }

    private void initViews() {
        recipe_BTN_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareRecipe();
            }
        });

        recipe_BTN_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editRecipe();
            }
        });

        recipe_BTN_remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeRecipe();
            }
        });

    }

    private void removeRecipe() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database
                .getReference(FirebaseTools.DATABASE_KEYS.USERS)
                .child(FirebaseAuth.getInstance().getUid())
                .child(FirebaseTools.DATABASE_KEYS.MY_RECIPES)
                .child(recipe.getRecipeID());
        ref.removeValue();
        finish();
    }

    private void visualizeRecipe() {
        if(!recipe.getOwnerID().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
            recipe_BTN_edit.setVisibility(View.GONE);
        }
        recipe_TXT_title.setText(recipe.getTitle());
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference(FirebaseTools.DATABASE_KEYS.USERS).child(recipe.getOwnerID()).child(FirebaseTools.DATABASE_KEYS.DISPLAY_NAME);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                recipe_TXT_owner.setText(snapshot.getValue(String.class));
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w("dddd", "Failed to read value.", error.toException());
            }
        });
        recipe_TXT_date.setText(recipe.getDate());
        recipe_TXT_method.setText(recipe.getMethod());
        FirebaseTools.downloadImage(this, recipe.getImagePath(), recipe.getRecipeID(), FirebaseTools.FILE_KEYS.JPG,
                recipe_IMG_image, ContextCompat.getDrawable(this, R.drawable.ic_loading), R.drawable.ic_no_image);
    }

    private void initAdapter() {
        recipe_LST_ingredients.setLayoutManager(new LinearLayoutManager(this));
        IngredientAdapter ingredientAdapter = new IngredientAdapter(this, recipe.getIngredients(), IngredientAdapter.CHECKBOX);

        ingredientAdapter.setClickListener(new IngredientAdapter.ItemClickListener() {
            @Override
            public void onRemoveClick(int position) {
            }
        });
        recipe_LST_ingredients.setAdapter(ingredientAdapter);
    }

    private void editRecipe() {
        Intent myIntent = new Intent(this, EditRecipeActivity.class);
        MySharedPreferences.getMsp().putObject(MySharedPreferences.KEYS.RECIPE, recipe); //Might be redundant
        startActivity(myIntent);
    }

    private void shareRecipe() {
        Intent myIntent = new Intent(this, ShareRecipeActivity.class);
        myIntent.putExtra(FirebaseTools.DATABASE_KEYS.RECIPE_ID, recipe.getRecipeID()); //ID of the recipe to share
        startActivity(myIntent);

    }

    private void loadRecipe() {
        recipe = (Recipe) MySharedPreferences.getMsp().getObject(MySharedPreferences.KEYS.RECIPE, new Recipe(), Recipe.class);
    }

    private void findViews() {
        recipe_BTN_share = findViewById(R.id.recipe_BTN_share);
        recipe_BTN_edit = findViewById(R.id.recipe_BTN_edit);
        recipe_BTN_remove = findViewById(R.id.recipe_BTN_remove);
        recipe_TXT_title = findViewById(R.id.recipe_TXT_title);
        recipe_TXT_owner = findViewById(R.id.recipe_TXT_owner);
        recipe_TXT_date = findViewById(R.id.recipe_TXT_date);
        recipe_TXT_method = findViewById(R.id.recipe_TXT_method);
        recipe_IMG_image = findViewById(R.id.recipe_IMG_image);
        recipe_LST_ingredients = findViewById(R.id.recipe_LST_ingredients);
    }
}