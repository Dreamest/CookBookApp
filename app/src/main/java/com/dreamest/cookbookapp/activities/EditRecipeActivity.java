package com.dreamest.cookbookapp.activities;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.dreamest.cookbookapp.R;
import com.dreamest.cookbookapp.logic.Ingredient;
import com.dreamest.cookbookapp.logic.IngredientAdapterRemoveBTN;
import com.dreamest.cookbookapp.logic.Recipe;
import com.dreamest.cookbookapp.logic.User;
import com.dreamest.cookbookapp.utility.HideUI;
import com.dreamest.cookbookapp.utility.KeyMaker;
import com.dreamest.cookbookapp.utility.MySharedPreferences;
import com.gildaswise.horizontalcounter.HorizontalCounter;
import com.github.drjacky.imagepicker.ImagePicker;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class EditRecipeActivity extends BaseActivity {
    private TextInputEditText edit_EDT_title;
    private MaterialButton edit_BTN_add_ingredient;
    private TextInputEditText edit_EDT_method;
    private ImageView edit_IMG_image;
    private MaterialButton edit_BTN_submit;
    private MaterialButton edit_BTN_preview;
    private RecyclerView edit_LST_ingredients;
    private ImageView[] stars;
    private HorizontalCounter edit_CTR_prepTime;
    private Recipe recipe;
    private ArrayList<Ingredient> ingredients;
    private int difficulty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_recipe);
        findViews();
        loadRecipe();
        initViews();
    }

    private void loadRecipe() {
        recipe = (Recipe) MySharedPreferences.getMsp().getObject(MySharedPreferences.KEYS.RECIPE, new Recipe());
        ingredients = recipe.getIngredients();
        difficulty = recipe.getDifficulty();
        edit_EDT_title.setText(recipe.getTitle());
        edit_EDT_method.setText(recipe.getMethod());
        changeDifficulty(recipe.getDifficulty());
        Glide        //Not working currently

                .with(this)
                .load(recipe.getImage())
                .centerCrop()
                .into(edit_IMG_image)
                .onLoadStarted(ContextCompat.getDrawable(edit_IMG_image.getContext(), R.drawable.ic_no_image));

        edit_CTR_prepTime.setCurrentValue((double) recipe.getPrepTime());
        edit_CTR_prepTime.setDisplayingInteger(true);
        /*
        Code for this library is slightly faulty. setCurrentValue updates the stored value but not the view.
        Calling setDisplayInteger activates a private function that updates the view.
        */

        loadIngredients();

    }

    private void initViews() {
        edit_IMG_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImagePicker.Companion.with(EditRecipeActivity.this)
                        .saveDir(new File(Environment.getExternalStorageDirectory(), "ImagePicker"))
                        //Currently stores in basic "camera" folder
                        .start();
                // TODO: 2/2/21 Image picker isn't working properly.
            }
        });

        edit_EDT_method.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    edit_EDT_method.clearFocus();
                    HideUI.hideSystemUI(EditRecipeActivity.this);
                }
                return false;
            }
        });

        edit_EDT_title.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    edit_EDT_title.clearFocus();
                    HideUI.hideSystemUI(EditRecipeActivity.this);
                }
                return false;
            }
        });

        edit_BTN_add_ingredient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addNewIngredient();
            }
        });


        edit_BTN_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitRecipe();
            }
        });

        edit_BTN_preview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                previewRecipe();
            }
        });

        for (ImageView star : stars) {
            star.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    changeDifficulty((int) star.getTag());
                }
            });
        }
        loadIngredients();
    }

    private void changeDifficulty(int difficulty) {
        for (ImageView star : stars) {
            if ((int) star.getTag() <= difficulty) {
                star.setImageResource(R.drawable.ic_full_star);
            } else {
                star.setImageResource(R.drawable.ic_empty_star);
            }
        }
        this.difficulty = difficulty;
    }

    private void loadIngredients() {
        edit_LST_ingredients.setLayoutManager(new LinearLayoutManager(this));
        IngredientAdapterRemoveBTN ingredientAdapter = new IngredientAdapterRemoveBTN(this, ingredients);

        ingredientAdapter.setClickListener(new IngredientAdapterRemoveBTN.ItemClickListener() {
            @Override
            public void onRemoveClick(int position) {
                ingredients.remove(position);
                updateAdapter(ingredientAdapter); //Needs to be reapplied to update the view
            }
        });
        updateAdapter(ingredientAdapter);
    }

    private void updateAdapter(IngredientAdapterRemoveBTN ingredientAdapter) {
        edit_LST_ingredients.setAdapter(ingredientAdapter);
        updateAddButton();
    }

    private void updateAddButton() {
        if (recipe.getIngredients().size() == 0) {
            edit_BTN_add_ingredient.setText(R.string.add_first_ingredient);
        } else {
            edit_BTN_add_ingredient.setText(R.string.add_another_ingredient);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(MySharedPreferences.getMsp().getBoolean(MySharedPreferences.KEYS.UPDATED_INGREDIENT, false)) {
            MySharedPreferences.getMsp().putBoolean(MySharedPreferences.KEYS.UPDATED_INGREDIENT, false);
            ingredients.add((Ingredient) MySharedPreferences.getMsp().getObject(MySharedPreferences.KEYS.INGREDIENT, new Ingredient()));
            loadIngredients();

        }
    }

    private void previewRecipe() {
        // TODO: 1/29/21 implement - send to recipeActivity without unnecessary buttons.
    }

    private void submitRecipe() {
        updateRecipe();
        MySharedPreferences.getMsp().putObject(MySharedPreferences.KEYS.RECIPE, recipe);
        recipe.storeInFirebase();
        User.addRecipeToCurrentUserDatabase(recipe.getRecipeID());
        finish();
    }

    private void updateRecipe() {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

        String pattern = "dd.MM.yyyy";
        SimpleDateFormat format = new SimpleDateFormat(pattern);
        recipe.setDate(format.format(new Date()));
        recipe.setDifficulty(difficulty);
//        recipe.setImage() //todo: Figure how to set the image
        recipe.setIngredients(ingredients);
        recipe.setMethod(edit_EDT_method.getText().toString());
        recipe.setOwner(firebaseUser.getDisplayName());
        recipe.setOwnerID(firebaseUser.getUid());
        recipe.setPrepTime(edit_CTR_prepTime.getCurrentValue().intValue());
        recipe.setDifficulty(difficulty);
        recipe.setTitle(edit_EDT_title.getText().toString());
        if (recipe.getRecipeID().equals("")) {
            recipe.setRecipeID(KeyMaker.getNewRecipeKey()); // TODO: 2/2/21 This isn't implemented.
            Log.d("dddd", recipe.getRecipeID());
        }
    }

    private void addNewIngredient() {
        MySharedPreferences.getMsp().putObject(MySharedPreferences.KEYS.INGREDIENT, new Ingredient()); //Reset the held ingredient
        Intent myIntent = new Intent(this, AddIngredientActivity.class);
        startActivity(myIntent);
    }

    private void findViews() {
        stars = new ImageView[5];
        edit_EDT_title = findViewById(R.id.edit_EDT_title);
        edit_BTN_add_ingredient = findViewById(R.id.edit_BTN_add_ingredient);
        edit_EDT_method = findViewById(R.id.edit_EDT_method);
        edit_IMG_image = findViewById(R.id.edit_IMG_image);
        edit_BTN_submit = findViewById(R.id.edit_BTN_submit);
        edit_BTN_preview = findViewById(R.id.edit_BTN_preview);
        edit_LST_ingredients = findViewById(R.id.edit_LST_ingredients);
        stars[0] = findViewById(R.id.edit_IMG_star1);
        stars[0].setTag(1);
        stars[1] = findViewById(R.id.edit_IMG_star2);
        stars[1].setTag(2);
        stars[2] = findViewById(R.id.edit_IMG_star3);
        stars[2].setTag(3);
        stars[3] = findViewById(R.id.edit_IMG_star4);
        stars[3].setTag(4);
        stars[4] = findViewById(R.id.edit_IMG_star5);
        stars[4].setTag(5);
        edit_CTR_prepTime = findViewById(R.id.edit_CTR_prepTime);
    }
}