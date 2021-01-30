package com.dreamest.cookbookapp.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.ImageView;

import com.dreamest.cookbookapp.R;
import com.dreamest.cookbookapp.logic.Ingredient;
import com.dreamest.cookbookapp.logic.IngredientAdapterCheckbox;
import com.dreamest.cookbookapp.logic.IngredientAdapterRemoveBTN;
import com.dreamest.cookbookapp.logic.Recipe;
import com.dreamest.cookbookapp.utility.MySharedPreferences;
import com.gildaswise.horizontalcounter.HorizontalCounter;
import com.github.drjacky.imagepicker.ImagePicker;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class EditRecipeActivity extends AppCompatActivity {
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
        edit_CTR_prepTime.setCurrentValue((double)recipe.getPrepTime());

        /*
        Code for this library is slightly faulty. setCurrentValue updates the stored value but not the view.
        Calling setDisplayInteger activates a private function that updates the view.
         */
        edit_CTR_prepTime.setDisplayingInteger(true);


    }

    private void initViews() {
        edit_IMG_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImagePicker.Companion.with(EditRecipeActivity.this)
                        .saveDir(new File(Environment.getExternalStorageDirectory(), "ImagePicker"))
                        //Currently stores in basic "camera" folder
                        .start();
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

    private void changeDifficulty(int rank) {
        // TODO: 1/29/21 requires testing
        for (ImageView star : stars) {
            if ((int) star.getTag() <= rank) {
                star.setImageResource(R.drawable.ic_full_star);
            } else {
                star.setImageResource(R.drawable.ic_empty_star);
            }
        }
        difficulty = rank;
    }

    private void loadIngredients() {
        edit_LST_ingredients.setLayoutManager(new LinearLayoutManager(this));
        IngredientAdapterRemoveBTN ingredientAdapter = new IngredientAdapterRemoveBTN(this, ingredients);

        ingredientAdapter.setClickListener(new IngredientAdapterRemoveBTN.ItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
            }

            @Override
            public void onRemoveClick(int position) {
                ingredients.remove(position);
                initAdapter(ingredientAdapter); //Needs to be reapplied to update the view
            }
        });
        initAdapter(ingredientAdapter);
    }

    private void initAdapter(IngredientAdapterRemoveBTN ingredientAdapter) {
        edit_LST_ingredients.setAdapter(ingredientAdapter);
        if (recipe.getIngredients().size() == 0) {
            edit_BTN_add_ingredient.setText(R.string.add_first_ingredient);
        } else {
            edit_BTN_add_ingredient.setText(R.string.add_another_ingredient);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // TODO: 1/29/21 assumption: need to call loadRecyclerView in order to update with new ingredient.
        // TODO: 1/29/21 but first, add new ingredient to ingredients list
        loadIngredients();
    }

    private void previewRecipe() {
        // TODO: 1/29/21 implement - send to recipeActivity without unnecessary buttons.
    }

    private void submitRecipe() {
        String pattern = "dd.MM.yyyy";
        SimpleDateFormat format = new SimpleDateFormat(pattern);
        recipe.setDate(format.format(new Date()));
        recipe.setDifficulty(difficulty);
//        recipe.setImage() //todo: Figure how to set the image
        recipe.setIngredients(ingredients);
        recipe.setMethod(edit_EDT_method.getText().toString());
//        recipe.setOwner() //todo: current user name
//        recipe.setOwnerID() //todo: current user
        recipe.setPrepTime(edit_CTR_prepTime.getCurrentValue().intValue());
        recipe.setDifficulty(difficulty);
        recipe.setTitle(edit_EDT_title.getText().toString());
//        recipe.setRecipeID() //todo: implement with firebase
    }

    private void addNewIngredient() {
        // TODO: 1/29/21 Implement move to a new activity
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