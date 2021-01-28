package com.dreamest.cookbookapp.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.dreamest.cookbookapp.R;
import com.dreamest.cookbookapp.logic.Ingredient;
import com.dreamest.cookbookapp.logic.IngredientAdapter;
import com.dreamest.cookbookapp.logic.Recipe;
import com.dreamest.cookbookapp.logic.RecipeAdapter;
import com.dreamest.cookbookapp.utility.MySharedPreferences;
import com.dreamest.cookbookapp.utility.TestUnit;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

public class RecipeActivity extends BaseActivity {
    ImageButton recipe_BTN_share;
    ImageButton recipe_BTN_edit;
    TextView recipe_TXT_title;
    TextView recipe_TXT_owner;
    TextView recipe_TXT_date;
    TextView recipe_TXT_method;
    ShapeableImageView recipe_IMG_image;
    RecyclerView recipe_LST_ingredients;
    private Recipe recipe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe);
        findViews();
        initViews();
    }

    private void initViews() {
        loadRecipe();
        recipe_LST_ingredients.setLayoutManager(new LinearLayoutManager(this));
        IngredientAdapter ingredientAdapter = new IngredientAdapter(this, recipe.getIngredients());

        ingredientAdapter.setClickListener(new IngredientAdapter.ItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {

            }
        });
        recipe_LST_ingredients.setAdapter(ingredientAdapter);

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
        // TODO: 1/28/21 return those lines after firebase is established more 
//        if(!recipe.getOwnerID().equals(/*firebase current user id*/)) {
//            recipe_BTN_edit.setVisibility(View.GONE);
//        }
        recipe_TXT_title.setText(recipe.getTitle());
        recipe_TXT_owner.setText(recipe.getOwner());
        recipe_TXT_date.setText(recipe.getDate());
        recipe_TXT_method.setText(recipe.getMethod());
        Glide
                .with(this)
                .load(recipe.getImage())
                .centerCrop()
                .into(recipe_IMG_image)
                .onLoadFailed(ContextCompat.getDrawable(this, R.drawable.ic_no_image));
    }

    private void editRecipe() {
        Log.d("dddd", "edit clicked");
        // TODO: 1/28/21 Implement later
    }

    private void shareRecipe() {
        Log.d("dddd", "share clicked");
        // TODO: 1/28/21 Implement later 
    }

    private void loadRecipe() {
        recipe = (Recipe) MySharedPreferences.getMsp().getObject(MySharedPreferences.KEYS.RECIPE, new Recipe());
    }

    private void changeBoxStatus(View v, int position) {

    }

    private void findViews() {
        recipe_BTN_share = findViewById(R.id.recipe_BTN_share);
        recipe_BTN_edit = findViewById(R.id.recipe_BTN_edit);
        recipe_TXT_title = findViewById(R.id.recipe_TXT_title);
        recipe_TXT_owner = findViewById(R.id.recipe_TXT_owner);
        recipe_TXT_date = findViewById(R.id.recipe_TXT_date);
        recipe_TXT_method = findViewById(R.id.recipe_TXT_method);
        recipe_IMG_image = findViewById(R.id.recipe_IMG_image);
        recipe_LST_ingredients = findViewById(R.id.recipe_LST_ingredients);
    }
}